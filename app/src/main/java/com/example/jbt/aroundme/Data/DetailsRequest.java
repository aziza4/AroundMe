package com.example.jbt.aroundme.Data;

import android.os.Parcel;
import android.os.Parcelable;


public class DetailsRequest implements Parcelable {


    private Place mPlace;

    public DetailsRequest(Place place)
    {
        mPlace = place;
    }

    public Place getPlace() {
        return mPlace;
    }

    protected DetailsRequest(Parcel in) {
        mPlace = in.readParcelable(Place.class.getClassLoader());
    }

    public static final Creator<DetailsRequest> CREATOR = new Creator<DetailsRequest>() {
        @Override
        public DetailsRequest createFromParcel(Parcel in) {
            return new DetailsRequest(in);
        }

        @Override
        public DetailsRequest[] newArray(int size) {
            return new DetailsRequest[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(mPlace, i);
    }
}
