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
    private ArrayList<Geofence> mRegisteredGeofences;

    private static final long GEOFENCE_EXPIRATION_IN_HOURS = 12;
    private static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS =
            GEOFENCE_EXPIRATION_IN_HOURS * 60 * 60 * 1000;


    public GoogleGeofencingApiHelper(Context context, GoogleApiClientHelper googleApiClientHelper)
    {
        mContext = context;
        mSharedPrefHelper = new SharedPrefHelper(mContext);
        mGoogleApiClientHelper = googleApiClientHelper;
        mRegisteredGeofences = new ArrayList<>();
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


        // convert places to geofences
        ArrayList<Geofence> geofencesCandidates = populateGeofencesList(places);

        // un-register geofences that were removed from favorites
        unregisterDeletedGeofences(geofencesCandidates);

        // register either new or updated geofences
        registerNewOrUpdatedGeofences(geofencesCandidates);

        // save it for next time comparison
        mRegisteredGeofences = geofencesCandidates;
    }


    private void registerNewOrUpdatedGeofences(ArrayList<Geofence> geofencesCandidates)
    {
        ArrayList<Geofence> itemsToAddOrUpdate = getItemsToAddOrUpdate(geofencesCandidates);

        if (itemsToAddOrUpdate.isEmpty())
            return;

        try {

            PendingIntent intent = getGeofencePendingIntent();
            GeofencingRequest request = getGeofencingRequest(itemsToAddOrUpdate);

            // Add new geofences
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClientHelper.getGoogleApiClient(),
                    // The GeofenceRequest object.
                    request,
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


    private ArrayList<Geofence> getItemsToAddOrUpdate(ArrayList<Geofence> geofencesCandidates)
    {
        if (mRegisteredGeofences.isEmpty() || geofencesCandidates.isEmpty())
            return geofencesCandidates;

        ArrayList<Geofence> itemsToAddOrUpdate = new ArrayList<>();

        for (Geofence candidate : geofencesCandidates)
            if (!mRegisteredGeofences.contains(candidate))
                itemsToAddOrUpdate.add(candidate);

        return itemsToAddOrUpdate;
    }


    private void unregisterDeletedGeofences(ArrayList<Geofence> geofencesCandidates)
    {
        ArrayList<String> removeList = getItemsToRemove(geofencesCandidates);

        if (removeList.isEmpty())
            return;

        LocationServices.GeofencingApi.removeGeofences(
                mGoogleApiClientHelper.getGoogleApiClient(), removeList);
    }


    private ArrayList<String> getItemsToRemove(ArrayList<Geofence> geofencesCandidates)
    {
        ArrayList<String> removeList = new ArrayList<>();
        ArrayList<String> candidatesKeys = getGeofencesKeys(geofencesCandidates);
        ArrayList<String> registeredKeys = getGeofencesKeys(mRegisteredGeofences);

        if (registeredKeys.isEmpty() || candidatesKeys.isEmpty())
            return removeList;

        for (String key : registeredKeys)
            if (!candidatesKeys.contains(key))
                removeList.add(key);

        return removeList;
    }


    private ArrayList<String> getGeofencesKeys(ArrayList<Geofence> geofences)
    {
        ArrayList<String> keys = new ArrayList<>();

        for (Geofence geofence: geofences)
            keys.add(geofence.getRequestId());

        return keys;
    }



    private GeofencingRequest getGeofencingRequest(ArrayList<Geofence> geofencesCandidates) {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        // INITIAL_TRIGGER_ENTER flag indicates that geofencing service should  trigger a
        // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the
        // device  is already inside that geofence.
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofencesCandidates);
        return builder.build();
    }


    private PendingIntent getGeofencePendingIntent() {
        Intent intent = new Intent(mContext, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling addgeoFences()
        return PendingIntent.getService(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    private ArrayList<Geofence> populateGeofencesList(ArrayList<Place> places)
    {
        ArrayList<Geofence> geofenceList = new ArrayList<>();
        int radius = mSharedPrefHelper.getGeofencesRadius();
        int transitionType = getTransitionType();


        for (Place place : places)
        {
            Geofence geofence = new Geofence.Builder()
                    .setRequestId(place.getName()) // This is a string to identify this geofence
                    .setCircularRegion( // Set the circular region of this geofence.
                            place.getLoc().latitude,
                            place.getLoc().longitude,
                            radius)
                    .setExpirationDuration(GEOFENCE_EXPIRATION_IN_MILLISECONDS) // expiration
                    .setTransitionTypes(transitionType) // for example enter/exit
                    .build();

            geofenceList.add(geofence);
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

