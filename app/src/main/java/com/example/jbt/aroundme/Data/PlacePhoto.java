package com.example.jbt.aroundme.Data;


import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.jbt.aroundme.Helpers.ImageHelper;

public class PlacePhoto implements Parcelable{

    public static final String PHOTO_MAX_WIDTH = "400";

    private final int mHeight;
    private final int mWidth;
    private final String[] mHtmlAttributionArr;
    private final String mReference;
    private Bitmap mBitmap;

    public PlacePhoto(String reference, Bitmap bitmap)
    {
        this(0, 0, null, reference, bitmap);
    }


    public PlacePhoto(int height, int width, String[] htmlAttributionArr, String reference, Bitmap bitmap)
    {
        this.mHeight = height;
        this.mWidth = width;
        this.mHtmlAttributionArr = htmlAttributionArr;
        this.mReference = reference;
        this.mBitmap = bitmap;
    }


    protected PlacePhoto(Parcel in) {
        mHeight = in.readInt();
        mWidth = in.readInt();
        mHtmlAttributionArr = in.createStringArray();
        mReference = in.readString();
        mBitmap = in.readParcelable(Bitmap.class.getClassLoader());
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

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public byte[] getBitmapAsByteArray()
    {
        return mBitmap == null ? null : ImageHelper.convertBitmapToByteArray(mBitmap);

    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
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
        parcel.writeParcelable(mBitmap, i);
    }
}
