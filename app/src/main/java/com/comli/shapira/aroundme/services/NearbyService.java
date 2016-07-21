package com.comli.shapira.aroundme.services;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;

import com.comli.shapira.aroundme.data.DetailsRequest;
import com.comli.shapira.aroundme.data.DetailsResponse;
import com.comli.shapira.aroundme.data.NearbyRequest;
import com.comli.shapira.aroundme.data.NearbyResponse;
import com.comli.shapira.aroundme.data.Place;
import com.comli.shapira.aroundme.db.AroundMeDBHelper;
import com.comli.shapira.aroundme.helpers.BroadcastHelper;
import com.comli.shapira.aroundme.helpers.GooglePlacesNearbyHelper;
import com.comli.shapira.aroundme.helpers.NetworkHelper;

import java.net.URL;
import java.util.ArrayList;


public class NearbyService extends IntentService {

    public static final String ACTION_NEARBY_PLACES = "com.comli.shapira.aroundme.Services.action.ACTION_NEARBY_GET";
    public static final String EXTRA_NEARBY_REQUEST = "com.comli.shapira.aroundme.Services.extra.nearbyplaces.request";

    public static final String ACTION_PLACE_FAVORITES_SAVE = "com.comli.shapira.aroundme.Services.action.ACTION_PLACE_FAVORITES_SAVE";
    public static final String ACTION_PLACE_FAVORITES_REMOVE = "com.comli.shapira.aroundme.Services.action.ACTION_PLACE_FAVORITES_REMOVE";
    public static final String ACTION_PLACE_FAVORITES_REMOVE_ALL = "com.comli.shapira.aroundme.Services.action.ACTION_PLACE_FAVORITES_REMOVE_ALL";
    public static final String EXTRA_PLACE_FAVORITES_DATA = "com.comli.shapira.aroundme.Services.extra.placedetails.request";
    public static final String EXTRA_PLACE_FAVORITES_ACTION_SAVE = "com.comli.shapira.aroundme.Services.extra.placedetails.action.save";
    public static final String EXTRA_PLACE_FAVORITES_ACTION_REMOVE = "com.ecomli.shapira.aroundme.Services.extra.placedetails.action.remove";


    private GooglePlacesNearbyHelper mNearbyHelper;
    private AroundMeDBHelper mDbHelper = new AroundMeDBHelper(this);
    private ArrayList<Place> mPlaces = new ArrayList<>();

    public NearbyService() {
        super("NearbyService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        mNearbyHelper = new GooglePlacesNearbyHelper(this);
        mDbHelper = new AroundMeDBHelper(this);

        if (intent == null)
            return;

        String action = intent.getAction();

        switch (action)
        {
            case ACTION_NEARBY_PLACES:
                NearbyRequest nearbyRequest = intent.getParcelableExtra(EXTRA_NEARBY_REQUEST);
                mDbHelper.searchDeleteAllPlaces();
                BroadcastHelper.broadcastSearchPlacesClearPrevSearch(this);

                if ( downloadNearbyPlacesWithPhotos(nearbyRequest))
                    downloadPlacesPhotoAndSaveToDB();
                break;

            case ACTION_PLACE_FAVORITES_SAVE:
                DetailsRequest saveDataRequest = intent.getParcelableExtra(EXTRA_PLACE_FAVORITES_DATA);

                if (intent.getBooleanExtra(EXTRA_PLACE_FAVORITES_ACTION_SAVE, false))
                    downloadPlaceDetailsAndAddToFavorites(saveDataRequest);
                break;

            case ACTION_PLACE_FAVORITES_REMOVE:
                DetailsRequest removeDataRequest = intent.getParcelableExtra(EXTRA_PLACE_FAVORITES_DATA);

                if (intent.getBooleanExtra(EXTRA_PLACE_FAVORITES_ACTION_REMOVE, false))
                    deleteFromFavorites(removeDataRequest);
                break;

            case ACTION_PLACE_FAVORITES_REMOVE_ALL:
                    deleteAllFromFavorites();
                break;
        }
    }

    private void deleteAllFromFavorites()
    {
        mDbHelper.favoritesDeleteAllPlaces();
        BroadcastHelper.broadcastFavoritesPlacesDeletedAll(this);
    }

    private void deleteFromFavorites(DetailsRequest request)
    {
        Place place = request.getPlace();
        mDbHelper.favoritesDeletePlace(place.getId());
        BroadcastHelper.broadcastFavoritesPlaceDeleted(this);
    }

    private void downloadPlaceDetailsAndAddToFavorites(DetailsRequest request)
    {
        URL url = mNearbyHelper.getDetailsUrl(request.getPlace());
        NetworkHelper networkHelper = new NetworkHelper(url);
        String jsonString = networkHelper.getJsonString();
        DetailsResponse res = mNearbyHelper.GetPlaceDetails(request, jsonString);
        Place place = res.getPlace();

        mDbHelper.searchUpdatePlace(place);
        mDbHelper.favoritesInsertPlace(place);
        downloadPlacePhoto(place);
        mDbHelper.searchUpdatePlace(place);

        BroadcastHelper.broadcastFavoritesPlaceSaved(this);
    }


    private boolean downloadNearbyPlacesWithPhotos(NearbyRequest request)
    {
        String pageToken = null;
        NearbyResponse response;

        do {

            String status;

            URL url = mNearbyHelper.getNearbyLocUrl(request, pageToken);
            NetworkHelper networkHelper = new NetworkHelper(url);

            String jsonString = networkHelper.getJsonString();
            response = mNearbyHelper.GetPlaces(jsonString);

            status = response.getStatus();

            if (response.isError()) {
                BroadcastHelper.broadcastSearchError(this, status);
                return false;
            }

            handleNewPlaces(response.getPlaces());

            pageToken = response.getNextPageToken();

        } while (pageToken != null);

        return mPlaces.size() > 0;
    }


    private void downloadPlacesPhotoAndSaveToDB()
    {
        mPlaces = mDbHelper.searchGetArrayList();

        for (int i=0; i < mPlaces.size(); i++ ) {
            Place place = mPlaces.get(i);
            downloadPlacePhoto(place);
            mDbHelper.searchUpdatePlace(place);
        }
    }

    private void downloadPlacePhoto(Place place)
    {

        if (place.getPhotoRef() == null)
            return;

        URL url = mNearbyHelper.getPhotoUrl(place);

        if (url == null)
            return;

        NetworkHelper networkHelper = new NetworkHelper(url);
        Bitmap photo = networkHelper.getImage();

        if (photo == null)
            return;

        place.setPhoto(photo);
    }


    private void handleNewPlaces(ArrayList<Place> places)
    {
        if (places.size() > 0) {
            mPlaces.addAll(places);
            mDbHelper.searchBulkInsert(places);
        }

        BroadcastHelper.broadcastSearchPlacesSaved(this, places);
    }
}
