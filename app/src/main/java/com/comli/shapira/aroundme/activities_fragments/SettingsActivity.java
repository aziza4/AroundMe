package com.comli.shapira.aroundme.activities_fragments;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.comli.shapira.aroundme.helpers.Utility;
import com.comli.shapira.aroundme.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Utility.setContentViewWithLocaleChange(this,R.layout.activity_settings, R.string.settings_name);

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.container, new SettingsFragment())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent); // force mainActivity restart to support lang change
        finish();
    }
}
