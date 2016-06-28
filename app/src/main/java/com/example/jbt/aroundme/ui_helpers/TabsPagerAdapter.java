package com.example.jbt.aroundme.ui_helpers;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.example.jbt.aroundme.activities_fragments.FavoritesFragment;
import com.example.jbt.aroundme.R;
import com.example.jbt.aroundme.activities_fragments.SearchFragment;

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
            case SEARCH_TAB: return SearchFragment.newInstance();
            case FAVORITES_TAB: return FavoritesFragment.newInstance();
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

