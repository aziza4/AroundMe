package com.example.jbt.aroundme.Helpers;

import android.content.Intent;
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


public class Utility {

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
}
