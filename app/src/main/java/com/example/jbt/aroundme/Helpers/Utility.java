package com.example.jbt.aroundme.Helpers;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;


public class Utility {

    public static void setContentViewWithLocaleChange(AppCompatActivity activity,
                                                      int layoutId, int titleId)
    {
        SharedPrefHelper sharedPrefHelper = new SharedPrefHelper(activity);
        sharedPrefHelper.changeLocale();
        activity.setContentView(layoutId);
        resetTitle(activity, titleId); // workaround android bug, see above
    }


    // Workaround android bug: http://stackoverflow.com/questions/22884068/troubles-with-activity-title-language
    public static void resetTitle(AppCompatActivity activity, int id)
    {
        ActionBar actionBar = activity.getSupportActionBar();

        if (actionBar != null)
            actionBar.setTitle(activity.getString(id));
    }
}
