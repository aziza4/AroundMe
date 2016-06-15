package com.example.jbt.aroundme.Services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.jbt.aroundme.ActivitiesAndFragments.MainActivity;
import com.example.jbt.aroundme.Data.NearbyRequest;
import com.example.jbt.aroundme.Data.NearbyResponse;
import com.example.jbt.aroundme.Data.Place;
import com.example.jbt.aroundme.Helpers.GooglePlacesNearbyHelper;
import com.example.jbt.aroundme.Helpers.NetworkHelper;

import java.net.URL;
import java.util.ArrayList;


public class NearbyService extends IntentService {

    public static final String ACTION_NEARBY_PLACES = "com.example.jbt.aroundme.Services.action.nearbyplaces";
    public static final String EXTRA_NEARBY_REQUEST = "com.example.jbt.aroundme.Services.extra.nearbyplaces.request";

    public NearbyService() {
        super("NearbyService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if (intent == null)
            return;

        switch (intent.getAction()) {

            case ACTION_NEARBY_PLACES:
                NearbyRequest request = intent.getParcelableExtra(EXTRA_NEARBY_REQUEST);
                handleNearbyPlaces(request);
                break;

            default: return;
        }
    }


    private void handleNearbyPlaces(NearbyRequest request)
    {
        ArrayList<Place> places = downloadNearbyPlaces(request);
        saveToDB(places);
    }


    private void saveToDB(ArrayList<Place> places) {
    }


    private ArrayList<Place> downloadNearbyPlaces(NearbyRequest request)
    {
        ArrayList<Place> places = new ArrayList<>();
        String pageToken = null;
        GooglePlacesNearbyHelper nearbyHelper = new GooglePlacesNearbyHelper(this);
        NearbyResponse response;

        do {

            URL url = nearbyHelper.getNearbyLocUrl(request, pageToken);
            NetworkHelper networkHelper = new NetworkHelper(url);

            String jsonString = networkHelper.GetJsonString();
            response = nearbyHelper.GetPlaces(jsonString);

            String status = response.getStatus();

            if (status.equals(NearbyResponse.STATUS_OK))
                places.addAll(response.getPlaces());
            else
                Log.e(MainActivity.LOG_TAG, "response status" + status);

            pageToken = response.getNextPageToken();

        } while (pageToken != null);

        Toast.makeText(NearbyService.this, places.size() + " places", Toast.LENGTH_SHORT).show();
        return places;
    }
}
