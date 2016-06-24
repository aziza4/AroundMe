package com.example.jbt.aroundme.UIHelpers;


import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.widget.Toast;
import com.example.jbt.aroundme.Data.NearbyRequest;
import com.example.jbt.aroundme.Helpers.SharedPrefHelper;
import com.example.jbt.aroundme.LocationProvider.LocationInterface;
import com.example.jbt.aroundme.R;
import com.example.jbt.aroundme.Services.NearbyService;
import com.google.android.gms.maps.model.LatLng;


public class UserCurrentLocation {

    private final Context mContext;
    private final LocationInterface mLocationProvider;
    private final OnLocationReadyListener mListener;
    private Location mLastLocation;
    private boolean mLocationReadyCalled;
    private String mPendingRequest;

    public UserCurrentLocation(Context context,
                               LocationInterface locationProvider,
                               OnLocationReadyListener listener)
    {
        mContext = context;
        mListener = listener;
        mLocationReadyCalled = false;
        mPendingRequest = null;

        mLocationProvider = locationProvider;

        mLocationProvider.setOnLocationChangeListener(new LocationInterface.onLocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                mLastLocation = location;

                if (!mLocationReadyCalled) {
                    mLocationReadyCalled = true;
                    mListener.onLocationReady();
                }

                if (mPendingRequest != null) {
                    getAndHandle(mPendingRequest);
                    mListener.onPendingRequestHandled();
                }
            }
        });
    }


    public void start() {
        mLocationProvider.start();
    }


    public void stop() {
        mLocationProvider.stop();
    }


    public boolean ready()
    {
        return mLastLocation != null;
    }


    public void getAndHandle(String keyword) {

        if ( !ready() ) {
            mPendingRequest = keyword;
            return;
        }

        Intent intent = new Intent(NearbyService.ACTION_NEARBY_PLACES, null, mContext, NearbyService.class);
        intent.putExtra(NearbyService.EXTRA_NEARBY_REQUEST, getNearbyRequest(keyword));
        mContext.startService(intent);

        mPendingRequest = null;
    }


    private NearbyRequest getNearbyRequest(String keyword)
    {
        SharedPrefHelper sharedPref = new SharedPrefHelper(mContext);

        LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        int radius = 100;
        String[] types = { "" /*"bank", "atm", "restaurant"*/};
        String language = sharedPref.isEnglish() ?
                mContext.getString(R.string.nearby_language_val_en) :
                mContext.getString(R.string.nearby_language_val_iw) ;

        String rank = mContext.getString(R.string.nearby_rank_val);
        return new NearbyRequest(latLng, radius, types, keyword, language, rank);
    }

    public interface OnLocationReadyListener {
        void onLocationReady();
        void onPendingRequestHandled();
    }
}
