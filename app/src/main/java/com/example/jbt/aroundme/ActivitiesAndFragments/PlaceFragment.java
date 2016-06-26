package com.example.jbt.aroundme.ActivitiesAndFragments;


import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.jbt.aroundme.Data.Place;
import com.example.jbt.aroundme.Helpers.Utility;
import com.example.jbt.aroundme.R;
import com.squareup.picasso.Picasso;


public class PlaceFragment extends Fragment {

    private Place mPlace;

    public ImageView mPlaceIV;
    public TextView mNameTV;
    public TextView mVicinityTV;
    public RatingBar mRatingRatingBar;
    public ImageView mIconIV;
    public TextView mPhoneTV;
    public TextView mDistanceTV;
    public LinearLayout mDialLayout;
    public LinearLayout mDistanceLayout;
    public LinearLayout mWebsiteLayout;

    public PlaceFragment() {}

    public void setPlace(Place place) { mPlace = place; }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {

        View view =  inflater.inflate(R.layout.favorite_list_item, container, false);

        mPlaceIV = (ImageView) view.findViewById(R.id.favoritesPlaceImageView);
        mNameTV = (TextView) view.findViewById(R.id.favoritesNameTextView);
        mVicinityTV = (TextView) view.findViewById(R.id.favoritesVicinityTextView);
        mRatingRatingBar = (RatingBar) view.findViewById(R.id.favoritesPlaceRatingBar);
        mIconIV = (ImageView) view.findViewById(R.id.favoritesIconImageView);
        mPhoneTV = (TextView) view.findViewById(R.id.favoritesPhoneTextView);
        mDistanceTV = (TextView) view.findViewById(R.id.favoritesDistanceTextView);
        mDialLayout = (LinearLayout)view.findViewById(R.id.favoritesDialLayout);
        mDistanceLayout = (LinearLayout)view.findViewById(R.id.favoritesDistanceLayout);
        mWebsiteLayout = (LinearLayout)view.findViewById(R.id.favoritesWebsiteLayout);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        Bitmap bitmap = mPlace.getPhoto().getBitmap();
        if ( bitmap != null)
            mPlaceIV.setImageBitmap(bitmap);

        mNameTV.setText(mPlace.getName());
        mVicinityTV.setText(mPlace.getVicinity());
        mDistanceTV.setText(Utility.getDistanceMsg(getActivity(), mPlace));
        mRatingRatingBar.setRating((float)mPlace.getRating());
        mPhoneTV.setText(mPlace.getPhone());

        String url = mPlace.getIcon();
        if (url != null)
            Picasso.with(getActivity()).load(Uri.parse(url)).into(mIconIV);
    }
}
