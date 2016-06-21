package com.example.jbt.aroundme.Services;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.example.jbt.aroundme.ActivitiesAndFragments.MainActivity;
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

    public static final String ACTION_PLACE_DETAILS = "com.example.jbt.aroundme.Services.action.ACTION_PLACE_DETAILS";
    public static final String EXTRA_PLACE_DETAILS = "com.example.jbt.aroundme.Services.extra.placedetails.request";

    public static final String ACTION_NEARBY_NOTIFY = "com.example.jbt.aroundme.Services.action.ACTION_NEARBY_NOTIFY";
    public static final String EXTRA_NEARBY_PLACES_SAVED = "com.example.jbt.aroundme.Services.extra.nearbyplaces.places.saved";

    public static final String ACTION_DETAILS_NOTIFY = "com.example.jbt.aroundme.Services.action.ACTION_DETAILS_NOTIFY";
    public static final String EXTRA_DETAILS_PLACE_SAVED = "com.example.jbt.aroundme.Services.extra.placedetails.saved";


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
                mDbHelper.deleteAllPlaces();
                if ( downloadNearbyPlacesWithPhotos(nearbyRequset))
                    downloadPlacesPhotos();
                break;

            case ACTION_PLACE_DETAILS:
                DetailsRequest detailsRequest = intent.getParcelableExtra(EXTRA_PLACE_DETAILS);
                downloadPlaceDetails(detailsRequest);
                break;
        }
    }


    private void downloadPlaceDetails(DetailsRequest request)
    {
        URL url = mNearbyHelper.getDetailsUrl(request.getPlace());
        NetworkHelper networkHelper = new NetworkHelper(url);
        String jsonString = networkHelper.getJsonString();
        DetailsResponse res = mNearbyHelper.GetPlaceDetails(request, jsonString);
        mDbHelper.updatePlace(res.getPlace());
        Intent intent = new Intent(ACTION_DETAILS_NOTIFY);
        intent.putExtra(EXTRA_DETAILS_PLACE_SAVED, 1);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }


    private boolean downloadNearbyPlacesWithPhotos(NearbyRequest request)
    {
        String pageToken = null;
        NearbyResponse response;

        do {

            URL url = mNearbyHelper.getNearbyLocUrl(request, pageToken);
            NetworkHelper networkHelper = new NetworkHelper(url);

            String jsonString = networkHelper.getJsonString();
            response = mNearbyHelper.GetPlaces(jsonString);

            String status = response.getStatus();

            // debug
            if (status.equals(NearbyResponse.STATUS_INVALID_REQUEST)) {
                @SuppressWarnings("UnusedAssignment") String myUrl = url.toString(); // Todo: investigate how come
            }

            if (status.equals(NearbyResponse.STATUS_OK))
                handleNewPlaces(response.getPlaces());
            else
                Log.e(MainActivity.LOG_TAG, "response status = " + status);

            pageToken = response.getNextPageToken();

        } while (pageToken != null);

        return mPlaces.size() > 0;
    }


    private void downloadPlacesPhotos()
    {

        mPlaces = mDbHelper.getPlacesArrayList();

        for (int i=0; i < mPlaces.size(); i++ ) {

            final Place place = mPlaces.get(i);

            if (place.getPhotoRef() == null)
                continue;

            URL url = mNearbyHelper.getPhotoUrl(place);

            if (url == null)
                continue;

            NetworkHelper networkHelper = new NetworkHelper(url);
            Bitmap photo = networkHelper.getImage();

            if (photo == null)
                continue;

            place.setPhoto(photo);
            mDbHelper.updatePlace(place);
        }
    }


    private void handleNewPlaces(ArrayList<Place> places)
    {
        mPlaces.addAll(places);
        mDbHelper.bulkInsertSearchResults(places);

        Intent intent = new Intent(ACTION_NEARBY_NOTIFY);
        intent.putExtra(EXTRA_NEARBY_PLACES_SAVED, mPlaces.size());
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
