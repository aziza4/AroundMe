
package com.example.jbt.aroundme.location_provider;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import com.example.jbt.aroundme.activities_fragments.MainActivity;
import com.example.jbt.aroundme.helpers.SharedPrefHelper;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


public class ContLocationProvider implements LocationInterface {

    private static final int MIN_TIME_MILLISECONDS = 1000;
    private static final int MIN_DISTANCE = 0;
    private static final int LOCATION_TIMEOUT_MILLISECONDS = 15000;

    private final LocationManager mLocationManager;
    private LocationInterface.onLocationListener mListener;
    private ProviderListener mProviderListener;
    private boolean mGotLocation;
    private final String mProviderName;
    private final Activity mActivity;
    private SharedPrefHelper mSharedPrefHelper;

    public ContLocationProvider(Activity activity, String providerName)
    {
        mActivity = activity;
        mGotLocation = false;
        mProviderName = providerName;
        mLocationManager = (LocationManager) mActivity.getSystemService(Context.LOCATION_SERVICE);
        mSharedPrefHelper = new SharedPrefHelper(mActivity);

        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ) {

            mSharedPrefHelper.setPermissionDeniedByUser(true);
            ActivityCompat.requestPermissions(
                    mActivity,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                    MainActivity.LOCATION_REQUEST_CODE);
        }
        else {
            mSharedPrefHelper.setPermissionDeniedByUser(false);
        }
    }

    @Override
    public void start()
    {
        if (mSharedPrefHelper.isPermissionDeniedByUser() || mListener == null)
            return;

        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED )
            return;

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

        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED )
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
            mGotLocation = true;
            mListener.onLocationChanged(location);
        }

        @Override public void onStatusChanged(String s, int i, Bundle bundle) {}
        @Override public void onProviderEnabled(String s) {}
        @Override public void onProviderDisabled(String s) {}
    }
}
