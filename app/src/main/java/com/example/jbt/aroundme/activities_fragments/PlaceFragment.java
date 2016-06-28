package com.example.jbt.aroundme.activities_fragments;


import android.content.Intent;
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

import com.example.jbt.aroundme.data.Place;
import com.example.jbt.aroundme.helpers.ImageHelper;
import com.example.jbt.aroundme.helpers.Utility;
import com.example.jbt.aroundme.R;
import com.squareup.picasso.Picasso;


public class PlaceFragment extends Fragment {

    private Place mPlace;

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
        View view =  inflater.inflate(R.layout.fragment_place, container, false);

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

        ImageHelper.SetImageViewLogic(getActivity(), mPlaceIV, mPlace, true);

        mNameTV.setText(mPlace.getName());
        mVicinityTV.setText(mPlace.getVicinity());

        float rating = (float)mPlace.getRating();
        if (rating > 0f)
            mRatingRatingBar.setRating((float)mPlace.getRating());
        else
            mRatingRatingBar.setVisibility(View.INVISIBLE);

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
