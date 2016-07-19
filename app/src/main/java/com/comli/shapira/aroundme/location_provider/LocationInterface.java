package com.comli.shapira.aroundme.location_provider;


import android.location.Location;

public interface LocationInterface { // my standard interface. open for new LocationProvider implementations

    void start();
    void stop();
    void setOnLocationChangeListener(onLocationListener listener);

    interface onLocationListener {
        void onLocationChanged(Location location);
        void onLocationNotAvailable();
    }
}
