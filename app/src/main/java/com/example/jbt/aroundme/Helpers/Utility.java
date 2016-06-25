package com.example.jbt.aroundme.Helpers;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;

import com.example.jbt.aroundme.Data.Place;
import com.example.jbt.aroundme.R;
import com.google.android.gms.maps.model.LatLng;

import java.util.Locale;


public class Utility {

    private static final double FEET_TO_METERS_RATIO = 0.3048;
    private static final double KM_TO_MILES_RATIO = 0.621;

    public static void setContentViewWithLocaleChange(AppCompatActivity activity,
                                                      int layoutId, int titleId)
    {
        SharedPrefHelper sharedPrefHelper = new SharedPrefHelper(activity);
        sharedPrefHelper.changeLocale();
        activity.setContentView(layoutId);
        resetTitle(activity, titleId); // workaround android bug, see above
    }


    // Workaround android bug: http://stackoverflow.com/questions/22884068/troubles-with-activity-title-language
    public static void resetTitle(AppCompatActivity activity, int id)
    {
        ActionBar actionBar = activity.getSupportActionBar();

        if (actionBar != null)
            actionBar.setTitle(activity.getString(id));
    }


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

    private static float distanceInKM(LatLng latLng1, LatLng latLng2)
    {
        Location loc1 = new Location("");
        loc1.setLatitude(latLng1.latitude);
        loc1.setLongitude(latLng1.longitude);

        Location loc2 = new Location("");
        loc2.setLatitude(latLng2.latitude);
        loc2.setLongitude(latLng2.longitude);

        return loc1.distanceTo(loc2)/1000;
    }


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
}
