package com.acr.landmarks.services;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.acr.landmarks.services.contracts.IImageService;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class PicassoImageService implements IImageService {

    private String landmarksUrl;
    private String toursUrl;

    public  PicassoImageService(String baseUrl){
        this.landmarksUrl = baseUrl+"images/landmarks/%s";
        this.toursUrl = baseUrl + "images/tours/%s";
        Picasso.get().setLoggingEnabled(true);
    }

    public void loadLandmarkImageToView(ImageView imageView, String fileName){
        String url =String.format(landmarksUrl,fileName);
        Picasso.get().load(url).into(imageView);
    }

    public void loadTourImageToView(ImageView imageView, String fileName) {
        String url =String.format(toursUrl,fileName);
        Picasso.get().load(url).into(imageView);
    }

    public void loadBitmap(String fileName, ImageLoadListener listener){
        Target target = new Target(){

            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
              listener.onImageLoaded(bitmap);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
        Picasso.get().load(String.format(landmarksUrl,fileName)).into(target);
    }
}
