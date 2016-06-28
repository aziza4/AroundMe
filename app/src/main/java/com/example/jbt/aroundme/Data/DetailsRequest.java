package com.example.jbt.aroundme.data;

import android.os.Parcel;
import android.os.Parcelable;


// Favorites' request object to share information between activity and service
// Basically it (currently) only encapsulate the Place object
public class DetailsRequest implements Parcelable {


    private final Place mPlace;

    public DetailsRequest(Place place)
    {
        mPlace = place;
    }

    public Place getPlace() {
        return mPlace;
    }

    private DetailsRequest(Parcel in) {
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
