package com.comli.shapira.aroundme.action_mode_callbacks;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.comli.shapira.aroundme.activities_fragments.FavoritesFragment;
import com.comli.shapira.aroundme.data.DetailsRequest;
import com.comli.shapira.aroundme.data.Place;
import com.comli.shapira.aroundme.helpers.Utility;
import com.comli.shapira.aroundme.R;
import com.comli.shapira.aroundme.services.NearbyService;


public class FavoritesActionModeCallbacks implements ActionMode.Callback {

    private final AppCompatActivity mActivity;
    private final FavoritesFragment mFavoritesFragment;
    private final Place mPlace;

    public FavoritesActionModeCallbacks(AppCompatActivity activity, FavoritesFragment favoritesFragment, Place place)
    {
        mActivity = activity;
        mFavoritesFragment = favoritesFragment;
        mPlace = place;
    }


    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {

        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.favorites_actionmode, menu);

        // http://stackoverflow.com/questions/23513647/share-item-in-actionbar-using-contextual-actionbar
        Utility.setShareActionProviderForLocation(menu, mPlace);

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
            case R.id.deleteMenuItem:

                mFavoritesFragment.addProgressBar();

                // delete via service to make db this operation async as well, as others are.
                Intent intent = new Intent(NearbyService.ACTION_PLACE_FAVORITES_REMOVE, null, mActivity, NearbyService.class);
                intent.putExtra(NearbyService.EXTRA_PLACE_FAVORITES_DATA, new DetailsRequest(mPlace));
                intent.putExtra(NearbyService.EXTRA_PLACE_FAVORITES_ACTION_REMOVE, true);
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
