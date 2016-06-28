package com.example.jbt.aroundme.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.jbt.aroundme.R;

public class PowerConnectionReceiver extends BroadcastReceiver
{

    @Override
    public void onReceive(Context context, Intent intent) {

        switch (intent.getAction()) {

            case Intent.ACTION_POWER_CONNECTED:
                Toast.makeText(context, context.getString(R.string.receiver_power_connected),
                        Toast.LENGTH_SHORT).show();
                break;

            case Intent.ACTION_POWER_DISCONNECTED:
                Toast.makeText(context, context.getString(R.string.receiver_power_disconnected),
                        Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
