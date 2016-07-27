package com.comli.shapira.aroundme.geoFencing;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;



public class GoogleApiClientHelper implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {


    private final Context mContext;
    private final OnConnectionReadyListener mOnConnectionReadyListener;

    private GoogleApiClient mGoogleApiClient;


    public GoogleApiClientHelper(Context context, OnConnectionReadyListener onConnectionReadyListener)
    {
        mContext = context;
        mOnConnectionReadyListener = onConnectionReadyListener;

        buildGoogleApiClient();
    }

    private synchronized void buildGoogleApiClient() {

        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    public GoogleApiClient getGoogleApiClient()
    {
        return mGoogleApiClient;
    }

    public void connect() // called on Activity's OnStart()
    {
        if (!mGoogleApiClient.isConnecting() || !mGoogleApiClient.isConnected())
            mGoogleApiClient.connect();
    }

    public void disconnect() // called on Activity's onStop()
    {
        if (mGoogleApiClient.isConnecting() || mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
    }

    public boolean isDisconnected()
    {
        return !mGoogleApiClient.isConnected();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //Toast.makeText(mContext, "GoogleApiClient Connected !", Toast.LENGTH_SHORT).show();
        mOnConnectionReadyListener.onConnected();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(mContext, "Connection Suspended. Try reconnect...", Toast.LENGTH_SHORT).show();
        connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(mContext, "Connection Failed", Toast.LENGTH_SHORT).show();
    }

    public interface OnConnectionReadyListener {
        void onConnected();
    }
}
