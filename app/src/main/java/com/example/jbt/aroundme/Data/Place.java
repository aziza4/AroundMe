package com.example.jbt.aroundme.Data;


import com.google.android.gms.maps.model.LatLng;
import java.io.Serializable;


public class Place implements Serializable {

    private final static long NOT_IN_DB = -1L; // -1 signals this place is "not yet save in db"

    private final long mId;
    private final LatLng mLoc;
    private final String mIcon;
    private final String mName;
    private final PlacePhoto[] mPhotosArr;
    private final String mPlaceId;
    private final double mRating;
    private final String mReference;
    private final String mScope;
    private final String[] mTypesArr;
    private final String mVicinity;

    public Place(LatLng loc, String icon, String name,
                 PlacePhoto[] photosArr, String placeId, double rating,
                 String reference, String scope, String[] typesArr, String vicinity) {
        this(NOT_IN_DB, loc, icon, name, photosArr, placeId,
                rating, reference, scope, typesArr, vicinity);
    }

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
