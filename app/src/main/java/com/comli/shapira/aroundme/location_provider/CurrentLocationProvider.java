
package com.comli.shapira.aroundme.location_provider;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;



public class CurrentLocationProvider implements LocationInterface {

    private static final int MIN_TIME_MILLISECONDS = 1000;
    private static final int MIN_DISTANCE = 10;
    private static final int NETWORK_TIMEOUT_MILLISECONDS = 3000;
    private static final int GPS_TIMEOUT_MILLISECONDS = 5000;

    private final LocationManager mLocationManager;
    private LocationInterface.onLocationListener mListener;
    private ProviderListener mProviderListener;
    private final Context mContext;
    private boolean mHasLocation;


    public CurrentLocationProvider(Context context)
    {
        mContext = context;
        mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        mHasLocation = false;
    }

    @Override
    public void start(String providerName)
    {
        if (mListener == null)
            return;

        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED )
            return;

        mProviderListener = new ProviderListener();

        mLocationManager.requestLocationUpdates(
                providerName,
                MIN_TIME_MILLISECONDS,
                MIN_DISTANCE,
                mProviderListener
        );

        TimerTask task = new TimerTask()
        {
            @Override
            public void run()
            {
                if (! mHasLocation)
                    handler.sendEmptyMessage(0);
            }
        };

        Timer timer = new Timer("");
        long timeoutMS = System.currentTimeMillis() +
                (providerName.equals(LocationManager.GPS_PROVIDER) ?
                        GPS_TIMEOUT_MILLISECONDS :
                        NETWORK_TIMEOUT_MILLISECONDS);

        Date timeout = new Date(timeoutMS);
        timer.schedule(task, timeout);
    }

    private final Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {

            mListener.onLocationNotAvailable();
        }
    };


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
            mListener.onLocationChanged(location);
        }

        @Override public void onStatusChanged(String s, int i, Bundle bundle) {}
        @Override public void onProviderEnabled(String s) {}
        @Override public void onProviderDisabled(String s) {}
    }
}
