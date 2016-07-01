package com.example.jbt.aroundme.activities_fragments;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.jbt.aroundme.data.Place;
import com.example.jbt.aroundme.db.AroundMeDBHelper;
import com.example.jbt.aroundme.helpers.MapManipulation;
import com.example.jbt.aroundme.helpers.Utility;
import com.example.jbt.aroundme.R;
import com.example.jbt.aroundme.ui_helpers.TransitionsHelper;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

public class MapActivity extends AppCompatActivity {

    public static final String INTENT_MAP_ID_KEY = "id";
    public static final String INTENT_MAP_TYPE_KEY = "type";
    public static final String INTENT_MAP_TYPE_SEARCH_VAL = "search";
    public static final String INTENT_MAP_TYPE_FAVORITES_VAL = "favorites";

    private Place mPlace;
    private MapManipulation mMapManipulation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set shared-place transition exit
        TransitionsHelper.setMapActivityEnterTransition(this);


        Utility.setContentViewWithLocaleChange(this, R.layout.activity_map, R.string.map_name);

        Intent intent = getIntent();
        long id = intent.getLongExtra(INTENT_MAP_ID_KEY,-1);
        String type = intent.getStringExtra(INTENT_MAP_TYPE_KEY);

        AroundMeDBHelper dbHelper = new AroundMeDBHelper(this);
        mPlace = type.equals(INTENT_MAP_TYPE_SEARCH_VAL) ?
            dbHelper.searchGetPlace(id) :
            dbHelper.favoriteGetPlace(id);

        PlaceFragment placeFragment = (PlaceFragment)getSupportFragmentManager().findFragmentById(R.id.placeFrag);
        placeFragment.setPlace(mPlace);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFrag);

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMapManipulation = new MapManipulation(MapActivity.this, googleMap);
                mMapManipulation.Manipulate(mPlace);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_action_settings)
            startActivity(new Intent(this, SettingsActivity.class));

        return super.onOptionsItemSelected(item);
    }
}
