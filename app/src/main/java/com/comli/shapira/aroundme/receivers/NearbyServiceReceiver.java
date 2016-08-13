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
            case BroadcastHelper.ACTION_NEARBY_NOTIFY_PLACES_SAVED:
            case BroadcastHelper.ACTION_NEARBY_NOTIFY_PLACES_ERROR_MESSAGE:

                mTabsPagerAdapter.manageFragmentsOps(TabsPagerAdapter.REFRESH_SEARCH_VIEW); // update ui
                mTabsPagerAdapter.manageFragmentsOps(TabsPagerAdapter.REMOVE_SEARCH_PROGRESS_BAR);

                String status = intent.getStringExtra(BroadcastHelper.EXTRA_NEARBY_ERROR_MESSAGE);
                if ( status != null && !status.equals(NearbyResponse.STATUS_INVALID_REQUEST)) {
                    String message = mActivity.getString(R.string.server_error_message);
                    Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
                    break;
                }

                int placesSaved = intent.getIntExtra(BroadcastHelper.EXTRA_NEARBY_PLACES_SAVED, -1);

                if ( placesSaved < 0)
                    break; // no extra...

                if ( placesSaved == 0) // must update the user on search's zero results
                    Toast.makeText(mActivity, mActivity.getString(R.string.msg_zero_results), Toast.LENGTH_SHORT).show();

                mViewPager.setCurrentItem(TabsPagerAdapter.SEARCH_TAB); // verify user focus on search tab
                break;


            case BroadcastHelper.ACTION_NEARBY_NOTIFY_PLACES_REMOVED:

                mTabsPagerAdapter.manageFragmentsOps(TabsPagerAdapter.REFRESH_SEARCH_VIEW); // update ui
                mViewPager.setCurrentItem(TabsPagerAdapter.SEARCH_TAB); // verify user focus on search tab
                break;


            case BroadcastHelper.ACTION_FAVORITES_NOTIFY_PLACE_SAVED:
            case BroadcastHelper.ACTION_FAVORITES_NOTIFY_PLACE_REMOVED:
            case BroadcastHelper.ACTION_FAVORITES_NOTIFY_ALL_PLACES_REMOVED:

                mGeofenceAppHelper.refresh();


                mTabsPagerAdapter.manageFragmentsOps(TabsPagerAdapter.REFRESH_FAVORITES_VIEW); // update ui
                mTabsPagerAdapter.manageFragmentsOps(TabsPagerAdapter.REMOVE_SEARCH_PROGRESS_BAR);
                mTabsPagerAdapter.manageFragmentsOps(TabsPagerAdapter.REMOVE_FAVORITES_PROGRESS_BAR);

                if (action.equals(BroadcastHelper.ACTION_FAVORITES_NOTIFY_PLACE_SAVED))
                    mViewPager.setCurrentItem(TabsPagerAdapter.FAVORITES_TAB);

                if (action.equals(BroadcastHelper.ACTION_FAVORITES_NOTIFY_ALL_PLACES_REMOVED))
                    mViewPager.setCurrentItem(TabsPagerAdapter.SEARCH_TAB);

                break;
        }
    }
}
