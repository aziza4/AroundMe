package com.comli.shapira.aroundme.ui_helpers;


import android.app.Activity;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.comli.shapira.aroundme.activities_fragments.SettingsActivity;
import com.comli.shapira.aroundme.R;
import com.comli.shapira.aroundme.helpers.LocationServiceHelper;
import com.comli.shapira.aroundme.services.NearbyService;

public class MainMenuHelper {

    private final AppCompatActivity mActivity;
    private LocationServiceHelper mLocationServiceHelper;
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

            case R.id.menu_action_settings:
                mActivity.startActivity(new Intent(mActivity, SettingsActivity.class));
                break;

            case R.id.menu_search_my_loc:
                mUserCurrentLocation.getAndHandle();
                break;

            case R.id.menu_search_places:
                mPlacesAutoComplete.start();
                break;

            case R.id.menu_delete_all_favorites:
                showDeleteConfirmationDialog();
                break;
        }
    }


    private void showDeleteConfirmationDialog() {

        final String deleteTitle = mActivity.getString(R.string.delete_all_title);
        final String deleteMsg = mActivity.getString(R.string.delete_all_message);
        final String deleteButton = mActivity.getString(R.string.delete_all_delete_button);
        final String cancelButton = mActivity.getString(R.string.delete_all_Cancel_button);

        new AlertDialog.Builder(mActivity)
                .setTitle(deleteTitle)
                .setMessage(deleteMsg)
                .setIcon(android.R.drawable.ic_delete)

                .setPositiveButton(deleteButton,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                mActivity.startService(
                                        new Intent(NearbyService.ACTION_PLACE_FAVORITES_REMOVE_ALL,
                                                null, mActivity, NearbyService.class));
                                dialog.dismiss();
                            }
                        })

                .setNegativeButton(cancelButton,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })

                .create()
                .show();
    }
}
