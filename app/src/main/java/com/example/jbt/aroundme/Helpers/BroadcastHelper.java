package com.example.jbt.aroundme.helpers;


import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.example.jbt.aroundme.data.Place;

import java.util.ArrayList;

public class BroadcastHelper
{

    public static final String ACTION_NEARBY_NOTIFY = "com.example.jbt.aroundme.Services.action.ACTION_NEARBY_NOTIFY";
    public static final String EXTRA_NEARBY_PLACES_SAVED = "com.example.jbt.aroundme.Services.extra.nearbyplaces.places.saved";

    public static final String ACTION_FAVORITES_NOTIFY = "com.example.jbt.aroundme.Services.action.ACTION_FAVORITES_NOTIFY";
    public static final String EXTRA_FAVORITES_PLACE_SAVED = "com.example.jbt.aroundme.Services.extra.placedetails.saved";
    public static final String EXTRA_FAVORITES_PLACE_REMOVED = "com.example.jbt.aroundme.Services.extra.placedetails.removed";
    public static final String EXTRA_FAVORITES_PLACE_REMOVED_ALL = "com.example.jbt.aroundme.Services.extra.placedetails.removedall";


    public static void broadcastSearchPlacesSaved(Context context, ArrayList<Place> places)
    {
        Intent intent = new Intent(ACTION_NEARBY_NOTIFY);
        intent.putExtra(EXTRA_NEARBY_PLACES_SAVED, places.size());
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public static void broadcastFavoritesPlaceSaved(Context context)
    {
        broadcastFavoritesOperation(context, EXTRA_FAVORITES_PLACE_SAVED);
    }

    public static void broadcastFavoritesPlaceDeleted(Context context)
    {
        broadcastFavoritesOperation(context, EXTRA_FAVORITES_PLACE_REMOVED);
    }

    public static void broadcastFavoritesPlacesDeletedAll(Context context)
    {
        broadcastFavoritesOperation(context, EXTRA_FAVORITES_PLACE_REMOVED_ALL);
    }

    private static void broadcastFavoritesOperation(Context context, String extra)
    {
        Intent intent = new Intent(ACTION_FAVORITES_NOTIFY);
        intent.putExtra(extra, 1);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
