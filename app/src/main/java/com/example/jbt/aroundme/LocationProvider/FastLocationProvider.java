package com.example.jbt.aroundme.LocationProvider;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.example.jbt.aroundme.ActivitiesAndFragments.MainActivity;


public class FastLocationProvider implements LocationInterface {

    private static final int MIN_TIME_MILLISECONDS = 0;
    private static final int MIN_DISTANCE = 0;
    private static final int TIMEOUT_MILLISECONDS = 2000;

    private boolean mIsGpsEnabled;
    private boolean mIsNetworkLocationEnabled;
    private LocationManager mLocationManager;
    private Handler mHandler;
    private GetLastLocation mLastLocationRunnable;
    private LocationListenerGps mLocationListenerGps;
    private LocationListenerNetwork mLocationListenerNetwork;
    private LocationInterface.onLocationListener mListener;

    public FastLocationProvider(Context context)
    {
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public void start()
    {
        if (mListener == null)
            return;

        // Exceptions will be thrown if provider is not permitted.
        try {
            mIsGpsEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
            // Handle exception
        }

        try {
            mIsNetworkLocationEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
            // Handle exception
        }

        mLocationListenerGps = new LocationListenerGps();
        mLocationListenerNetwork = new LocationListenerNetwork();

        // Queue GPS location request
        if (mIsGpsEnabled)
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    MIN_TIME_MILLISECONDS,
                    MIN_DISTANCE,
                    mLocationListenerGps
            );

        // Queue network location request
        if (mIsNetworkLocationEnabled)
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    MIN_TIME_MILLISECONDS,
                    MIN_DISTANCE,
                    mLocationListenerNetwork
            );

        // Create a task that retrieves the last known location for the user in case we can't get the current location.
        mLastLocationRunnable = new GetLastLocation();

        if (!mIsNetworkLocationEnabled && !mIsGpsEnabled) {
            // Both network and GPS are disabled. We need to ask the user to enable at least one of them
            mListener.onNoGPSAndNetworkArePermitted();
        } else {
            // Use the last known location if we cannot obtain the user's location in 2000 msec
            mHandler = new Handler();
            mHandler.postDelayed(mLastLocationRunnable, TIMEOUT_MILLISECONDS);
        }
    }

    @Override
    public void stop() {

    }

    public void cancelTimer() {
        mHandler.removeCallbacks(mLastLocationRunnable);
        mLocationManager.removeUpdates(mLocationListenerGps);
        mLocationManager.removeUpdates(mLocationListenerNetwork);
    }


    private class LocationListenerGps implements LocationListener
    {
        @Override
        public void onLocationChanged(Location location) {
            cancelTimer(); // Cancels the timer
            mListener.onLocationChanged(location);
            mLocationManager.removeUpdates(this);
            mLocationManager.removeUpdates(mLocationListenerNetwork);
        }

        @Override public void onStatusChanged(String s, int i, Bundle bundle) {}
        @Override public void onProviderEnabled(String s) {}
        @Override public void onProviderDisabled(String s) {}
    }

    private class LocationListenerNetwork implements LocationListener
    {
        @Override
        public void onLocationChanged(Location location) {
            cancelTimer(); // Cancels the timer
            mListener.onLocationChanged(location);
            mLocationManager.removeUpdates(this);
            mLocationManager.removeUpdates(mLocationListenerGps);
        }

        @Override public void onStatusChanged(String s, int i, Bundle bundle) {}
        @Override public void onProviderEnabled(String s) {}
        @Override public void onProviderDisabled(String s) {}
    }


    private class GetLastLocation implements Runnable {

        @Override
        public void run() {

            Log.i(MainActivity.LOG_TAG, "Using last location");
            mLocationManager.removeUpdates(mLocationListenerGps);
            mLocationManager.removeUpdates(mLocationListenerNetwork);

            // Try to get last known gps location
            Location networkLocation = null, gpsLocation = null;
            if (mIsGpsEnabled)
                gpsLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            // Try to get last known network location
            if (mIsNetworkLocationEnabled)
                networkLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            // if there are both values use the latest one
            if (gpsLocation != null && networkLocation != null) {
                if (gpsLocation.getTime() > networkLocation.getTime())
                    mListener.onLocationChanged(gpsLocation);
                else
                    mListener.onLocationChanged(networkLocation);
                return;
            }

            if (gpsLocation != null) {
                mListener.onLocationChanged(gpsLocation);
                return;
            }

            if (networkLocation != null) {
                mListener.onLocationChanged(networkLocation);
                return;
            }

            mListener.onNoGPSAndNetworkSignalsAvailable();
        }
    }

    public void setOnLocationChangeListener(onLocationListener listener)
    {
        mListener = listener;
    }
}
