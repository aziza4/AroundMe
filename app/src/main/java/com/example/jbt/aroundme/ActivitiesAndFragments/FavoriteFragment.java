package com.example.jbt.aroundme.ActivitiesAndFragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jbt.aroundme.R;


public class FavoriteFragment extends Fragment {


    private String mTitle;
    private int mPage;


    public static FavoriteFragment newInstance(int page, String title) {
        FavoriteFragment fragmentFirst = new FavoriteFragment();
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
        return inflater.inflate(R.layout.fragment_favorite, container, false);
    }
}
