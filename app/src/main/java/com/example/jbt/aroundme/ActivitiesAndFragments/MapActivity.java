package com.example.jbt.aroundme.ActivitiesAndFragments;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.jbt.aroundme.Data.Place;
import com.example.jbt.aroundme.Helpers.Utility;
import com.example.jbt.aroundme.R;

public class MapActivity extends AppCompatActivity {

    public static final String INTENT_MAP_PLACE_KEY = "place";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.setContentViewWithLocaleChange(this, R.layout.activity_map, R.string.map_name);

        Intent intent = getIntent();
        Place place = intent.getParcelableExtra(INTENT_MAP_PLACE_KEY);

        PlaceFragment placeFragment = (PlaceFragment)getSupportFragmentManager().findFragmentById(R.id.placeFrag);
        placeFragment.setPlace(place);
    }
}
