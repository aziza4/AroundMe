package com.comli.shapira.aroundme.ui_helpers;


import android.app.Activity;
import android.content.Intent;
import android.location.Location;


import com.comli.shapira.aroundme.data.LastLocationInfo;
import com.comli.shapira.aroundme.data.NearbyRequest;
import com.comli.shapira.aroundme.helpers.BroadcastHelper;
import com.comli.shapira.aroundme.helpers.SharedPrefHelper;
import com.comli.shapira.aroundme.helpers.Utility;
import com.comli.shapira.aroundme.R;
import com.comli.shapira.aroundme.services.NearbyService;
import com.google.android.gms.maps.model.LatLng;


public class UserCurrentLocation { // controls the availability of location via LocationProvider it manages.

    private final Activity mActivity;
    private Location mLastLocation;
    private final OnLocationReadyListener mListener;
    private final SharedPrefHelper mSharedPrefHelper;
    private boolean mLocationReadyCalled;
    private String mKeyword;

    public static final String LAST_LOC_INFO_KEY = "last_loc_info";


    public UserCurrentLocation(Activity activity, LastLocationInfo lastLocationInfo, String keyword,
                               OnLocationReadyListener listener)
    {
        mActivity = activity;
        mKeyword = keyword;
        mListener = listener;
        mLocationReadyCalled = false;
        mSharedPrefHelper = new SharedPrefHelper(mActivity);
        mLastLocation = null;

        if (lastLocationInfo != null)
            mLastLocation = lastLocationInfo.getLocation();
        else if ( mSharedPrefHelper.lastUserLocationExist())
            mLastLocation = mSharedPrefHelper.getLastUserLocation();
        else mLastLocation = null;

        if ( !mKeyword.isEmpty()) {
            getAndHandle();
            mKeyword = "";
        }
    }

    public Location getLastLocation()
    {
        return mLastLocation;
    }


    public boolean ready()
    {
        return mLastLocation != null;
    }


    public void getAndHandle() {

        if ( !ready() )
            return;

        // if location is available then GO! - use our service to download from internet...
        Intent intent = new Intent(NearbyService.ACTION_NEARBY_PLACES, null, mActivity, NearbyService.class);
        intent.putExtra(NearbyService.EXTRA_NEARBY_REQUEST, getNearbyRequest());
        mActivity.startService(intent);

        BroadcastHelper.broadcastSearchStarted(mActivity);  // start progress bar
    }


    private NearbyRequest getNearbyRequest() // prepare request object with all query parameters
    {
        LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());

        int radius = mSharedPrefHelper.getRadius();
        if (! mSharedPrefHelper.isMeters())
            radius = Utility.feetToMeters(radius);

        String typesString = mSharedPrefHelper.getSelectedTypes();
        String[] types = typesString.split("\\|");
        if (types[0].equals(mActivity.getString(R.string.pref_types_all)))
            types[0] = "";

        String language = mSharedPrefHelper.isEnglish() ?
                mActivity.getString(R.string.nearby_language_val_en) :
                mActivity.getString(R.string.nearby_language_val_iw) ;

        String rank = mActivity.getString(R.string.nearby_rank_val);
        return new NearbyRequest(latLng, radius, types, mKeyword, language, rank);
    }


    public void onLocationChanged(Location location) {

        mLastLocation = location;
        mSharedPrefHelper.saveLastUserLocation(location);

        if (!mLocationReadyCalled) {
            mLocationReadyCalled = true;
            mListener.onLocationReady(); // update activity to refresh menu icons
        }
    }


    public interface OnLocationReadyListener {
        void onLocationReady();
    }
}
