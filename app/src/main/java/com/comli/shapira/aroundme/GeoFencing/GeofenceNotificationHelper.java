package com.comli.shapira.aroundme.geoFencing;



import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.comli.shapira.aroundme.R;
import com.comli.shapira.aroundme.activities_fragments.MainActivity;
import com.comli.shapira.aroundme.activities_fragments.SettingsActivity;
import com.comli.shapira.aroundme.helpers.SharedPrefHelper;
import com.google.android.gms.location.Geofence;
import java.util.List;


class GeofenceNotificationHelper {

    private final Context mContext;
    private final SharedPrefHelper mSharedPrefHelper;

    private static final int MAIN_ID = 0;
    private static final int SETTINGS_ID = 1;

    public GeofenceNotificationHelper(Context context)
    {
        mContext = context;
        mSharedPrefHelper = new SharedPrefHelper(mContext);
    }

    public void sendNotification(int geofenceTransition, List<Geofence> triggeringGeofences)
    {
        if (!mSharedPrefHelper.isNotificationOn())
            return;

        // Get the transition details as a String.
        String geofenceTransitionDetails = GeofenceHelper.getGeofenceTransitionDetails(
                mContext, geofenceTransition, triggeringGeofences);

        if (geofenceTransitionDetails == null)
            return;

        PendingIntent notificationPendingIntent = GetPendingIntent(MainActivity.class, MAIN_ID);
        PendingIntent settingsActionPendingIntent = GetPendingIntent(SettingsActivity.class, SETTINGS_ID);

        // Get a notification builder that's compatible with platform versions >= 4
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);

        // Define the notification settings.
        builder.setContentTitle(geofenceTransitionDetails)
                .setContentText(mContext.getString(R.string.geofence_transition_notification_text))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher)) // consider Volley library to decode the bitmap
                .setContentIntent(notificationPendingIntent)
                .setColor(Color.GREEN)

                .setVibrate(new long[] {100, 2000, 500, 2000})
                .setLights(Color.GREEN, 400, 400)
                .addAction(R.drawable.ic_open, mContext.getString(R.string.geofence_action_open), settingsActionPendingIntent)
                .setAutoCancel(true); // Dismiss notification once the user touches it.

        if (mSharedPrefHelper.isSoundOn())
            builder.setDefaults(Notification.DEFAULT_SOUND);

        // Get an instance of the Notification manager and issue the notification
        NotificationManager mNotificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, builder.build());
    }


    private PendingIntent GetPendingIntent(Class target, int id)
    {
        // Create an explicit content Intent that starts the main Activity.
        Intent notificationIntent = new Intent(mContext.getApplicationContext(), target);

        // Construct a task stack.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);

        // Add the main Activity to the task stack as the parent.
        stackBuilder.addParentStack(target);

        // Push the content Intent onto the stack.
        stackBuilder.addNextIntent(notificationIntent);

        // Get a PendingIntent containing the entire back stack.
        return stackBuilder.getPendingIntent(id, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
