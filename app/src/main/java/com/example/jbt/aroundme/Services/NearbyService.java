package com.example.jbt.aroundme.Services;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.content.LocalBroadcastManager;

import com.example.jbt.aroundme.Data.DetailsRequest;
import com.example.jbt.aroundme.Data.DetailsResponse;
import com.example.jbt.aroundme.Data.NearbyRequest;
import com.example.jbt.aroundme.Data.NearbyResponse;
import com.example.jbt.aroundme.Data.Place;
import com.example.jbt.aroundme.Helpers.AroundMeDBHelper;
import com.example.jbt.aroundme.Helpers.GooglePlacesNearbyHelper;
import com.example.jbt.aroundme.Helpers.NetworkHelper;
import java.net.URL;
import java.util.ArrayList;


public class NearbyService extends IntentService {

    public static final String ACTION_NEARBY_PLACES = "com.example.jbt.aroundme.Services.action.ACTION_NEARBY_GET";
    public static final String EXTRA_NEARBY_REQUEST = "com.example.jbt.aroundme.Services.extra.nearbyplaces.request";

    public static final String ACTION_PLACE_FAVORITES_SAVE = "com.example.jbt.aroundme.Services.action.ACTION_PLACE_FAVORITES_SAVE";
    public static final String ACTION_PLACE_FAVORITES_REMOVE = "com.example.jbt.aroundme.Services.action.ACTION_PLACE_FAVORITES_REMOVE";
    public static final String ACTION_PLACE_FAVORITES_REMOVE_ALL = "com.example.jbt.aroundme.Services.action.ACTION_PLACE_FAVORITES_REMOVE_ALL";
    public static final String EXTRA_PLACE_FAVORITES_DATA = "com.example.jbt.aroundme.Services.extra.placedetails.request";
    public static final String EXTRA_PLACE_FAVORITES_ACTION_SAVE = "com.example.jbt.aroundme.Services.extra.placedetails.action.save";
    public static final String EXTRA_PLACE_FAVORITES_ACTION_REMOVE = "com.example.jbt.aroundme.Services.extra.placedetails.action.remove";

    public static final String ACTION_NEARBY_NOTIFY = "com.example.jbt.aroundme.Services.action.ACTION_NEARBY_NOTIFY";
    public static final String EXTRA_NEARBY_PLACES_SAVED = "com.example.jbt.aroundme.Services.extra.nearbyplaces.places.saved";

    public static final String ACTION_FAVORITES_NOTIFY = "com.example.jbt.aroundme.Services.action.ACTION_FAVORITES_NOTIFY";
    public static final String EXTRA_FAVORITES_PLACE_SAVED = "com.example.jbt.aroundme.Services.extra.placedetails.saved";
    public static final String EXTRA_FAVORITES_PLACE_REMOVED = "com.example.jbt.aroundme.Services.extra.placedetails.removed";
    public static final String EXTRA_FAVORITES_PLACE_REMOVED_ALL = "com.example.jbt.aroundme.Services.extra.placedetails.removedall";


    private GooglePlacesNearbyHelper mNearbyHelper;
    private AroundMeDBHelper mDbHelper = new AroundMeDBHelper(this);
    private ArrayList<Place> mPlaces = new ArrayList<>();


    public NearbyService() { super("NearbyService"); }


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
                NearbyRequest nearbyRequset = intent.getParcelableExtra(EXTRA_NEARBY_REQUEST);
                mDbHelper.searchDeleteAllPlaces();

                if ( downloadNearbyPlacesWithPhotos(nearbyRequset))
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
        notifyFavorites(EXTRA_FAVORITES_PLACE_REMOVED);
    }

    private void deleteFromFavorites(DetailsRequest request)
    {
        Place place = request.getPlace();
        mDbHelper.favoritesDeletePlace(place.getId());

        notifyFavorites(EXTRA_FAVORITES_PLACE_REMOVED);
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

        notifyFavorites(EXTRA_FAVORITES_PLACE_SAVED);
    }

    private void notifyFavorites(String extra)
    {
        Intent intent = new Intent(ACTION_FAVORITES_NOTIFY);
        intent.putExtra(EXTRA_FAVORITES_PLACE_SAVED, 1);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
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

            // debug
            if (status.equals(NearbyResponse.STATUS_INVALID_REQUEST)) {
                @SuppressWarnings("UnusedAssignment")
                String myUrl = url.toString(); // Todo: investigate how come
            }

            if (status.equals(NearbyResponse.STATUS_OK) || status.equals(NearbyResponse.STATUS_ZERO_RESULTS))
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

        Intent intent = new Intent(ACTION_NEARBY_NOTIFY);
        intent.putExtra(EXTRA_NEARBY_PLACES_SAVED, mPlaces.size());
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
