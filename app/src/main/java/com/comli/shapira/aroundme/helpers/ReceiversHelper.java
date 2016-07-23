package com.comli.shapira.aroundme.helpers;


import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;

import com.comli.shapira.aroundme.receivers.LocationProviderServiceReceiver;
import com.comli.shapira.aroundme.receivers.NearbyServiceReceiver;
import com.comli.shapira.aroundme.receivers.PowerConnectionReceiver;

public class ReceiversHelper {

    private final AppCompatActivity mActivity;
    private final LocalBroadcastManager mLocalBroadcastManager;
    private final NearbyServiceReceiver mNearbyServiceReceiver;
    private final PowerConnectionReceiver mPowerConnectionReceiver;
    private final LocationProviderServiceReceiver mLocationProviderServiceReceiver;

    public ReceiversHelper(AppCompatActivity activity,
                           NearbyServiceReceiver nearbyServiceReceiver,
                           LocationProviderServiceReceiver locationProviderServiceReceiver,
                           PowerConnectionReceiver powerConnectionReceiver)
    {
        mActivity = activity;
        mNearbyServiceReceiver = nearbyServiceReceiver;
        mLocationProviderServiceReceiver = locationProviderServiceReceiver;
        mPowerConnectionReceiver = powerConnectionReceiver;
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(mActivity);

    }

    public void registerLocalReceivers()
    {
        // register nearby service local receiver
        IntentFilter nearby = new IntentFilter(BroadcastHelper.ACTION_NEARBY_NOTIFY);
        IntentFilter details = new IntentFilter(BroadcastHelper.ACTION_FAVORITES_NOTIFY);
        mLocalBroadcastManager.registerReceiver(mNearbyServiceReceiver, nearby);
        mLocalBroadcastManager.registerReceiver(mNearbyServiceReceiver, details);

        // register location-provider service local receiver
        IntentFilter locationChanged = new IntentFilter(BroadcastHelper.ACTION_LOCATION_CHANGED_NOTIFY);
        IntentFilter locationNotAvailable = new IntentFilter(BroadcastHelper.ACTION_LOCATION_NOT_AVAILABLE_NOTIFY);
        mLocalBroadcastManager.registerReceiver(mLocationProviderServiceReceiver, locationChanged);
        mLocalBroadcastManager.registerReceiver(mLocationProviderServiceReceiver, locationNotAvailable);
    }


    public void unRegisterLocalReceivers()
    {
        // unregister local receivers
        mLocalBroadcastManager.unregisterReceiver(mNearbyServiceReceiver);
        mLocalBroadcastManager.unregisterReceiver(mLocationProviderServiceReceiver);
    }

    public void registerGlobalReceivers()
    {
        // register global power receiver
        mActivity.registerReceiver(mPowerConnectionReceiver, new IntentFilter(Intent.ACTION_POWER_CONNECTED));
        mActivity.registerReceiver(mPowerConnectionReceiver, new IntentFilter(Intent.ACTION_POWER_DISCONNECTED));
    }

    public void unRegisterGlobalReceivers()
    {
        // unregister global receiver
        mActivity.unregisterReceiver(mPowerConnectionReceiver); // stop alerting the user on app exit
    }

}
