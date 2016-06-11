package com.example.jbt.aroundme;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.jbt.aroundme.LocationProvider.AndroidLocation;
import com.example.jbt.aroundme.LocationProvider.LocationInterface;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "PlaceSelectionListener";
    private static final LatLngBounds BOUNDS_HOME_VIEW =
            new LatLngBounds(
                    new LatLng(32.081541, 34.804215),
                    new LatLng(32.089150, 34.816542));

    public static final int REQUEST_SELECT_PLACE = 1000;

    private DrawerLayout mDrawer;

    private LocationInterface mLocationProvider;

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
        mLocationProvider = new AndroidLocation(this);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null )
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        if (mNavigationView != null)
            mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

                @SuppressWarnings("StatementWithEmptyBody")
                @Override
                public boolean onNavigationItemSelected(MenuItem item) {
                    // Handle navigation view item clicks here.
                    int id = item.getItemId();

                    if (id == R.id.nav_camera) {
                        // Handle the camera action
                    } else if (id == R.id.nav_gallery) {

                    } else if (id == R.id.nav_slideshow) {

                    } else if (id == R.id.nav_manage) {

                    } else if (id == R.id.nav_share) {

                    } else if (id == R.id.nav_send) {

                    }

                    mDrawer.closeDrawer(GravityCompat.START);
                    return true;
                }
            });
        }

    @Override
    protected void onStart() {
        super.onStart();
        mLocationProvider.start();
    }

    @Override
    protected void onStop() {
        mLocationProvider.stop();
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
                Location location = mLocationProvider.GetCurrentLocation();
                if (location != null) {
                    String info = "Lat: " + location.getLatitude() + "\nLng: " + location.getLongitude();
                    Toast.makeText(MainActivity.this, info, Toast.LENGTH_SHORT).show();
                }
                return true;

            case R.id.search_places:

                // Place Autocomplete widget
                try {
                    Intent intent = new PlaceAutocomplete
                            .IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .setBoundsBias(BOUNDS_HOME_VIEW)
                            .build(MainActivity.this);

                    startActivityForResult(intent, REQUEST_SELECT_PLACE);

                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    Toast.makeText(MainActivity.this, "PlaceAutocomplete failed", Toast.LENGTH_SHORT).show();
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_SELECT_PLACE) {

            if (resultCode == RESULT_OK) {

                Place place = PlaceAutocomplete.getPlace(this, data);
                String info = getString(R.string.formatted_place_data,
                        place.getName(),
                        place.getAddress(),
                        place.getPhoneNumber(),
                        place.getWebsiteUri(),
                        place.getRating(),
                        place.getLatLng() != null ? place.getLatLng().latitude : "",
                        place.getLatLng() != null ? place.getLatLng().longitude : "",
                        place.getId());
                Toast.makeText(this, info, Toast.LENGTH_LONG).show();

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {

                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.e(LOG_TAG, "onError: Status = " + status.toString());
                Toast.makeText(MainActivity.this,
                        "Place selection failed: " + status.getStatusMessage(),
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }
}
