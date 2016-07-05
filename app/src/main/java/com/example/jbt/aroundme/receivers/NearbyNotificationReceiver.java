package com.example.jbt.aroundme.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import com.example.jbt.aroundme.activities_fragments.FavoritesFragment;
import com.example.jbt.aroundme.activities_fragments.SearchFragment;
import com.example.jbt.aroundme.data.NearbyResponse;
import com.example.jbt.aroundme.helpers.BroadcastHelper;
import com.example.jbt.aroundme.R;
import com.example.jbt.aroundme.adapters.TabsPagerAdapter;

// this is the (local) receiver that MainActivity holds to get and process the service various notifications
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

        SearchFragment searchFragment = (SearchFragment) mTabsPagerAdapter
                .getRegisteredFragment(TabsPagerAdapter.SEARCH_TAB);

        switch (action)
        {

            case BroadcastHelper.ACTION_NEARBY_NOTIFY:

                String status = intent.getStringExtra(BroadcastHelper.EXTRA_NEARBY_ERROR_MESSAGE);
                if ( status != null && !status.equals(NearbyResponse.STATUS_INVALID_REQUEST)) {
                    String message = mActivity.getString(R.string.server_error_message);
                    Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
                    searchFragment.removeProgressBar();
                    break;
                }

                boolean clearList = intent.getBooleanExtra(BroadcastHelper.EXTRA_NEARBY_CLEAR_LIST, false);

                int placesSaved = intent.getIntExtra(BroadcastHelper.EXTRA_NEARBY_PLACES_SAVED, -1);

                if (!clearList) {

                    if ( placesSaved < 0)
                        break; // no extra...

                    if ( placesSaved == 0) // must update the user on search's zero results
                        Toast.makeText(mActivity, mActivity.getString(R.string.msg_zero_results),
                                Toast.LENGTH_SHORT).show();
                }

                searchFragment.removeProgressBar();
                searchFragment.refresh(mActivity); // update UI
                mViewPager.setCurrentItem(TabsPagerAdapter.SEARCH_TAB); // verify user focus on search tab
                break;


            case BroadcastHelper.ACTION_FAVORITES_NOTIFY:

                int detailsSaved = intent.getIntExtra(BroadcastHelper.EXTRA_FAVORITES_PLACE_SAVED, -1);
                int detailsRemoved = intent.getIntExtra(BroadcastHelper.EXTRA_FAVORITES_PLACE_REMOVED, -1);
                int detailsRemovedAll = intent.getIntExtra(BroadcastHelper.EXTRA_FAVORITES_PLACE_REMOVED_ALL, -1);

                if (detailsSaved < 0 && detailsRemoved < 0 && detailsRemovedAll < 0)
                    break;

                FavoritesFragment favoritesFragment = (FavoritesFragment) mTabsPagerAdapter
                        .getRegisteredFragment(TabsPagerAdapter.FAVORITES_TAB);

                favoritesFragment.refresh(mActivity); // update UI

                if (detailsSaved > 0) { // saved done
                    searchFragment.removeProgressBar();
                    mViewPager.setCurrentItem(TabsPagerAdapter.FAVORITES_TAB); // verify user focus on favorites tab. only for add operation.
                }
                break;
        }
    }
}
