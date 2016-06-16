package com.example.jbt.aroundme.UIHelpers;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;

import com.example.jbt.aroundme.Data.Place;
import com.example.jbt.aroundme.Helpers.AroundMeDBHelper;

import java.util.ArrayList;


public class NearbyAsyncLoaderCallbacks implements LoaderManager.LoaderCallbacks<ArrayList<Place>>
{

    private final Context mContext;
    private final PlaceRecyclerAdapter mAdapter;
    private final AroundMeDBHelper mDbHelper;


    public NearbyAsyncLoaderCallbacks(Context context, PlaceRecyclerAdapter adapter, AroundMeDBHelper dbHelper)
    {
        mContext = context;
        mAdapter = adapter;
        mDbHelper = dbHelper;
    }


    @Override
    public android.support.v4.content.Loader<ArrayList<Place>> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<ArrayList<Place>>(mContext) {
            @Override
            public ArrayList<Place> loadInBackground() {
                return mDbHelper.getPlacesArrayList();
            }
        };
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<ArrayList<Place>> loader, ArrayList<Place> data) {
        mAdapter.setData(data);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<ArrayList<Place>> loader) {
        mAdapter.clearData();
    }
}
