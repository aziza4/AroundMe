package com.example.jbt.aroundme.UIHelpers;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.jbt.aroundme.ActivitiesAndFragments.FavoriteFragment;
import com.example.jbt.aroundme.R;
import com.example.jbt.aroundme.ActivitiesAndFragments.SearchFragment;

public class TabsPagerAdapter extends SmartFragmentStatePagerAdapter {

    private static final int NUM_ITEMS = 2;
    private final Context mContext;

    public TabsPagerAdapter(Context context, FragmentManager fragmentManager) {
        super(fragmentManager);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0: return SearchFragment.newInstance(0,  mContext.getString(R.string.search_title));
            case 1: return FavoriteFragment.newInstance(1, mContext.getString(R.string.favorite_title));
            default: return null;
        }
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        switch (position) {
            case 0: return mContext.getString(R.string.search_title);
            case 1: return mContext.getString(R.string.favorite_title);
            default: return null;
        }
    }
}

