package com.example.jbt.aroundme.UIHelpers;


import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.widget.Toast;
import com.example.jbt.aroundme.Data.NearbyRequest;
import com.example.jbt.aroundme.LocationProvider.LocationInterface;
import com.example.jbt.aroundme.R;
import com.example.jbt.aroundme.Services.NearbyService;
import com.google.android.gms.maps.model.LatLng;



public class UserCurrentLocation {

    private final Context mContext;
    private final LocationInterface mLocationProvider;
    private Location mLastLocation;

    public UserCurrentLocation(Context context, LocationInterface locationProvider)
    {
        mContext = context;
        mLocationProvider = locationProvider;

        mLocationProvider.setOnLocationChangeListener(new LocationInterface.onLocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mLastLocation = location;
            }
        });
    }

    public void start() {
        mLocationProvider.start();
    }
    public void stop() {
        mLocationProvider.stop();
    }

    public void getAndHandle() {

        if (mLastLocation == null) {
            Toast.makeText(mContext, "Failed to get Location", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(NearbyService.ACTION_NEARBY_PLACES, null, mContext, NearbyService.class);
        intent.putExtra(NearbyService.EXTRA_NEARBY_REQUEST, getNearbyRequest());
        mContext.startService(intent);
    }


    private NearbyRequest getNearbyRequest()
    {
        LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        int radius = 500;
        String[] types = { "bank", "atm", "restaurant"};
        String language = mContext.getString(R.string.nearby_language_val);
        String rank = mContext.getString(R.string.nearby_rank_val);
        return new NearbyRequest(latLng, radius, types, language, rank);
    }
}
