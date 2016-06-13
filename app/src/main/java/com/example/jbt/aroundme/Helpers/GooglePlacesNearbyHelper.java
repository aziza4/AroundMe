package com.example.jbt.aroundme.Helpers;


import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.example.jbt.aroundme.ActivitiesAndFragments.MainActivity;
import com.example.jbt.aroundme.Data.Place;
import com.example.jbt.aroundme.Data.PlacePhoto;
import com.example.jbt.aroundme.R;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;



public class GooglePlacesNearbyHelper { // encapsulates GooglePlaces website specific data logic

    // request strings
    private final String mScheme;
    private final String mAuthority;
    private final String mPath;
    private final String mLangKey;
    private final String mLangVal;
    private final String mLocKey;
    private final String mRadiusKey;
    private final String mTypesKey;
    private final String mRankKey;
    private final String mRankVal;
    private final String mApiKeyKey;
    private final String mApiKeyVal;

    private static final String STATUS_OK = "OK";
//    private static final String STATUS_ZERO_RESULTS = "ZERO_RESULTS";
//    private static final String STATUS_OVER_QUERY_LIMIT = "OVER_QUERY_LIMIT";
//    private static final String STATUS_REQUEST_DENIED = "REQUEST_DENIED";
//    private static final String STATUS_INVALID_REQUEST = "INVALID_REQUEST";

    // response strings
    private final String mStatusKey;
    private final String mNextPageTokenKey;
    private final String mHtmlAttributionsKey;
    private final String mResultsKey;
    private final String mGeometryKey;
    private final String mLocationKey;
    private final String mLatKey;
    private final String mLngKey;
    private final String mIconKey;
    private final String mNameKey;
    private final String mPlaceIdKey;
    private final String mRatingKey;
    private final String mReferenceKey;
    private final String mScopeKey;
    private final String mVicinityKey;
    private final String mPhotosKey;
    private final String mPhotoHeightKey;
    private final String mPhotoWidthKey;
    private final String mPhotoReferenceKey;
    private final String mPhotoHtmlAttributionsKey;

    private String mStatusVal;
    private String mNextPageTokenVal;
    private String[] mHtmlAttributionsVal;



    // https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=32.0850818,34.8128246&radius=1000&type=restaurant&rank=prominence&key=AIzaSyBS45GLyDCuEaMBvpfWekbJ-6bSzdzaR_I
    // -------------------------------------------------------------------
    // https://maps.googleapis.com/maps/api/place/nearbysearch/
    // json?
    // language=iw&
    // location=32.0850818,34.8128246&
    // radius=1000&
    // types=atm|bank&
    // rank=prominence&
    // key=AIzaSyBS45GLyDCuEaMBvpfWekbJ-6bSzdzaR_I

    public GooglePlacesNearbyHelper(Context context) {

        mScheme = context.getString(R.string.https_scheme);
        mAuthority = context.getString(R.string.authority);
        mPath = context.getString(R.string.nearby_path);
        mLangKey = context.getString(R.string.nearby_language_key);
        mLangVal = context.getString(R.string.nearby_language_val);
        mLocKey = context.getString(R.string.nearby_lat_lng_key);
        mRadiusKey = context.getString(R.string.nearby_radius_key);
        mTypesKey = context.getString(R.string.nearby_types_key);
        mRankKey = context.getString(R.string.nearby_rank_key);
        mRankVal = context.getString(R.string.nearby_rank_val);
        mApiKeyKey = context.getString(R.string.nearby_api_key_key);
        mApiKeyVal = context.getString(R.string.nearby_api_key_val);

        mStatusKey = context.getString(R.string.nearby_status_key);
        mNextPageTokenKey = context.getString(R.string.nearby_next_page_token_key);
        mHtmlAttributionsKey = context.getString(R.string.nearby_html_attributions_key);
        mResultsKey = context.getString(R.string.nearby_results_key);
        mGeometryKey = context.getString(R.string.nearby_results_geometry_key);
        mLocationKey = context.getString(R.string.nearby_results_geometry_location_key);
        mLatKey = context.getString(R.string.nearby_results_geometry_location_lat_key);
        mLngKey = context.getString(R.string.nearby_results_geometry_location_lng_key);
        mIconKey = context.getString(R.string.nearby_results_icon_key);
        mNameKey = context.getString(R.string.nearby_results_name_key);
        mPlaceIdKey = context.getString(R.string.nearby_results_place_id_key);
        mRatingKey = context.getString(R.string.nearby_results_rating_key);
        mReferenceKey = context.getString(R.string.nearby_results_reference_key);
        mScopeKey = context.getString(R.string.nearby_results_scope_key);
        mVicinityKey = context.getString(R.string.nearby_results_vicinity_key);
        mPhotosKey = context.getString(R.string.nearby_results_photos_key);
        mPhotoHeightKey = context.getString(R.string.nearby_results_photos_height_key);
        mPhotoWidthKey = context.getString(R.string.nearby_results_photos_width_key);
        mPhotoReferenceKey = context.getString(R.string.nearby_results_photos_photo_reference_key);
        mPhotoHtmlAttributionsKey = context.getString(R.string.nearby_results_photos_html_attributions_key);
    }


    private String getNearbyLocUrlString(LatLng loc, int radius, String[] types)
    {
        // https://
        // maps.googleapis.com/
        // maps/api/place/nearbysearch/json?
        // language=iw&
        // location=32.0850818,34.8128246&
        // radius=1000&
        // types=atm|bank&
        // rank=prominence&
        // key=AIzaSyBS45GLyDCuEaMBvpfWekbJ-6bSzdzaR_I

        if (loc == null || types == null || types.length <=0 )
            return null;

        String locVal = "" + loc.latitude + "," + loc.longitude;

        String typesVal = "";
        for (int i = 0; i < types.length-1; i++) {
            typesVal += types[i] + "|";
        }
        typesVal += types[types.length-1];

        Uri.Builder builder = new Uri.Builder();
        builder.scheme(mScheme)
                .authority(mAuthority)
                .path(mPath)
                .appendQueryParameter(mLangKey, mLangVal)
                .appendQueryParameter(mLocKey, locVal)
                .appendQueryParameter(mRadiusKey, ""+ radius)
                .appendQueryParameter(mTypesKey, typesVal)
                .appendQueryParameter(mRankKey, mRankVal)
                .appendQueryParameter(mApiKeyKey, mApiKeyVal);

        return builder.build().toString();
    }


    public URL getNearbyLocUrl(LatLng loc, int radius, String[] types)
    {
        URL url = null;

        try {

            url =  new URL(getNearbyLocUrlString(loc, radius, types));

        } catch (MalformedURLException e) {

            Log.e(MainActivity.LOG_TAG, "" + e.getMessage());
        }

        return url;
    }



    public ArrayList<Place> GetPlaces(String jsonString)
    {
        ArrayList<Place> list = new ArrayList<>();

        try {

            JSONObject placeObj = new JSONObject(jsonString);

            // status
            mStatusVal = placeObj.getString(mStatusKey);
            if (mStatusVal == null || !mStatusVal.equals(STATUS_OK))
                return null;

            // next page token
            mNextPageTokenVal = placeObj.has(mNextPageTokenKey) ? placeObj.getString(mNextPageTokenKey) : "";

            // html attributions
            if (placeObj.has(mHtmlAttributionsKey))
            {
                JSONArray htmlAttsArr = placeObj.getJSONArray(mHtmlAttributionsKey);
                mHtmlAttributionsVal = new String[htmlAttsArr.length()];
                for (int i = 0; i < htmlAttsArr.length(); i++)
                    mHtmlAttributionsVal[i] = htmlAttsArr.getString(i);
            }

            // results
            JSONArray resArr = placeObj.getJSONArray(mResultsKey);
            for (int i=0; i< resArr.length(); i++)
            {
                JSONObject resObj = resArr.getJSONObject(i);

                JSONObject geometryObj = resObj.getJSONObject(mGeometryKey);
                JSONObject locationObj = geometryObj.getJSONObject(mLocationKey);
                double lat = locationObj.getDouble(mLatKey);
                double lng = locationObj.getDouble(mLngKey);
                LatLng loc = new LatLng(lat, lng);

                String icon = resObj.has(mIconKey) ? resObj.getString(mIconKey) : "";
                String name = resObj.has(mNameKey) ? resObj.getString(mNameKey) : "";
                String placeId = resObj.has(mPlaceIdKey) ? resObj.getString(mPlaceIdKey) : "";
                double rating = resObj.has(mRatingKey) ? resObj.getDouble(mRatingKey) : 0.0;
                String reference = resObj.has(mReferenceKey) ? resObj.getString(mReferenceKey) : "";
                String scope = resObj.has(mScopeKey) ? resObj.getString(mScopeKey) : "";
                String vicinity = resObj.has(mVicinityKey) ? resObj.getString(mVicinityKey) : "";

                String[] types = new String[0];
                if ( placeObj.has(mResultsKey) ) {
                    JSONArray typesArr = placeObj.getJSONArray(mResultsKey);
                    types = new String[typesArr.length()];
                    for (int j = 0; j < typesArr.length(); j++)
                        types[j] = typesArr.getString(j);
                }

                PlacePhoto[] photos = new PlacePhoto[0];
                if ( placeObj.has(mPhotosKey) ) {
                    JSONArray photosArr = placeObj.getJSONArray(mPhotosKey);
                    photos = new PlacePhoto[photosArr.length()];
                    for (int j = 0; j < photosArr.length(); j++) {

                        JSONObject photo = (JSONObject) photosArr.get(j);
                        int height = photo.has(mPhotoHeightKey) ? photo.getInt(mPhotoHeightKey) : 0;
                        int width = photo.has(mPhotoWidthKey) ? photo.getInt(mPhotoWidthKey) : 0;
                        String pReference = photo.has(mPhotoReferenceKey) ? photo.getString(mPhotoReferenceKey) : "";

                        String[] attArr = new String[0];
                        if ( photo.has(mPhotoHtmlAttributionsKey) ) {
                            JSONArray htmlAttArr = photo.getJSONArray(mPhotoHtmlAttributionsKey);
                            attArr = new String[htmlAttArr.length()];
                            for (int k = 0; k < htmlAttArr.length(); k++)
                                attArr[k] = htmlAttArr.getString(k);
                        }

                        photos[j] = new PlacePhoto(height, width, attArr, pReference);
                    }
                }

                Place place = new Place(
                        loc, icon, name,
                        photos, placeId, rating,
                        reference, scope, types, vicinity
                );

                list.add(place);
            }

        } catch (JSONException e) {
            Log.e(MainActivity.LOG_TAG, "" + e.getMessage());
        }

        return list;
    }
}
