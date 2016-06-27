package com.example.jbt.aroundme.UIHelpers;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.jbt.aroundme.ActivitiesAndFragments.MapActivity;
import com.example.jbt.aroundme.Data.Place;
import com.example.jbt.aroundme.Helpers.ImageHelper;
import com.example.jbt.aroundme.Helpers.Utility;
import com.example.jbt.aroundme.R;
import java.util.ArrayList;



public class SearchRecyclerAdapter extends RecyclerView.Adapter<SearchRecyclerAdapter.PlaceViewHolder> {

    private final Context mContext;
    private ArrayList<Place> mPlaces;



    public SearchRecyclerAdapter(Context context) {
        mContext = context;
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


    class PlaceViewHolder extends RecyclerView.ViewHolder {

        public final ImageView mPlaceIV;
        public final TextView mNameTV;
        public final TextView mVicinityTV;
        public final TextView mDistanceTV;
        public final RatingBar mRatingRatingBar;

        private Place mPlace;

        public PlaceViewHolder(View view) {
            super(view);

            mPlaceIV = (ImageView) view.findViewById(R.id.searchPlaceImageView);
            mNameTV = (TextView)view.findViewById(R.id.searchNameTextView);
            mVicinityTV = (TextView)view.findViewById(R.id.searchVicinityTextView);
            mDistanceTV = (TextView)view.findViewById(R.id.searchDistanceTextView);
            mRatingRatingBar = (RatingBar) view.findViewById(R.id.searchPlaceRatingBar);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, MapActivity.class);
                    intent.putExtra(MapActivity.INTENT_MAP_ID_KEY, mPlace.getId());
                    intent.putExtra(MapActivity.INTENT_MAP_TYPE_KEY, MapActivity.INTENT_MAP_TYPE_SEARCH_VAL);
                    mContext.startActivity(intent);
                }
            });

            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    AppCompatActivity activity = (AppCompatActivity) mContext;
                    activity.startSupportActionMode(new SearchActionModeCallbacks(activity, mPlace));
                    return true;
                }
            });
        }


        public void bind(Place place)
        {
            mPlace = place;

            ImageHelper.SetImageViewLogic(mContext, mPlaceIV, mPlace, false);

            mNameTV.setText(place.getName());
            mVicinityTV.setText(place.getVicinity());
            mDistanceTV.setText(Utility.getDistanceMsg(mContext, place));

            float rating = (float)place.getRating();
            if (rating > 0f)
                mRatingRatingBar.setRating((float)place.getRating());
            else
                mRatingRatingBar.setVisibility(View.INVISIBLE);
        }
    }
}