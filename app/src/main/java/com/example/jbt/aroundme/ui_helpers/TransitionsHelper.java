package com.example.jbt.aroundme.ui_helpers;

import android.app.ActivityOptions;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Pair;
import android.view.View;

import com.example.jbt.aroundme.R;


public class TransitionsHelper {


    public static void setMainActivityExitTransition(AppCompatActivity activity)
    {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            return;

        TransitionInflater inflater = TransitionInflater.from(activity);

        Transition cardTransition = inflater.inflateTransition(R.transition.place_shared_element);
        activity.getWindow().setSharedElementExitTransition(cardTransition);

//        Slide slideExitTransition = new Slide(Gravity.TOP);
//        slideExitTransition.excludeTarget(R.id.toolbar, true);
//        activity.getWindow().setExitTransition(slideExitTransition);
    }



    public static void setMapActivityEnterTransition(AppCompatActivity activity)
    {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            return;

        TransitionInflater inflater = TransitionInflater.from(activity);

        Transition transition = inflater.inflateTransition(R.transition.place_shared_element);
        activity.getWindow().setSharedElementEnterTransition(transition);

//        Slide slideEnterTransition = new Slide(Gravity.BOTTOM);
//        slideEnterTransition.excludeTarget(R.id.googleMapContainer, true);
//        activity.getWindow().setExitTransition(slideEnterTransition);
    }



    public static Bundle getTransitionBundle(AppCompatActivity activity, View placeView)
    {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            return null;

        String toolbarTransName = activity.getString(R.string.toolbar_transition_name);
        String placeTransName = activity.getString(R.string.shared_place_transition_name);
        placeView.setTransitionName(placeTransName);

        View toolbarView = activity.findViewById(R.id.toolbar);

        Pair placePair = Pair.create(placeView, placeTransName); //connect the view fab to the transitionName "fab"
        Pair toolbarPair = Pair.create(toolbarView, toolbarTransName); // connect header (toolbar) to transitionName "holder1"

        ActivityOptions transitionActivityOptions =
                ActivityOptions.makeSceneTransitionAnimation(
                        activity,
                        placePair,
                        toolbarPair);

        return transitionActivityOptions.toBundle();
    }
}
