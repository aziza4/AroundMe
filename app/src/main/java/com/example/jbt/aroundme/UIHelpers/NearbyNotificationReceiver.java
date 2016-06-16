package com.example.jbt.aroundme.UIHelpers;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.jbt.aroundme.Services.NearbyService;


public class NearbyNotificationReceiver extends BroadcastReceiver {

    private Activity mActivity;

    public NearbyNotificationReceiver(Activity activity)
    {
        mActivity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int placesSaved = intent.getIntExtra(NearbyService.EXTRA_NEARBY_PLACES_SAVED, -1);

        if (placesSaved > 0)
            Toast.makeText(mActivity, placesSaved + " places received", Toast.LENGTH_SHORT).show();
    }
}