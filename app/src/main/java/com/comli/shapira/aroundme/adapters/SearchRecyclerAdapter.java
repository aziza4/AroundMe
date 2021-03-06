package com.comli.shapira.aroundme.adapters;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bignerdranch.android.multiselector.MultiSelector;
import com.bignerdranch.android.multiselector.SwappingHolder;
import com.comli.shapira.aroundme.activities_fragments.MapActivity;
import com.comli.shapira.aroundme.activities_fragments.SearchFragment;
import com.comli.shapira.aroundme.data.Place;
import com.comli.shapira.aroundme.helpers.ImageHelper;
import com.comli.shapira.aroundme.helpers.Utility;
import com.comli.shapira.aroundme.R;
import com.comli.shapira.aroundme.action_mode_callbacks.SearchActionModeCallbacks;
import com.comli.shapira.aroundme.ui_helpers.TransitionsHelper;

import java.util.ArrayList;



public class SearchRecyclerAdapter extends RecyclerView.Adapter<SearchRecyclerAdapter.PlaceViewHolder> {

    private final Context mContext;
    private final SearchFragment mSearchFragment;
    private final MultiSelector mMultiSelector;
    private ArrayList<Place> mPlaces;



    public SearchRecyclerAdapter(Context context, SearchFragment searchFragment) {
        mContext = context;
        mSearchFragment = searchFragment;
        mMultiSelector = new MultiSelector(); // see: https://www.bignerdranch.com/blog/recyclerview-part-2-choice-modes/;
        mPlaces = null;
    }


    public void setData(ArrayList<Place> places)
    {
        if (places != null) {
            mPlaces = places;
            notifyDataSetChanged();
        }
    }


    public void clearData()
    {
        if (mPlaces == null)
            return;

        int size = mPlaces.size();

        if (size == 0)
            return;

        for (int i = 0; i < size; i++)
            mPlaces.remove(0);

        notifyItemRangeRemoved(0, size);
    }


    @Override
    public PlaceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(mContext);

        @SuppressLint("InflateParams")
        View view = inflater.inflate(R.layout.search_list_item, null);
        return new PlaceViewHolder(view);
    }


    @Override
    public void onBindViewHolder(PlaceViewHolder holder, int position) {

        if (mPlaces != null)
            holder.bind(mPlaces.get(position));
    }


    @Override
    public int getItemCount() {

        return mPlaces != null ?
                mPlaces.size() :
                0;
    }

    class PlaceViewHolder extends SwappingHolder {

        public final ImageView mPlaceIV;
        public final TextView mNameTV;
        public final TextView mVicinityTV;
        public final TextView mDistanceTV;
        public final RatingBar mRatingRatingBar;


        private Place mPlace;

        public PlaceViewHolder(View view) {
            super(view, mMultiSelector);

            // see: http://stackoverflow.com/questions/30906793/android-cant-call-void-android-view-view-settranslationzfloat-on-null-object
            if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                setSelectionModeStateListAnimator(null);
                setDefaultModeStateListAnimator(null);
            }


            mPlaceIV = (ImageView) view.findViewById(R.id.searchPlaceImageView);
            mNameTV = (TextView)view.findViewById(R.id.searchNameTextView);
            mVicinityTV = (TextView)view.findViewById(R.id.searchVicinityTextView);
            mDistanceTV = (TextView)view.findViewById(R.id.searchDistanceTextView);
            mRatingRatingBar = (RatingBar) view.findViewById(R.id.searchPlaceRatingBar);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!mMultiSelector.tapSelection(PlaceViewHolder.this)) {
                        Intent intent = new Intent(mContext, MapActivity.class);
                        intent.putExtra(MapActivity.INTENT_MAP_ID_KEY, mPlace.getId());
                        intent.putExtra(MapActivity.INTENT_MAP_TYPE_KEY, MapActivity.INTENT_MAP_TYPE_SEARCH_VAL);

                        Bundle transitionBundle = TransitionsHelper
                                .getTransitionBundle((AppCompatActivity) mContext, view);

                        mContext.startActivity(intent, transitionBundle);
                    }
                }
            });

            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    AppCompatActivity activity = (AppCompatActivity) mContext;
                    activity.startSupportActionMode(new SearchActionModeCallbacks(activity,
                            mSearchFragment, mMultiSelector, mPlaces, mPlace));

                    mMultiSelector.setSelected(PlaceViewHolder.this, true);

                    return true;
                }
            });
        }


        public void bind(Place place)
        {
            mPlace = place;

            // set image if exists, placeholder if n/a, or download on last resort
            ImageHelper.SetImageViewLogic(mContext, mPlaceIV, mPlace, false);

            mNameTV.setText(place.getName());
            mVicinityTV.setText(place.getVicinity());

            String distance = Utility.getDistanceMsg(mContext, place);
            if (distance == null || distance.isEmpty())
                mDistanceTV.setVisibility(View.GONE);
            else {
                mDistanceTV.setVisibility(View.VISIBLE);
                mDistanceTV.setText(distance);
            }

            float rating = (float)mPlace.getRating();
            if (rating == 0f)
                mRatingRatingBar.setVisibility(View.GONE);
            else {
                mRatingRatingBar.setVisibility(View.VISIBLE);
                mRatingRatingBar.setRating((float) mPlace.getRating());
            }
        }
    }
}