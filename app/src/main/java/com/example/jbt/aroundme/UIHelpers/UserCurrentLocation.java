package com.example.jbt.aroundme.UIHelpers;


import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.jbt.aroundme.Data.NearbyRequest;
import com.example.jbt.aroundme.Data.NearbyResponse;
import com.example.jbt.aroundme.Helpers.GooglePlacesNearbyHelper;
import com.example.jbt.aroundme.LocationProvider.LocationInterface;
import com.example.jbt.aroundme.Helpers.NetworkHelper;
import com.example.jbt.aroundme.Data.Place;
import com.example.jbt.aroundme.R;
import com.example.jbt.aroundme.Services.NearbyService;
import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
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

        Intent intent = new Intent(NearbyService.ACTION_NEARBY_PLACES, null, mContext, NearbyService.class);
        intent.putExtra(NearbyService.EXTRA_NEARBY_REQUEST, getNearbyRequest());
        mContext.startService(intent);

//        MyAsyncTask task = new MyAsyncTask();
//        task.execute(mLastLocation);
    }


    private NearbyRequest getNearbyRequest()
    {

        LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        int radius = 1000;
        String[] types = { "bank", "atm", "restaurant"};
        String language = mContext.getString(R.string.nearby_language_val);
        String rank = mContext.getString(R.string.nearby_rank_val);
        GooglePlacesNearbyHelper nearbyHelper = new GooglePlacesNearbyHelper(mContext);
        return new NearbyRequest(latLng, radius, types, language, rank);
    }


//    private class MyAsyncTask extends AsyncTask<Location, Void, ArrayList<Place>> {
//
//        @Override
//        protected ArrayList<Place> doInBackground(Location... params) {
//
//            String pageToken = null;
//            ArrayList<Place> places = new ArrayList<>();
//
//            String pageToken = null;
//            Location location = params[0];
//            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//            int radius = 1000;
//            String[] types = { "bank", "atm", "restaurant"};
//            String language = mContext.getString(R.string.nearby_language_val);
//            String rank = mContext.getString(R.string.nearby_rank_val);
//            GooglePlacesNearbyHelper nearbyHelper = new GooglePlacesNearbyHelper(mContext);
//            NearbyRequest request = new NearbyRequest(latLng, radius, types, language, rank);
//
//            NearbyResponse response;
//
//            do {
//
//                URL url = nearbyHelper.getNearbyLocUrl(request, pageToken);
//                NetworkHelper networkHelper = new NetworkHelper(url);
//
//                String jsonString = networkHelper.GetJsonString();
//                response = nearbyHelper.GetPlaces(jsonString);
//
//                String status = response.getStatus();
//
//                if (status.equals(NearbyResponse.STATUS_OK))
//                    places.addAll(response.getPlaces());
//                else // debug
//                    Toast.makeText(mContext, "Response Status = " + status, Toast.LENGTH_SHORT).show();
//
//                pageToken = response.getNextPageToken();
//
//            } while (pageToken != null);
//
//            return places;
//        }
//
//        @Override
//        protected void onPostExecute(ArrayList<Place> places) {
//
//            String msg = "No Places found";
//
//            if (places.size() > 0)
//                msg = String.format(
//                        Locale.ENGLISH, "Loc: %.4f, %.4f\n\n%d places",
//                        mLastLocation.getLatitude(), mLastLocation.getLongitude(), places.size());
//
//            Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
//        }
//    }
}
