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
import java.util.Locale;


public class UserCurrentLocation {

    private final Context mContext;
    private final LocationInterface mLocationProvider;
    private Location mLastLocation;

    public UserCurrentLocation(Context context, LocationInterface locationProvider)
    {
        mContext = context;
        mLocationProvider = locationProvider;

        mLocationProvider.setOnLocationChangeListener(new LocationInterface.onLocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mLastLocation = location;
            }
        });
    }

    public void start() {
        mLocationProvider.start();
    }
    public void stop() {
        mLocationProvider.stop();
    }

    public void getAndHandle() {

        if (mLastLocation == null) {
            Toast.makeText(mContext, "Failed to get Location", Toast.LENGTH_SHORT).show();
            return;
        }

        MyAsyncTask task = new MyAsyncTask();
        task.execute(mLastLocation);
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

                String status = nearbyResponse.getStatus();

                if (status.equals(NearbyResponse.STATUS_OK))
                    places.addAll(nearbyResponse.getPlaces());

                pageToken = nearbyResponse.getNextPageToken();

            } while (pageToken != null);

            return places;
        }

        @Override
        protected void onPostExecute(ArrayList<Place> places) {

            String msg = "No Places found";

            if (places.size() > 0)
                msg = String.format(
                        Locale.ENGLISH, "Loc: %.4f, %.4f\n\n%d places",
                        mLastLocation.getLatitude(), mLastLocation.getLongitude(), places.size());

            Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
        }
    }
}
