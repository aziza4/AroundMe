package com.example.jbt.aroundme.ActivitiesAndFragments;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.jbt.aroundme.Data.Place;
import com.example.jbt.aroundme.Helpers.AroundMeDBHelper;
import com.example.jbt.aroundme.Helpers.Utility;
import com.example.jbt.aroundme.R;

public class MapActivity extends AppCompatActivity {

    public static final String INTENT_MAP_ID_KEY = "id";
    public static final String INTENT_MAP_TYPE_KEY = "type";
    public static final String INTENT_MAP_TYPE_SEARCH_VAL = "search";
    public static final String INTENT_MAP_TYPE_FAVORITES_VAL = "favorites";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.setContentViewWithLocaleChange(this, R.layout.activity_map, R.string.map_name);

        Intent intent = getIntent();
        long id = intent.getLongExtra(INTENT_MAP_ID_KEY,-1);
        String type = intent.getStringExtra(INTENT_MAP_TYPE_KEY);

        AroundMeDBHelper dbHelper = new AroundMeDBHelper(this);
        Place place = type.equals(INTENT_MAP_TYPE_SEARCH_VAL) ?
            dbHelper.searchGetPlace(id) :
            dbHelper.favoriteGetPlace(id);

        PlaceFragment placeFragment = (PlaceFragment)getSupportFragmentManager().findFragmentById(R.id.placeFrag);
        placeFragment.setPlace(place);
    }
}