package com.acr.landmarks.services;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.acr.landmarks.services.contracts.IImageService;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

public class PicassoLoadingTarget implements Target {

    private IImageService.ImageLoadListener mListener;
    private List<PicassoLoadingTarget> collection;

    public PicassoLoadingTarget(IImageService.ImageLoadListener listener, List<PicassoLoadingTarget> targets) {
        mListener = listener;
        collection = targets;
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        mListener.onImageLoaded(bitmap);
        collection.remove(this);
    }

    @Override
    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
        mListener.onImageLoaded(drawableToBitmap(errorDrawable));
        collection.remove(this);
    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {

    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}
