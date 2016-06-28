package com.example.jbt.aroundme.logic;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;

import com.example.jbt.aroundme.data.Place;
import com.example.jbt.aroundme.helpers.AroundMeDBHelper;

import java.util.ArrayList;


public class FavoritesAsyncLoaderCallbacks implements LoaderManager.LoaderCallbacks<ArrayList<Place>>
{

    private final Context mContext;
    private final FavoritesRecyclerAdapter mFavoritesAdapter;
    private final AroundMeDBHelper mDbHelper;


    public FavoritesAsyncLoaderCallbacks(Context context, FavoritesRecyclerAdapter adapter)
    {
        mContext = context;
        mFavoritesAdapter = adapter;
        mDbHelper = new AroundMeDBHelper(mContext);
    }


    @Override
    public android.support.v4.content.Loader<ArrayList<Place>> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<ArrayList<Place>>(mContext) {
            @Override
            public ArrayList<Place> loadInBackground() {
                return mDbHelper.favoritesGetArrayList();
            }
        };
    }


    @Override
    public void onLoadFinished(android.support.v4.content.Loader<ArrayList<Place>> loader, ArrayList<Place> data) {
        mFavoritesAdapter.setData(data);
    }


    @Override
    public void onLoaderReset(android.support.v4.content.Loader<ArrayList<Place>> loader) {
        mFavoritesAdapter.clearData();
    }
}
