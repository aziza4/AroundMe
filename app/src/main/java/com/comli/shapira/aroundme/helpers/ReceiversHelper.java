package com.comli.shapira.aroundme.helpers;


import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;

import com.comli.shapira.aroundme.receivers.ServicesBroadcastReceiver;
import com.comli.shapira.aroundme.receivers.PowerConnectionReceiver;

public class ReceiversHelper {

    private final AppCompatActivity mActivity;
    private final ServicesBroadcastReceiver mReceiver;
    private final PowerConnectionReceiver mPowerConnectionReceiver;

    public ReceiversHelper(AppCompatActivity activity, ServicesBroadcastReceiver receiver, PowerConnectionReceiver powerConnectionReceiver)
    {
        mActivity = activity;
        mReceiver = receiver;
        mPowerConnectionReceiver = powerConnectionReceiver;
    }

    public void registerLocalAndGlobalReceivers()
    {
        // register local notification receiver
        IntentFilter nearby = new IntentFilter(BroadcastHelper.ACTION_NEARBY_NOTIFY);
        IntentFilter details = new IntentFilter(BroadcastHelper.ACTION_FAVORITES_NOTIFY);
        IntentFilter locationChanged = new IntentFilter(BroadcastHelper.ACTION_LOCATION_CHANGED_NOTIFY);
        IntentFilter locationNotAvailable = new IntentFilter(BroadcastHelper.ACTION_LOCATION_NOT_AVAILABLE_NOTIFY);

        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(mActivity);
        localBroadcastManager.registerReceiver(mReceiver, nearby);
        localBroadcastManager.registerReceiver(mReceiver, details);
        localBroadcastManager.registerReceiver(mReceiver, locationChanged);
        localBroadcastManager.registerReceiver(mReceiver, locationNotAvailable);

        // register global power receiver
        mActivity.registerReceiver(mPowerConnectionReceiver, new IntentFilter(Intent.ACTION_POWER_CONNECTED));
        mActivity.registerReceiver(mPowerConnectionReceiver, new IntentFilter(Intent.ACTION_POWER_DISCONNECTED));
    }

    public void unRegisterLocalAndGlobalReceivers()
    {
        LocalBroadcastManager.getInstance(mActivity).unregisterReceiver(mReceiver);
        mActivity.unregisterReceiver(mPowerConnectionReceiver); // stop alerting the user on app exit
    }
}
