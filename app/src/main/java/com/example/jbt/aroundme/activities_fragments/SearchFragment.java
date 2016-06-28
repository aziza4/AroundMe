package com.example.jbt.aroundme.activities_fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jbt.aroundme.R;
import com.example.jbt.aroundme.ui_helpers.SearchAsyncLoaderCallbacks;
import com.example.jbt.aroundme.ui_helpers.SearchRecyclerAdapter;


public class SearchFragment extends Fragment {

    private static final int SEARCH_LOADER_ID = 1;

    private SearchAsyncLoaderCallbacks mSearchLoaderCallbacks;


    public static SearchFragment newInstance() {
        return new SearchFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_search, container, false);

        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.searchListView);
        SearchRecyclerAdapter searchAdapter = new SearchRecyclerAdapter(getActivity());
        recyclerView.setAdapter(searchAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mSearchLoaderCallbacks = new SearchAsyncLoaderCallbacks(getActivity(), searchAdapter);

        getActivity().getSupportLoaderManager()
                .initLoader(SEARCH_LOADER_ID, null, mSearchLoaderCallbacks)
                .forceLoad(); // see: http://stackoverflow.com/questions/10524667/android-asynctaskloader-doesnt-start-loadinbackground

        return v;
    }

    public void refresh(AppCompatActivity activity)
    {
        activity.getSupportLoaderManager()
                .restartLoader(SEARCH_LOADER_ID, null, mSearchLoaderCallbacks)
                .forceLoad();
    }
}