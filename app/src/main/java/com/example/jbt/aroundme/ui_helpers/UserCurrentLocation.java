package com.example.jbt.aroundme.ui_helpers;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.support.v7.app.AlertDialog;

import com.example.jbt.aroundme.data.NearbyRequest;
import com.example.jbt.aroundme.helpers.BroadcastHelper;
import com.example.jbt.aroundme.helpers.SharedPrefHelper;
import com.example.jbt.aroundme.helpers.Utility;
import com.example.jbt.aroundme.location_provider.CurrentLocationProvider;
import com.example.jbt.aroundme.location_provider.LocationInterface;
import com.example.jbt.aroundme.R;
import com.example.jbt.aroundme.services.NearbyService;
import com.google.android.gms.maps.model.LatLng;


public class UserCurrentLocation { // controls the availability of location via LocationProvider it manages.

    private final Activity mActivity;
    private LocationInterface mLocationProvider;
    private final OnLocationReadyListener mListener;
    private Location mLastLocation;
    private boolean mLocationReadyCalled;
    private String mPendingRequest;
    private final UserCurrentLocationListener mUserCurrentLocListener;
    private final SharedPrefHelper mSharedPrefHelper;


    public UserCurrentLocation(Activity activity, OnLocationReadyListener listener)
    {
        mActivity = activity;
        mListener = listener;
        mLocationReadyCalled = false;
        mPendingRequest = null;
        mSharedPrefHelper = new SharedPrefHelper(mActivity);

        mUserCurrentLocListener = new UserCurrentLocationListener();
        startListening(LocationManager.GPS_PROVIDER); // first thing to do...
    }


    public boolean ready()
    {
        return mLastLocation != null; // signals UI to enable/disable search functionality
    }


    public void getAndHandle(String keyword) { // all search request go through here

        if ( !ready() ) {
            mPendingRequest = keyword; // we can (later) process that once we have location available
            mLocationProvider.start(); // ask for location updates
            return;
        }

        // if location is available then GO! - use our service to download from internet...
        Intent intent = new Intent(NearbyService.ACTION_NEARBY_PLACES, null, mActivity, NearbyService.class);
        intent.putExtra(NearbyService.EXTRA_NEARBY_REQUEST, getNearbyRequest(keyword));
        mActivity.startService(intent);

        BroadcastHelper.broadcastSearchStarted(mActivity);  // start progress bar
        mPendingRequest = null;
    }


    private NearbyRequest getNearbyRequest(String keyword) // prepare request object with all query parameters
    {
        LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());

        int radius = mSharedPrefHelper.getRadius();
        if (! mSharedPrefHelper.isMeters())
            radius = Utility.feetToMeters(radius);

        String typesString = mSharedPrefHelper.getSelectedTypes();
        String[] types = typesString.split("\\|");
        if (types[0].equals(mActivity.getString(R.string.pref_types_all)))
            types[0] = "";

        String language = mSharedPrefHelper.isEnglish() ?
                mActivity.getString(R.string.nearby_language_val_en) :
                mActivity.getString(R.string.nearby_language_val_iw) ;

        String rank = mActivity.getString(R.string.nearby_rank_val);
        return new NearbyRequest(latLng, radius, types, keyword, language, rank);
    }



    public interface OnLocationReadyListener {
        void onLocationReady();
        void onPendingRequestHandled();
    }


    private void showLocationOffDialog() {

        if (mSharedPrefHelper.isPermissionDeniedByUser())
            return;

        final String noSensorTitle = mActivity.getString(R.string.no_sensor_enabled);
        final String enableSensorMsg = mActivity.getString(R.string.sensor_enabled_message);
        final String gpsButton = mActivity.getString(R.string.sensor_gps_ok_button);
        final String networkButton = mActivity.getString(R.string.sensor_network_ok_button);
        final String stayOfflineButton = mActivity.getString(R.string.sensor_stay_offline_button);

        new AlertDialog.Builder(mActivity)
                .setTitle(noSensorTitle)
                .setMessage(enableSensorMsg)

                .setPositiveButton(gpsButton, // gps
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                mLocationProvider.stop();
                                startListening(LocationManager.GPS_PROVIDER);
                                dialog.dismiss();
                            }
                        })

                .setNeutralButton(stayOfflineButton, // cancel
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        })

                .setNegativeButton(networkButton,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) { // network
                                mLocationProvider.stop();
                                startListening(LocationManager.NETWORK_PROVIDER);
                                dialog.dismiss();
                            }
                        })
                .create()
                .show();
    }


    private class UserCurrentLocationListener implements LocationInterface.onLocationListener
    {
        @Override
        public void onLocationChanged(Location location) {

            mLastLocation = location;
            mSharedPrefHelper.saveLastUserLocation(location);

            if (!mLocationReadyCalled) {
                mLocationReadyCalled = true;
                mListener.onLocationReady(); // update activity to refresh menu icons
            }

            if (mPendingRequest != null) {
                getAndHandle(mPendingRequest); // we have pending request, now its time to handle it
                mListener.onPendingRequestHandled(); // update activity to refresh menu icons
            }
        }

        @Override
        public void onLocationNotAvailable() {
            showLocationOffDialog(); // need the user intervention here...
        }
    }


    public void startListening(String providerName)
    {
        mLocationProvider = new CurrentLocationProvider(mActivity, providerName);
        mLocationProvider.setOnLocationChangeListener(mUserCurrentLocListener);
        mLocationProvider.start();
    }
}
