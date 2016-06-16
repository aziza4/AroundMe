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
                        "%s TEXT, %s BLOB, %s TEXT, %s REAL, " +                                  // photo-ref, photo, place-id, rating
                        "%s TEXT, %s TEXT, %s TEXT, %s TEXT" +                                  // ref, scope, types, vicinity
                        ");",
                SEARCH_TABLE_NAME, SEARCH_COL_ID, SEARCH_COL_NAME,
                SEARCH_COL_LOC_LAT, SEARCH_COL_LOC_LNG, SEARCH_COL_ICON,
                SEARCH_COL_PHOTO_REF, SEARCH_COL_PHOTO, SEARCH_COL_PLACE_ID, SEARCH_COL_RATING,
                SEARCH_COL_REFERENCE, SEARCH_COL_SCOPE, SEARCH_COL_TYPES, SEARCH_COL_VICINITY );

        db.execSQL(createSearchTable);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}


    public void bulkInsertSearchResults(ArrayList<Place> places) {

        ContentValues[] values = new ContentValues[places.size()];

        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();

        try
        {
            for (int i=0; i< places.size(); i++)
            {
                values[i] = new ContentValues();
                values[i].put(SEARCH_COL_NAME, places.get(i).getName());
                values[i].put(SEARCH_COL_LOC_LAT, places.get(i).getLoc().latitude);
                values[i].put(SEARCH_COL_LOC_LNG, places.get(i).getLoc().longitude);
                values[i].put(SEARCH_COL_ICON, places.get(i).getIcon());
                values[i].put(SEARCH_COL_PHOTO_REF, places.get(i).getPhotoRef());
                values[i].put(SEARCH_COL_PHOTO,  places.get(i).getPhoto() == null ? null : places.get(i).getPhoto().getBitmapAsByteArray());
                values[i].put(SEARCH_COL_PLACE_ID, places.get(i).getPlaceId());
                values[i].put(SEARCH_COL_RATING, places.get(i).getRating());
                values[i].put(SEARCH_COL_REFERENCE, places.get(i).getReference());
                values[i].put(SEARCH_COL_SCOPE, places.get(i).getScope());
                values[i].put(SEARCH_COL_TYPES, places.get(i).getTypesAsString());
                values[i].put(SEARCH_COL_VICINITY, places.get(i).getVicinity());

                db.insert(SEARCH_TABLE_NAME, null, values[i]);
            }

            db.setTransactionSuccessful();

        } finally {
            db.endTransaction();
        }

        db.close();
    }

    public boolean deletePlace(long id) {

        SQLiteDatabase db = getWritableDatabase();
        long rowsDeleted = db.delete(SEARCH_TABLE_NAME, SEARCH_COL_ID + " =" +  id , null);
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

            Place place = new Place(
                    _id, lat, lng, icon, name,
                    photoRef, bitmap, placeId, rating,
                    reference, scope, types, vicinity);

            places.add(place);
        }

        c.close();
        return places;
    }
}
