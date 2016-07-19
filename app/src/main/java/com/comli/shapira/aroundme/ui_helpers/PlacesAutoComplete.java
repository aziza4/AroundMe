package com.comli.shapira.aroundme.ui_helpers;


import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.comli.shapira.aroundme.activities_fragments.MainActivity;
import com.comli.shapira.aroundme.db.AroundMeDBHelper;
import com.comli.shapira.aroundme.helpers.BroadcastHelper;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;


public class PlacesAutoComplete {

    private final Activity mActivity;

    private static final LatLngBounds BOUNDS_HOME_VIEW =
            new LatLngBounds(
                    new LatLng(32.081541, 34.804215),
                    new LatLng(32.089150, 34.816542)
            );

    private static final int REQUEST_SELECT_PLACE = 1000;



    public PlacesAutoComplete(Activity activity) {
        this.mActivity = activity;
    }


    public void start()
    {
        try {
            Intent intent = new PlaceAutocomplete
                    .IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                    .setBoundsBias(BOUNDS_HOME_VIEW)
                    .build(mActivity);

            mActivity.startActivityForResult(intent, REQUEST_SELECT_PLACE);

        } catch (Exception e) {
            Log.e(MainActivity.LOG_TAG, "" + e.getMessage());
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode != REQUEST_SELECT_PLACE)
            return;

        if (resultCode == Activity.RESULT_OK) {

            // convert google 'Place' class to my internal 'Place' class via ctor's overloading
            Place place = PlaceAutocomplete.getPlace(mActivity, data);
            com.comli.shapira.aroundme.data.Place myPlace = new com.comli.shapira.aroundme.data.Place(place);

            ArrayList<com.comli.shapira.aroundme.data.Place> places = new ArrayList<>();
            places.add(myPlace);

            // replace 'search' result with this single result
            AroundMeDBHelper dbHelper = new AroundMeDBHelper(mActivity);
            dbHelper.searchDeleteAllPlaces();
            dbHelper.searchBulkInsert(places);
            BroadcastHelper.broadcastSearchPlacesSaved(mActivity, places);

        } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
            Status status = PlaceAutocomplete.getStatus(mActivity, data);
            Log.e(MainActivity.LOG_TAG, "onError: Status = " + status.toString());
        }
    }
}
