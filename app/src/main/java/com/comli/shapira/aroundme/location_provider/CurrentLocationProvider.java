
package com.comli.shapira.aroundme.location_provider;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import com.comli.shapira.aroundme.activities_fragments.MainActivity;
import com.comli.shapira.aroundme.helpers.SharedPrefHelper;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


public class CurrentLocationProvider implements LocationInterface {

    private static final int MIN_TIME_MILLISECONDS = 1000;
    private static final int MIN_DISTANCE = 0;
    private static final int NETWORK_TIMEOUT_MILLISECONDS = 2000;
    private static final int GPS_TIMEOUT_MILLISECONDS = 2000;

    private final LocationManager mLocationManager;
    private LocationInterface.onLocationListener mListener;
    private ProviderListener mProviderListener;
    private boolean mGotLocation;
    private final String mProviderName;
    private final Activity mActivity;
    private final SharedPrefHelper mSharedPrefHelper;


    public CurrentLocationProvider(Activity activity, String providerName)
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

        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED )
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
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if( ! mGotLocation)
                            mListener.onLocationNotAvailable();
                    }
                });
            }
        };

        Timer timer = new Timer("");
        long timeoutMS = System.currentTimeMillis() +
                (mProviderName == LocationManager.GPS_PROVIDER ?
                        GPS_TIMEOUT_MILLISECONDS :
                        NETWORK_TIMEOUT_MILLISECONDS);

        Date timeout = new Date(timeoutMS);
        timer.schedule(task, timeout);
    }


    @Override
    public void stop() {

        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION)
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
            mGotLocation = true;
            mListener.onLocationChanged(location);
        }

        @Override public void onStatusChanged(String s, int i, Bundle bundle) {}
        @Override public void onProviderEnabled(String s) {}
        @Override public void onProviderDisabled(String s) {}
    }
}
