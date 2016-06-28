package com.example.jbt.aroundme.activities_fragments;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.multidex.MultiDex;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.jbt.aroundme.helpers.BroadcastHelper;
import com.example.jbt.aroundme.helpers.SharedPrefHelper;
import com.example.jbt.aroundme.helpers.Utility;
import com.example.jbt.aroundme.R;
import com.example.jbt.aroundme.receivers.NearbyNotificationReceiver;
import com.example.jbt.aroundme.receivers.PowerConnectionReceiver;
import com.example.jbt.aroundme.ui_helpers.*;


public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = "AroundMe";
    public static final int LOCATION_REQUEST_CODE = 1;

    private PlacesAutoComplete mPlacesAutoComplete;
    private MainMenuHelper mMainMenuHelper;
    private UserCurrentLocation mUserCurrentLocation;
    PowerConnectionReceiver mPowerConnectionReceiver;
    /*
    private DrawerHandler mDrawerHandler;
    */

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Utility.setContentViewWithLocaleChange(this, R.layout.activity_main, R.string.app_name);

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Utility.resetTitle(this, R.string.app_name);


        // Tabs
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPagerContainer);
        TabsPagerAdapter tabsPagerAdapter = new TabsPagerAdapter(this, getSupportFragmentManager());
        viewPager.setAdapter(tabsPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs_layout);
        tabLayout.setupWithViewPager(viewPager);

        // User Location
        mUserCurrentLocation = new UserCurrentLocation(this, new UserCurrentLocation.OnLocationReadyListener() {
            @Override
            public void onLocationReady() {
                invalidateOptionsMenu();
            }

            @Override
            public void onPendingRequestHandled() {
                invalidateOptionsMenu();
            }
        });

        // Places AutoComplete Widget
        mPlacesAutoComplete = new PlacesAutoComplete(this);

        // Drawer - no time for implementing this... later...
        /*
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        if (drawerLayout != null && navigationView != null) {

            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this,
                    drawerLayout,
                    toolbar,
                    R.string.navigation_drawer_open,
                    R.string.navigation_drawer_close);

            drawerLayout.addDrawerListener(toggle);
            toggle.syncState();
            mDrawerHandler = new DrawerHandler(drawerLayout);
            navigationView.setNavigationItemSelectedListener(mDrawerHandler);
        }
        */

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
    protected void onDestroy() {
        unregisterReceiver(mPowerConnectionReceiver);
        super.onDestroy();
    }

    /*
    @Override
    public void onBackPressed() {

        if (mDrawerHandler.drawerClosingHandled())
            return;

        super.onBackPressed();
    }
    */


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mMainMenuHelper.onPrepareOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMainMenuHelper.onCreateOptionsMenu(menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mMainMenuHelper.onOptionsItemSelected(item);
        return true;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        mPlacesAutoComplete.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_REQUEST_CODE ) {

            SharedPrefHelper sharedPrefHelper = new SharedPrefHelper(this);

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mUserCurrentLocation.startListening(LocationManager.GPS_PROVIDER);
                sharedPrefHelper.setPermissionDeniedByUser(false);
            } else {
                sharedPrefHelper.setPermissionDeniedByUser(true);
            }
        }
    }
}
