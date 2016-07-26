package com.comli.shapira.aroundme.geoFencing;


import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.comli.shapira.aroundme.activities_fragments.MainActivity;
import com.comli.shapira.aroundme.data.Place;
import com.comli.shapira.aroundme.db.AroundMeDBHelper;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

public class GoogleGeofencingApiHelper implements ResultCallback<Status> {

    private final Context mContext;
    private final GoogleApiClientHelper mGoogleApiClientHelper;
    private ArrayList<Geofence> mGeofenceList;
    private final ArrayList<Place> mPlaces;

    private static final long GEOFENCE_EXPIRATION_IN_HOURS = 12;
    private static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS =
            GEOFENCE_EXPIRATION_IN_HOURS * 60 * 60 * 1000;
    private static final float GEOFENCE_RADIUS_IN_METERS = 50; // 1 mile, 1.6 km


    public GoogleGeofencingApiHelper(Context context, GoogleApiClientHelper googleApiClientHelper)
    {
        mContext = context;
        mGoogleApiClientHelper = googleApiClientHelper;


        AroundMeDBHelper dbHelper = new AroundMeDBHelper(mContext);
        mPlaces = dbHelper.favoritesGetArrayList();

    }

    @Override
    public void onResult(@NonNull Status status) {

        if (!status.isSuccess()) {
            String errorMessage = GeofenceHelper.getErrorString(status.getStatusCode());
            Log.e(MainActivity.LOG_TAG, errorMessage);
        }
    }


    public void add()
    {
        if (mPlaces.isEmpty())
            return;

        if (!mGoogleApiClientHelper.isConnected())
        {
            Log.e(MainActivity.LOG_TAG, "try to add geofences when not connected");
            return;
        }

        try {

            mGeofenceList = populateGeofenceList();

            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClientHelper.getGoogleApiClient(),
                    // The GeofenceRequest object.
                    getGeofencingRequest(),
                    // A pending intent that that is reused when calling removeGeofences(). This
                    // pending intent is used to generate an intent when a matched geofence
                    // transition is observed.
                    getGeofencePendingIntent()
            ).setResultCallback(this); // Result processed in onResult().

        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
        }
    }


    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        // INITIAL_TRIGGER_ENTER flag indicates that geofencing service should  trigger a
        // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the
        // device  is already inside that geofence.
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }


    private PendingIntent getGeofencePendingIntent() {
        Intent intent = new Intent(mContext, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling addgeoFences()
        return PendingIntent.getService(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private ArrayList<Geofence> populateGeofenceList() {


        // Empty list for storing geofences.
        ArrayList<Geofence> geofenceList = new ArrayList<>();


        for (Place place : mPlaces) {

            geofenceList.add(new Geofence.Builder()
                    // Set the request ID of the geofence. This is a string to identify this
                    // geofence.
                    .setRequestId(place.getName())

                    // Set the circular region of this geofence.
                    .setCircularRegion(
                            place.getLoc().latitude,
                            place.getLoc().longitude,
                            GEOFENCE_RADIUS_IN_METERS
                    )

                    // Set the expiration duration of the geofence. This geofence gets automatically
                    // removed after this period of time.
                    .setExpirationDuration(GEOFENCE_EXPIRATION_IN_MILLISECONDS)

                    // Set the transition types of interest. Alerts are only generated for these
                    // transition. We track entry and exit transitions in this sample.
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                            Geofence.GEOFENCE_TRANSITION_EXIT)

                    // Create the geofence.
                    .build());
        }

        return geofenceList;
    }
}
