package com.comli.shapira.aroundme.ui_helpers;


import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.comli.shapira.aroundme.R;
import com.comli.shapira.aroundme.activities_fragments.SettingsActivity;
import com.comli.shapira.aroundme.adapters.TabsPagerAdapter;
import com.comli.shapira.aroundme.helpers.SharedPrefHelper;
import com.comli.shapira.aroundme.services.NearbyService;

public class NavigationDrawerHelper implements NavigationView.OnNavigationItemSelectedListener {

    private final AppCompatActivity mActivity;
    private final UserCurrentLocation mUserCurrentLocation;
    private final ViewPager mViewPager;

    public NavigationDrawerHelper(AppCompatActivity activity, UserCurrentLocation userCurrentLocation, ViewPager viewPager)
    {
        mActivity = activity;
        mUserCurrentLocation = userCurrentLocation;
        mViewPager = viewPager;

        ActionBar actionBar = mActivity.getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        DrawerLayout drawer = (DrawerLayout) mActivity.findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        SharedPrefHelper sharedPrefHelper = new SharedPrefHelper(mActivity);
        String currentTypes = sharedPrefHelper.getSelectedTypes();

        String selectedTypes;

        switch (item.getItemId()) {
            case R.id.nav_menu_all:
                selectedTypes = mActivity.getString(R.string.pref_types_all);
                break;

            case R.id.nav_menu_restaurants:
                selectedTypes = mActivity.getString(R.string.pref_types_restaurant_cafe_bar_nightclub);
                break;

            case R.id.nav_menu_parking:
                selectedTypes = mActivity.getString(R.string.pref_types_parking);
                break;

            case R.id.nav_menu_banks:
                selectedTypes = mActivity.getString(R.string.pref_types_bank_atm);
                break;

            case R.id.nav_menu_museums:
                selectedTypes = mActivity.getString(R.string.pref_types_museum_gallery);
                break;

            case R.id.nav_menu_gas:
                selectedTypes = mActivity.getString(R.string.pref_types_gas);
                break;

            case R.id.nav_menu_doctors:
                selectedTypes = mActivity.getString(R.string.pref_types_doctor_hospital_pharmacy);
                break;

            case R.id.nav_menu_bus:
                selectedTypes = mActivity.getString(R.string.pref_types_bus_train_taxi);
                break;

            case R.id.nav_menu_delete_favorites:
                mViewPager.setCurrentItem(TabsPagerAdapter.FAVORITES_TAB);
                showDeleteConfirmationDialog();
                return true;

            case R.id.nav_menu_settings:
                mActivity.startActivity(new Intent(mActivity, SettingsActivity.class));
                return true;

            default:
                return false;
        }

        if (!selectedTypes.equals(currentTypes)) {
            sharedPrefHelper.setSelectedTypes(selectedTypes);
            String keyword = sharedPrefHelper.getSearchKeyword();
            mUserCurrentLocation.searchCurrentLocation(keyword);
        }

        return true;
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
