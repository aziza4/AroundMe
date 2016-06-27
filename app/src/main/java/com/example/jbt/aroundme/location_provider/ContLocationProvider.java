
package com.example.jbt.aroundme.location_provider;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


public class ContLocationProvider implements LocationInterface {

    private static final int MIN_TIME_MILLISECONDS = 1000;
    private static final int MIN_DISTANCE = 0;
    private static final int LOCATION_TIMEOUT_MILLISECONDS = 5000;

    private boolean mIsProviderEnabled;
    private final LocationManager mLocationManager;
    private LocationInterface.onLocationListener mListener;
    private ProviderListener mProviderListener;
    private boolean mGotLocation;
    private final String mProviderName;
    private final Activity mActivity;

    public ContLocationProvider(Activity activity, String providerName)
    {
        mActivity = activity;
        mGotLocation = false;
        mProviderName = providerName;
        mLocationManager = (LocationManager) mActivity.getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public void start()
    {
        if (mListener == null)
            return;

        try {
            mIsProviderEnabled = mLocationManager.isProviderEnabled(mProviderName);
        } catch (Exception ex) {
            // Exceptions will be thrown if provider is not permitted.
        }

        if (!mIsProviderEnabled) {
            mListener.onNoGPSAndNetworkArePermitted();
            return;
        }

        mProviderListener = new ProviderListener();

        mLocationManager.requestLocationUpdates(
                mProviderName,
                MIN_TIME_MILLISECONDS,
                MIN_DISTANCE,
                mProviderListener
        );


        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if( ! mGotLocation)
                {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mListener.onNoGPSAndNetworkSignalsAvailable();
                        }
                    });
                }
            }
        };

        Timer timer = new Timer("");
        Date timeout = new Date(System.currentTimeMillis() + LOCATION_TIMEOUT_MILLISECONDS);
        timer.schedule(task, timeout);
    }

    @Override
    public void stop() {
        mLocationManager.removeUpdates(mProviderListener);
    }

    @Override
    public void setOnLocationChangeListener(onLocationListener listener)
    {
        mListener = listener;
    }


    private class ProviderListener implements LocationListener
    {
        @Override
        public void onLocationChanged(Location location) {
            mGotLocation = true;
            mListener.onLocationChanged(location);
        }

        @Override public void onStatusChanged(String s, int i, Bundle bundle) {}
        @Override public void onProviderEnabled(String s) {}
        @Override public void onProviderDisabled(String s) {}
    }
}
