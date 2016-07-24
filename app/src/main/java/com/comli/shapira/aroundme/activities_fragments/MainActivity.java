package com.comli.shapira.aroundme.activities_fragments;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.multidex.MultiDex;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.comli.shapira.aroundme.adapters.TabsPagerAdapter;
import com.comli.shapira.aroundme.data.LastLocationInfo;
import com.comli.shapira.aroundme.helpers.LocationServiceHelper;
import com.comli.shapira.aroundme.helpers.ReceiversHelper;
import com.comli.shapira.aroundme.helpers.SharedPrefHelper;
import com.comli.shapira.aroundme.helpers.Utility;
import com.comli.shapira.aroundme.R;
import com.comli.shapira.aroundme.receivers.LocationProviderServiceReceiver;
import com.comli.shapira.aroundme.receivers.NearbyServiceReceiver;
import com.comli.shapira.aroundme.receivers.PowerConnectionReceiver;
import com.comli.shapira.aroundme.ui_helpers.*;


public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = "AroundMe";
    public static final int LOCATION_REQUEST_CODE = 1;


    private PlacesAutoComplete mPlacesAutoComplete;
    private MainMenuHelper mMainMenuHelper;
    private UserCurrentLocation mUserCurrentLocation;
    private LocationServiceHelper mLocationServiceHelper;
    private ReceiversHelper mReceiversHelper;

    private SharedPrefHelper mSharedPrefHelper;
    private boolean mLangChanged;
    private String mSearchKeyWord;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mSharedPrefHelper = new SharedPrefHelper(this);
        mLangChanged = mSharedPrefHelper.isLangChanged();
        mSharedPrefHelper.setLangChanged(false);

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

        // search
        mSearchKeyWord = "";
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction()))
            mSearchKeyWord = intent.getStringExtra(SearchManager.QUERY);

        // LocationServiceHelper
        LastLocationInfo lastInfoLocation = savedInstanceState == null ? null :
                (LastLocationInfo)savedInstanceState.getParcelable(LocationServiceHelper.LAST_LOC_INFO_KEY);
        mLocationServiceHelper = new LocationServiceHelper(this, lastInfoLocation, new LocationServiceHelper.OnLocationReadyListener() {
            @Override public void onLocationReady() { invalidateOptionsMenu(); } });

        // UserCurrentLocation
        mUserCurrentLocation = new UserCurrentLocation(this, mLocationServiceHelper);

        // Google's places' AutoComplete Widget
        mPlacesAutoComplete = new PlacesAutoComplete(this);

        // create local notification receiver (register later on onResume)
        NearbyServiceReceiver nearbyServiceReceiver = new NearbyServiceReceiver(this, tabsPagerAdapter, viewPager);
        LocationProviderServiceReceiver locationProviderServiceReceiver = new LocationProviderServiceReceiver(mLocationServiceHelper);

        // create global receiver (register later on onResume)
        PowerConnectionReceiver powerConnectionReceiver = new PowerConnectionReceiver();
        mReceiversHelper = new ReceiversHelper(this, nearbyServiceReceiver, locationProviderServiceReceiver, powerConnectionReceiver);
        mReceiversHelper.registerGlobalReceivers();

        // MainMenuHelper
        mMainMenuHelper = new MainMenuHelper(this, mLocationServiceHelper, mUserCurrentLocation, mPlacesAutoComplete);
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        boolean isDialogOpen = mLocationServiceHelper.isAlertDialogOpen();
        Location lastLocation = mLocationServiceHelper.getLastLocation();
        LastLocationInfo info = new LastLocationInfo(lastLocation, isDialogOpen);

        outState.putParcelable(LocationServiceHelper.LAST_LOC_INFO_KEY, info);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mReceiversHelper.registerLocalReceivers();
        mLocationServiceHelper.startService();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mLangChanged) // renew previous search upon language change
        {
            String keyword = mSharedPrefHelper.getSearchKeyword();
            mUserCurrentLocation.searchCurrentLocation(keyword);
            mSharedPrefHelper.setSearchKeyword("");
            mLangChanged = false;
        }

        if (!mSearchKeyWord.isEmpty())  // search with new search keyword
        {
            mUserCurrentLocation.searchCurrentLocation(mSearchKeyWord);
            mSharedPrefHelper.setSearchKeyword(mSearchKeyWord);
            mSearchKeyWord = "";
        }
    }

    @Override
    protected void onStop() {
        mReceiversHelper.unRegisterLocalReceivers();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mLocationServiceHelper.dismissDialogIfOpen();
        mReceiversHelper.unRegisterGlobalReceivers();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mSharedPrefHelper.onUserLeaveApplication();
        mLocationServiceHelper.stopService();
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
        mLocationServiceHelper.onRuntimePermissionGrant();
        mSharedPrefHelper.setPermissionDeniedByUser(false); // user granted permission
    }
}
