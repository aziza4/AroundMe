package com.comli.shapira.aroundme.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Location;
import android.preference.PreferenceManager;
import com.comli.shapira.aroundme.R;
import com.google.android.gms.maps.model.LatLng;

import java.util.Locale;


public class SharedPrefHelper {

    private final Context mContext;
    private final SharedPreferences mPrefs;



    public SharedPrefHelper(Context context)
    {
        mContext = context;
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @SuppressWarnings("deprecation")
    public void changeLocale() {

        String key = mContext.getString(R.string.pref_lang_key);
        String def = mContext.getString(R.string.pref_lang_def);
        String lang = mPrefs.getString(key, def);

        Locale locale = new Locale(lang);
        Locale.setDefault(locale);

        Configuration configuration = new Configuration();
        configuration.locale = locale;
        Resources resources = mContext.getResources();
        resources.updateConfiguration(configuration, mContext.getResources().getDisplayMetrics());
    }

    public String getSelectedLanguage() {
        String key = mContext.getString(R.string.pref_lang_key);
        String def = mContext.getString(R.string.pref_lang_def);
        return mPrefs.getString(key, def);
    }


    public boolean isEnglish() {
        String english = mContext.getString(R.string.pref_lang_english);
        return getSelectedLanguage().equals(english);
    }

    public boolean isMeters() {
        String key = mContext.getString(R.string.pref_units_key);
        String def = mContext.getString(R.string.pref_units_def);
        String meters = mContext.getString(R.string.pref_units_meters);
        return mPrefs.getString(key, def).equals(meters);
    }

    public int getRadius() {
        String key = mContext.getString(R.string.pref_radius_key);
        String def = mContext.getString(R.string.pref_radius_def);
        String val_string = mPrefs.getString(key, "");
        return Integer.parseInt( val_string.equals("") ? def: val_string);
//      return mPrefs.getInt(key, Integer.parseInt(def)); --> not working! see: http://stackoverflow.com/questions/3721358/preferenceactivity-save-value-as-integer
    }

    public String getSelectedTypes() {
        String key = mContext.getString(R.string.pref_types_key);
        String def = mContext.getString(R.string.pref_types_def);
        return mPrefs.getString(key, def);
    }


    public void saveLastUserLocation(Location location)
    {
        String lat_key = mContext.getString(R.string.shared_pref_last_location_lat);
        String lng_key = mContext.getString(R.string.shared_pref_last_location_lng);

        mPrefs.edit()
                .putFloat(lat_key, (float)location.getLatitude())
                .putFloat(lng_key, (float)location.getLongitude())
                .apply();
    }

    public LatLng getLastUserLatLng()
    {
        String lat_key = mContext.getString(R.string.shared_pref_last_location_lat);
        String lng_key = mContext.getString(R.string.shared_pref_last_location_lng);

        double lat_val = mPrefs.getFloat(lat_key, 0);
        double lng_val = mPrefs.getFloat(lng_key, 0);

        return new LatLng(lat_val, lng_val);
    }

    public boolean lastUserLocationExist() {

        String lat_key = mContext.getString(R.string.shared_pref_last_location_lat);
        String lng_key = mContext.getString(R.string.shared_pref_last_location_lng);

        double lat_val = mPrefs.getFloat(lat_key, 0);
        double lng_val = mPrefs.getFloat(lng_key, 0);

        return lat_val != 0 && lng_val !=0;
    }

    public Location getLastUserLocation() {

        Location location = new Location("");
        LatLng latLng = getLastUserLatLng();
        location.setLatitude(latLng.latitude);
        location.setLongitude(latLng.longitude);
        return location;
    }

    public void setPermissionDeniedByUser(boolean isDenied) {

        String key = mContext.getString(R.string.shared_pref_loc_permission_denied_by_user);
        mPrefs.edit().putBoolean(key, isDenied).apply();
    }

    public boolean isPermissionDeniedByUser() {

        String key = mContext.getString(R.string.shared_pref_loc_permission_denied_by_user);
        return mPrefs.getBoolean(key, false);
    }

    public void setLangChanged(boolean langChanged) {

        String key = mContext.getString(R.string.shared_pref_lang_changed);
        mPrefs.edit().putBoolean(key, langChanged).apply();
    }

    public boolean isLangChanged() {

        String key = mContext.getString(R.string.shared_pref_lang_changed);
        return mPrefs.getBoolean(key, false);
    }

    public void setSearchKeyword(String keyword) {

        String key = mContext.getString(R.string.shared_pref_search_keyword);
        mPrefs.edit().putString(key, keyword).apply();
    }

    public String getSearchKeyword() {

        String key = mContext.getString(R.string.shared_pref_search_keyword);
        return mPrefs.getString(key, "");
    }

    public void onUserLeaveApplication()
    {
        String lat_key = mContext.getString(R.string.shared_pref_last_location_lat);
        String lng_key = mContext.getString(R.string.shared_pref_last_location_lng);
        String lang_changed_key = mContext.getString(R.string.shared_pref_lang_changed);
        String permission_key = mContext.getString(R.string.shared_pref_loc_permission_denied_by_user);

        mPrefs.edit()
                .remove(lat_key)
                .remove(lng_key)
                .remove(lang_changed_key)
                .remove(permission_key)
                .apply();
    }
}
