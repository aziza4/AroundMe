package com.example.jbt.aroundme.Data;


import java.util.ArrayList;

public class NearbyResponse {

    private ArrayList<Place> mPlaces;
    private String mNextPageToken;
    private String[] mHtmlAttributions;

    public NearbyResponse(ArrayList<Place> places, String nextPageToken, String[] htmlAttributions) {
        this.mPlaces = places;
        this.mNextPageToken = nextPageToken;
        this.mHtmlAttributions = htmlAttributions;
    }

    public ArrayList<Place> getPlaces() {
        return mPlaces;
    }

    public String getNextPageToken() {
        return mNextPageToken;
    }

    public String[] getHtmlAttributions() {
        return mHtmlAttributions;
    }
}
