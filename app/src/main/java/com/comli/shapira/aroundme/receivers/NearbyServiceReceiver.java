package com.comli.shapira.aroundme.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.comli.shapira.aroundme.data.NearbyResponse;
import com.comli.shapira.aroundme.geoFencing.GeofenceAppHelper;
import com.comli.shapira.aroundme.helpers.BroadcastHelper;
import com.comli.shapira.aroundme.R;
import com.comli.shapira.aroundme.adapters.TabsPagerAdapter;


public class NearbyServiceReceiver extends BroadcastReceiver {

    private final AppCompatActivity mActivity;
    private final TabsPagerAdapter mTabsPagerAdapter;
    private final ViewPager mViewPager;
    private final GeofenceAppHelper mGeofenceAppHelper;

    public NearbyServiceReceiver(AppCompatActivity activity, TabsPagerAdapter tabsPagerAdapter,
                                 ViewPager viewPager, GeofenceAppHelper geofenceAppHelper)
    {
        mActivity = activity;
        mTabsPagerAdapter = tabsPagerAdapter;
        mViewPager = viewPager;
        mGeofenceAppHelper = geofenceAppHelper;
    }


    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        switch (action)
        {

            case BroadcastHelper.ACTION_NEARBY_NOTIFY:

                mTabsPagerAdapter.manageFragmentsOps(TabsPagerAdapter.REFRESH_SEARCH_VIEW); // update ui

                String status = intent.getStringExtra(BroadcastHelper.EXTRA_NEARBY_ERROR_MESSAGE);
                if ( status != null && !status.equals(NearbyResponse.STATUS_INVALID_REQUEST)) {
                    String message = mActivity.getString(R.string.server_error_message);
                    Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
                    break;
                }

                boolean clearList = intent.getBooleanExtra(BroadcastHelper.EXTRA_NEARBY_CLEAR_LIST, false);
                int placesSaved = intent.getIntExtra(BroadcastHelper.EXTRA_NEARBY_PLACES_SAVED, -1);

                if (!clearList) {

                    mTabsPagerAdapter.manageFragmentsOps(TabsPagerAdapter.REMOVE_SEARCH_PROGRESS_BAR);

                    if ( placesSaved < 0)
                        break; // no extra...

                    if ( placesSaved == 0) // must update the user on search's zero results
                        Toast.makeText(mActivity, mActivity.getString(R.string.msg_zero_results), Toast.LENGTH_SHORT).show();
                }

                mViewPager.setCurrentItem(TabsPagerAdapter.SEARCH_TAB); // verify user focus on search tab
                break;


            case BroadcastHelper.ACTION_FAVORITES_NOTIFY:

                int detailsSaved = intent.getIntExtra(BroadcastHelper.EXTRA_FAVORITES_PLACE_SAVED, -1);
                int detailsRemoved = intent.getIntExtra(BroadcastHelper.EXTRA_FAVORITES_PLACE_REMOVED, -1);
                int detailsRemovedAll = intent.getIntExtra(BroadcastHelper.EXTRA_FAVORITES_PLACE_REMOVED_ALL, -1);

                if (detailsSaved < 0 && detailsRemoved < 0 && detailsRemovedAll < 0)
                    break;

                mGeofenceAppHelper.refresh();

                mTabsPagerAdapter.manageFragmentsOps(TabsPagerAdapter.REFRESH_FAVORITES_VIEW); // update ui
                mTabsPagerAdapter.manageFragmentsOps(TabsPagerAdapter.REMOVE_SEARCH_PROGRESS_BAR);
                mTabsPagerAdapter.manageFragmentsOps(TabsPagerAdapter.REMOVE_FAVORITES_PROGRESS_BAR);

                if (detailsSaved > 0) // saved done
                    mViewPager.setCurrentItem(TabsPagerAdapter.FAVORITES_TAB); // verify user focus on favorites tab. only for add operation.

                if (detailsRemovedAll > 0) // remove all done
                    mViewPager.setCurrentItem(TabsPagerAdapter.SEARCH_TAB);

                break;
        }
    }
}
