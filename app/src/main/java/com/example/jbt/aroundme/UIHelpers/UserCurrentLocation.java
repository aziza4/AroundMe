package com.example.jbt.aroundme.UIHelpers;


import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.widget.Toast;
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

        if (location == null)
            return;

        String info = "Lat: " + location.getLatitude() + "\nLng: " + location.getLongitude();
        Toast.makeText(mContext, info, Toast.LENGTH_SHORT).show();

        LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
        String[] types = { "bank", "atm", "restaurant"};
        GooglePlacesNearbyHelper nearbyHelper = new GooglePlacesNearbyHelper(mContext);
        URL url = nearbyHelper.getNearbyLocUrl(latlng, 500, types);
        MyAsyncTask task = new MyAsyncTask();
        task.execute(url);
    }


    private class MyAsyncTask extends AsyncTask<URL, Void, ArrayList<Place>> {

        @Override
        protected ArrayList<Place> doInBackground(URL... params) {

            NetworkHelper networkHelper = new NetworkHelper(params[0]);
            String jsonString = networkHelper.GetJsonString();

            GooglePlacesNearbyHelper nearbyHelper = new GooglePlacesNearbyHelper(mContext);
            return nearbyHelper.GetPlaces(jsonString);
        }

        @Override
        protected void onPostExecute(ArrayList<Place> places) {

            Toast.makeText(mContext, "" + places.size() + " places", Toast.LENGTH_SHORT).show();
        }
    }
}
