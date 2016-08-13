package com.comli.shapira.aroundme.activities_fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.comli.shapira.aroundme.R;
import com.comli.shapira.aroundme.async_loaders.SearchAsyncLoaderCallbacks;
import com.comli.shapira.aroundme.adapters.SearchRecyclerAdapter;


public class SearchFragment extends Fragment {

    private static final int SEARCH_LOADER_ID = 1;

    private ProgressBar mProgressBar;
    private boolean mShowProgressBar = false;

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

        // user recycler-adapter with async-loader
        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.searchListView);
        SearchRecyclerAdapter searchAdapter = new SearchRecyclerAdapter(getActivity(), this);
        recyclerView.setAdapter(searchAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mSearchLoaderCallbacks = new SearchAsyncLoaderCallbacks(getActivity(), searchAdapter);

        return v;
    }


    @Override
    public void onResume() {
        super.onResume();

        int visibility = mShowProgressBar ? View.VISIBLE : View.INVISIBLE;
        mProgressBar.setVisibility(visibility);
        mShowProgressBar = false;

        getActivity().getSupportLoaderManager()
                .initLoader(SEARCH_LOADER_ID, null, mSearchLoaderCallbacks)
                .forceLoad(); // see: http://stackoverflow.com/questions/10524667/android-asynctaskloader-doesnt-start-loadinbackground

    }


    public void addProgressBar()
    {
        if (mProgressBar != null)
            mProgressBar.setVisibility(View.VISIBLE);
        else
            mShowProgressBar = true;  // wait for onResume to happen
    }


    public void removeProgressBar()
    {
        if (mProgressBar != null)
            mProgressBar.setVisibility(View.INVISIBLE);
        else
            mShowProgressBar = false;  // wait for onResume to happen
    }


    public void refresh(AppCompatActivity activity) // called when service finished downloading
    {
        if (mSearchLoaderCallbacks != null)
            activity.getSupportLoaderManager()
                    .restartLoader(SEARCH_LOADER_ID, null, mSearchLoaderCallbacks)
                    .forceLoad();
    }
}
