package com.comli.shapira.aroundme.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

import com.comli.shapira.aroundme.data.Place;
import com.comli.shapira.aroundme.R;
import com.squareup.picasso.Picasso;

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

    public static void SetImageViewLogic(Context context, ImageView placeIV, Place place, boolean isPlaceHolder)
    {
        GooglePlacesNearbyHelper nearbyHelper = new GooglePlacesNearbyHelper(context);

        Bitmap bitmap = place.getPhoto().getBitmap();

        if ( bitmap != null) {

            placeIV.setImageBitmap(bitmap); // if image exists
            return;
        }

        if (place.getPhotoRef() == null) { // if image n/a on google's servers

            // go for placeholder image (on map view)
            if (isPlaceHolder) {
                Bitmap imagePlaceHolder = BitmapFactory.decodeResource(context.getResources(), R.drawable.placeholder);
                placeIV.setImageBitmap(imagePlaceHolder);
                return;
            }

            // go for blank space (on search/favorites view) for cleaner UX.
            placeIV.setImageBitmap(null);
            return;
        }

        // image does not exist but can be downloaded
        Uri uri = nearbyHelper.getPhotoUri(place);
        Picasso.with(context)
                .load(uri)
                .placeholder(R.drawable.placeholder)
                .into(placeIV);
    }
}
