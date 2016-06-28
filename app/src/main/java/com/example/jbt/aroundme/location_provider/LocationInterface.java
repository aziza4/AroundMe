package com.example.jbt.aroundme.location_provider;


import android.location.Location;

public interface LocationInterface { // my stadard interface. open for new LocationProvider implementations

    void start();
    void stop();
    void setOnLocationChangeListener(onLocationListener listener);

    interface onLocationListener {
        void onLocationChanged(Location location);
        void onLocationNotAvailable();
    }
}
