package com.example.jbt.aroundme.UIHelpers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import com.example.jbt.aroundme.ActivitiesAndFragments.FavoritesFragment;
import com.example.jbt.aroundme.ActivitiesAndFragments.SearchFragment;
import com.example.jbt.aroundme.R;
import com.example.jbt.aroundme.Services.NearbyService;


public class NearbyNotificationReceiver extends BroadcastReceiver {

    private final AppCompatActivity mActivity;
    private final TabsPagerAdapter mTabsPagerAdapter;
    private ViewPager mViewPager;

    public NearbyNotificationReceiver(AppCompatActivity activity, TabsPagerAdapter tabsPagerAdapter, ViewPager viewPager)
    {
        mActivity = activity;
        mTabsPagerAdapter = tabsPagerAdapter;
        mViewPager = viewPager;
    }


    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        switch (action)
        {
            case NearbyService.ACTION_NEARBY_NOTIFY:
                int placesSaved = intent.getIntExtra(NearbyService.EXTRA_NEARBY_PLACES_SAVED, -1);

                if (placesSaved < 0 )
                    break;

                if (placesSaved == 0)
                    Toast.makeText(mActivity, mActivity.getString(R.string.msg_zero_results),
                            Toast.LENGTH_SHORT).show();

                SearchFragment searchFragment = (SearchFragment) mTabsPagerAdapter
                        .getRegisteredFragment(TabsPagerAdapter.SEARCH_TAB);

                searchFragment.refresh(mActivity);
                mViewPager.setCurrentItem(TabsPagerAdapter.SEARCH_TAB);
                break;

            case NearbyService.ACTION_FAVORITES_NOTIFY:
                int detailsSaved = intent.getIntExtra(NearbyService.EXTRA_FAVORITES_PLACE_SAVED, -1);
                int detailsRemoved = intent.getIntExtra(NearbyService.EXTRA_FAVORITES_PLACE_REMOVED, -1);
                int detailsRemovedAll = intent.getIntExtra(NearbyService.EXTRA_FAVORITES_PLACE_REMOVED_ALL, -1);

                if (detailsSaved < 0 && detailsRemoved < 0 && detailsRemovedAll < 0)
                    break;

                FavoritesFragment favoritesFragment = (FavoritesFragment) mTabsPagerAdapter
                        .getRegisteredFragment(TabsPagerAdapter.FAVORITES_TAB);

                favoritesFragment.refresh(mActivity);
                mViewPager.setCurrentItem(TabsPagerAdapter.FAVORITES_TAB);
                break;
        }
    }
}
