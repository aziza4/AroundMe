package com.example.jbt.aroundme.Helpers;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import com.example.jbt.aroundme.Data.Place;
import java.util.ArrayList;

public class AroundMeDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "places.db";
    private static final int DATABASE_VERSION = 1;

    private static final String SEARCH_TABLE_NAME = "search";
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
    private static final String DETAILS_COL_ADDRESS = "address";
    private static final String DETAILS_COL_PHONE = "phone";
    private static final String DETAILS_COL_INTL_PHONE = "intl_phone";
    private static final String DETAILS_COL_URL = "url";

    private final Context mContext;

    public AroundMeDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        String createSearchTable = String.format(
                "CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT NOT NULL, " +   // table-name, id, name
                        "%s REAL, %s REAL, %s TEXT, " +                                         // lat, lng, icon
                        "%s TEXT, %s BLOB, %s TEXT, %s REAL, " +                                // photo-ref, photo, place-id, rating
                        "%s TEXT, %s TEXT, %s TEXT, %s TEXT, " +                                // ref, scope, types, vicinity
                        "%s TEXT, %s TEXT, %s TEXT, %s TEXT " +                                 // address, phone, intl-phone, url
                        ");",
                SEARCH_TABLE_NAME, SEARCH_COL_ID, SEARCH_COL_NAME,
                SEARCH_COL_LOC_LAT, SEARCH_COL_LOC_LNG, SEARCH_COL_ICON,
                SEARCH_COL_PHOTO_REF, SEARCH_COL_PHOTO, SEARCH_COL_PLACE_ID, SEARCH_COL_RATING,
                SEARCH_COL_REFERENCE, SEARCH_COL_SCOPE, SEARCH_COL_TYPES, SEARCH_COL_VICINITY,
                DETAILS_COL_ADDRESS, DETAILS_COL_PHONE, DETAILS_COL_INTL_PHONE, DETAILS_COL_URL);

        db.execSQL(createSearchTable);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}



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


    public void bulkInsertSearchResults(ArrayList<Place> places) {

        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();

        try
        {
            for (int i=0; i < places.size(); i++)
            {
                ContentValues values = GetValues(places.get(i));
                db.insert(SEARCH_TABLE_NAME, null, values);
            }

            db.setTransactionSuccessful();

        } finally {
            db.endTransaction();
        }

        db.close();
    }


    public boolean updatePlace(Place place) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = GetValues(place);
        long rowsAffected = db.update(SEARCH_TABLE_NAME, values, SEARCH_COL_ID + " = " + place.getId(), null);

        db.close();

        return rowsAffected > 0;
    }


    public boolean deletePlace(long id) {

        SQLiteDatabase db = getWritableDatabase();
        long rowsDeleted = db.delete(SEARCH_TABLE_NAME, SEARCH_COL_ID + "=?" +  id , null);
        db.close();

        return rowsDeleted > 0;
    }


    public boolean deleteAllPlaces() {

        SQLiteDatabase db = getWritableDatabase();
        long rowsDeleted = db.delete(SEARCH_TABLE_NAME, null , null);
        db.close();

        return rowsDeleted > 0;
    }

    private Cursor getPlacesCursor()
    {
        SQLiteDatabase db = getReadableDatabase();

        String sqlQuery = "SELECT * FROM " + SEARCH_TABLE_NAME +
                " ORDER BY " + SEARCH_COL_ID + ";";

        return db.rawQuery(sqlQuery, null);
    }


    public ArrayList<Place> getPlacesArrayList()
    {
        ArrayList<Place> places = new ArrayList<>();

        Cursor c = getPlacesCursor();

        final int id_index = c.getColumnIndex(SEARCH_COL_ID);
        final int id_name = c.getColumnIndex(SEARCH_COL_NAME);
        final int id_lat = c.getColumnIndex(SEARCH_COL_LOC_LAT);
        final int id_lng = c.getColumnIndex(SEARCH_COL_LOC_LNG);
        final int id_icon = c.getColumnIndex(SEARCH_COL_ICON);
        final int id_photo_ref = c.getColumnIndex(SEARCH_COL_PHOTO_REF);
        final int id_photo = c.getColumnIndex(SEARCH_COL_PHOTO);
        final int id_place_id = c.getColumnIndex(SEARCH_COL_PLACE_ID);
        final int id_rating = c.getColumnIndex(SEARCH_COL_RATING);
        final int id_reference = c.getColumnIndex(SEARCH_COL_REFERENCE);
        final int id_scope = c.getColumnIndex(SEARCH_COL_SCOPE);
        final int id_types = c.getColumnIndex(SEARCH_COL_TYPES);
        final int id_vicinity = c.getColumnIndex(SEARCH_COL_VICINITY);
        final int id_address = c.getColumnIndex(DETAILS_COL_ADDRESS);
        final int id_phone = c.getColumnIndex(DETAILS_COL_PHONE);
        final int id_intl_phone = c.getColumnIndex(DETAILS_COL_INTL_PHONE);
        final int id_url = c.getColumnIndex(DETAILS_COL_URL);

        while(c.moveToNext()) {

            long _id = c.getInt(id_index);
            String name = c.getString(id_name);
            float lat = c.getFloat(id_lat);
            float lng = c.getFloat(id_lng);
            String icon = c.getString(id_icon);
            String photoRef = c.getString(id_photo_ref);
            Bitmap bitmap = ImageHelper.convertByteArrayToBitmap(c.getBlob(id_photo));
            String placeId = c.getString(id_place_id);
            float rating = c.getFloat(id_rating);
            String reference = c.getString(id_reference);
            String scope = c.getString(id_scope);
            String types = c.getString(id_types);
            String vicinity = c.getString(id_vicinity);
            String address = c.getString(id_address);
            String phone = c.getString(id_phone);
            String intlPhone = c.getString(id_intl_phone);
            String url = c.getString(id_url);

            Place place = new Place(
                    _id, lat, lng, icon, name,
                    photoRef, bitmap, placeId, rating,
                    reference, scope, types, vicinity,
                    address, phone, intlPhone, url);

            places.add(place);
        }

        c.close();
        return places;
    }
}
