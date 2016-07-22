package com.comli.shapira.aroundme.receivers;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;

import com.comli.shapira.aroundme.helpers.BroadcastHelper;
import com.comli.shapira.aroundme.helpers.LocationServiceHelper;


public class LocationProviderServiceReceiver extends BroadcastReceiver {

    private final LocationServiceHelper mLocationServiceHelper;


    public LocationProviderServiceReceiver(LocationServiceHelper locationServiceHelper)
    {
        mLocationServiceHelper = locationServiceHelper;
    }


    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        switch (action)
        {
            case BroadcastHelper.ACTION_LOCATION_CHANGED_NOTIFY:
                Location location = intent.getParcelableExtra(BroadcastHelper.EXTRA_LOCATION_CHANGED_DATA);
                mLocationServiceHelper.onLocationChanged(location);
                break;

            case BroadcastHelper.ACTION_LOCATION_NOT_AVAILABLE_NOTIFY:
                mLocationServiceHelper.onLocationNotAvailable();
                break;
        }
    }
}

