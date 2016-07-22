package com.comli.shapira.aroundme.helpers;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.comli.shapira.aroundme.R;
import com.comli.shapira.aroundme.activities_fragments.MainActivity;
import com.comli.shapira.aroundme.data.LastLocationInfo;
import com.comli.shapira.aroundme.services.LocationProviderService;
import com.comli.shapira.aroundme.ui_helpers.UserCurrentLocation;


public class LocationServiceHelper {

    private final AppCompatActivity mActivity;
    private AlertDialog mAlertDialog;
    private final SharedPrefHelper mSharedPrefHelper;
    private final UserCurrentLocation mUserCurrentLocation;

    private boolean mPermissionDenied;
    private final boolean mAlertDialogWasOnPriorToDeviceRotation;



    public LocationServiceHelper(AppCompatActivity activity, UserCurrentLocation userCurrentLocation,
                                 LastLocationInfo lastLocationInfo, String keyword)
    {
        mActivity = activity;
        mSharedPrefHelper = new SharedPrefHelper(mActivity);
        mUserCurrentLocation = userCurrentLocation;

        boolean onCreateDueToDeviceRotation = lastLocationInfo != null;
        mAlertDialogWasOnPriorToDeviceRotation = onCreateDueToDeviceRotation && lastLocationInfo.getAlertDialogOn();
        mPermissionDenied = permissionDenied();
    }


    public void startService()
    {
        if ( mPermissionDenied )
            return;

        startListening("");

        if (mAlertDialogWasOnPriorToDeviceRotation)
            showLocationOffDialog();
    }


    public void stopService()
    {
        if ( mPermissionDenied )
            return;

        dismissDialogIfOpen();

        Intent intent = new Intent(mActivity, LocationProviderService.class);
        mActivity.stopService(intent);
    }

    public void onRuntimePermissionGrant()
    {
        mPermissionDenied = false;
        startListening("");
    }

    public boolean isAlertDialogOpen()
    {
        return mAlertDialog != null;
    }


    private void startListening(String provider)
    {
        dismissDialogIfOpen();

        Intent intent = new Intent(LocationProviderService.ACTION_LOCATION_PROVIDER_RESTART, null, mActivity, LocationProviderService.class);
        intent.putExtra(LocationProviderService.EXTRA_LOCATION_PROVIDER_NAME, provider);
        mActivity.startService(intent);
    }


    private boolean permissionDenied()
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


    public void onLocationChanged(Location location)
    {
        dismissDialogIfOpen();
        mUserCurrentLocation.onLocationChanged(location);
    }


    public void onLocationNotAvailable()
    {
        showLocationOffDialog(); // need the user intervention here...
    }


    private void showLocationOffDialog() {

        if (mSharedPrefHelper.isPermissionDeniedByUser() || mAlertDialog != null)
            return;

        dismissDialogIfOpen();

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
                                dismissDialogIfOpen();
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


    public void dismissDialogIfOpen()
    {
        if (mAlertDialog != null) {

            mAlertDialog.dismiss();
            mAlertDialog = null;
        }
    }
}
