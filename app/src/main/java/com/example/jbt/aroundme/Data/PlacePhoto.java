package com.example.jbt.aroundme.Data;


public class PlacePhoto {

    private final int mHeight;
    private final int mWidth;
    private final String[] mHtmlAttributionArr;
    private final String mReference;

    public PlacePhoto(int height, int width, String[] htmlAttributionArr, String reference)
    {
        this.mHeight = height;
        this.mWidth = width;
        this.mHtmlAttributionArr = htmlAttributionArr;
        this.mReference = reference;
    }

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
}
