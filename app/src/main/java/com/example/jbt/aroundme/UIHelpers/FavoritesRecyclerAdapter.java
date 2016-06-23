package com.example.jbt.aroundme.UIHelpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
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




public class FavoritesRecyclerAdapter extends RecyclerView.Adapter<FavoritesRecyclerAdapter.PlaceViewHolder> {

    private final Context mContext;
    private GooglePlacesNearbyHelper mNearbyHelper;
    private ArrayList<Place> mPlaces;


    public FavoritesRecyclerAdapter(Context context) {
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
        public final TextView mPhoneTV;
        public final ImageView mIconIV;

        private Place mPlace;

        public PlaceViewHolder(View view) {
            super(view);

            mPlaceIV = (ImageView) view.findViewById(R.id.favoritesPlaceImageView);
            mNameTV = (TextView)view.findViewById(R.id.favoritesNameTextView);
            mVicinityTV = (TextView)view.findViewById(R.id.favoritesVicinityTextView);
            mRatingRatingBar = (RatingBar) view.findViewById(R.id.favoritesPlaceRatingBar);
            mPhoneTV = (TextView)view.findViewById(R.id.favoritesPhoneTextView);
            mIconIV = (ImageView) view.findViewById(R.id.favoritesIconImageView);

            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Intent intent = new Intent(NearbyService.ACTION_PLACE_FAVORITES, null, mContext, NearbyService.class);
                    intent.putExtra(NearbyService.EXTRA_PLACE_FAVORITES_DATA, new DetailsRequest(mPlace));
                    intent.putExtra(NearbyService.EXTRA_PLACE_FAVORITES_ACTION_REMOVE, true);
                    mContext.startService(intent);
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
            mPhoneTV.setText(place.getPhone());

            String url = place.getIcon();
            if (url != null)
                Picasso.with(mContext).load(Uri.parse(url)).into(mIconIV);
        }
    }
}
