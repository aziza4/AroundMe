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

    private AppCompatActivity mActivity;
    private ViewPager mViewPager;
    private TabsPagerAdapter mTabsPagerAdapter;

    public NearbyNotificationReceiver(AppCompatActivity activity, ViewPager viewPager, TabsPagerAdapter tabsPagerAdapter)
    {
        mActivity = activity;
        mViewPager = viewPager;
        mTabsPagerAdapter = tabsPagerAdapter;
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

                mViewPager.setCurrentItem(TabsPagerAdapter.SEARCH_TAB);

                if (placesSaved == 0)
                    Toast.makeText(mActivity, mActivity.getString(R.string.msg_zero_results),
                            Toast.LENGTH_SHORT).show();

                SearchFragment searchFragment = (SearchFragment) mTabsPagerAdapter
                        .getRegisteredFragment(TabsPagerAdapter.SEARCH_TAB);

                searchFragment.refresh(mActivity);
                break;

            case NearbyService.ACTION_FAVORITES_NOTIFY:
                int detailsSaved = intent.getIntExtra(NearbyService.EXTRA_FAVORITES_PLACE_SAVED, -1);
                int detailsremoved = intent.getIntExtra(NearbyService.EXTRA_FAVORITES_PLACE_REMOVED, -1);
                int detailsremovedAll = intent.getIntExtra(NearbyService.EXTRA_FAVORITES_PLACE_REMOVED_ALL, -1);

                if (detailsSaved < 0 && detailsremoved < 0 && detailsremovedAll < 0)
                    break;

                mViewPager.setCurrentItem(TabsPagerAdapter.FAVORITES_TAB);

                FavoritesFragment favoritesFragment = (FavoritesFragment) mTabsPagerAdapter
                        .getRegisteredFragment(TabsPagerAdapter.FAVORITES_TAB);

                favoritesFragment.refresh(mActivity);
                break;
        }
    }
}
