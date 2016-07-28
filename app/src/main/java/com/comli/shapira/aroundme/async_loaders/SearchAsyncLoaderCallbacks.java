package com.comli.shapira.aroundme.async_loaders;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;

import com.comli.shapira.aroundme.adapters.SearchRecyclerAdapter;
import com.comli.shapira.aroundme.data.Place;
import com.comli.shapira.aroundme.db.AroundMeDBHelper;

import java.util.ArrayList;


public class SearchAsyncLoaderCallbacks implements LoaderManager.LoaderCallbacks<ArrayList<Place>>
{

    private final Context mContext;
    private final SearchRecyclerAdapter mSearchAdapter;
    private final AroundMeDBHelper mDbHelper;


    public SearchAsyncLoaderCallbacks(Context context, SearchRecyclerAdapter adapter)
    {
        mContext = context;
        mSearchAdapter = adapter;
        mDbHelper = new AroundMeDBHelper(mContext);
    }


    @Override
    public android.support.v4.content.Loader<ArrayList<Place>> onCreateLoader(int id, Bundle args) {

        return new AsyncTaskLoader<ArrayList<Place>>(mContext) {
            @Override
            public ArrayList<Place> loadInBackground() {
                return mDbHelper.searchGetArrayList();
            }
        };
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<ArrayList<Place>> loader, ArrayList<Place> data) {
        mSearchAdapter.setData(data);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<ArrayList<Place>> loader) {
        mSearchAdapter.clearData();
    }
}
