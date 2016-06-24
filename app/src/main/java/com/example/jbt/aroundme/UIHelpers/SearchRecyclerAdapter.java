package com.example.jbt.aroundme.UIHelpers;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import com.example.jbt.aroundme.Data.DetailsRequest;
import com.example.jbt.aroundme.Data.Place;
import com.example.jbt.aroundme.Helpers.GooglePlacesNearbyHelper;
import com.example.jbt.aroundme.R;
import com.example.jbt.aroundme.Services.NearbyService;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;



public class SearchRecyclerAdapter extends RecyclerView.Adapter<SearchRecyclerAdapter.PlaceViewHolder> {

    private final Context mContext;
    private GooglePlacesNearbyHelper mNearbyHelper;
    private ArrayList<Place> mPlaces;


    public SearchRecyclerAdapter(Context context) {
        mContext = context;
        mNearbyHelper = new GooglePlacesNearbyHelper(mContext);
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


    private void removeItem(Place place)
    {
        if (mPlaces != null ) {
            mPlaces.remove(place);
            notifyDataSetChanged();
        }
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
        public final RatingBar mRatingRatingBar;

        private Place mPlace;

        public PlaceViewHolder(View view) {
            super(view);

            mPlaceIV = (ImageView) view.findViewById(R.id.searchPlaceImageView);
            mNameTV = (TextView)view.findViewById(R.id.searchNameTextView);
            mVicinityTV = (TextView)view.findViewById(R.id.searchVicinityTextView);
            mRatingRatingBar = (RatingBar) view.findViewById(R.id.searchPlaceRatingBar);

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

            //Bitmap imagePlaceHolder = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.placeholder);

            Bitmap bitmap = place.getPhoto().getBitmap();
            if ( bitmap != null) {
                mPlaceIV.setImageBitmap(bitmap);
            } else {
                if (place.getPhotoRef() == null) {
                    mPlaceIV.setImageBitmap(null);
                } else {
                    Uri uri = mNearbyHelper.getPhotoUri(place);
                    Picasso.with(mContext)
                            .load(uri)
                            .placeholder(R.drawable.placeholder)
                            .into(mPlaceIV);
                }
            }

            mNameTV.setText(place.getName());
            mVicinityTV.setText(place.getVicinity());
            mRatingRatingBar.setRating((float)place.getRating());
        }
    }
}