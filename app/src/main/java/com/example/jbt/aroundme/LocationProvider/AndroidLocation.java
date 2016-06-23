package com.example.jbt.aroundme.LocationProvider;


import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.example.jbt.aroundme.ActivitiesAndFragments.MainActivity;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


public class AndroidLocation implements LocationInterface {

    private static final int MIN_TIME_MILLISECONDS = 1000;
    private static final int MIN_DISTANCE = 0;
    private static final int GPS_TIMEOUT_MILLISECONDS = 5000;

    private Activity mActivity;
    private final LocationManager mLocationManager;
    private LocationListener mLocationListener;
    private onLocationListener mListener;
    private Timer mTimer;
    private boolean mGotLocation= false;

    public AndroidLocation(Activity activity)
    {
        mActivity = activity;
        mLocationManager = (LocationManager) mActivity.getSystemService(Context.LOCATION_SERVICE);
        mTimer = new Timer("gps provider");
        mGotLocation = false;
    }

    @Override
    public void start() {

        try {

            mLocationListener = new LocationListener() {
                @Override public void onLocationChanged(Location location) {

                    mGotLocation = true;
                    mTimer.cancel();

                    if (mListener != null)
                        mListener.onLocationChanged(location);
                }

                @Override public void onStatusChanged(String s, int i, Bundle bundle) {}
                @Override public void onProviderEnabled(String s) {}
                @Override public void onProviderDisabled(String s) {}
            };

            requestLocationFromProvider(LocationManager.GPS_PROVIDER);
//            requestLocationFromNetworkOnTimeout();

        } catch (SecurityException e) {
            Log.e(MainActivity.LOG_TAG, "Missing permission");
        }
    }

    @Override
    public void stop() {

        try {
            mLocationManager.removeUpdates(mLocationListener); }
        catch (SecurityException ex) {
            Log.e(MainActivity.LOG_TAG, "Failed to Stop Android location updates");
        }
    }

    @Override
    public Location GetCurrentLocation()
    {
        return mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }

    private void requestLocationFromProvider(String providerName)
    {
        try {
            mLocationManager.requestLocationUpdates(
                    providerName,
                    MIN_TIME_MILLISECONDS,
                    MIN_DISTANCE,
                    mLocationListener
            );
        }
        catch (SecurityException e) {
            Log.e(MainActivity.LOG_TAG, "Failed to Start " + providerName + " provider");
        }
    }

    private void requestLocationFromNetworkOnTimeout()
    {
        TimerTask timerTask = new TimerTask() {

            @Override
            public void run() {

                if( mGotLocation )
                    return;

                try {

                    stop();

                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            requestLocationFromProvider(LocationManager.NETWORK_PROVIDER);
                        }
                    });

                } catch(SecurityException e) {
                    Log.e(MainActivity.LOG_TAG, e.getMessage());
                }
            }
        };

        Date scheduledTime = new Date(System.currentTimeMillis() + GPS_TIMEOUT_MILLISECONDS);
        mTimer.schedule(timerTask, scheduledTime);
    }


    public void setOnLocationChangeListener(onLocationListener listener)
    {
        mListener = listener;
    }
}

