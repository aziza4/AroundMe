package com.comli.shapira.aroundme.geoFencing;


import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.comli.shapira.aroundme.R;
import com.comli.shapira.aroundme.activities_fragments.MainActivity;
import com.comli.shapira.aroundme.data.Place;
import com.comli.shapira.aroundme.db.AroundMeDBHelper;
import com.comli.shapira.aroundme.helpers.SharedPrefHelper;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

public class GoogleGeofencingApiHelper implements ResultCallback<Status> {

    private final Context mContext;
    private final GoogleApiClientHelper mGoogleApiClientHelper;
    private final SharedPrefHelper mSharedPrefHelper;
    private ArrayList<Geofence> mGeofenceList;

    private static final long GEOFENCE_EXPIRATION_IN_HOURS = 12;
    private static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS =
            GEOFENCE_EXPIRATION_IN_HOURS * 60 * 60 * 1000;


    public GoogleGeofencingApiHelper(Context context, GoogleApiClientHelper googleApiClientHelper)
    {
        mContext = context;
        mSharedPrefHelper = new SharedPrefHelper(mContext);
        mGoogleApiClientHelper = googleApiClientHelper;
    }

    @Override
    public void onResult(@NonNull Status status) {

        if (!status.isSuccess()) {
            String errorMessage = GeofenceHelper.getErrorString(status.getStatusCode());
            Log.e(MainActivity.LOG_TAG, errorMessage);
        }
    }


    public void refresh()
    {
        AroundMeDBHelper dbHelper = new AroundMeDBHelper(mContext);
        ArrayList<Place> places = dbHelper.favoritesGetArrayList();

        if (places.isEmpty())
            return;

        if (mGoogleApiClientHelper.isDisconnected())
        {
            Log.e(MainActivity.LOG_TAG, "try to add geofences when not connected");
            return;
        }

        try {

            PendingIntent intent = getGeofencePendingIntent();

            // Remove existing geofences
            LocationServices.GeofencingApi.removeGeofences(
                    mGoogleApiClientHelper.getGoogleApiClient(),
                    // This is the same pending intent that was used in addGeofences().
                    intent
            ).setResultCallback(this); // Result processed in onResult().

            // Convert places to geofences
            mGeofenceList = populateGeofenceList(places);

            // Add new geofences
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClientHelper.getGoogleApiClient(),
                    // The GeofenceRequest object.
                    getGeofencingRequest(),
                    // A pending intent that that is reused when calling removeGeofences(). This
                    // pending intent is used to generate an intent when a matched geofence
                    // transition is observed.
                    intent
            ).setResultCallback(this); // Result processed in onResult().

        } catch (SecurityException ex) {
            Toast.makeText(mContext, "" + ex.getMessage(), Toast.LENGTH_SHORT).show();
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


    private ArrayList<Geofence> populateGeofenceList(ArrayList<Place> places)
    {
        // Empty list for storing geofences.
        ArrayList<Geofence> geofenceList = new ArrayList<>();
        int radius = mSharedPrefHelper.getGeofencesRadius();
        int transitionType = getTransitionType();


        for (Place place : places) {

            geofenceList.add(new Geofence.Builder()
                    // Set the request ID of the geofence. This is a string to identify this
                    // geofence.
                    .setRequestId(place.getName())

                    // Set the circular region of this geofence.
                    .setCircularRegion(
                            place.getLoc().latitude,
                            place.getLoc().longitude,
                            radius
                    )

                    // Set the expiration duration of the geofence. This geofence gets automatically
                    // removed after this period of time.
                    .setExpirationDuration(GEOFENCE_EXPIRATION_IN_MILLISECONDS)

                    // Set the transition types of interest. Alerts are only generated for these
                    // transition. We track entry and exit transitions in this sample.
                    .setTransitionTypes(transitionType)

                    // Create the geofence.
                    .build());
        }

        return geofenceList;
    }

    private int getTransitionType()
    {
        String enter = mContext.getString(R.string.pref_geofences_type_enter);
        String leave = mContext.getString(R.string.pref_geofences_type_leave);

        String type = mSharedPrefHelper.getTransitionType();

        if (type.equals(enter))
            return Geofence.GEOFENCE_TRANSITION_ENTER;

        if (type.equals(leave))
            return Geofence.GEOFENCE_TRANSITION_EXIT;

        return Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT;
    }
}

