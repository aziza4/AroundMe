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
    private SearchAsyncLoaderCallbacks mSearchLoaderCallbacks;
    private boolean mSearchStarted = false;

    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    public void searchStarted(boolean searchStarted)
    {
        mSearchStarted = searchStarted;
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

        mProgressBar.setVisibility( mSearchStarted ? View.VISIBLE : View.INVISIBLE);

        getActivity().getSupportLoaderManager()
                .initLoader(SEARCH_LOADER_ID, null, mSearchLoaderCallbacks)
                .forceLoad(); // see: http://stackoverflow.com/questions/10524667/android-asynctaskloader-doesnt-start-loadinbackground

    }

    @Override
    public void onPause() {
        mSearchStarted = false;
        super.onPause();
    }

    public void addProgressBar()
    {
        mProgressBar.setVisibility(View.VISIBLE);
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
