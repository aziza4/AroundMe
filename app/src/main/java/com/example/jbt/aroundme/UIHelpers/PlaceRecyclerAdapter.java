package com.example.jbt.aroundme.UIHelpers;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import com.example.jbt.aroundme.Data.Place;
import com.example.jbt.aroundme.Helpers.GooglePlacesNearbyHelper;
import com.example.jbt.aroundme.R;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;



public class PlaceRecyclerAdapter extends RecyclerView.Adapter<PlaceRecyclerAdapter.PlaceViewHolder> {

    private final Context mContext;
    private GooglePlacesNearbyHelper mNearbyHelper;
    private ArrayList<Place> mPlaces;


    public PlaceRecyclerAdapter(Context context) {
        mContext = context;
        mNearbyHelper = new GooglePlacesNearbyHelper(mContext);
        mPlaces = null;
    }


    public void setData(ArrayList<Place> places) // called whenever refresh is needed
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

        public final ImageView placeIV;
        public final TextView nameTV;
        public final TextView vicinityTV;
        public final RatingBar ratingRatingBar;

        private Place mPlace;

        public PlaceViewHolder(View view) {
            super(view);

            placeIV = (ImageView) view.findViewById(R.id.placeImageView);
            nameTV = (TextView)view.findViewById(R.id.nameTextView);
            vicinityTV = (TextView)view.findViewById(R.id.vicinityTextView);
            ratingRatingBar = (RatingBar) view.findViewById(R.id.placeRatingBar);
        }


        public void bind(Place place)
        {
            mPlace = place;

            vicinityTV.setText(place.getVicinity());
            Bitmap imageNA = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.image_na);

            Bitmap bitmap = place.getPhoto().getBitmap();
            if ( bitmap != null) {
                placeIV.setImageBitmap(bitmap);
            } else {
                if (place.getPhotoRef() == null) {
                    placeIV.setImageBitmap(imageNA);
                } else {
                    Uri uri = mNearbyHelper.getPhotoUri(place);
                    Picasso.with(mContext)
                            .load(uri)
                            .placeholder(R.drawable.placeholder)
                            //.error(R.drawable.image_na) // Todo: change to error image
                            .into(placeIV);
                }
            }

            nameTV.setText(place.getName());
            vicinityTV.setText(place.getVicinity());
            ratingRatingBar.setRating((float)place.getRating());
        }
    }
}