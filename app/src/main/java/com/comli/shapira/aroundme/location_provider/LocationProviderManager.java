package com.comli.shapira.aroundme.location_provider;


import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

import com.comli.shapira.aroundme.helpers.BroadcastHelper;


import java.util.HashMap;
import java.util.Map;

public class LocationProviderManager {

    private final static String GPS_NAME = LocationManager.GPS_PROVIDER;
    private final static String NETWORK_NAME = LocationManager.NETWORK_PROVIDER;

    private final static int RES_NONE = 0;
    private final static int RES_OK = 1;
    private final static int RES_NA = 2;

    private static final long GPS_TIMEOUT = 10000;
    private static final long NETWORK_TIMEOUT = 5000;
    private static final int RUNTIME_TIMEOUT = 3000;

    private final Context mContext;
    private HashMap<String, Provider> mProviders;
    private String mFinalProvider = null;


    public LocationProviderManager(Context context)
    {
        mContext = context;
        init();
    }

    private void init()
    {
        mProviders = new HashMap<>();
        mProviders.put(GPS_NAME, new Provider(GPS_NAME, GPS_TIMEOUT));
        mProviders.put(NETWORK_NAME, new Provider(NETWORK_NAME, NETWORK_TIMEOUT));
    }

    private void stop()
    {
        Provider gpsProvider = mProviders.get(GPS_NAME);
        Provider netProvider = mProviders.get(NETWORK_NAME);

        if (gpsProvider.loc != null) gpsProvider.loc.stop();
        if (netProvider.loc != null) netProvider.loc.stop();
    }

    public void start()
    {
        for(Map.Entry<String, Provider> entry : mProviders.entrySet()) {

            final Provider provider = entry.getValue();
            provider.loc = new CurrentLocationProvider(mContext, provider.name, provider.timeout );

            provider.loc.setOnLocationChangeListener(new LocationInterface.onLocationListener() {
                @Override
                public void onLocationChanged(String providerName, Location location) {
                    analyze(providerName, RES_OK, location);
                }

                @Override
                public void onLocationNotAvailable(String providerName) {
                    analyze(providerName, RES_NA, null);
                }

            });

            provider.loc.start();
        }
    }


    private void analyze(String name, int result, Location location)
    {
        Provider provider = mProviders.get(name);

        if (location != null)
            provider.location = location;

        provider.result = result;

        switch ( provider.result ) {

            case RES_OK:

                if (provider.name.equals(GPS_NAME)) { // gps connected
                    mProviders.get(NETWORK_NAME).loc.stop();
                    mFinalProvider = GPS_NAME;
                }

                BroadcastHelper.broadcastOnLocationChanged(mContext, provider.location, mFinalProvider);
                break;

            case RES_NA:

                Provider other = name.equals(GPS_NAME) ? mProviders.get(NETWORK_NAME) : mProviders.get(GPS_NAME);

                if (provider.result == RES_NA && other.result == RES_NA) {  // both n/a
                    BroadcastHelper.broadcastOnLocationNotAvailable(mContext);
                    return;
                }

                if (provider.name.equals(GPS_NAME) && other.result == RES_OK) { // network connected
                    provider.loc.stop();
                    mFinalProvider = NETWORK_NAME;
                    BroadcastHelper.broadcastOnLocationChanged(mContext, other.location, mFinalProvider);
                    return;
                }

                break;
        }
    }


    public void start(String providerName)
    {
        if (!providerName.equals(GPS_NAME) && !providerName.equals(NETWORK_NAME))
            return;

        stop();
        init();

        final Provider provider = mProviders.get(providerName);
        provider.timeout = RUNTIME_TIMEOUT;
        provider.loc = new CurrentLocationProvider(mContext, provider.name, provider.timeout );

        provider.loc.setOnLocationChangeListener(new LocationInterface.onLocationListener() {
            @Override
            public void onLocationChanged(String providerName, Location location) {
                provider.result = RES_OK;
                provider.location = location;
                BroadcastHelper.broadcastOnLocationChanged(mContext, provider.location, provider.name);
            }

            @Override
            public void onLocationNotAvailable(String providerName) {
                provider.result = RES_NA;
                provider.loc.stop();
                BroadcastHelper.broadcastOnLocationNotAvailable(mContext);
            }
        });

        provider.loc.start();
    }


    private class Provider {

        public LocationInterface loc;
        public final String name;
        public long timeout;
        public int result;
        public Location location;

        public Provider(String name, long timeout) {
            this.loc = null;
            this.name = name;
            this.timeout = timeout;
            this.result = RES_NONE;
            this.location = null;
        }
    }
}
