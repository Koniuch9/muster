package com.example.olekc.musterforparty;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class PicassoMarker implements Target {

    Marker mMarker;

    public PicassoMarker(Marker m)
    {
        mMarker = m;
    }

    @Override
    public int hashCode() {
        return mMarker.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof PicassoMarker)
        {
            Marker marker = ((PicassoMarker) obj).mMarker;
            return mMarker.equals(marker);
        }else{
            return false;
        }
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        mMarker.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap));
    }

    @Override
    public void onBitmapFailed(Exception e, Drawable errorDrawable) {

    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {

    }
}
