package com.example.jbt.aroundme.UIHelpers;


import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.jbt.aroundme.Data.NearbyResponse;
import com.example.jbt.aroundme.Helpers.GooglePlacesNearbyHelper;
import com.example.jbt.aroundme.LocationProvider.LocationInterface;
import com.example.jbt.aroundme.Helpers.NetworkHelper;
import com.example.jbt.aroundme.Data.Place;
import com.google.android.gms.maps.model.LatLng;
import java.net.URL;
import java.util.ArrayList;


public class UserCurrentLocation {

    private final Context mContext;
    private final LocationInterface mLocationProvider;

    public UserCurrentLocation(Context context, LocationInterface locationProvider)
    {
        mContext = context;
        mLocationProvider = locationProvider;
    }

    public void start() {
        mLocationProvider.start();
    }
    public void stop() {
        mLocationProvider.stop();
    }

    public void getAndHandle() {

        Location location = mLocationProvider.GetCurrentLocation();

        if (location == null) {
            Toast.makeText(mContext, "Failed to get Location", Toast.LENGTH_SHORT).show();
            return;
        }

        String info = "Lat: " + location.getLatitude() + "\nLng: " + location.getLongitude();
        Toast.makeText(mContext, info, Toast.LENGTH_SHORT).show();


        MyAsyncTask task = new MyAsyncTask();
        task.execute(location);
    }


    private class MyAsyncTask extends AsyncTask<Location, Void, ArrayList<Place>> {

        @Override
        protected ArrayList<Place> doInBackground(Location... params) {

            ArrayList<Place> places = new ArrayList<>();

            String pageToken = null;
            Location location = params[0];
            LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
            String[] types = { "bank", "atm", "restaurant"};
            GooglePlacesNearbyHelper nearbyHelper = new GooglePlacesNearbyHelper(mContext);


            NearbyResponse nearbyResponse;

            do {

                URL url = nearbyHelper.getNearbyLocUrl(pageToken, latlng, 1000, types);
                NetworkHelper networkHelper = new NetworkHelper(url);

                String jsonString = networkHelper.GetJsonString();
                nearbyResponse = nearbyHelper.GetPlaces(jsonString);

                if (nearbyResponse == null)
                    break;

                if (nearbyResponse.getPlaces() != null)
                    places.addAll(nearbyResponse.getPlaces());

                pageToken = nearbyResponse.getNextPageToken();

            } while (pageToken != null);

            return places;
        }

        @Override
        protected void onPostExecute(ArrayList<Place> places) {

            Toast.makeText(mContext, "" + places.size() + " places", Toast.LENGTH_SHORT).show();
        }
    }
}
