package com.comli.shapira.aroundme.data;


import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;


public class LastLocationInfo implements Parcelable {

    private Location mLocation;
    private boolean mAlertDialogOn;


    public LastLocationInfo(Location location, boolean alertDialogOn) {
        mLocation = location;
        mAlertDialogOn = alertDialogOn;
    }


    public Location getLocation() {
        return mLocation;
    }
    public boolean getAlertDialogOn() {
        return mAlertDialogOn;
    }



    protected LastLocationInfo(Parcel in) {
        mLocation = in.readParcelable(Location.class.getClassLoader());
        mAlertDialogOn = in.readByte() != 0;
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
        parcel.writeParcelable(mLocation, i);
        parcel.writeByte((byte) (mAlertDialogOn ? 1 : 0));
    }
}
