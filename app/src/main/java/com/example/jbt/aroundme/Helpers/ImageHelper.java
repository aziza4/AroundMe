package com.example.jbt.aroundme.Helpers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;

import java.io.ByteArrayOutputStream;


public class ImageHelper {


    public static byte[] convertBitmapToByteArray(Bitmap bitmap) {

        if (bitmap == null)
            return null;

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    public static Bitmap convertByteArrayToBitmap(byte[] byteArray) {

        if (byteArray == null)
            return null;

        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
    }

    public static void setDrawableEnableDisableColor( Drawable icon, boolean enable)
    {
        if (enable)
            icon.setColorFilter(null);
        else
            icon.setColorFilter(Color.LTGRAY, PorterDuff.Mode.MULTIPLY);
    }
}
