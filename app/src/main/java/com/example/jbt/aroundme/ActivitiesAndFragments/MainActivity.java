package com.example.jbt.aroundme.ActivitiesAndFragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.multidex.MultiDex;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import com.example.jbt.aroundme.LocationProvider.*;
import com.example.jbt.aroundme.R;
import com.example.jbt.aroundme.UIHelpers.*;


public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = "AroundMe:";
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
        TabsPagerAdapter mTabsPagerAdapter = new TabsPagerAdapter(this, getSupportFragmentManager());
        ViewPager mViewPager = (ViewPager) findViewById(R.id.viewPagerContainer);
        if (mViewPager != null) {
            mViewPager.setAdapter(mTabsPagerAdapter);
            TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs_layout);
            if (tabLayout != null)
                tabLayout.setupWithViewPager(mViewPager);
        }

        // User Location
        LocationInterface mLocationProvider = new AndroidLocation(this);
        mUserCurrentLocation = new UserCurrentLocation(this, mLocationProvider);

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
        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView mNavigationView = (NavigationView) findViewById(R.id.nav_view);

        if (mDrawerLayout != null && mNavigationView != null) {

            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this,
                    mDrawerLayout,
                    toolbar,
                    R.string.navigation_drawer_open,
                    R.string.navigation_drawer_close);

            mDrawerLayout.addDrawerListener(toggle);
            toggle.syncState();
            mDrawerHandler = new DrawerHandler(mDrawerLayout);
            mNavigationView.setNavigationItemSelectedListener(mDrawerHandler);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        mUserCurrentLocation.start();
    }


    @Override
    protected void onStop() {
        mUserCurrentLocation.stop();
        super.onStop();
    }


    @Override
    public void onBackPressed() {

        if (mDrawerHandler.drawerClosingHandled())
            return;

        super.onBackPressed();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_settings:
                return true;

            case R.id.search_my_loc:
                mUserCurrentLocation.getAndHandle();
                return true;

            case R.id.search_places:
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
