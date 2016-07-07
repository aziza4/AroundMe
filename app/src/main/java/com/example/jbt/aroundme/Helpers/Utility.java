package com.example.jbt.aroundme.helpers;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;

import com.example.jbt.aroundme.data.Place;
import com.example.jbt.aroundme.R;
import com.google.android.gms.maps.model.LatLng;

import java.util.Locale;


public class Utility {

    private static final double FEET_TO_METERS_RATIO = 0.3048;
    private static final double KM_TO_MILES_RATIO = 0.621371192;

    public static void setContentViewWithLocaleChange(AppCompatActivity activity,
                                                      int layoutId, int titleId)
    {
        SharedPrefHelper sharedPrefHelper = new SharedPrefHelper(activity);
        sharedPrefHelper.changeLocale();
        activity.setContentView(layoutId);
        resetTitle(activity, titleId);
    }


    // Workaround android bug: http://stackoverflow.com/questions/22884068/troubles-with-activity-title-language
    public static void resetTitle(AppCompatActivity activity, int id)
    {
        ActionBar actionBar = activity.getSupportActionBar();

        if (actionBar != null) {

            // set title
            actionBar.setTitle(activity.getString(id));

            // set up icon
            final Drawable upArrow = ContextCompat.getDrawable(activity, R.drawable.ic_arrow_back_black_24dp);
            final int arrowColor = ContextCompat.getColor(activity, android.R.color.primary_text_dark);
            upArrow.setColorFilter(arrowColor, PorterDuff.Mode.SRC_ATOP);
            actionBar.setHomeAsUpIndicator(upArrow);
        }
    }


    // Prepare url that suits not only google map, but waze (for example) as well...
    public static void setShareActionProviderForLocation(Menu menu, Place place)
    {
        MenuItem item = menu.findItem(R.id.shareMenuItem);
        ShareActionProvider provider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        LatLng latLng =  place.getLoc();
        double lat = latLng.latitude;
        double lng = latLng.longitude;
        String address = place.getAddress() != null ? place.getAddress() : "";
        String name = place.getName();
        Uri uri = Uri.parse("geo:" + lat + "," + lng + "?" + "z=17" + "&q=" + Uri.encode(name) + "+" + Uri.encode(address));
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        provider.setShareIntent(intent);
    }


    public static int feetToMeters(int feet)
    {
        return (int)(feet * FEET_TO_METERS_RATIO);
    }


    private static float kmToMiles(float km)
    {
        return (float) (km * KM_TO_MILES_RATIO);
    }


    public static double radiusToZoom(double radiusInKM)
    {
        double radiusInKMExtended = radiusInKM * 1.3;
        double radiusInMilesExtended = radiusInKMExtended * KM_TO_MILES_RATIO;
        return (14 - Math.log(radiusInMilesExtended) / Math.log(2));
    }


    public static float distanceInKM(LatLng latLng1, LatLng latLng2)
    {
        Location loc1 = new Location("");
        loc1.setLatitude(latLng1.latitude);
        loc1.setLongitude(latLng1.longitude);

        Location loc2 = new Location("");
        loc2.setLatitude(latLng2.latitude);
        loc2.setLongitude(latLng2.longitude);

        return loc1.distanceTo(loc2)/1000;
    }


    // Although user's input are in meter/feet - better to display result on KM/miles...
    public static String getDistanceMsg(Context context, Place place)
    {
        SharedPrefHelper sharedPrefHelper = new SharedPrefHelper(context);
        LatLng userLoc = sharedPrefHelper.getLastUserLocation();

        if (userLoc.longitude == 0 && userLoc.latitude == 0) // first time app running
            return "";

        LatLng placeLoc = place.getLoc();

        float distanceInKM = Utility.distanceInKM(userLoc, placeLoc);

        String formatString = context.getString(R.string.formatted_away_from_me_msg);

        if (sharedPrefHelper.isMeters()) { // metric system
            String kmString = context.getString(R.string.formatted_away_from_me_km);
            return String.format(Locale.ENGLISH, "%.1f %s %s", distanceInKM, kmString, formatString);
        }

        // english system
        float distanceInMiles = kmToMiles(distanceInKM);
        String milesString = context.getString(R.string.formatted_away_from_me_miles);
        return String.format(Locale.ENGLISH, "%.1f %s %s", distanceInMiles, milesString, formatString);
    }


    public static Uri getDialingUri(Context context, Place place)
    {
        String scheme = context.getString(R.string.dialer_scheme);
        return Uri.parse(scheme + ":" + place.getPhone());
    }


    public static Uri getDirectionsUri(Context context, Place place)
    {
        SharedPrefHelper sharedPrefHelper = new SharedPrefHelper(context);
        LatLng userLoc = sharedPrefHelper.getLastUserLocation();

        String scheme = context.getString(R.string.https_scheme);
        String authority = context.getString(R.string.directions_authority);
        String path = context.getString(R.string.directions_path);
        String reqKey = context.getString(R.string.directions_req_key);
        String reqVal = context.getString(R.string.directions_req_val);
        String langKey = context.getString(R.string.directions_lang_key);
        String modeKey = context.getString(R.string.directions_mode_key);
        String modeVal = context.getString(R.string.directions_mode_val);
        String startAddressKey = context.getString(R.string.directions_start_address_key);
        String endAddressKey = context.getString(R.string.directions_end_address_key);

        String langVal = sharedPrefHelper.getSelectedLanguage();
        String startAddressVal = userLoc.latitude + "," + userLoc.longitude;

        // if we have full data (place name and address on 'Favorites') lets help googlemaps
        // do a better job... Otherwise ('Search'), we use what we have (LatLng only...)
        String endAddressVal = place.getAddress() == null || place.getAddress().isEmpty() ?
                place.getLoc().latitude + "," + place.getLoc().longitude :
                place.getName() + ", " + place.getAddress();

        Uri.Builder builder = new Uri.Builder();

        return builder.scheme(scheme)
                .authority(authority)
                .path(path)
                .appendQueryParameter(reqKey, reqVal)
                .appendQueryParameter(langKey, langVal)
                .appendQueryParameter(modeKey, modeVal)
                .appendQueryParameter(startAddressKey, startAddressVal)
                .appendQueryParameter(endAddressKey, endAddressVal)
                .build();
    }
}
