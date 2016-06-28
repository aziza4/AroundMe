package com.example.jbt.aroundme.helpers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.jbt.aroundme.activities_fragments.MainActivity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;


public class NetworkHelper {

    private final URL mUrl;


    public NetworkHelper(URL url)
    {
        this.mUrl = url;
    }

    public String getJsonString()
    {
        String jsonString = "";
        HttpsURLConnection con = null;

        try {

            con = (HttpsURLConnection) mUrl.openConnection();

            int resCode = con.getResponseCode();

            if (resCode != HttpsURLConnection.HTTP_OK)
                return null;

            BufferedReader r = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String line;
            while((line = r.readLine()) != null)
                jsonString += line;

        } catch (Exception e) {
            Log.e(MainActivity.LOG_TAG, "" + e.getMessage());
            return null;

        } finally {

            if (con != null)
                con.disconnect();
        }

        return jsonString;
    }


    public Bitmap getImage()
    {
        InputStream stream;

        try {

            stream = (InputStream) mUrl.getContent();

            if (stream == null)
                return null;

        } catch (Exception e) {

            Log.e(MainActivity.LOG_TAG, "" + e.getMessage());
            return null;
        }

        return BitmapFactory.decodeStream(stream);
    }
}