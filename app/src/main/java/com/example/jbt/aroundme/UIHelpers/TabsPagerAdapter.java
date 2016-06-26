package com.example.jbt.aroundme.UIHelpers;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.example.jbt.aroundme.ActivitiesAndFragments.FavoritesFragment;
import com.example.jbt.aroundme.R;
import com.example.jbt.aroundme.ActivitiesAndFragments.SearchFragment;

public class TabsPagerAdapter extends SmartFragmentStatePagerAdapter {

    public static final int SEARCH_TAB = 0;
    public static final int FAVORITES_TAB = 1;
    private static final int NUM_ITEMS = 2;
    private final Context mContext;

    public TabsPagerAdapter(Context context, FragmentManager fragmentManager) {

        super(fragmentManager);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case SEARCH_TAB: return SearchFragment.newInstance(0,  mContext.getString(R.string.search_title));

            case FAVORITES_TAB: return FavoritesFragment.newInstance(1, mContext.getString(R.string.favorite_title));
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
            case SEARCH_TAB: return mContext.getString(R.string.search_title);
            case FAVORITES_TAB: return mContext.getString(R.string.favorite_title);
            default: return null;
        }
    }
}

