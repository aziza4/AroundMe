package com.example.jbt.aroundme.ActivitiesAndFragments;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.jbt.aroundme.Helpers.Utility;
import com.example.jbt.aroundme.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Utility.setContentViewWithLocaleChange(this,R.layout.activity_settings, R.string.settings_name);

        getFragmentManager()
                .beginTransaction()
                .add(R.id.container, new SettingsFragment())
                .commit();
    }
}
