package com.comli.shapira.aroundme.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.comli.shapira.aroundme.activities_fragments.FavoritesFragment;
import com.comli.shapira.aroundme.R;
import com.comli.shapira.aroundme.activities_fragments.SearchFragment;

import java.util.ArrayList;

public class TabsPagerAdapter extends SmartFragmentStatePagerAdapter { // standard implementation

    public static final int SEARCH_TAB = 0;
    public static final int FAVORITES_TAB = 1;
    private static final int NUM_ITEMS = 2;

    public static final int ADD_SEARCH_PROGRESS_BAR = 0;
    public static final int ADD_FAVORITES_PROGRESS_BAR = 1;
    public static final int REMOVE_SEARCH_PROGRESS_BAR = 2;
    public static final int REMOVE_FAVORITES_PROGRESS_BAR = 3;
    public static final int REFRESH_SEARCH_VIEW = 4;
    public static final int REFRESH_FAVORITES_VIEW = 5;

    private final AppCompatActivity mActivity;
    private SearchFragment mSearchFragment;
    private FavoritesFragment mFavoritesFragment;

    public TabsPagerAdapter(AppCompatActivity activity, FragmentManager fragmentManager) {
        super(fragmentManager);

        mActivity = activity;
        mSearchFragment = SearchFragment.newInstance();
        mFavoritesFragment = FavoritesFragment.newInstance();
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case SEARCH_TAB: return mSearchFragment;
            case FAVORITES_TAB: return mFavoritesFragment;
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
            case SEARCH_TAB: return mActivity.getString(R.string.search_title);
            case FAVORITES_TAB: return mActivity.getString(R.string.favorite_title);
            default: return null;
        }
    }

    public void manageFragmentsOps(int op) {

        switch (op)
        {
            case ADD_SEARCH_PROGRESS_BAR:
                mSearchFragment.addProgressBar();
                break;

            case REMOVE_SEARCH_PROGRESS_BAR:
                mSearchFragment.removeProgressBar();
                break;

            case ADD_FAVORITES_PROGRESS_BAR:
                mFavoritesFragment.addProgressBar();
                break;

            case REMOVE_FAVORITES_PROGRESS_BAR:
                mFavoritesFragment.removeProgressBar();
                break;

            case REFRESH_SEARCH_VIEW:
                mSearchFragment.refresh(mActivity);
                break;

            case REFRESH_FAVORITES_VIEW:
                mFavoritesFragment.refresh(mActivity);
                break;
        }
    }
}

