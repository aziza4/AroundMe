package com.example.jbt.aroundme.UIHelpers;


import android.app.Activity;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.example.jbt.aroundme.ActivitiesAndFragments.SettingsActivity;
import com.example.jbt.aroundme.Helpers.ImageHelper;
import com.example.jbt.aroundme.R;
import com.example.jbt.aroundme.Services.NearbyService;

public class MainMenuHelper {

    private final AppCompatActivity mActivity;
    private final UserCurrentLocation mUserCurrentLocation;
    private final PlacesAutoComplete mPlacesAutoComplete;
    private final Resources mResources;


    public MainMenuHelper(AppCompatActivity activity, UserCurrentLocation userCurrentLocation, PlacesAutoComplete placesAutoComplete) {

        mActivity = activity;
        mUserCurrentLocation = userCurrentLocation;
        mPlacesAutoComplete = placesAutoComplete;
        mResources = mActivity.getResources();
    }

    public void onPrepareOptionsMenu(Menu menu) {

        boolean enabled = mUserCurrentLocation.ready();
        Drawable myLocIcon = ResourcesCompat.getDrawable(mResources, R.drawable.ic_action_search_by_keyword, null);
        Drawable byKeywordIcon = ResourcesCompat.getDrawable(mResources, R.drawable.ic_action_search_by_keyword, null);
        ImageHelper.setDrawableEnableDisableColor(myLocIcon, enabled);
        ImageHelper.setDrawableEnableDisableColor(byKeywordIcon, enabled);

        MenuItem myLocation = menu.findItem(R.id.menu_search_by_keyword);
        MenuItem byKeyword = menu.findItem(R.id.menu_search_by_keyword);
        myLocation.setEnabled(enabled);
        byKeyword.setEnabled(enabled);
    }

    public void onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = mActivity.getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        // Todo: gray icon until location updates are available. Also make search by keyword work
        // http://stackoverflow.com/questions/9642990/is-it-possible-to-grey-out-not-just-disable-a-menuitem-in-android

        SearchView searchView = (SearchView)menu.findItem(R.id.menu_search_by_keyword).getActionView();
        SearchManager searchManager = (SearchManager) mActivity.getSystemService(Activity.SEARCH_SERVICE);
        ComponentName componentName = mActivity.getComponentName();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName));
    }


    public void onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.menu_action_settings:
                mActivity.startActivity(new Intent(mActivity, SettingsActivity.class));
                break;

            case R.id.menu_search_my_loc:
                mUserCurrentLocation.getAndHandle("");
                break;

            case R.id.menu_search_places:
                mPlacesAutoComplete.start();
                break;

            case R.id.menu_delete_all_favorites:
                mActivity.startService(
                        new Intent(NearbyService.ACTION_PLACE_FAVORITES_REMOVE_ALL,
                                null, mActivity, NearbyService.class));
                break;
        }
    }
}
