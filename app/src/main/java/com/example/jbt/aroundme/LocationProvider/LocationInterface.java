package com.example.jbt.aroundme.LocationProvider;


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
