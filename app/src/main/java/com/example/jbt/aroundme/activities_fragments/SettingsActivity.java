package com.example.jbt.aroundme.activities_fragments;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.jbt.aroundme.helpers.Utility;
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
