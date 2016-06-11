package com.example.jbt.aroundme.UIHelpers;


import android.content.Context;
import android.location.Location;
import android.widget.Toast;

import com.example.jbt.aroundme.LocationProvider.LocationInterface;

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
    }
}
