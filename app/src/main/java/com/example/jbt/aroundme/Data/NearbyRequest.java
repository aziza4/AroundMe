package com.example.jbt.aroundme.data;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

// Search' request object to share information between activity and service
// Basically it contains all info to build a request to googlemaps nearby service
public class NearbyRequest implements  Parcelable {

    private final LatLng mLatLng;
    private final int mRadius;
    private final String[] mTypes;
    private final String mKeyword;
    private final String mLanguage;
    private final String mRank;

    public NearbyRequest(LatLng latLng, int radius, String[] types, String keyword, String language, String rank) {
        this.mLatLng = latLng;
        this.mRadius = radius;
        this.mTypes = types;
        this.mKeyword = keyword;
        this.mLanguage = language;
        this.mRank = rank;
    }

    private NearbyRequest(Parcel in) {
        mLatLng = in.readParcelable(LatLng.class.getClassLoader());
        mRadius = in.readInt();
        mTypes = in.createStringArray();
        mKeyword = in.readString();
        mLanguage = in.readString();
        mRank = in.readString();
    }

    public static final Creator<NearbyRequest> CREATOR = new Creator<NearbyRequest>() {
        @Override
        public NearbyRequest createFromParcel(Parcel in) {
            return new NearbyRequest(in);
        }

        @Override
        public NearbyRequest[] newArray(int size) {
            return new NearbyRequest[size];
        }
    };

    public String getLatLngAsString() {
        return "" + mLatLng.latitude + "," + mLatLng.longitude;
    }

    public String getRadiusAsString() {
        return "" + mRadius;
    }

    public String getTypesAsString() {

        String concatenated = "";

        for (int i = 0; i < mTypes.length-1; i++)
            concatenated += mTypes[i] + "|";

        concatenated += mTypes[mTypes.length-1];

        return concatenated;
    }

    public String getLanguage() {
        return mLanguage;
    }

    public String getKeyword() { return mKeyword; }

    public String getRank() {
        return mRank;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(mLatLng, i);
        parcel.writeInt(mRadius);
        parcel.writeStringArray(mTypes);
        parcel.writeString(mKeyword);
        parcel.writeString(mLanguage);
        parcel.writeString(mRank);
    }
}
