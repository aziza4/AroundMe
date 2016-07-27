package com.comli.shapira.aroundme.geoFencing;


import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.comli.shapira.aroundme.activities_fragments.MainActivity;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import java.util.List;


public class GeofenceTransitionsIntentService extends IntentService {

    private static final String TAG = "gfservice";


    public GeofenceTransitionsIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        Log.e(MainActivity.LOG_TAG, "GeofenceTransitionsIntentService::onHandleIntent() called");

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if (geofencingEvent.hasError()) {
            String errorMessage = GeofenceHelper.getErrorString(geofencingEvent.getErrorCode());
            Log.e(MainActivity.LOG_TAG, errorMessage);
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

            // Get the geofences that were triggered. A single event can trigger multiple geofences.
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            // Send notification and log the transition details.
            GeofenceNotificationHelper nHelper = new GeofenceNotificationHelper(this);
            nHelper.sendNotification(geofenceTransition, triggeringGeofences);

        }
        else
        {
            Log.e(MainActivity.LOG_TAG, "Geofence transition error: invalid transition type " + geofenceTransition);
        }
    }
}