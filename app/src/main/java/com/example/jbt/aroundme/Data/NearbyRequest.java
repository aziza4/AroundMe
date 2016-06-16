package com.example.jbt.aroundme.Data;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

public class NearbyRequest implements Parcelable {

    private LatLng mLatLng;
    private int mRadius;
    private String[] mTypes;
    private String mLanguage;
    private String mRank;

    public NearbyRequest(LatLng latLng, int radius, String[] types, String language, String rank) {
        this.mLatLng = latLng;
        this.mRadius = radius;
        this.mTypes = types;
        this.mLanguage = language;
        this.mRank = rank;
    }

    private NearbyRequest(Parcel in) {
        mLatLng = in.readParcelable(LatLng.class.getClassLoader());
        mRadius = in.readInt();
        mTypes = in.createStringArray();
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
        parcel.writeString(mLanguage);
        parcel.writeString(mRank);
    }
}
