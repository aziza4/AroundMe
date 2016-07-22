package com.comli.shapira.aroundme.services;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.widget.Toast;

import com.comli.shapira.aroundme.R;
import com.comli.shapira.aroundme.helpers.BroadcastHelper;
import com.comli.shapira.aroundme.helpers.SharedPrefHelper;
import com.comli.shapira.aroundme.location_provider.CurrentLocationProvider;
import com.comli.shapira.aroundme.location_provider.LocationInterface;

public class LocationProviderService extends Service implements LocationInterface.onLocationListener {

    private LocationInterface mLocationProvider;
    private Location mLastLocation;
    private SharedPrefHelper mSharedPrefHelper;
    private boolean mFirstTime;
    private String mProviderName;
    private boolean mConnectedToastAlreadyDisplayed;


    public static final String ACTION_LOCATION_PROVIDER_RESTART = "com.comli.shapira.aroundme.Services.action.ACTION_LOCATION_PROVIDER_RESTART";
    public static final String EXTRA_LOCATION_PROVIDER_NAME = "com.comli.shapira.aroundme.Services.extra.location.provider.name";

    public LocationProviderService() {}

    @Override
    public void onCreate() {
        super.onCreate();

        mSharedPrefHelper = new SharedPrefHelper(this);
        mProviderName = mSharedPrefHelper.getLastUsedLocationProvider();
        mFirstTime = false;
        mConnectedToastAlreadyDisplayed = true; // display only on clean installation

        if (mProviderName.isEmpty()) {
            mProviderName = LocationManager.GPS_PROVIDER; // start with GPS, then try Network...
            mFirstTime = true;
            mConnectedToastAlreadyDisplayed = false;
        }

        mFirstTime = true;
        mLastLocation = null;
        mLocationProvider = new CurrentLocationProvider(this);
        mLocationProvider.setOnLocationChangeListener(this);
        startProvider(mProviderName);
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

        if (!provider.equals(LocationManager.GPS_PROVIDER) &&
                !provider.equals(LocationManager.NETWORK_PROVIDER))
            return Service.START_STICKY;

        mConnectedToastAlreadyDisplayed = false;
        startProvider(provider);
        return Service.START_STICKY;
    }


    private void startProvider(String provider)
    {
        mProviderName = provider;
        mSharedPrefHelper.setLastUsedLocationProvider(provider); // save it for service restart
        mLocationProvider.stop();
        mLocationProvider.start(provider);
    }


    @Override
    public void onLocationChanged(Location location)
    {
        mLastLocation = location;
        displayConnectingToast();
        BroadcastHelper.broadcastOnLocationChanged(this, location);
    }

    @Override
    public void onLocationNotAvailable()
    {
        if (!mFirstTime) {
            BroadcastHelper.broadcastOnLocationNotAvailable(this);
            return;
        }

        mFirstTime = false;
        mProviderName = LocationManager.NETWORK_PROVIDER;
        startProvider(mProviderName);
    }

    private void displayConnectingToast()
    {
        if ( mConnectedToastAlreadyDisplayed )
            return;

        mConnectedToastAlreadyDisplayed = true;

        String message = getString(R.string.sensor_connected_message);
        String sensor = mProviderName.equals(LocationManager.GPS_PROVIDER) ?
                getString(R.string.sensor_gps_ok_button) :
                getString(R.string.sensor_network_ok_button);

        Toast.makeText(this, message + " " + sensor, Toast.LENGTH_SHORT).show();
    }
}
