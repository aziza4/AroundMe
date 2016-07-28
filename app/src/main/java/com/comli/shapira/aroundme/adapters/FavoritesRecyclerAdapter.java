package com.comli.shapira.aroundme.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.comli.shapira.aroundme.activities_fragments.FavoritesFragment;
import com.comli.shapira.aroundme.activities_fragments.MapActivity;
import com.comli.shapira.aroundme.data.Place;
import com.comli.shapira.aroundme.helpers.ImageHelper;
import com.comli.shapira.aroundme.helpers.Utility;
import com.comli.shapira.aroundme.R;
import com.comli.shapira.aroundme.action_mode_callbacks.FavoritesActionModeCallbacks;
import com.comli.shapira.aroundme.ui_helpers.TransitionsHelper;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;




public class FavoritesRecyclerAdapter extends RecyclerView.Adapter<FavoritesRecyclerAdapter.PlaceViewHolder> {

    private final Context mContext;
    private final FavoritesFragment mFavoritesFragment;
    private ArrayList<Place> mPlaces;


    public FavoritesRecyclerAdapter(Context context, FavoritesFragment favoritesFragment) {
        mContext = context;
        mFavoritesFragment = favoritesFragment;
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
        View view = inflater.inflate(R.layout.favorite_list_item, null);
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
        public final RatingBar mRatingRatingBar;
        public final ImageView mIconIV;
        public final TextView mPhoneTV;
        public final TextView mDistanceTV;
        public final LinearLayout mDialLayout;
        public final LinearLayout mDistanceLayout;

        private Place mPlace;

        public PlaceViewHolder(View view) {
            super(view);

            mPlaceIV = (ImageView) view.findViewById(R.id.favoritesPlaceImageView);
            mNameTV = (TextView) view.findViewById(R.id.favoritesNameTextView);
            mVicinityTV = (TextView) view.findViewById(R.id.favoritesVicinityTextView);
            mRatingRatingBar = (RatingBar) view.findViewById(R.id.favoritesPlaceRatingBar);
            mIconIV = (ImageView) view.findViewById(R.id.favoritesIconImageView);
            mPhoneTV = (TextView) view.findViewById(R.id.favoritesPhoneTextView);
            mDistanceTV = (TextView) view.findViewById(R.id.favoritesDistanceTextView);
            mDialLayout = (LinearLayout)view.findViewById(R.id.favoritesDialLayout);
            mDistanceLayout = (LinearLayout)view.findViewById(R.id.favoritesDistanceLayout);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(mContext, MapActivity.class);
                    intent.putExtra(MapActivity.INTENT_MAP_ID_KEY, mPlace.getId());
                    intent.putExtra(MapActivity.INTENT_MAP_TYPE_KEY, MapActivity.INTENT_MAP_TYPE_FAVORITES_VAL);

                    Bundle transitionBundle = TransitionsHelper
                            .getTransitionBundle((AppCompatActivity)mContext, view);

                    mContext.startActivity(intent, transitionBundle);
                }
            });

            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    AppCompatActivity activity = (AppCompatActivity) mContext;
                    activity.startSupportActionMode(new FavoritesActionModeCallbacks(activity, mFavoritesFragment, mPlace));
                    return true;
                }
            });

            mDialLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Uri uri = Utility.getDialingUri(mContext, mPlace);
                    Intent intent = new Intent(Intent.ACTION_DIAL, uri);
                    mContext.startActivity(intent);
                }
            });

            mDistanceLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Uri uri = Utility.getDirectionsUri(mContext, mPlace);
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri);
                    mContext.startActivity(intent);
                }
            });
        }


        public void bind(Place place)
        {
            mPlace = place;

            // set image if exists, placeholder if n/a, or download on last resort
            ImageHelper.SetImageViewLogic(mContext, mPlaceIV, mPlace, true);

            mNameTV.setText(place.getName());
            mVicinityTV.setText(place.getVicinity());

            float rating = (float)place.getRating();
            if (rating > 0f)
                mRatingRatingBar.setRating((float)place.getRating());
            else
                mRatingRatingBar.setVisibility(View.INVISIBLE);

            String distance = Utility.getDistanceMsg(mContext, place);
            if (distance == null || distance.isEmpty())
                mDistanceLayout.setVisibility(View.GONE);
            else
                mDistanceTV.setText(distance);

            String phone = place.getPhone();
            if (phone == null || phone.isEmpty())
                mDialLayout.setVisibility(View.GONE);
            else
                mPhoneTV.setText(phone);

            String url = place.getIcon();
            if (url != null)
                Picasso.with(mContext).load(Uri.parse(url)).into(mIconIV);
        }
    }
}
