package com.example.jbt.aroundme.UIHelpers;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.support.v7.app.AlertDialog;

import com.example.jbt.aroundme.Data.NearbyRequest;
import com.example.jbt.aroundme.Helpers.SharedPrefHelper;
import com.example.jbt.aroundme.Helpers.Utility;
import com.example.jbt.aroundme.LocationProvider.ContLocationProvider;
import com.example.jbt.aroundme.LocationProvider.LocationInterface;
import com.example.jbt.aroundme.R;
import com.example.jbt.aroundme.Services.NearbyService;
import com.google.android.gms.maps.model.LatLng;


public class UserCurrentLocation {

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
        mLocationProvider = new ContLocationProvider(mActivity, LocationManager.GPS_PROVIDER);
        mLocationProvider.setOnLocationChangeListener(mUserCurrentLocListener);
        mLocationProvider.start();
    }


    public boolean ready()
    {
        return mLastLocation != null;
    }


    public void getAndHandle(String keyword) {

        if ( !ready() ) {
            mPendingRequest = keyword;
            mLocationProvider.start();
            return;
        }

        Intent intent = new Intent(NearbyService.ACTION_NEARBY_PLACES, null, mActivity, NearbyService.class);
        intent.putExtra(NearbyService.EXTRA_NEARBY_REQUEST, getNearbyRequest(keyword));
        mActivity.startService(intent);

        mPendingRequest = null;
    }


    private NearbyRequest getNearbyRequest(String keyword)
    {
        SharedPrefHelper sharedPref = new SharedPrefHelper(mActivity);

        LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());

        int radius = sharedPref.getRadius();
        if (! sharedPref.isMeters())
            radius = Utility.feetToMeters(radius);

        String[] types = { "restaurant" };
        String language = sharedPref.isEnglish() ?
                mActivity.getString(R.string.nearby_language_val_en) :
                mActivity.getString(R.string.nearby_language_val_iw) ;

        String rank = mActivity.getString(R.string.nearby_rank_val);
        return new NearbyRequest(latLng, radius, types, keyword, language, rank);
    }

    public interface OnLocationReadyListener {
        void onLocationReady();
        void onPendingRequestHandled();
    }

    private void showNoSensorEnabledDialog() {

        final String noSensorTitle = mActivity.getString(R.string.no_sensor_enabled);
        final String enableSensorMsg = mActivity.getString(R.string.sensor_enabled_message);
        final String gpsButton = mActivity.getString(R.string.sensor_gps_ok_button);
        final String networkButton = mActivity.getString(R.string.sensor_network_ok_button);
        final String stayOfflineButton = mActivity.getString(R.string.sensor_stay_offline_button);

        new AlertDialog.Builder(mActivity)
                .setTitle(noSensorTitle)
                .setMessage(enableSensorMsg)

                .setPositiveButton(gpsButton,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                mLocationProvider.stop();
                                mLocationProvider = new ContLocationProvider(mActivity, LocationManager.GPS_PROVIDER);
                                mLocationProvider.setOnLocationChangeListener(mUserCurrentLocListener);
                                mLocationProvider.start();
                                dialog.dismiss();
                            }
                        })

                .setNeutralButton(stayOfflineButton,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        })

                .setNegativeButton(networkButton,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                mLocationProvider.stop();
                                mLocationProvider = new ContLocationProvider(mActivity, LocationManager.NETWORK_PROVIDER);
                                mLocationProvider.setOnLocationChangeListener(mUserCurrentLocListener);
                                mLocationProvider.start();
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
                mListener.onLocationReady();
            }

            if (mPendingRequest != null) {
                getAndHandle(mPendingRequest);
                mListener.onPendingRequestHandled();
            }
        }

        @Override
        public void onNoGPSAndNetworkArePermitted() {
            //Toast.makeText(mActivity, "GPS and Network are not permitted", Toast.LENGTH_LONG).show();
            showNoSensorEnabledDialog();
        }

        @Override
        public void onNoGPSAndNetworkSignalsAvailable() {
            //Toast.makeText(mActivity, "GPS and Network are off\nPlease turn on at least one", Toast.LENGTH_LONG).show();
            showNoSensorEnabledDialog();
        }
    }
}
