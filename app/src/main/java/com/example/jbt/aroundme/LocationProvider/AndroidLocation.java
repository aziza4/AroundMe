package com.example.jbt.aroundme.LocationProvider;


import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;


public class AndroidLocation implements LocationInterface {

    private static final String LOG_TAG = "AndroidLocation";

    private final LocationManager mLocationManager;
    private LocationListener mLocationListener;
    private onLocationListener mListener;

    public AndroidLocation(Context context)
    {
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public void start() {

        try {
            // set up the listener
            mLocationListener = new LocationListener() {
                @Override public void onLocationChanged(Location location) {
                    if (mListener != null)
                        mListener.onLocationChanged(location);
                }

                @Override public void onStatusChanged(String s, int i, Bundle bundle) {}
                @Override public void onProviderEnabled(String s) {}
                @Override public void onProviderDisabled(String s) {}
            };

            // request updates
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,   // provider
                    1000,                           // minTime (ms)
                    0,                              // minDistance (meters)
                    mLocationListener               // listener
            );

        } catch (SecurityException e) {
            Log.d(LOG_TAG, "Missing permission");
        }
    }

    @Override
    public void stop() {

        try {
            mLocationManager.removeUpdates(mLocationListener); }
        catch (SecurityException ex) {
            Log.d(LOG_TAG, "Failed to Stop Android location updates");
        }
    }

    @Override
    public Location GetCurrentLocation()
    {
        return mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }


    public void setOnLocationChangeListener(onLocationListener listener)
    {
        mListener = listener;
    }
}

