package com.comli.shapira.aroundme.activities_fragments;


import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.comli.shapira.aroundme.R;
import com.comli.shapira.aroundme.helpers.BroadcastHelper;
import com.comli.shapira.aroundme.receivers.SearchFragBroadcastReceiver;
import com.comli.shapira.aroundme.async_loaders.SearchAsyncLoaderCallbacks;
import com.comli.shapira.aroundme.adapters.SearchRecyclerAdapter;


public class SearchFragment extends Fragment {

    private static final int SEARCH_LOADER_ID = 1;

    private SearchFragBroadcastReceiver mReceiver;
    private LocalBroadcastManager mLocalBroadcastManager;
    private ProgressBar mProgressBar;
    private SearchAsyncLoaderCallbacks mSearchLoaderCallbacks;


    public static SearchFragment newInstance() {
        return new SearchFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_search, container, false);

        // downloading progressbar
        mProgressBar = (ProgressBar) v.findViewById(R.id.downloadProgressBar);

        // register receiver for both search-start and add-place-to-favorite actions
        mReceiver = new SearchFragBroadcastReceiver(mProgressBar);
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(getActivity());
        IntentFilter searchStartedIntent = new IntentFilter(BroadcastHelper.ACTION_SEARCH_OP_STARTED);
        mLocalBroadcastManager.registerReceiver(mReceiver, searchStartedIntent);
        IntentFilter addFavoritesIntent = new IntentFilter(BroadcastHelper.ACTION_ADD_FAVORITE_OP_STARTED);
        mLocalBroadcastManager.registerReceiver(mReceiver, addFavoritesIntent);


        // user recycler-adapter with async-loader
        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.searchListView);
        SearchRecyclerAdapter searchAdapter = new SearchRecyclerAdapter(getActivity());
        recyclerView.setAdapter(searchAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mSearchLoaderCallbacks = new SearchAsyncLoaderCallbacks(getActivity(), this, searchAdapter);
        getActivity().getSupportLoaderManager()
                .initLoader(SEARCH_LOADER_ID, null, mSearchLoaderCallbacks)
                .forceLoad(); // see: http://stackoverflow.com/questions/10524667/android-asynctaskloader-doesnt-start-loadinbackground

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onDestroy() {
        mLocalBroadcastManager.unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    public void removeProgressBar()
    {
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    public void refresh(AppCompatActivity activity) // called when service finished downloading
    {
        activity.getSupportLoaderManager()
                .restartLoader(SEARCH_LOADER_ID, null, mSearchLoaderCallbacks)
                .forceLoad();
    }

}
