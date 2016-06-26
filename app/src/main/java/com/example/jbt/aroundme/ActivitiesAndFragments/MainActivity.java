package com.example.jbt.aroundme.ActivitiesAndFragments;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.multidex.MultiDex;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.jbt.aroundme.Helpers.Utility;
import com.example.jbt.aroundme.R;
import com.example.jbt.aroundme.Services.NearbyService;
import com.example.jbt.aroundme.UIHelpers.*;


public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = "AroundMe";
    private PlacesAutoComplete mPlacesAutoComplete;
    private DrawerHandler mDrawerHandler;
    private MainMenuHelper mMainMenuHelper;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Utility.setContentViewWithLocaleChange(this, R.layout.activity_main, R.string.app_name);
        //setContentView(R.layout.activity_main);

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
        UserCurrentLocation mUserCurrentLocation = new UserCurrentLocation(this, new UserCurrentLocation.OnLocationReadyListener() {
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

        // FAB
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null )
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });

        // Drawer
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

        // register receiver
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        NearbyNotificationReceiver receiver = new NearbyNotificationReceiver(this, tabsPagerAdapter, viewPager);
        IntentFilter nearby = new IntentFilter(NearbyService.ACTION_NEARBY_NOTIFY);
        IntentFilter details = new IntentFilter(NearbyService.ACTION_FAVORITES_NOTIFY);
        localBroadcastManager.registerReceiver(receiver, nearby);
        localBroadcastManager.registerReceiver(receiver, details);

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
    public void onBackPressed() {

        if (mDrawerHandler.drawerClosingHandled())
            return;

        super.onBackPressed();
    }


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
}
