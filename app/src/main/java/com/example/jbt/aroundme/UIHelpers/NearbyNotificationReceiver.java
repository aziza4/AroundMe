package com.example.jbt.aroundme.UIHelpers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.jbt.aroundme.ActivitiesAndFragments.SearchFragment;
import com.example.jbt.aroundme.Services.NearbyService;


public class NearbyNotificationReceiver extends BroadcastReceiver {

    private AppCompatActivity mActivity;
    private TabsPagerAdapter mTabsPagerAdapter;

    public NearbyNotificationReceiver(AppCompatActivity activity, TabsPagerAdapter tabsPagerAdapter)
    {
        mActivity = activity;
        mTabsPagerAdapter = tabsPagerAdapter;
    }


    @Override
    public void onReceive(Context context, Intent intent) {

        int placesSaved = intent.getIntExtra(NearbyService.EXTRA_NEARBY_PLACES_SAVED, -1);

        if (placesSaved < 0)
            return;

        Toast.makeText(mActivity, placesSaved + " places received", Toast.LENGTH_SHORT).show();

        SearchFragment searchFragment = (SearchFragment) mTabsPagerAdapter.getRegisteredFragment(0);
        searchFragment.refresh();
    }
}
