package com.example.jbt.aroundme.ActivitiesAndFragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jbt.aroundme.R;
import com.example.jbt.aroundme.UIHelpers.FavoritesAsyncLoaderCallbacks;
import com.example.jbt.aroundme.UIHelpers.FavoritesRecyclerAdapter;


public class FavoritesFragment extends Fragment {

    private static final int FAVORITES_LOADER_ID = 2;
    private FavoritesAsyncLoaderCallbacks mFavoritesLoaderCallbacks;
    private static ViewPager mViewPager;


    public static FavoritesFragment newInstance(int page, String title, ViewPager viewPager) {
        mViewPager = viewPager;

        FavoritesFragment fragment = new FavoritesFragment();
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

        View v = inflater.inflate(R.layout.fragment_favorite, container, false);

        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.favoritesListView);
        FavoritesRecyclerAdapter favoritesAdapter = new FavoritesRecyclerAdapter(getActivity());
        recyclerView.setAdapter(favoritesAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mFavoritesLoaderCallbacks = new FavoritesAsyncLoaderCallbacks(getActivity(), favoritesAdapter, mViewPager);

        getActivity().getSupportLoaderManager()
                .initLoader(FAVORITES_LOADER_ID, null, mFavoritesLoaderCallbacks)
                .forceLoad(); // see: http://stackoverflow.com/questions/10524667/android-asynctaskloader-doesnt-start-loadinbackground


        return v;
    }

    public void refresh(AppCompatActivity activity)
    {
        activity.getSupportLoaderManager()
                .restartLoader(FAVORITES_LOADER_ID, null, mFavoritesLoaderCallbacks)
                .forceLoad();
    }
}

