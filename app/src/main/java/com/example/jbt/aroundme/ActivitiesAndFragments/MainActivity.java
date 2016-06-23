package com.example.jbt.aroundme.ActivitiesAndFragments;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.multidex.MultiDex;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.jbt.aroundme.Helpers.ImageHelper;
import com.example.jbt.aroundme.LocationProvider.*;
import com.example.jbt.aroundme.R;
import com.example.jbt.aroundme.Services.NearbyService;
import com.example.jbt.aroundme.UIHelpers.*;


public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = "AroundMe";
    private UserCurrentLocation mUserCurrentLocation;
    private PlacesAutoComplete mPlacesAutoComplete;
    private DrawerHandler mDrawerHandler;


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Tabs
        TabsPagerAdapter tabsPagerAdapter = new TabsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPagerContainer);
        viewPager.setAdapter(tabsPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs_layout);
        tabLayout.setupWithViewPager(viewPager);

        // User Location
        LocationInterface locationProvider = new AndroidLocation(this);
        mUserCurrentLocation = new UserCurrentLocation(this, locationProvider,
                new UserCurrentLocation.OnLocationReadyListener() {
            @Override public void onLocationReady() { invalidateOptionsMenu(); }
            @Override public void onPendingRequestHandled() { invalidateOptionsMenu(); }
        });

        mUserCurrentLocation.start();

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
        NearbyNotificationReceiver receiver = new NearbyNotificationReceiver(this, viewPager, tabsPagerAdapter);
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
    }


    @Override
    protected void onDestroy() {
        mUserCurrentLocation.stop();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {

        if (mDrawerHandler.drawerClosingHandled())
            return;

        super.onBackPressed();
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        boolean enabled = mUserCurrentLocation.ready();
        Drawable myLocIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_action_search_by_keyword, null);
        Drawable byKeywordIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_action_search_by_keyword, null);
        ImageHelper.setDrawableEnableDisableColor(myLocIcon, enabled);
        ImageHelper.setDrawableEnableDisableColor(byKeywordIcon, enabled);

        MenuItem myLocation = menu.findItem(R.id.menu_search_by_keyword);
        MenuItem byKeyword = menu.findItem(R.id.menu_search_by_keyword);
        myLocation.setEnabled(enabled);
        byKeyword.setEnabled(enabled);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);

        // Todo: gray icon until location updates are available. Also make search by keyword work
        // http://stackoverflow.com/questions/9642990/is-it-possible-to-grey-out-not-just-disable-a-menuitem-in-android

        SearchView searchView = (SearchView)menu.findItem(R.id.menu_search_by_keyword).getActionView();
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.menu_action_settings:
                return true;

            case R.id.menu_search_my_loc:
                mUserCurrentLocation.getAndHandle("");
                return true;

            case R.id.menu_search_places:
                mPlacesAutoComplete.start();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        mPlacesAutoComplete.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
}
