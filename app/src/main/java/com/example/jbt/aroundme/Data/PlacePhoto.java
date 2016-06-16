package com.example.jbt.aroundme.Data;


import android.os.Parcel;
import android.os.Parcelable;

public class PlacePhoto implements Parcelable {

    private final int mHeight;
    private final int mWidth;
    private final String[] mHtmlAttributionArr;
    private final String mReference;

    public PlacePhoto(String reference)
    {
        this(0, 0, null, reference);
    }


    public PlacePhoto(int height, int width, String[] htmlAttributionArr, String reference)
    {
        this.mHeight = height;
        this.mWidth = width;
        this.mHtmlAttributionArr = htmlAttributionArr;
        this.mReference = reference;
    }

    protected PlacePhoto(Parcel in) {
        mHeight = in.readInt();
        mWidth = in.readInt();
        mHtmlAttributionArr = in.createStringArray();
        mReference = in.readString();
    }

    public static final Creator<PlacePhoto> CREATOR = new Creator<PlacePhoto>() {
        @Override
        public PlacePhoto createFromParcel(Parcel in) {
            return new PlacePhoto(in);
        }

        @Override
        public PlacePhoto[] newArray(int size) {
            return new PlacePhoto[size];
        }
    };

    public int getHeight() {
        return mHeight;
    }

    public int getWidth() {
        return mWidth;
    }

    public String[] getHtmlAttributionArr() {
        return mHtmlAttributionArr;
    }

    public String getReference() {
        return mReference;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(mHeight);
        parcel.writeInt(mWidth);
        parcel.writeStringArray(mHtmlAttributionArr);
        parcel.writeString(mReference);
    }
}
