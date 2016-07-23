package com.comli.shapira.aroundme.ui_helpers;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;


import com.comli.shapira.aroundme.data.NearbyRequest;
import com.comli.shapira.aroundme.helpers.BroadcastHelper;
import com.comli.shapira.aroundme.helpers.LocationServiceHelper;
import com.comli.shapira.aroundme.helpers.SharedPrefHelper;
import com.comli.shapira.aroundme.helpers.Utility;
import com.comli.shapira.aroundme.R;
import com.comli.shapira.aroundme.services.NearbyService;
import com.google.android.gms.maps.model.LatLng;


public class UserCurrentLocation { // controls the availability of location via LocationProvider it manages.

    private final Activity mActivity;
    private Location mLastLocation;


    private final SharedPrefHelper mSharedPrefHelper;
    private LocationServiceHelper mLocationServiceHelper;
    private String mKeyword;


    public UserCurrentLocation(Activity activity, LocationServiceHelper locationServiceHelper, String keyword)
    {
        mActivity = activity;
        mLocationServiceHelper = locationServiceHelper;
        mLastLocation = null;
        mKeyword = keyword;
        mSharedPrefHelper = new SharedPrefHelper(mActivity);

        if ( !mKeyword.isEmpty()) {
            getAndHandle();
            mKeyword = "";
        }
    }

    public void getAndHandle() {

        mLastLocation = mLocationServiceHelper.getLastLocation();

        if ( mLastLocation == null )
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
}
