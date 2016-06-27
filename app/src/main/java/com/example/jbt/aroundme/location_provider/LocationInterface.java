package com.example.jbt.aroundme.location_provider;


import android.location.Location;

public interface LocationInterface {

    void start();
    void stop();
    void setOnLocationChangeListener(onLocationListener listener);

    interface onLocationListener {
        void onLocationChanged(Location location);
        void onNoGPSAndNetworkArePermitted();
        void onNoGPSAndNetworkSignalsAvailable();
    }
}
