package com.comli.shapira.aroundme.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ProgressBar;


public class SearchFragBroadcastReceiver extends BroadcastReceiver {

    private final ProgressBar mProgressBar;


    public SearchFragBroadcastReceiver(ProgressBar progressBar)
    {
        mProgressBar = progressBar;
    }


    @Override
    public void onReceive(Context context, Intent intent)
    {
        mProgressBar.setVisibility(View.VISIBLE);
    }
}