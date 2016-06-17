package com.example.jbt.aroundme.ActivitiesAndFragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.example.jbt.aroundme.Helpers.AroundMeDBHelper;
import com.example.jbt.aroundme.R;
import com.example.jbt.aroundme.Services.NearbyService;
import com.example.jbt.aroundme.UIHelpers.NearbyAsyncLoaderCallbacks;
import com.example.jbt.aroundme.UIHelpers.PlaceRecyclerAdapter;


public class SearchFragment extends Fragment {

    private static final int LOADER_ID = 1;
    private PlaceRecyclerAdapter mAdapter;
    private AroundMeDBHelper mDbHelper;
    private NearbyAsyncLoaderCallbacks mLoaderCallbacks;

    private String mTitle;
    private int mPage;


    public static SearchFragment newInstance(int page, String title) {
        SearchFragment fragmentFirst = new SearchFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt("someInt", 0);
        mTitle = getArguments().getString("someTitle");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_search, container, false);

        mDbHelper = new AroundMeDBHelper(getActivity());

        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.searchListView);
        mAdapter = new PlaceRecyclerAdapter(getActivity());
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mLoaderCallbacks = new NearbyAsyncLoaderCallbacks(getActivity(), mAdapter, mDbHelper);
        getActivity().getSupportLoaderManager()
                .initLoader(LOADER_ID, null, mLoaderCallbacks)
                .forceLoad(); // see: http://stackoverflow.com/questions/10524667/android-asynctaskloader-doesnt-start-loadinbackground


        return v;
    }

    public void refresh()
    {
        getActivity().getSupportLoaderManager().restartLoader(LOADER_ID, null, mLoaderCallbacks);
    }
}
