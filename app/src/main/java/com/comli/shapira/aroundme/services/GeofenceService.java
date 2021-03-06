package com.comli.shapira.aroundme.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.comli.shapira.aroundme.geoFencing.GoogleApiClientHelper;
import com.comli.shapira.aroundme.geoFencing.GoogleGeofencingApiHelper;

public class GeofenceService extends Service implements GoogleApiClientHelper.OnConnectionReadyListener {

    public static final String ACTION_GEOFENCE_REFRESH_WATCHING = "com.comli.shapira.aroundme.Services.action.ACTION_GEOFENCE_REFRESH_WATCHING";

    private GoogleApiClientHelper mGoogleApiClientHelper;
    private GoogleGeofencingApiHelper mGoogleGeofencingApiHelper;
    private boolean mIsConnected;


    public GeofenceService() {}


    @Override
    public void onCreate() {
        super.onCreate();

        mIsConnected = false;
        mGoogleApiClientHelper = new GoogleApiClientHelper(this, this);
        mGoogleApiClientHelper.connect();
        mGoogleGeofencingApiHelper = new GoogleGeofencingApiHelper(this, mGoogleApiClientHelper);
    }


    @Override
    public void onDestroy() {
        mGoogleApiClientHelper.disconnect();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if (intent == null || !mIsConnected)
            return START_STICKY;

        String action = intent.getAction();

        if (action != null && action.equals(ACTION_GEOFENCE_REFRESH_WATCHING))
            mGoogleGeofencingApiHelper.refresh();

        return Service.START_STICKY;
    }

    @Override
    public void onConnected() {
        if ( !mIsConnected ) {
            mIsConnected = true;
            mGoogleGeofencingApiHelper.refresh();
        }
    }
}

