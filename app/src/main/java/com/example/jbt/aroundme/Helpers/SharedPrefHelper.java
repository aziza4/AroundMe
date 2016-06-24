package com.example.jbt.aroundme.Helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import com.example.jbt.aroundme.R;
import java.util.Locale;


public class SharedPrefHelper {

    private final Context mContext;
    private final SharedPreferences mPrefs;


    public SharedPrefHelper(Context context)
    {
        mContext = context;
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void changeLocale() {

        String key = mContext.getString(R.string.pref_lang_key);
        String def = mContext.getString(R.string.pref_lang_english);
        String lang = mPrefs.getString(key, def);

        Locale locale = new Locale(lang);
        Locale.setDefault(locale);

        Configuration configuration = new Configuration();
        configuration.locale = locale;
        Resources resources = mContext.getResources();
        resources.updateConfiguration(configuration, mContext.getResources().getDisplayMetrics());
    }

    public boolean isEnglish() {
        String key = mContext.getString(R.string.pref_lang_key);
        String def = mContext.getString(R.string.pref_lang_english);
        String english = mContext.getString(R.string.pref_lang_english);
        return mPrefs.getString(key, def).equals(english);
    }
}
