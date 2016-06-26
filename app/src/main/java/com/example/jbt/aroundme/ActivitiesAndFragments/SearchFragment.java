package com.example.jbt.aroundme.ActivitiesAndFragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jbt.aroundme.R;
import com.example.jbt.aroundme.UIHelpers.SearchAsyncLoaderCallbacks;
import com.example.jbt.aroundme.UIHelpers.SearchRecyclerAdapter;


public class SearchFragment extends Fragment {

    private static final int SEARCH_LOADER_ID = 1;

    private SearchAsyncLoaderCallbacks mSearchLoaderCallbacks;


    public static SearchFragment newInstance(int page, String title) {

        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int mPage = getArguments().getInt("someInt", 0);
        String mTitle = getArguments().getString("someTitle");
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
