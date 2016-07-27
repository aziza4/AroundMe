package com.comli.shapira.aroundme.helpers;


import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v4.content.LocalBroadcastManager;
import com.comli.shapira.aroundme.data.Place;
import java.util.ArrayList;

public class BroadcastHelper
{

    public static final String ACTION_NEARBY_NOTIFY = "com.comli.shapira.aroundme.Services.action.ACTION_NEARBY_NOTIFY";
    public static final String EXTRA_NEARBY_PLACES_SAVED = "com.comli.shapira.aroundme.Services.extra.nearbyplaces.places.saved";
    public static final String EXTRA_NEARBY_ERROR_MESSAGE = "com.comli.shapira.aroundme.Services.extra.nearbyplaces.places.isError";
    public static final String EXTRA_NEARBY_CLEAR_LIST = "com.comli.shapira.aroundme.Services.extra.nearbyplaces.places.clearList";

    public static final String ACTION_FAVORITES_NOTIFY = "com.comli.shapira.aroundme.Services.action.ACTION_FAVORITES_NOTIFY";
    public static final String EXTRA_FAVORITES_PLACE_SAVED = "com.comli.shapira.aroundme.Services.extra.placedetails.saved";
    public static final String EXTRA_FAVORITES_PLACE_REMOVED = "com.comli.shapira.aroundme.Services.extra.placedetails.removed";
    public static final String EXTRA_FAVORITES_PLACE_REMOVED_ALL = "com.comli.shapira.aroundme.Services.extra.placedetails.removedall";

    public static final String ACTION_LOCATION_CHANGED_NOTIFY = "com.comli.shapira.aroundme.Services.action.ACTION_LOCATION_CHANGED_NOTIFY";
    public static final String EXTRA_LOCATION_LOCATION_DATA = "com.comli.shapira.aroundme.Services.extra.location.data";
    public static final String EXTRA_LOCATION_PROVIDER_NAME = "com.comli.shapira.aroundme.Services.extra.provider.name";

    public static final String ACTION_LOCATION_NOT_AVAILABLE_NOTIFY = "com.comli.shapira.aroundme.Services.action.ACTION_LOCATION_NOT_AVAILABLE_NOTIFY";



    public static void broadcastSearchPlacesSaved(Context context, ArrayList<Place> places)
    {
        Intent intent = new Intent(ACTION_NEARBY_NOTIFY);
        intent.putExtra(EXTRA_NEARBY_PLACES_SAVED, places.size());
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public static void broadcastSearchPlacesClearPrevSearch(Context context)
    {
        Intent intent = new Intent(ACTION_NEARBY_NOTIFY);
        intent.putExtra(EXTRA_NEARBY_CLEAR_LIST, true);
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

    public static void broadcastSearchError(Context context, String error)
    {
        Intent intent = new Intent(ACTION_NEARBY_NOTIFY);
        intent.putExtra(EXTRA_NEARBY_ERROR_MESSAGE, error);
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
