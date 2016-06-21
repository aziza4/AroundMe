package com.example.jbt.aroundme.Helpers;


import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.example.jbt.aroundme.ActivitiesAndFragments.MainActivity;
import com.example.jbt.aroundme.Data.DetailsRequest;
import com.example.jbt.aroundme.Data.DetailsResponse;
import com.example.jbt.aroundme.Data.NearbyRequest;
import com.example.jbt.aroundme.Data.NearbyResponse;
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

    private final String mNearByPath;
    private final String mLangKey;
    private final String mLocKey;
    private final String mRadiusKey;
    private final String mTypesKey;
    private final String mKeywordKey;
    private final String mRankKey;
    private final String mPageTokenKey;
    private final String mApiKeyKey;
    private final String mApiKeyVal;

    private final String mPhotoPath;
    private final String mPhotoMaxWidth;
    private final String mPhotoReference;

    private final String mDetailsPath;
    private final String mDetailsPlaceId;


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

    private final String mResultKey;
    private final String mAddressKey;
    private final String mPhoneKey;
    private final String mIntlPhoneKey;
    private final String mUrlKey;


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

        mNearByPath = context.getString(R.string.nearby_path);
        mLangKey = context.getString(R.string.nearby_language_key);
        mLocKey = context.getString(R.string.nearby_lat_lng_key);
        mRadiusKey = context.getString(R.string.nearby_radius_key);
        mTypesKey = context.getString(R.string.nearby_types_key);
        mKeywordKey = context.getString(R.string.nearby_keyword_key);
        mRankKey = context.getString(R.string.nearby_rank_key);
        mPageTokenKey = context.getString(R.string.nearby_page_token_key);
        mApiKeyKey = context.getString(R.string.nearby_api_key_key);
        mApiKeyVal = context.getString(R.string.nearby_api_key_val);

        mPhotoPath = context.getString(R.string.photo_path);
        mPhotoMaxWidth = context.getString(R.string.photo_max_width);
        mPhotoReference = context.getString(R.string.photo_photo_reference);

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

        mDetailsPath = context.getString(R.string.details_path);
        mDetailsPlaceId = context.getString(R.string.details_place_id);

        mResultKey = context.getString(R.string.details_result_key);
        mAddressKey = context.getString(R.string.details_result_formatted_address_key);
        mPhoneKey = context.getString(R.string.details_result_formatted_phone_number_key);
        mIntlPhoneKey = context.getString(R.string.details_result_international_phone_number_key);
        mUrlKey = context.getString(R.string.details_result_url_key);
    }


    private String getNearbyLocUrlString(NearbyRequest request, String pageToken)
    {
        // https://
        // maps.googleapis.com/
        // maps/api/place/nearbysearch/json?
        // language=iw&
        // location=32.0850818,34.8128246&
        // radius=1000&
        // keyword=cafe&
        // types=atm|bank&
        // rank=prominence&
        // key=AIzaSyBS45GLyDCuEaMBvpfWekbJ-6bSzdzaR_I

        Uri.Builder builder = new Uri.Builder();
        builder.scheme(mScheme)
                .authority(mAuthority)
                .path(mNearByPath)
                .appendQueryParameter(mLangKey, request.getLanguage())
                .appendQueryParameter(mLocKey, request.getLatLngAsString())
                .appendQueryParameter(mRadiusKey, request.getRadiusAsString())
                .appendQueryParameter(mKeywordKey, request.getKeyword())
                .appendQueryParameter(mTypesKey, request.getTypesAsString())
                .appendQueryParameter(mRankKey, request.getRank())
                .appendQueryParameter(mApiKeyKey, mApiKeyVal);

        if ( pageToken != null)
            builder.appendQueryParameter(mPageTokenKey, pageToken);

        return builder.build().toString();
    }


    public URL getNearbyLocUrl(NearbyRequest request, String pageToken)
    {
        URL url = null;

        try {

            url =  new URL(getNearbyLocUrlString(request, pageToken));

        } catch (MalformedURLException e) {

            Log.e(MainActivity.LOG_TAG, "" + e.getMessage());
        }

        return url;
    }


    public Uri getPhotoUri(Place place) {

        // https://
        // maps.googleapis.com/
        // maps/api/place/photo?
        // maxwidth=400&
        // photoreference=CnRtAAAATLZNl354RwP_9UKbQ_5Psy40texXePv4oAlgP4qNEkdIrkyse7rPXYGd9D_Uj1rVsQdWT4oRz4QrYAJNpFX7rzqqMlZw2h2E2y5IKMUZ7ouD_SlcHxYq1yL4KbKUv3qtWgTK0A6QbGh87GB3sscrHRIQiG2RrmU_jF4tENr9wGS_YxoUSSDrYjWmrNfeEHSGSc3FyhNLlBU&
        // key=YOUR_API_KEY

        String photoRef = place.getPhotoRef();

        if (photoRef == null || photoRef.isEmpty())
            return null;

        try {
            Uri.Builder builder = new Uri.Builder();
            builder.scheme(mScheme)
                    .authority(mAuthority)
                    .path(mPhotoPath)
                    .appendQueryParameter(mPhotoMaxWidth, PlacePhoto.PHOTO_MAX_WIDTH)
                    .appendQueryParameter(mPhotoReference, photoRef)
                    .appendQueryParameter(mApiKeyKey, mApiKeyVal);

            return builder.build();

        } catch (Exception e) {

            Log.e(MainActivity.LOG_TAG, "" + e.getMessage());
            return null;
        }
    }

    public URL getPhotoUrl(Place place)
    {
        URL url = null;

        try {

            url =  new URL(getPhotoUri(place).toString());

        } catch (MalformedURLException e) {

            Log.e(MainActivity.LOG_TAG, "" + e.getMessage());
        }

        return url;
    }


    public Uri getDetailsUri(Place place) {

        // https://
        // maps.googleapis.com/
        // maps/api/place/details/json?
        // placeid=ChIJrTLr-GyuEmsRBfy61i59si0&
        // key=YOUR_API_KEY

        try {
            Uri.Builder builder = new Uri.Builder();
            builder.scheme(mScheme)
                    .authority(mAuthority)
                    .path(mDetailsPath)
                    .appendQueryParameter(mDetailsPlaceId, place.getPlaceId())
                    .appendQueryParameter(mApiKeyKey, mApiKeyVal);

            return builder.build();

        } catch (Exception e) {

            Log.e(MainActivity.LOG_TAG, "" + e.getMessage());
            return null;
        }
    }

    public URL getDetailsUrl(Place place)
    {
        URL url = null;

        try {

            url =  new URL(getDetailsUri(place).toString());

        } catch (MalformedURLException e) {

            Log.e(MainActivity.LOG_TAG, "" + e.getMessage());
        }

        return url;
    }


    public DetailsResponse GetPlaceDetails(DetailsRequest request, String jsonString) {

        String status = null;
        Place place = request.getPlace();

        try {

            JSONObject placeObj = new JSONObject(jsonString);

            status = placeObj.has(mStatusKey) ? placeObj.getString(mStatusKey) : null;

            if (status != null && status.equals(NearbyResponse.STATUS_OK)) {

                JSONObject resultObj = placeObj.has(mResultKey) ?  placeObj.getJSONObject(mResultKey) : null;

                if (resultObj != null) {

                    String address = resultObj.has(mAddressKey) ? resultObj.getString(mAddressKey) : null;
                    String phone = resultObj.has(mPhoneKey) ? resultObj.getString(mPhoneKey) : null;
                    String intlPhone = resultObj.has(mIntlPhoneKey) ? resultObj.getString(mIntlPhoneKey) : null;
                    String url = resultObj.has(mUrlKey) ? resultObj.getString(mUrlKey) : null;

                    place.setAdditionalDetails(address, phone, intlPhone, url);
                }

            } else {
                Log.e(MainActivity.LOG_TAG, "response status" + status);
            }

        } catch (JSONException e) {
            Log.e(MainActivity.LOG_TAG, "" + e.getMessage());
        }

        return new DetailsResponse(status, place);
    }


    @SuppressWarnings("ConstantConditions")
    public NearbyResponse GetPlaces(String jsonString)
    {
        String status = null;
        ArrayList<Place> places = new ArrayList<>();
        String nextPageToken = null;
        String[] htmlAttributions = new String[0];

        try {

            JSONObject placeObj = new JSONObject(jsonString);

            // status
            status = placeObj.has(mStatusKey) ? placeObj.getString(mStatusKey) : null;

            if (status != null && status.equals(NearbyResponse.STATUS_OK))
            {
                // next page token
                nextPageToken = placeObj.has(mNextPageTokenKey) ? placeObj.getString(mNextPageTokenKey) : null;

                // html attributions
                if (placeObj.has(mHtmlAttributionsKey))
                {
                    JSONArray htmlAttsArr = placeObj.getJSONArray(mHtmlAttributionsKey);
                    htmlAttributions = new String[htmlAttsArr.length()];
                    for (int i = 0; i < htmlAttsArr.length(); i++)
                        htmlAttributions[i] = htmlAttsArr.getString(i);
                }

                // results
                JSONArray resArr = placeObj.getJSONArray(mResultsKey);
                for (int i=0; i< resArr.length(); i++) {
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
                    if (resObj.has(mTypesKey)) {
                        JSONArray typesArr = resObj.getJSONArray(mTypesKey);
                        types = new String[typesArr.length()];
                        for (int j = 0; j < typesArr.length(); j++)
                            types[j] = typesArr.getString(j);
                    }

                    PlacePhoto photo = null;
                    if (resObj.has(mPhotosKey)) {
                        JSONArray photosArr = resObj.getJSONArray(mPhotosKey);
                        JSONObject photoObj = (JSONObject) photosArr.get(0); // api states only at most one photo available
                        if (photoObj != null) {
                            int height = photoObj.has(mPhotoHeightKey) ? photoObj.getInt(mPhotoHeightKey) : 0;
                            int width = photoObj.has(mPhotoWidthKey) ? photoObj.getInt(mPhotoWidthKey) : 0;
                            String pReference = photoObj.has(mPhotoReferenceKey) ? photoObj.getString(mPhotoReferenceKey) : "";

                            String[] attArr = new String[0];
                            if (photoObj.has(mPhotoHtmlAttributionsKey)) {
                                JSONArray htmlAttArr = photoObj.getJSONArray(mPhotoHtmlAttributionsKey);
                                attArr = new String[htmlAttArr.length()];
                                for (int k = 0; k < htmlAttArr.length(); k++)
                                    attArr[k] = htmlAttArr.getString(k);
                            }

                            photo = new PlacePhoto(height, width, attArr, pReference, null);
                        }
                    }

                    Place place = new Place(
                            loc, icon, name,
                            photo, placeId, rating,
                            reference, scope, types, vicinity
                    );

                    places.add(place);
                }
            } else {
                Log.e(MainActivity.LOG_TAG, "response status" + status);
            }


        } catch (JSONException e) {
            Log.e(MainActivity.LOG_TAG, "" + e.getMessage());
        }

        return new NearbyResponse(status, places, nextPageToken, htmlAttributions);
    }
}
