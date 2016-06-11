package com.example.jbt.aroundme.UIHelpers;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.jbt.aroundme.ActivitiesAndFragments.FavoriteFragment;
import com.example.jbt.aroundme.R;
import com.example.jbt.aroundme.ActivitiesAndFragments.SearchFragment;

public class TabsPagerAdapter extends FragmentPagerAdapter {

    private final Context mContext;

    public TabsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0: return new SearchFragment();
            case 1: return new FavoriteFragment();
        }

        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        switch (position) {
            case 0: return mContext.getString(R.string.search_title);
            case 1: return mContext.getString(R.string.favorite_title);
        }
        return null;
    }
}

