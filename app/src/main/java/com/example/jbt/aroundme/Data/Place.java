package com.example.jbt.aroundme.Data;


import com.google.android.gms.maps.model.LatLng;

public class Place {

    private long mId;
    private LatLng mLoc;
    private String mIcon;
    private String mName;
    private PlacePhoto[] mPhotosArr;
    private String mPlaceId;
    private double mRating;
    private String mReference;
    private String mScope;
    private String[] mTypesArr;
    private String mVicinity;

    public Place(long id, LatLng loc, String icon, String name,
                 PlacePhoto[] photosArr, String placeId, double rating,
                 String reference, String scope, String[] typesArr, String vicinity)
    {
        this.mId = id;
        this.mLoc = loc;
        this.mIcon = icon;
        this.mName = name;
        this.mPhotosArr = photosArr;
        this.mPlaceId = placeId;
        this.mRating = rating;
        this.mReference = reference;
        this.mScope = scope;
        this.mTypesArr = typesArr;
        this.mVicinity = vicinity;
    }

    public long getId() {
        return mId;
    }

    public LatLng getLoc() {
        return mLoc;
    }

    public String getIcon() {
        return mIcon;
    }

    public String getName() {
        return mName;
    }

    public PlacePhoto[] getPhotosArr() {
        return mPhotosArr;
    }

    public String getPlaceId() {
        return mPlaceId;
    }

    public double getRating() {
        return mRating;
    }

    public String getReference() {
        return mReference;
    }

    public String getScope() {
        return mScope;
    }

    public String[] getTypesArr() {
        return mTypesArr;
    }

    public String getVicinity() {
        return mVicinity;
    }
}
