package com.example.jbt.aroundme.Data;


import android.os.Parcel;
import android.os.Parcelable;


public class DetailsResponse implements Parcelable {

    public static final String STATUS_OK = "OK";

    private final String mStatus;
    private final Place mPlace;

    public DetailsResponse(String status, Place place)
    {
        mStatus = status;
        mPlace = place;
    }

    private DetailsResponse(Parcel in) {
        mStatus = in.readString();
        mPlace = in.readParcelable(Place.class.getClassLoader());
    }

    public static final Creator<DetailsResponse> CREATOR = new Creator<DetailsResponse>() {
        @Override
        public DetailsResponse createFromParcel(Parcel in) {
            return new DetailsResponse(in);
        }

        @Override
        public DetailsResponse[] newArray(int size) {
            return new DetailsResponse[size];
        }
    };

    public String getStatus() {
        return mStatus;
    }

    public Place getPlace() {
        return mPlace;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mStatus);
        parcel.writeParcelable(mPlace, i);
    }
}
