package com.example.jbt.aroundme.Services;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.example.jbt.aroundme.ActivitiesAndFragments.MainActivity;
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

    public static final String ACTION_NEARBY_NOTIFY = "com.example.jbt.aroundme.Services.action.ACTION_NEARBY_NOTIFY";
    public static final String EXTRA_NEARBY_PLACES_SAVED = "com.example.jbt.aroundme.Services.extra.nearbyplaces.places.saved";

    private GooglePlacesNearbyHelper mNearbyHelper;
    private AroundMeDBHelper mDbHelper = new AroundMeDBHelper(this);
    private final ArrayList<Place> mPlaces = new ArrayList<>();


    public NearbyService() { super("NearbyService"); }


    @Override
    protected void onHandleIntent(Intent intent) {

        mNearbyHelper = new GooglePlacesNearbyHelper(this);
        mDbHelper = new AroundMeDBHelper(this);

        if (intent == null)
            return;

        if (intent.getAction() == ACTION_NEARBY_PLACES) {

            NearbyRequest request = intent.getParcelableExtra(EXTRA_NEARBY_REQUEST);

            mDbHelper.deleteAllPlaces();

            if ( downloadNearbyPlacesWithPhotos(request));
                downloadPlacesPhotos(mPlaces);
        }
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


    private void downloadPlacesPhotos(ArrayList<Place> places)
    {
        for (int i=0; i < places.size(); i++ ) {

            final Place place = places.get(i);

            URL url = mNearbyHelper.getPhotoUrl(place);

            if (place != null) {
                NetworkHelper networkHelper = new NetworkHelper(url);
                Bitmap photo = networkHelper.getImage();
                place.setPhoto(photo);
            }
        }
    }


    private void handleNewPlaces(ArrayList<Place> places)
    {
        mPlaces.addAll(places);

        //downloadPlacesPhotos(places);

        mDbHelper.bulkInsertSearchResults(places);

        Intent intent = new Intent(ACTION_NEARBY_NOTIFY);
        intent.putExtra(EXTRA_NEARBY_PLACES_SAVED, mPlaces.size());
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
