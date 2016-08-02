package com.comli.shapira.aroundme.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.comli.shapira.aroundme.services.GeofenceService;


public class OnBootCompletedBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
            Intent serviceIntent = new Intent(context, GeofenceService.class);
            context.startService(serviceIntent);
        }
    }
}
