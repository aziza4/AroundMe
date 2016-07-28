package com.comli.shapira.aroundme.helpers;


import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v4.content.LocalBroadcastManager;
import com.comli.shapira.aroundme.data.Place;
import java.util.ArrayList;

public class BroadcastHelper
{

    // Search - Places saved
    public static final String ACTION_NEARBY_NOTIFY_PLACES_SAVED = "com.comli.shapira.aroundme.Services.action.ACTION_NEARBY_NOTIFY_PLACES_SAVED";
    public static final String EXTRA_NEARBY_PLACES_SAVED = "com.comli.shapira.aroundme.Services.extra.nearbyplaces.places.saved";

    // Search - Places error
    public static final String ACTION_NEARBY_NOTIFY_PLACES_ERROR_MESSAGE = "com.comli.shapira.aroundme.Services.action.ACTION_NEARBY_NOTIFY_PLACES_ERROR_MESSAGE";
    public static final String EXTRA_NEARBY_ERROR_MESSAGE = "com.comli.shapira.aroundme.Services.extra.nearbyplaces.places.isError";

    // Search - Places removed
    public static final String ACTION_NEARBY_NOTIFY_PLACES_REMOVED = "com.comli.shapira.aroundme.Services.action.ACTION_NEARBY_NOTIFY_PLACES_REMOVED";


    // Favorites - Place saved
    public static final String ACTION_FAVORITES_NOTIFY_PLACE_SAVED = "com.comli.shapira.aroundme.Services.action.ACTION_FAVORITES_NOTIFY_PLACE_SAVED";

    // Favorites - Place removed
    public static final String ACTION_FAVORITES_NOTIFY_PLACE_REMOVED = "com.comli.shapira.aroundme.Services.action.ACTION_FAVORITES_NOTIFY_PLACE_REMOVED";

    // Favorites - All Places removed
    public static final String ACTION_FAVORITES_NOTIFY_ALL_PLACES_REMOVED = "com.comli.shapira.aroundme.Services.action.ACTION_FAVORITES_NOTIFY_ALL_PLACES_REMOVED";

    // Location Provider - location available
    public static final String ACTION_LOCATION_CHANGED_NOTIFY = "com.comli.shapira.aroundme.Services.action.ACTION_LOCATION_CHANGED_NOTIFY";
    public static final String EXTRA_LOCATION_LOCATION_DATA = "com.comli.shapira.aroundme.Services.extra.location.data";
    public static final String EXTRA_LOCATION_PROVIDER_NAME = "com.comli.shapira.aroundme.Services.extra.provider.name";

    // Location Provider - location NOT available
    public static final String ACTION_LOCATION_NOT_AVAILABLE_NOTIFY = "com.comli.shapira.aroundme.Services.action.ACTION_LOCATION_NOT_AVAILABLE_NOTIFY";



    public static void broadcastSearchPlacesSaved(Context context, ArrayList<Place> places)
    {
        Intent intent = new Intent(ACTION_NEARBY_NOTIFY_PLACES_SAVED);
        intent.putExtra(EXTRA_NEARBY_PLACES_SAVED, places.size());
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }


    public static void broadcastSearchPlacesClearPrevSearch(Context context)
    {
        Intent intent = new Intent(ACTION_NEARBY_NOTIFY_PLACES_REMOVED);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }


    public static void broadcastSearchError(Context context, String error)
    {
        Intent intent = new Intent(ACTION_NEARBY_NOTIFY_PLACES_ERROR_MESSAGE);
        intent.putExtra(EXTRA_NEARBY_ERROR_MESSAGE, error);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }


    public static void broadcastFavoritesPlaceSaved(Context context)
    {
        broadcastFavoritesOperation(context, ACTION_FAVORITES_NOTIFY_PLACE_SAVED);
    }


    public static void broadcastFavoritesPlaceDeleted(Context context)
    {
        broadcastFavoritesOperation(context, ACTION_FAVORITES_NOTIFY_PLACE_REMOVED);
    }


    public static void broadcastFavoritesPlacesDeletedAll(Context context)
    {
        broadcastFavoritesOperation(context, ACTION_FAVORITES_NOTIFY_ALL_PLACES_REMOVED);
    }


    private static void broadcastFavoritesOperation(Context context, String action)
    {
        Intent intent = new Intent(action);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }


    public static void broadcastOnLocationChanged(Context context, Location location, String providerName)
    {
        Intent intent = new Intent(ACTION_LOCATION_CHANGED_NOTIFY);
        intent.putExtra(EXTRA_LOCATION_LOCATION_DATA, location);
        intent.putExtra(EXTRA_LOCATION_PROVIDER_NAME, providerName);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public static void broadcastOnLocationNotAvailable(Context context)
    {
        Intent intent = new Intent(ACTION_LOCATION_NOT_AVAILABLE_NOTIFY);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
