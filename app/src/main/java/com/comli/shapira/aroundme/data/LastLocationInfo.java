package com.comli.shapira.aroundme.data;


import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;


public class LastLocationInfo implements Parcelable {

    private String mProvider;
    private Location mLocation;


    public LastLocationInfo(String provider, Location location) {
        mProvider = provider;
        mLocation = location;
    }

    public String getProvider() {
        return mProvider;
    }

    public Location getLocation() {
        return mLocation;
    }

    protected LastLocationInfo(Parcel in) {
        mProvider = in.readString();
        mLocation = in.readParcelable(Location.class.getClassLoader());
    }

    public static final Creator<LastLocationInfo> CREATOR = new Creator<LastLocationInfo>() {
        @Override
        public LastLocationInfo createFromParcel(Parcel in) {
            return new LastLocationInfo(in);
        }

        @Override
        public LastLocationInfo[] newArray(int size) {
            return new LastLocationInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mProvider);
        parcel.writeParcelable(mLocation, i);
    }
}
