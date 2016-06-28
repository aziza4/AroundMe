package com.example.jbt.aroundme.helpers;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;

import com.example.jbt.aroundme.data.Place;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class MapManipulation {

    private static final double REASONABLE_DISTANCE = 5.0;
    private static final float DEFAULT_ZOOM_FOR_PLACE_CENTERED = 17.0f;

    private final Context mContext;
    private final boolean mIsPermissionDeniedByUser;
    private final GoogleMap mMap;


    public MapManipulation(Context context, GoogleMap map) // execute all map "task" (moving, drawing, etc..)
    {
        mContext = context;
        mMap = map;
        SharedPrefHelper sharedPrefHelper = new SharedPrefHelper(mContext);
        mIsPermissionDeniedByUser = sharedPrefHelper.isPermissionDeniedByUser();

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED )
             mMap.setMyLocationEnabled(true); // This is google's "current location" widget on map
    }


    public void Manipulate(Place place) {

        if (mMap == null)
            return;

        // clear map
        mMap.clear();

        SharedPrefHelper sharedPrefHelper = new SharedPrefHelper(mContext);

        // get place and user locations
        LatLng userLoc = sharedPrefHelper.getLastUserLocation();
        LatLng placeLoc = place.getLoc();

        float distInKM = Utility.distanceInKM(userLoc, placeLoc);

        // define place marker options
        MarkerOptions placeMarkerOptions = new MarkerOptions()
                .position(placeLoc)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
                .title(place.getName())
                .snippet(place.getAddress())
                //.alpha(0.7f)
                .title(place.getName());

        // add place marker for place
        mMap.addMarker(placeMarkerOptions);

        if ( distInKM > REASONABLE_DISTANCE || mIsPermissionDeniedByUser ) {

            // move camera to user location
            float zoom = DEFAULT_ZOOM_FOR_PLACE_CENTERED;
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(placeLoc, zoom);
            mMap.moveCamera(cameraUpdate);

        } else {

            // move camera to user location
            float zoom = (float)Utility.radiusToZoom(distInKM);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(userLoc, zoom);
            mMap.moveCamera(cameraUpdate);

            // add circle
            CircleOptions circle = new CircleOptions()
                    .center(userLoc)
                    .radius(distInKM * 1000)
                    .fillColor(Color.parseColor("#100000ff"))
                    .strokeColor(Color.TRANSPARENT)
                    .strokeWidth(1);

            mMap.addCircle(circle);
        }
    }
}
