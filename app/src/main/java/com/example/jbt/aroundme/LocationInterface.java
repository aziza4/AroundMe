package com.example.jbt.aroundme;


import android.location.Location;

public interface LocationInterface {

    void start();
    void stop();
    void setOnLocationChangeListener(onLocationListener listener);
    Location GetCurrentLocation();

    interface onLocationListener {
        void onLocationChanged(Location location);
    }
}
