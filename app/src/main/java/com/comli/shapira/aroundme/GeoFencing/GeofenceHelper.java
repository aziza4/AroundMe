package com.comli.shapira.aroundme.geoFencing;


import android.content.Context;
import android.text.TextUtils;

import com.comli.shapira.aroundme.R;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;

import java.util.ArrayList;
import java.util.List;


class GeofenceHelper {


    public static String getGeofenceTransitionDetails(Context context, int geofenceTransition,
                                                      List<Geofence> triggeringGeofences)
    {
        String geofenceTransitionString = getTransitionString(context, geofenceTransition);

        // Get the Ids of each geofence that was triggered.
        ArrayList<String> triggeringGeofencesIdsList = new ArrayList<>();
        for (Geofence geofence : triggeringGeofences)
            triggeringGeofencesIdsList.add(geofence.getRequestId());

        String triggeringGeofencesIdsString = TextUtils.join(", ", triggeringGeofencesIdsList);

        if (geofenceTransitionString == null || triggeringGeofencesIdsList.isEmpty())
            return null;

        return " " + geofenceTransitionString + triggeringGeofencesIdsString;
    }


    private static String getTransitionString(Context context, int transitionType)
    {
        switch (transitionType) {

            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return context.getString(R.string.geofence_transition_entered);

            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return context.getString(R.string.geofence_transition_exited);

            default:
                return null;
        }
    }


    public static String getErrorString(int errorCode)
    {
        switch (errorCode) {

            case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                return "Geofence service is not available now";

            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                return "This app has registered too many geofences";

            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                return "This app have provided too many PendingIntents to the addGeofences() call";

            default:
                return "Unknown error: the Geofence service is not available now";
        }
    }
}
