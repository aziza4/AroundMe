package com.example.jbt.aroundme;


import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;


public class SearchFragment extends Fragment {

    private TextView mLocationTV;
    private TextView mAttributionTV;
    private TextView mCurrUserLocation;

    public SearchFragment() {}

    public void setPlace(Place place)
    {
        String info = getString(R.string.formatted_place_data,
                place.getName(),
                place.getAddress(),
                place.getPhoneNumber(),
                place.getWebsiteUri(),
                place.getRating(),
                place.getLatLng().latitude,
                place.getLatLng().longitude,
                place.getId());

        mLocationTV.setText(info);

        if ( ! TextUtils.isEmpty(place.getAttributions())) {

            String attributes = Html.fromHtml(place.getAttributions().toString()).toString();
            mAttributionTV.setText(attributes);
        }
    }

    public void setCurUserLocation(Location location) {
        String info = "" + location.getLatitude() + ", " + location.getLongitude();
        mCurrUserLocation.setText(info);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_search, container, false);

        mLocationTV = (TextView) v.findViewById(R.id.txt_location);
        mAttributionTV = (TextView) v.findViewById(R.id.txt_attributions);
        mCurrUserLocation = (TextView) v.findViewById(R.id.txt_cur_user_location);

        return v;
    }
}
