package com.comli.shapira.aroundme.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.comli.shapira.aroundme.activities_fragments.FavoritesFragment;
import com.comli.shapira.aroundme.R;
import com.comli.shapira.aroundme.activities_fragments.SearchFragment;

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
    private boolean mSearchStarted = false;
    private boolean mFavoritesNeedRefresh = false;

    public TabsPagerAdapter(AppCompatActivity activity, FragmentManager fragmentManager) {

        super(fragmentManager);
        mActivity = activity;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case SEARCH_TAB:
                mSearchFragment = SearchFragment.newInstance();
                mSearchFragment.searchStarted(mSearchStarted);
                return mSearchFragment;

            case FAVORITES_TAB:
                mFavoritesFragment = FavoritesFragment.newInstance();
                mFavoritesFragment.needRefresh(mFavoritesNeedRefresh);
                return mFavoritesFragment;
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
                if (mSearchFragment == null)
                    mSearchStarted = true;
                else
                    mSearchFragment.addProgressBar();
                return;

            case REMOVE_SEARCH_PROGRESS_BAR:
                if (mSearchFragment != null)
                    mSearchFragment.removeProgressBar();
                return;

            case ADD_FAVORITES_PROGRESS_BAR:
                if (mFavoritesFragment != null)
                    mFavoritesFragment.addProgressBar();
                return;

            case REMOVE_FAVORITES_PROGRESS_BAR:
                if (mFavoritesFragment != null)
                    mFavoritesFragment.removeProgressBar();
                return;

            case REFRESH_SEARCH_VIEW:
                if (mSearchFragment != null)
                    mSearchFragment.refresh(mActivity);
                return;

            case REFRESH_FAVORITES_VIEW:
                if (mFavoritesFragment != null)
                    mFavoritesFragment.refresh(mActivity);
                else
                    mFavoritesNeedRefresh = true;
        }
    }
}

