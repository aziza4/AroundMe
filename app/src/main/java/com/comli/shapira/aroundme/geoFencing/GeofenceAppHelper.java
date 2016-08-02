package com.comli.shapira.aroundme.geoFencing;

import android.content.Context;
import android.content.Intent;

import com.comli.shapira.aroundme.services.GeofenceService;

public class GeofenceAppHelper {

    private final Context mContext;

    public GeofenceAppHelper(Context context)
    {
        mContext = context;
    }

    public void refresh()
    {
        Intent intent = new Intent(GeofenceService.ACTION_GEOFENCE_REFRESH_WATCHING,
                null, mContext, GeofenceService.class);

        mContext.startService(intent);
    }
}
