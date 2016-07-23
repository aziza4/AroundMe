
package com.comli.shapira.aroundme.location_provider;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;



public class CurrentLocationProvider implements LocationInterface {

    private static final int MIN_TIME_MILLISECONDS = 3000;
    private static final int MIN_DISTANCE = 0;

    private final LocationManager mLocationManager;
    private LocationInterface.onLocationListener mListener;
    private ProviderListener mProviderListener;
    private final Context mContext;
    private final String mProviderName;
    private final long mTimeout;
    private boolean mHasLocation;
    private final Handler mHandler = new Handler();


    public CurrentLocationProvider(Context context, String providerName, long timeout)
    {
        mContext = context;
        mProviderName = providerName;
        mTimeout = timeout;
        mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        mHasLocation = false;
    }

    @Override
    public void start()
    {
        if (mListener == null)
            return;

        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED )
            return;

        mProviderListener = new ProviderListener();

        mLocationManager.requestLocationUpdates(
                mProviderName,
                MIN_TIME_MILLISECONDS,
                MIN_DISTANCE,
                mProviderListener
        );


        TimerTask task = new TimerTask()
        {
            @Override
            public void run() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (! mHasLocation)
                            mListener.onLocationNotAvailable(mProviderName);
                    }
                });
            }
        };

        Timer timer = new Timer("");
        Date timeout = new Date(System.currentTimeMillis() + mTimeout);
        timer.schedule(task, timeout);
    }

    @Override
    public void stop() {

        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED )
            return;

        if (mLocationManager != null && mProviderListener != null)
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
            mHasLocation = true;
            mListener.onLocationChanged(mProviderName, location);
        }

        @Override public void onStatusChanged(String s, int i, Bundle bundle) {}
        @Override public void onProviderEnabled(String s) {}
        @Override public void onProviderDisabled(String s) {}
    }
}
