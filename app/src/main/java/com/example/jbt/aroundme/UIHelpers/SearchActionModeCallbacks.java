package com.example.jbt.aroundme.UIHelpers;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.jbt.aroundme.Data.DetailsRequest;
import com.example.jbt.aroundme.Data.Place;
import com.example.jbt.aroundme.R;
import com.example.jbt.aroundme.Services.NearbyService;


public class SearchActionModeCallbacks implements ActionMode.Callback {

    private AppCompatActivity mActivity;
    private Place mPlace;

    public SearchActionModeCallbacks(AppCompatActivity activity, Place place)
    {
        mActivity = activity;
        mPlace = place;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.search_actionmode, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.favoritesMenuItem:

                Intent intent = new Intent(NearbyService.ACTION_PLACE_FAVORITES_SAVE, null, mActivity, NearbyService.class);
                intent.putExtra(NearbyService.EXTRA_PLACE_FAVORITES_DATA, new DetailsRequest(mPlace));
                intent.putExtra(NearbyService.EXTRA_PLACE_FAVORITES_ACTION_SAVE, true);
                mActivity.startService(intent);
                mode.finish();
                return true;

            default:
                return false;
        }
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) { }
}

