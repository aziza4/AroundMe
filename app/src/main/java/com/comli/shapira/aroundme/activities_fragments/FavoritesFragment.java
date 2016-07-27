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
import com.comli.shapira.aroundme.async_loaders.FavoritesAsyncLoaderCallbacks;
import com.comli.shapira.aroundme.adapters.FavoritesRecyclerAdapter;


public class FavoritesFragment extends Fragment {

    private static final int FAVORITES_LOADER_ID = 2;
    private FavoritesAsyncLoaderCallbacks mFavoritesLoaderCallbacks;
    private ProgressBar mProgressBar;


    public static FavoritesFragment newInstance()
    {
        return new FavoritesFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_favorite, container, false);

        // user recycler-adapter with async-loader
        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.favoritesListView);
        FavoritesRecyclerAdapter favoritesAdapter = new FavoritesRecyclerAdapter(getActivity());
        recyclerView.setAdapter(favoritesAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // downloading progressbar
        mProgressBar = (ProgressBar) v.findViewById(R.id.downloadProgressBar);

        mFavoritesLoaderCallbacks = new FavoritesAsyncLoaderCallbacks(getActivity(), this, favoritesAdapter);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        getActivity().getSupportLoaderManager()
                .initLoader(FAVORITES_LOADER_ID, null, mFavoritesLoaderCallbacks)
                .forceLoad(); // see: http://stackoverflow.com/questions/10524667/android-asynctaskloader-doesnt-start-loadinbackground
    }

    public void addProgressBar()
    {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    public void removeProgressBar()
    {
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    public void refresh(AppCompatActivity activity) // called when service finished download-and-save/delete/delete-all
    {
        activity.getSupportLoaderManager()
                .restartLoader(FAVORITES_LOADER_ID, null, mFavoritesLoaderCallbacks)
                .forceLoad();
    }
}

