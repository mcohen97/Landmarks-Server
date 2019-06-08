package com.acr.landmarks.services;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.acr.landmarks.services.contracts.IImageService;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PicassoImageService implements IImageService {

    private String landmarksUrl;
    private String toursUrl;
    private final List<PicassoLoadingTarget> targets;


    public  PicassoImageService(String baseUrl){
        this.landmarksUrl = baseUrl+"images/landmarks/%s";
        this.toursUrl = baseUrl + "images/tours/%s";
        this.targets = new ArrayList<>();
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
        final PicassoLoadingTarget target = new PicassoLoadingTarget(listener,targets);
        targets.add(target);
        Picasso.get().load(String.format(landmarksUrl,fileName)).into(target);
    }
}
