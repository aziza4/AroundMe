package com.example.jbt.aroundme.Data;


import com.google.android.gms.maps.model.LatLng;


public class Place {

    private final static long NOT_IN_DB = -1L; // -1 signals this place is "not yet save in db"

    private final long mId;
    private final LatLng mLoc;
    private final String mIcon;
    private final String mName;
    private final PlacePhoto mPhoto;
    private final String mPlaceId;
    private final double mRating;
    private final String mReference;
    private final String mScope;
    private final String[] mTypes;
    private final String mVicinity;

    public Place(LatLng loc, String icon, String name,
                 PlacePhoto photo, String placeId, double rating,
                 String reference, String scope, String[] typesArr, String vicinity) {

        this(NOT_IN_DB, loc, icon, name, photo, placeId, rating, reference, scope, typesArr, vicinity);
    }


    public Place(long id, LatLng loc, String icon, String name,
                 PlacePhoto photo, String placeId, double rating,
                 String reference, String scope, String[] typesArr, String vicinity)
    {
        this.mId = id;
        this.mLoc = loc;
        this.mIcon = icon;
        this.mName = name;
        this.mPhoto = photo;
        this.mPlaceId = placeId;
        this.mRating = rating;
        this.mReference = reference;
        this.mScope = scope;
        this.mTypes = typesArr;
        this.mVicinity = vicinity;
    }

    public Place(long id, double lat, double lng, String icon, String name,
                 String photoRef, String placeId, double rating,
                 String reference, String scope, String types, String vicinity)
    {
        this(id, new LatLng(lat,lng),
                icon, name,
                new PlacePhoto(photoRef),
                placeId, rating, reference, scope,
                types.split("|"), vicinity);
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

    public PlacePhoto getPhoto() {
        return mPhoto;
    }

    public String getFirstPhotoRef()
    {
        return mPhoto == null ? null : mPhoto.getReference();
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

    public String getTypesAsString() {

        String concatenated = "";

        for (int i = 0; i < mTypes.length-1; i++)
            concatenated += mTypes[i] + "|";

        concatenated += mTypes[mTypes.length-1];

        return concatenated;
    }

    public String getVicinity() {
        return mVicinity;
    }
}
