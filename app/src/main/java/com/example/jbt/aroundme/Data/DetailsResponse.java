package com.example.jbt.aroundme.data;


import android.os.Parcel;
import android.os.Parcelable;

// Favorites' response object to share information between GooglePlacesNearbyHelper object and its client
// Basically it contains place's details plus additional metadata (status)
public class DetailsResponse implements Parcelable {

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
