package com.example.jbt.aroundme.LocationProvider;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;


public class GoogleFusedLocation implements LocationInterface {

    private static final String LOG_TAG = "GoogleFusedLocation";


    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private onLocationListener mListener;
    private boolean mConnected = false;

    public GoogleFusedLocation(Context context, final boolean repeatedUpdates)
    {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {

                        mConnected = true;

                        if (repeatedUpdates) {

                            mLocationRequest = LocationRequest.create()
                                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                                    .setInterval(1000);

                            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, new LocationListener() {
                                @Override
                                public void onLocationChanged(Location location) {
                                    if (mListener != null)
                                        mListener.onLocationChanged(location);
                                }
                            });
                        }
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        mConnected = false;
                        Log.d(LOG_TAG, "GoogleApiClient connection has been suspended");
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        mConnected = false;
                        Log.d(LOG_TAG, "GoogleApiClient connection has benn failed");
                    }
                })
                .build();
    }

    @Override
    public void start()
    {
        mGoogleApiClient.connect();
    }

    @Override
    public void stop()
    {
        mGoogleApiClient.disconnect();
    }

    @Override
    public Location GetCurrentLocation()
    {
        return mConnected ?
                LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient)
                : null;
    }

    public void setOnLocationChangeListener(onLocationListener listener)
    {
        mListener = listener;
    }

}

