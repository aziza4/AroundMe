package com.comli.shapira.aroundme.ui_helpers;


import android.app.Activity;
import android.app.SearchManager;
import android.content.ComponentName;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.comli.shapira.aroundme.R;
import com.comli.shapira.aroundme.helpers.LocationServiceHelper;

public class MainMenuHelper {

    private final AppCompatActivity mActivity;
    private final LocationServiceHelper mLocationServiceHelper;
    private final PlacesAutoComplete mPlacesAutoComplete;
    private final UserCurrentLocation mUserCurrentLocation;


    public MainMenuHelper(AppCompatActivity activity,
                          LocationServiceHelper locationServiceHelper,
                          UserCurrentLocation userCurrentLocation,
                          PlacesAutoComplete placesAutoComplete) {

        mActivity = activity;
        mLocationServiceHelper = locationServiceHelper;
        mUserCurrentLocation = userCurrentLocation;
        mPlacesAutoComplete = placesAutoComplete;
    }


    public void onPrepareOptionsMenu(Menu menu) {

        boolean enabled = mLocationServiceHelper.ready();

        MenuItem myLocation = menu.findItem(R.id.menu_search_my_loc);
        MenuItem mySearch = menu.findItem(R.id.menu_search_places);
        MenuItem byKeyword = menu.findItem(R.id.menu_search_by_keyword);
        myLocation.setEnabled(enabled);
        mySearch.setEnabled(enabled);
        byKeyword.setEnabled(enabled);
    }


    public void onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = mActivity.getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        SearchView searchView = (SearchView)menu.findItem(R.id.menu_search_by_keyword).getActionView();
        SearchManager searchManager = (SearchManager) mActivity.getSystemService(Activity.SEARCH_SERVICE);
        ComponentName componentName = mActivity.getComponentName();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName));
    }


    public void onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.menu_search_my_loc:
                mUserCurrentLocation.searchCurrentLocation("");
                break;

            case R.id.menu_search_places:
                mPlacesAutoComplete.start();
                break;
        }
    }
}
