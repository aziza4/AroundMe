package com.comli.shapira.aroundme.services;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;

import com.comli.shapira.aroundme.helpers.BroadcastHelper;
import com.comli.shapira.aroundme.helpers.SharedPrefHelper;
import com.comli.shapira.aroundme.location_provider.CurrentLocationProvider;
import com.comli.shapira.aroundme.location_provider.LocationInterface;

public class LocationProviderService extends Service implements LocationInterface.onLocationListener {

    private LocationInterface mLocationProvider;
    private Location mLastLocation;
    private SharedPrefHelper mSharedPrefHelper;


    public static final String ACTION_LOCATION_PROVIDER_RESTART = "com.comli.shapira.aroundme.Services.action.ACTION_LOCATION_PROVIDER_RESTART";
    public static final String EXTRA_LOCATION_PROVIDER_NAME = "com.comli.shapira.aroundme.Services.extra.location.provider.name";

    public LocationProviderService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mSharedPrefHelper = new SharedPrefHelper(this);
        String savedProvider = mSharedPrefHelper.getLastUsedLocationProvider();
        String provider = savedProvider.isEmpty() ? LocationManager.NETWORK_PROVIDER : savedProvider;

        mLastLocation = null;
        mLocationProvider = new CurrentLocationProvider(this);
        mLocationProvider.setOnLocationChangeListener(this);
        startProvider(provider);
    }

     @Override
    public void onDestroy() {
        mLocationProvider.stop();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if (mLastLocation != null) {
            BroadcastHelper.broadcastOnLocationChanged(this, mLastLocation);
            return START_STICKY;
        }

        if (intent == null)
            return START_STICKY;

        String action = intent.getAction();

        if (action == null || !action.equals(ACTION_LOCATION_PROVIDER_RESTART))
            return START_STICKY;

        String provider = intent.getStringExtra(EXTRA_LOCATION_PROVIDER_NAME);

        if (provider.isEmpty())
            return Service.START_STICKY;

        mLocationProvider.stop();
        startProvider(provider);
        return Service.START_STICKY;
    }


    private void startProvider(String provider)
    {
        mSharedPrefHelper.setLastUsedLocationProvider(provider); // save it for service restart
        mLocationProvider.start(provider);
    }


    @Override
    public void onLocationChanged(Location location)
    {
        mLastLocation = location;
        BroadcastHelper.broadcastOnLocationChanged(this, location);
    }

    @Override
    public void onLocationNotAvailable()
    {
        BroadcastHelper.broadcastOnLocationNotAvailable(this);
    }
}
