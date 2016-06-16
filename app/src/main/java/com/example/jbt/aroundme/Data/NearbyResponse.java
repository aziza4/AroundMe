package com.example.jbt.aroundme.Data;


import java.io.Serializable;
import java.util.ArrayList;

public class NearbyResponse implements Serializable {

    public static final String STATUS_OK = "OK";
    public static final String STATUS_ZERO_RESULTS = "ZERO_RESULTS";
    public static final String STATUS_OVER_QUERY_LIMIT = "OVER_QUERY_LIMIT";
    public static final String STATUS_REQUEST_DENIED = "REQUEST_DENIED";
    public static final String STATUS_INVALID_REQUEST = "INVALID_REQUEST";

    private String mStatus;
    private ArrayList<Place> mPlaces;
    private String mNextPageToken;
    private String[] mHtmlAttributions;

    public NearbyResponse(String status, ArrayList<Place> places, String nextPageToken, String[] htmlAttributions) {

        this.mStatus = status;
        this.mPlaces = places;
        this.mNextPageToken = nextPageToken;
        this.mHtmlAttributions = htmlAttributions;
    }

    public String getStatus() { return mStatus; }

    public ArrayList<Place> getPlaces() { return mPlaces; }

    public String getNextPageToken() {
        return mNextPageToken;
    }
}
