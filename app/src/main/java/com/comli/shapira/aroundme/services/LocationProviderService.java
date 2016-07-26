package com.comli.shapira.aroundme.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.comli.shapira.aroundme.geoFencing.GoogleApiClientHelper;
import com.comli.shapira.aroundme.geoFencing.GoogleGeofencingApiHelper;
import com.comli.shapira.aroundme.location_provider.LocationProviderManager;

public class LocationProviderService extends Service implements GoogleApiClientHelper.OnConnectionReadyListener {

    public static final String ACTION_LOCATION_PROVIDER_RESTART = "com.comli.shapira.aroundme.Services.action.ACTION_LOCATION_PROVIDER_RESTART";
    public static final String EXTRA_LOCATION_PROVIDER_NAME = "com.comli.shapira.aroundme.Services.extra.location.provider.name";


    private LocationProviderManager mLocManager;
    private GoogleApiClientHelper mGoogleApiClientHelper;
    private GoogleGeofencingApiHelper mGoogleGeofencingApiHelper;


    public LocationProviderService() {}


    @Override
    public void onCreate() {
        super.onCreate();

        mLocManager = new LocationProviderManager(this);
        mLocManager.start();

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
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if (intent == null)
            return START_STICKY;

        String name = intent.getStringExtra(EXTRA_LOCATION_PROVIDER_NAME);
        mLocManager.start(name);

        return Service.START_STICKY;
    }

    @Override
    public void onConnected() {
        mGoogleGeofencingApiHelper.add();
    }
}
