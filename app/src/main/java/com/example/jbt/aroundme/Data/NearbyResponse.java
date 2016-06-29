package com.example.jbt.aroundme.data;


import java.io.Serializable;
import java.util.ArrayList;

// Search' response object to share information between GooglePlacesNearbyHelper object and its client
// Basically it contains list of places plus additional metadata (status, next-page-token, attribution)
public class NearbyResponse implements Serializable {

    public static final String STATUS_OK = "OK";
    public static final String STATUS_ZERO_RESULTS = "ZERO_RESULTS";
    public static final String STATUS_OVER_QUERY_LIMIT = "OVER_QUERY_LIMIT";
    public static final String STATUS_REQUEST_DENIED = "REQUEST_DENIED";
    public static final String STATUS_INVALID_REQUEST = "INVALID_REQUEST";

    private final String mStatus;
    private final ArrayList<Place> mPlaces;
    private final String mNextPageToken;


    private final String[] mHtmlAttributions;

    public NearbyResponse(String status, ArrayList<Place> places, String nextPageToken, String[] htmlAttributions) {

        this.mStatus = status;
        this.mPlaces = places;
        this.mNextPageToken = nextPageToken;
        this.mHtmlAttributions = htmlAttributions;
    }

    public boolean isOK()
    {
        return mStatus.equals(STATUS_OK) || mStatus.equals(STATUS_ZERO_RESULTS);
    }

    public boolean isError()
    {
        return mStatus.equals(STATUS_OVER_QUERY_LIMIT) ||
                mStatus.equals(STATUS_REQUEST_DENIED) ||
                mStatus.equals(STATUS_INVALID_REQUEST);
    }

    public String getStatus() { return mStatus; }

    public ArrayList<Place> getPlaces() { return mPlaces; }

    public String getNextPageToken() {
        return mNextPageToken;
    }

    public String[] getHtmlAttributions() { return mHtmlAttributions; }
}
