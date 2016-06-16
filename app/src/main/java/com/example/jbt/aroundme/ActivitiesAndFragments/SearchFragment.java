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

    public SearchFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_search, container, false);

        mDbHelper = new AroundMeDBHelper(getActivity());

        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.searchListView);
        mAdapter = new PlaceRecyclerAdapter(getActivity());
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        NearbyAsyncLoaderCallbacks loaderCallbacks = new NearbyAsyncLoaderCallbacks(getActivity(), mAdapter, mDbHelper);
        getActivity().getSupportLoaderManager()
                .initLoader(LOADER_ID, null, loaderCallbacks)
                .forceLoad(); // see: http://stackoverflow.com/questions/10524667/android-asynctaskloader-doesnt-start-loadinbackground

        // register receiver
        NotificationReceiver receiver = new NotificationReceiver();
        IntentFilter filter = new IntentFilter(NearbyService.ACTION_NEARBY_NOTIFY);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(receiver, filter);

        return v;
    }


    public class NotificationReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            int placesSaved = intent.getIntExtra(NearbyService.EXTRA_NEARBY_PLACES_SAVED, -1);

            if (placesSaved < 0)
                return;

            Toast.makeText(getActivity(), placesSaved + " places received", Toast.LENGTH_SHORT).show();
            NearbyAsyncLoaderCallbacks loaderCallbacks = new NearbyAsyncLoaderCallbacks(getActivity(), mAdapter, mDbHelper);
            getActivity().getSupportLoaderManager().restartLoader(LOADER_ID, null, loaderCallbacks);
        }
    }
}
