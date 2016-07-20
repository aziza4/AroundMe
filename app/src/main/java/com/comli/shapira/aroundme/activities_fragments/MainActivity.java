package com.comli.shapira.aroundme.activities_fragments;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.multidex.MultiDex;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.comli.shapira.aroundme.adapters.TabsPagerAdapter;
import com.comli.shapira.aroundme.data.LastLocationInfo;
import com.comli.shapira.aroundme.helpers.BroadcastHelper;
import com.comli.shapira.aroundme.helpers.SharedPrefHelper;
import com.comli.shapira.aroundme.helpers.Utility;
import com.comli.shapira.aroundme.R;
import com.comli.shapira.aroundme.receivers.NearbyNotificationReceiver;
import com.comli.shapira.aroundme.receivers.PowerConnectionReceiver;
import com.comli.shapira.aroundme.ui_helpers.*;


public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = "AroundMe";
    public static final int LOCATION_REQUEST_CODE = 1;

    private PlacesAutoComplete mPlacesAutoComplete;
    private MainMenuHelper mMainMenuHelper;
    private UserCurrentLocation mUserCurrentLocation;
    private PowerConnectionReceiver mPowerConnectionReceiver;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // set shared-place transition exit
        TransitionsHelper.setMainActivityExitTransition(this);

        // setContentView with locale change
        Utility.setContentViewWithLocaleChange(this, R.layout.activity_main, R.string.app_name);

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Utility.resetTitle(this, R.string.app_name);


        // Viewpager - 2 tabs: Search & Favorites
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPagerContainer);
        TabsPagerAdapter tabsPagerAdapter = new TabsPagerAdapter(this, getSupportFragmentManager());
        viewPager.setAdapter(tabsPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs_layout);
        tabLayout.setupWithViewPager(viewPager);

        // User Location
        LastLocationInfo lastInfoLocation = savedInstanceState == null ? null :
                (LastLocationInfo)savedInstanceState.getParcelable(UserCurrentLocation.LAST_LOC_INFO_KEY);
        mUserCurrentLocation = new UserCurrentLocation(this, lastInfoLocation,
                new UserCurrentLocation.OnLocationReadyListener() {
            @Override public void onLocationReady() {
                invalidateOptionsMenu();
            }
            @Override public void onPendingRequestHandled() {
                invalidateOptionsMenu();
            }
        });

        // Google's places' AutoComplete Widget
        mPlacesAutoComplete = new PlacesAutoComplete(this);

        // register local notification receiver
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        NearbyNotificationReceiver receiver = new NearbyNotificationReceiver(this, tabsPagerAdapter, viewPager);
        IntentFilter nearby = new IntentFilter(BroadcastHelper.ACTION_NEARBY_NOTIFY);
        IntentFilter details = new IntentFilter(BroadcastHelper.ACTION_FAVORITES_NOTIFY);
        localBroadcastManager.registerReceiver(receiver, nearby);
        localBroadcastManager.registerReceiver(receiver, details);

        // register global broadcast receiver
        mPowerConnectionReceiver = new PowerConnectionReceiver();
        registerReceiver(mPowerConnectionReceiver, new IntentFilter(Intent.ACTION_POWER_CONNECTED));
        registerReceiver(mPowerConnectionReceiver, new IntentFilter(Intent.ACTION_POWER_DISCONNECTED));

        // search
        Intent searchIntent = getIntent();
        if (Intent.ACTION_SEARCH.equals(searchIntent.getAction())) {
            String keyword = searchIntent.getStringExtra(SearchManager.QUERY);
            mUserCurrentLocation.getAndHandle(keyword);
        }

        // MainMenuHelper
        mMainMenuHelper = new MainMenuHelper(this, mUserCurrentLocation, mPlacesAutoComplete);
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        outState.putParcelable(UserCurrentLocation.LAST_LOC_INFO_KEY, mUserCurrentLocation.getLastLocationInfo());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mPowerConnectionReceiver); // stop alerting the user on app exit
        super.onDestroy();
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mMainMenuHelper.onPrepareOptionsMenu(menu); // handle toolbar icons disable/enable states
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMainMenuHelper.onCreateOptionsMenu(menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mMainMenuHelper.onOptionsItemSelected(item); // workflow majority starts here...
        return true;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        // google's place autocomplete widget support
        mPlacesAutoComplete.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode != LOCATION_REQUEST_CODE || grantResults[0] != PackageManager.PERMISSION_GRANTED)
            return;

        // runtime permission handling
        mUserCurrentLocation.startListening(LocationManager.GPS_PROVIDER);
        SharedPrefHelper sharedPrefHelper = new SharedPrefHelper(this);
        sharedPrefHelper.setPermissionDeniedByUser(false); // user granted permission
    }
}