package com.comli.shapira.aroundme.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;

import com.comli.shapira.aroundme.data.Place;
import com.comli.shapira.aroundme.helpers.ImageHelper;
import com.comli.shapira.aroundme.helpers.Utility;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class AroundMeDBHelper extends SQLiteOpenHelper {

    private final Context mContext;

    private static final String DATABASE_NAME = "places.db";
    private static final int DATABASE_VERSION = 1;

    private static final String SEARCH_TABLE_NAME = "search";
    private static final String FAVORITES_TABLE_NAME = "favorites";

    // These columns are used by both 'search' and 'favorites' table
    private static final String SEARCH_COL_ID = "_id";
    private static final String SEARCH_COL_NAME = "name";
    private static final String SEARCH_COL_LOC_LAT = "lat";
    private static final String SEARCH_COL_LOC_LNG = "lng";
    private static final String SEARCH_COL_ICON = "icon";
    private static final String SEARCH_COL_PHOTO_REF = "photo_ref";
    private static final String SEARCH_COL_PHOTO = "photo";
    private static final String SEARCH_COL_PLACE_ID = "place_id";
    private static final String SEARCH_COL_RATING = "rating";
    private static final String SEARCH_COL_REFERENCE = "reference";
    private static final String SEARCH_COL_SCOPE = "scope";
    private static final String SEARCH_COL_TYPES = "types";
    private static final String SEARCH_COL_VICINITY = "vicinity";

    // These columns are used only by 'favorites' table, in addition to the above columns
    private static final String DETAILS_COL_ADDRESS = "address";
    private static final String DETAILS_COL_PHONE = "phone";
    private static final String DETAILS_COL_INTL_PHONE = "intl_phone";
    private static final String DETAILS_COL_URL = "url";


    public AroundMeDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        onCreate(db, SEARCH_TABLE_NAME);
        onCreate(db, FAVORITES_TABLE_NAME);
    }

    private void onCreate(SQLiteDatabase db, String tableName) {

        String searchTable = String.format(
                "CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT NOT NULL, " +   // table-name, id, name
                        "%s REAL, %s REAL, %s TEXT, " +                                         // lat, lng, icon
                        "%s TEXT, %s BLOB, %s TEXT, %s REAL, " +                                // photo-ref, photo, place-id, rating
                        "%s TEXT, %s TEXT, %s TEXT, %s TEXT, " +                                // ref, scope, types, vicinity
                        "%s TEXT, %s TEXT, %s TEXT, %s TEXT " +                                 // address, phone, intl-phone, url
                        ");",
                tableName, SEARCH_COL_ID, SEARCH_COL_NAME,
                SEARCH_COL_LOC_LAT, SEARCH_COL_LOC_LNG, SEARCH_COL_ICON,
                SEARCH_COL_PHOTO_REF, SEARCH_COL_PHOTO, SEARCH_COL_PLACE_ID, SEARCH_COL_RATING,
                SEARCH_COL_REFERENCE, SEARCH_COL_SCOPE, SEARCH_COL_TYPES, SEARCH_COL_VICINITY,
                DETAILS_COL_ADDRESS, DETAILS_COL_PHONE, DETAILS_COL_INTL_PHONE, DETAILS_COL_URL);

        db.execSQL(searchTable);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}


    public void searchBulkInsert(ArrayList<Place> places) { bulkInsert(places, SEARCH_TABLE_NAME); }
    public boolean searchUpdatePlace(Place place) { return updatePlace(place, SEARCH_TABLE_NAME); }
    public boolean searchDeleteAllPlaces() { return deleteAllPlaces(SEARCH_TABLE_NAME); }
    public ArrayList<Place> searchGetArrayList() { return getArrayList(SEARCH_TABLE_NAME); }
    public Place searchGetPlace(long id) { return getPlace(SEARCH_TABLE_NAME, id); }

    public void favoritesInsertPlace(Place place) { insertPlace(place, FAVORITES_TABLE_NAME); }
    public boolean favoritesDeletePlace(long id) { return deletePlace(id, FAVORITES_TABLE_NAME); }
    public boolean favoritesDeleteAllPlaces() { return deleteAllPlaces(FAVORITES_TABLE_NAME); }
    public ArrayList<Place> favoritesGetArrayList() { return getArrayList(FAVORITES_TABLE_NAME); }
    public Place favoriteGetPlace(long id) { return getPlace(FAVORITES_TABLE_NAME, id); }



    private ContentValues GetValues(Place place)
    {
        ContentValues values = new ContentValues();

        values.put(SEARCH_COL_NAME, place.getName());
        values.put(SEARCH_COL_LOC_LAT, place.getLoc().latitude);
        values.put(SEARCH_COL_LOC_LNG, place.getLoc().longitude);
        values.put(SEARCH_COL_ICON, place.getIcon());
        values.put(SEARCH_COL_PHOTO_REF, place.getPhotoRef());
        values.put(SEARCH_COL_PHOTO, place.getPhotoByteArray());
        values.put(SEARCH_COL_PLACE_ID, place.getPlaceId());
        values.put(SEARCH_COL_RATING, place.getRating());
        values.put(SEARCH_COL_REFERENCE, place.getReference());
        values.put(SEARCH_COL_SCOPE, place.getScope());
        values.put(SEARCH_COL_TYPES, place.getTypesAsString());
        values.put(SEARCH_COL_VICINITY, place.getVicinity());
        values.put(DETAILS_COL_ADDRESS, place.getAddress());
        values.put(DETAILS_COL_PHONE, place.getPhone());
        values.put(DETAILS_COL_INTL_PHONE, place.getIntlPhone());
        values.put(DETAILS_COL_URL, place.getUrl());

        return values;
    }



    private void bulkInsert(ArrayList<Place> places, String tableName) {

        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();

        try
        {
            for (int i=0; i < places.size(); i++)
            {
                ContentValues values = GetValues(places.get(i));
                db.insert(tableName, null, values);
            }

            db.setTransactionSuccessful();

        } finally {
            db.endTransaction();
        }

        db.close();
    }


    private void insertPlace(Place place, String tableName) {

        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();

        try
        {
            ContentValues values = GetValues(place);
            db.insert(tableName, null, values);

            db.setTransactionSuccessful();

        } finally {
            db.endTransaction();
        }

        db.close();
    }


    private boolean updatePlace(Place place, String tableName) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = GetValues(place);
        long rowsAffected = db.update(tableName, values, SEARCH_COL_ID + " = " + place.getId(), null);

        db.close();

        return rowsAffected > 0;
    }



    private boolean deletePlace(long id, String tableName) {

        SQLiteDatabase db = getWritableDatabase();

        long rowsDeleted = db.delete(tableName, SEARCH_COL_ID + "=" +  id , null);
        db.close();

        return rowsDeleted > 0;
    }


    private boolean deleteAllPlaces(String tableName) {

        SQLiteDatabase db = getWritableDatabase();
        long rowsDeleted = db.delete(tableName, null , null);
        db.close();

        return rowsDeleted > 0;
    }


    private Cursor getPlacesCursor(String tableName)
    {
        SQLiteDatabase db = getReadableDatabase();

        String sortOrder = tableName.equals(FAVORITES_TABLE_NAME) ? " DESC" : "";

        String sqlQuery = "SELECT * FROM " + tableName +
                " ORDER BY " + SEARCH_COL_ID + sortOrder + ";";

        return db.rawQuery(sqlQuery, null);
    }

    private Place getPlace(String tableName, long id)
    {
        SQLiteDatabase db = getReadableDatabase();
        Place place = null;

        String query = "SELECT * FROM " +  tableName + " WHERE " +
                SEARCH_COL_ID + "=" + id + ";";

        Cursor c = db.rawQuery(query, null);
        Columns cols = new Columns(c);

        try {

            if( c.moveToFirst() )
                place = extractPlaceFromCursor(c, cols);

        } finally {

            c.close();
            db.close();
        }

        return place;
    }


    private ArrayList<Place> getArrayList(String tableName)
    {
        ArrayList<Place> places = new ArrayList<>();
        Cursor c = getPlacesCursor(tableName);
        Columns cols = new Columns(c);

        try {

            while (c.moveToNext())
                places.add(extractPlaceFromCursor(c, cols));

        } finally {

            c.close();
        }

        return places;
    }


    private Place extractPlaceFromCursor(Cursor c, Columns cols)
    {
        long _id = c.getInt(cols.id_index);
        String name = c.getString(cols.id_name);
        float lat = c.getFloat(cols.id_lat);
        float lng = c.getFloat(cols.id_lng);
        String icon = c.getString(cols.id_icon);
        String photoRef = c.getString(cols.id_photo_ref);
        Bitmap bitmap = ImageHelper.convertByteArrayToBitmap(c.getBlob(cols.id_photo));
        String placeId = c.getString(cols.id_place_id);
        float rating = c.getFloat(cols.id_rating);
        String reference = c.getString(cols.id_reference);
        String scope = c.getString(cols.id_scope);
        String types = c.getString(cols.id_types);
        String vicinity = c.getString(cols.id_vicinity);
        String address = c.getString(cols.id_address);
        String phone = c.getString(cols.id_phone);
        String intlPhone = c.getString(cols.id_intl_phone);
        String url = c.getString(cols.id_url);

        double distance = Utility.getDistanceInKM(mContext, new LatLng(lat, lng));

        return new Place(
                _id, lat, lng, icon, name,
                photoRef, bitmap, placeId, rating,
                reference, scope, types, vicinity,
                address, phone, intlPhone, url, distance);
    }



    private class Columns {

        private final int id_index;
        private final int id_name;
        private final int id_lat;
        private final int id_lng;
        private final int id_icon;
        private final int id_photo_ref;
        private final int id_photo;
        private final int id_place_id;
        private final int id_rating;
        private final int id_reference;
        private final int id_scope;
        private final int id_types;
        private final int id_vicinity;
        private final int id_address;
        private final int id_phone;
        private final int id_intl_phone;
        private final int id_url;

        public Columns(Cursor c)
        {
            id_index = c.getColumnIndex(SEARCH_COL_ID);
            id_name = c.getColumnIndex(SEARCH_COL_NAME);
            id_lat = c.getColumnIndex(SEARCH_COL_LOC_LAT);
            id_lng = c.getColumnIndex(SEARCH_COL_LOC_LNG);
            id_icon = c.getColumnIndex(SEARCH_COL_ICON);
            id_photo_ref = c.getColumnIndex(SEARCH_COL_PHOTO_REF);
            id_photo = c.getColumnIndex(SEARCH_COL_PHOTO);
            id_place_id = c.getColumnIndex(SEARCH_COL_PLACE_ID);
            id_rating = c.getColumnIndex(SEARCH_COL_RATING);
            id_reference = c.getColumnIndex(SEARCH_COL_REFERENCE);
            id_scope = c.getColumnIndex(SEARCH_COL_SCOPE);
            id_types = c.getColumnIndex(SEARCH_COL_TYPES);
            id_vicinity = c.getColumnIndex(SEARCH_COL_VICINITY);
            id_address = c.getColumnIndex(DETAILS_COL_ADDRESS);
            id_phone = c.getColumnIndex(DETAILS_COL_PHONE);
            id_intl_phone = c.getColumnIndex(DETAILS_COL_INTL_PHONE);
            id_url = c.getColumnIndex(DETAILS_COL_URL);
        }
    }
}

