package com.comli.shapira.aroundme.data;


import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

// This is the main data object on this app.
// It encapsulate all place (from both Search, Favorites, and photo-download queries)
public class Place implements Parcelable {

    private final static long NOT_IN_DB = -1L; // -1 signals this place is "not yet save in db"

    private final long mId;
    private final LatLng mLoc;
    private final String mIcon;
    private final String mName;
    private final String mPlaceId;
    private final double mRating;
    private final String mReference;
    private final String mScope;
    private final String[] mTypes;
    private final String mVicinity;

    private String mAddress;
    private String mPhone;
    private String mIntlPhone;
    private String mUrl;
    private PlacePhoto mPhoto;

    private final double mDistanceInKM;

    public Place(com.google.android.gms.location.places.Place googlePlace)
    {
        this.mId = NOT_IN_DB;
        this.mLoc = googlePlace.getLatLng();
        this.mIcon = "";
        this.mName = googlePlace.getName() != null ? googlePlace.getName().toString() : "";
        this.mPhoto = null;
        this.mPlaceId = googlePlace.getId();
        this.mRating = googlePlace.getRating();
        this.mReference = "";
        this.mScope = "";
        this.mTypes = new String[] { "" };
        this.mVicinity = googlePlace.getAddress() != null? googlePlace.getAddress().toString() : "";
        this.mAddress = googlePlace.getAddress() != null ? googlePlace.getAddress().toString() : "";
        this.mPhone = googlePlace.getPhoneNumber() != null ? googlePlace.getPhoneNumber().toString() : "";
        this.mIntlPhone = "";
        this.mUrl = googlePlace.getWebsiteUri() != null? googlePlace.getWebsiteUri().toString() : "";
        this.mDistanceInKM = 0.0;
    }

    public Place(LatLng loc, String icon, String name,
                 PlacePhoto photo, String placeId, double rating,
                 String reference, String scope, String[] typesArr, String vicinity) {

        this(NOT_IN_DB, loc, icon, name, photo, placeId, rating,
                reference, scope, typesArr, vicinity,
                null, null, null, null, 0.0);
    }


    private Place(long id, LatLng loc, String icon, String name,
                  PlacePhoto photo, String placeId, double rating,
                  String reference, String scope, String[] typesArr, String vicinity,
                  String address, String phone, String intlPhone, String url, double distance)
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
        this.mAddress = address;
        this.mPhone = phone;
        this.mIntlPhone = intlPhone;
        this.mUrl = url;
        this.mDistanceInKM = distance;
    }

    public Place(long id, double lat, double lng, String icon, String name,
                 String photoRef, Bitmap photo, String placeId, double rating,
                 String reference, String scope, String types, String vicinity,
                 String address, String phone, String intlPhone, String url, double distance)
    {
        this(id, new LatLng(lat,lng),
                icon, name,
                new PlacePhoto(photoRef, photo),
                placeId, rating, reference, scope,
                types.split("|"), vicinity,
                address, phone, intlPhone, url, distance);
    }


    Place(Parcel in) {
        mId = in.readLong();
        mLoc = in.readParcelable(LatLng.class.getClassLoader());
        mIcon = in.readString();
        mName = in.readString();
        mPlaceId = in.readString();
        mRating = in.readDouble();
        mReference = in.readString();
        mScope = in.readString();
        mTypes = in.createStringArray();
        mVicinity = in.readString();
        mAddress = in.readString();
        mPhone = in.readString();
        mIntlPhone = in.readString();
        mUrl = in.readString();
        mPhoto = in.readParcelable(PlacePhoto.class.getClassLoader());
        mDistanceInKM = in.readDouble();
    }

    public static final Creator<Place> CREATOR = new Creator<Place>() {
        @Override
        public Place createFromParcel(Parcel in) {
            return new Place(in);
        }

        @Override
        public Place[] newArray(int size) {
            return new Place[size];
        }
    };

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

    public String getPhotoRef()
    {
        return mPhoto == null ? null : mPhoto.getReference();
    }


    public byte[] getPhotoByteArray()
    {
        return mPhoto == null ? null : mPhoto.getBitmapAsByteArray();
    }

    public double getDistanceInKM() { return mDistanceInKM; }

    public void setPhoto(Bitmap bitmap)
    {
        if ( mPhoto != null)
            mPhoto.setBitmap(bitmap);
    }


    public String getPlaceId() {
        return mPlaceId;
    }

    public double getRating() {
        return mRating;
    }

    public String getReference() { return mReference; }

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

    public String getAddress() {
        return mAddress;
    }

    public String getPhone() {
        return mPhone;
    }

    public String getIntlPhone() {
        return mIntlPhone;
    }

    public String getUrl() {
        return mUrl;
    }


    public void setAdditionalDetails(String address, String phone, String intlPhone, String url, PlacePhoto photo)
    {
        mAddress = address;
        mPhone = phone;
        mIntlPhone = intlPhone;
        mUrl = url;

        if (mPhoto == null)
            mPhoto = photo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(mId);
        parcel.writeParcelable(mLoc, i);
        parcel.writeString(mIcon);
        parcel.writeString(mName);
        parcel.writeString(mPlaceId);
        parcel.writeDouble(mRating);
        parcel.writeString(mReference);
        parcel.writeString(mScope);
        parcel.writeStringArray(mTypes);
        parcel.writeString(mVicinity);
        parcel.writeString(mAddress);
        parcel.writeString(mPhone);
        parcel.writeString(mIntlPhone);
        parcel.writeString(mUrl);
        parcel.writeParcelable(mPhoto, i);
        parcel.writeDouble(mDistanceInKM);
    }
}
