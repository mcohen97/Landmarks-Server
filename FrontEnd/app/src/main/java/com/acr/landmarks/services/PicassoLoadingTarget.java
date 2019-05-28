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
    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {

    }
}
