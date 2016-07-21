package com.comli.shapira.aroundme.ui_helpers;


import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;


import com.comli.shapira.aroundme.activities_fragments.MainActivity;
import com.comli.shapira.aroundme.data.LastLocationInfo;
import com.comli.shapira.aroundme.data.NearbyRequest;
import com.comli.shapira.aroundme.helpers.BroadcastHelper;
import com.comli.shapira.aroundme.helpers.SharedPrefHelper;
import com.comli.shapira.aroundme.helpers.Utility;
import com.comli.shapira.aroundme.R;
import com.comli.shapira.aroundme.services.LocationProviderService;
import com.comli.shapira.aroundme.services.NearbyService;
import com.google.android.gms.maps.model.LatLng;


public class UserCurrentLocation { // controls the availability of location via LocationProvider it manages.

    private final Activity mActivity;
    private Location mLastLocation;
    private final OnLocationReadyListener mListener;
    private final SharedPrefHelper mSharedPrefHelper;
    private boolean mLocationReadyCalled;
    private AlertDialog mAlertDialog;
    private String mKeyword;

    public static final String LAST_LOC_INFO_KEY = "last_loc_info";


    public UserCurrentLocation(Activity activity, LastLocationInfo lastLocationInfo, String keyword,
                               OnLocationReadyListener listener)
    {
        mActivity = activity;
        mKeyword = keyword;
        mListener = listener;
        mLocationReadyCalled = false;
        mSharedPrefHelper = new SharedPrefHelper(mActivity);
        mAlertDialog = null;
        mLastLocation = lastLocationInfo == null ? null : lastLocationInfo.getLocation();
        startListening("");

        if (lastLocationInfo != null && lastLocationInfo.getAlertDialogOn())
            showLocationOffDialog();
    }

    public LastLocationInfo getLastLocationInfo()
    {
        return new LastLocationInfo(mLastLocation, mAlertDialog != null);
    }


    public boolean ready()
    {
        return mLastLocation != null; // signals UI to enable/disable search functionality
    }


    public void getAndHandle() { // all search request go through here

        if ( !ready() )
            return;

        // if location is available then GO! - use our service to download from internet...
        Intent intent = new Intent(NearbyService.ACTION_NEARBY_PLACES, null, mActivity, NearbyService.class);
        intent.putExtra(NearbyService.EXTRA_NEARBY_REQUEST, getNearbyRequest());
        mActivity.startService(intent);

        BroadcastHelper.broadcastSearchStarted(mActivity);  // start progress bar
    }


    private NearbyRequest getNearbyRequest() // prepare request object with all query parameters
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
        return new NearbyRequest(latLng, radius, types, mKeyword, language, rank);
    }



    public interface OnLocationReadyListener {
        void onLocationReady();
    }


    public void startListening(String provider)
    {
        if ( permissionDenied() )
            return;

        dismissDialog();

        Intent intent = new Intent(LocationProviderService.ACTION_LOCATION_PROVIDER_RESTART, null, mActivity, LocationProviderService.class);
        intent.putExtra(LocationProviderService.EXTRA_LOCATION_PROVIDER_NAME, provider);
        mActivity.startService(intent);
    }


    public boolean permissionDenied()
    {
        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ) {

            mSharedPrefHelper.setPermissionDeniedByUser(true);
            ActivityCompat.requestPermissions(
                    mActivity,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                    MainActivity.LOCATION_REQUEST_CODE);
            return true;
        }

        mSharedPrefHelper.setPermissionDeniedByUser(false);
        return false;
    }


    public void onLocationChanged(Location location) {

        mLastLocation = location;
        mSharedPrefHelper.saveLastUserLocation(location);

        dismissDialog();

        if (!mLocationReadyCalled) {
            mLocationReadyCalled = true;
            mListener.onLocationReady(); // update activity to refresh menu icons
        }

        if ( !mKeyword.isEmpty()) {
            getAndHandle();
            mKeyword = "";
        }
    }


    public void onLocationNotAvailable()
    {
        showLocationOffDialog(); // need the user intervention here...
    }


    private void showLocationOffDialog() {

        if (mSharedPrefHelper.isPermissionDeniedByUser() || mAlertDialog != null)
            return;

        final String noSensorTitle = mActivity.getString(R.string.no_sensor_enabled);
        final String enableSensorMsg = mActivity.getString(R.string.sensor_enabled_message);
        final String gpsButton = mActivity.getString(R.string.sensor_gps_ok_button);
        final String networkButton = mActivity.getString(R.string.sensor_network_ok_button);
        final String stayOfflineButton = mActivity.getString(R.string.sensor_stay_offline_button);

        mAlertDialog = new AlertDialog.Builder(mActivity)
                .setCancelable(false)
                .setTitle(noSensorTitle)
                .setMessage(enableSensorMsg)

                .setPositiveButton(gpsButton, // gps
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                startListening(LocationManager.GPS_PROVIDER);
                            }
                        })

                .setNeutralButton(stayOfflineButton, // cancel
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dismissDialog();
                            }
                        })

                .setNegativeButton(networkButton,  // network
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                startListening(LocationManager.NETWORK_PROVIDER);
                            }
                        })
                .create();

        mAlertDialog.show();
    }

    public void dismissDialog()
    {
        if (mAlertDialog!= null) {
            mAlertDialog.dismiss();
            mAlertDialog = null;
        }
    }
}
