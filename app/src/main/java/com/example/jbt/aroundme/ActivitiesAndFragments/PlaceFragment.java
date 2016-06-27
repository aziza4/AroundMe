package com.example.jbt.aroundme.ActivitiesAndFragments;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
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
import com.example.jbt.aroundme.Helpers.GooglePlacesNearbyHelper;
import com.example.jbt.aroundme.Helpers.Utility;
import com.example.jbt.aroundme.R;
import com.squareup.picasso.Picasso;


public class PlaceFragment extends Fragment {

    private Place mPlace;
    private GooglePlacesNearbyHelper mNearbyHelper;

    private ImageView mPlaceIV;
    private TextView mNameTV;
    private TextView mVicinityTV;
    private RatingBar mRatingRatingBar;
    private ImageView mIconIV;
    private TextView mPhoneTV;
    private TextView mDistanceTV;
    private LinearLayout mDialLayout;
    private LinearLayout mDistanceLayout;

    public PlaceFragment() {}

    public void setPlace(Place place) { mPlace = place; }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view =  inflater.inflate(R.layout.favorite_list_item, container, false);

        mNearbyHelper = new GooglePlacesNearbyHelper(getActivity());

        mPlaceIV = (ImageView) view.findViewById(R.id.favoritesPlaceImageView);
        mNameTV = (TextView) view.findViewById(R.id.favoritesNameTextView);
        mVicinityTV = (TextView) view.findViewById(R.id.favoritesVicinityTextView);
        mRatingRatingBar = (RatingBar) view.findViewById(R.id.favoritesPlaceRatingBar);
        mIconIV = (ImageView) view.findViewById(R.id.favoritesIconImageView);
        mPhoneTV = (TextView) view.findViewById(R.id.favoritesPhoneTextView);
        mDistanceTV = (TextView) view.findViewById(R.id.favoritesDistanceTextView);
        mDialLayout = (LinearLayout)view.findViewById(R.id.favoritesDialLayout);
        mDistanceLayout = (LinearLayout)view.findViewById(R.id.favoritesDistanceLayout);

        mDialLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Utility.getDialingUri(getActivity(), mPlace);
                Intent intent = new Intent(Intent.ACTION_DIAL, uri);
                startActivity(intent);
            }
        });

        mDistanceLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Utility.getDirectionsUri(getActivity(), mPlace);
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();


        Bitmap bitmap = mPlace.getPhoto().getBitmap();
        if ( bitmap != null) {
            mPlaceIV.setImageBitmap(bitmap);
        } else {
            if (mPlace.getPhotoRef() == null) {
                Bitmap imagePlaceHolder = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.placeholder);
                mPlaceIV.setImageBitmap(imagePlaceHolder);
            } else {
                Uri uri = mNearbyHelper.getPhotoUri(mPlace);
                Picasso.with(getActivity())
                        .load(uri)
                        .placeholder(R.drawable.placeholder)
                        .into(mPlaceIV, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {
                                Bitmap bitmap = ((BitmapDrawable)mPlaceIV.getDrawable()).getBitmap();
                                mPlace.getPhoto().setBitmap(bitmap);
                            }

                            @Override public void onError() {}
                        });
            }
        }

        mNameTV.setText(mPlace.getName());
        mVicinityTV.setText(mPlace.getVicinity());
        mRatingRatingBar.setRating((float)mPlace.getRating());


        String distance = Utility.getDistanceMsg(getActivity(), mPlace);
        if (distance == null || distance.isEmpty())
            mDistanceLayout.setVisibility(View.GONE);
        else
            mDistanceTV.setText(distance);

        String phone = mPlace.getPhone();
        if (phone == null || phone.isEmpty())
            mDialLayout.setVisibility(View.GONE);
        else
            mPhoneTV.setText(phone);

        String url = mPlace.getIcon();
        if (url != null)
            Picasso.with(getActivity()).load(Uri.parse(url)).into(mIconIV);
    }
}
