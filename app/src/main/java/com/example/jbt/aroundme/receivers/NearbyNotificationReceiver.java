package com.example.jbt.aroundme.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import com.example.jbt.aroundme.activities_fragments.FavoritesFragment;
import com.example.jbt.aroundme.activities_fragments.SearchFragment;
import com.example.jbt.aroundme.helpers.BroadcastHelper;
import com.example.jbt.aroundme.R;
import com.example.jbt.aroundme.ui_helpers.TabsPagerAdapter;


public class NearbyNotificationReceiver extends BroadcastReceiver {

    private final AppCompatActivity mActivity;
    private final TabsPagerAdapter mTabsPagerAdapter;
    private final ViewPager mViewPager;

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
            case BroadcastHelper.ACTION_NEARBY_NOTIFY:

                int placesSaved = intent.getIntExtra(BroadcastHelper.EXTRA_NEARBY_PLACES_SAVED, -1);

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

            case BroadcastHelper.ACTION_FAVORITES_NOTIFY:

                int detailsSaved = intent.getIntExtra(BroadcastHelper.EXTRA_FAVORITES_PLACE_SAVED, -1);
                int detailsRemoved = intent.getIntExtra(BroadcastHelper.EXTRA_FAVORITES_PLACE_REMOVED, -1);
                int detailsRemovedAll = intent.getIntExtra(BroadcastHelper.EXTRA_FAVORITES_PLACE_REMOVED_ALL, -1);

                if (detailsSaved < 0 && detailsRemoved < 0 && detailsRemovedAll < 0)
                    break;

                FavoritesFragment favoritesFragment = (FavoritesFragment) mTabsPagerAdapter
                        .getRegisteredFragment(TabsPagerAdapter.FAVORITES_TAB);

                favoritesFragment.refresh(mActivity);

                if (detailsSaved > 0)
                    mViewPager.setCurrentItem(TabsPagerAdapter.FAVORITES_TAB);
                break;
        }
    }
}
