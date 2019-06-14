package com.acr.landmarks.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.acr.landmarks.R;
import com.acr.landmarks.models.Landmark;
import com.acr.landmarks.models.LandmarkClusterMarker;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import java.util.ArrayList;
import java.util.Collection;


public class ClusterManagerRenderer extends DefaultClusterRenderer<LandmarkClusterMarker> {
    public static final int landmarkLimit = 10;
    private final IconGenerator iconGenerator;
    private final ImageView imageView;
    private final int markerWidth;
    private final int markerHeight;

    public ClusterManagerRenderer(Context context, GoogleMap googleMap,
                                  ClusterManager<LandmarkClusterMarker> clusterManager) {

        super(context, googleMap, clusterManager);

        // initialize cluster item icon generator
        iconGenerator = new IconGenerator(context.getApplicationContext());
        imageView = new ImageView(context.getApplicationContext());
        markerWidth = (int) context.getResources().getDimension(R.dimen.custom_marker_image);
        markerHeight = (int) context.getResources().getDimension(R.dimen.custom_marker_image);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(markerWidth, markerHeight));
        int padding = (int) context.getResources().getDimension(R.dimen.custom_marker_padding);
        imageView.setPadding(padding, padding, padding, padding);
        iconGenerator.setContentView(imageView);
    }


    /**
     * Rendering of the individual ClusterItems
     *
     * @param item
     * @param markerOptions
     */
    @Override
    protected void onBeforeClusterItemRendered(LandmarkClusterMarker item, MarkerOptions markerOptions) {
        Bitmap icon = drawIcon(item.getIconPicture());
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(item.getTitle()).snippet("");
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster cluster) {
        return cluster.getSize()>landmarkLimit;
    }

    @Override
    protected void onBeforeClusterRendered(Cluster<LandmarkClusterMarker> cluster, MarkerOptions markerOptions) {
        Collection<LandmarkClusterMarker> items = cluster.getItems();
        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        Bitmap icon = Bitmap.createBitmap(100, 100, conf);
        int quantity = items.size();

        Paint color = new Paint();
        color.setTextSize(35);
        color.setColor(Color.BLACK);
        int counter = 0;
        for( LandmarkClusterMarker item : items){
            Canvas canvas1 = new Canvas(icon);

            // modify canvas
            if(counter == 0){
                canvas1.drawBitmap(item.getIconPicture(), 0,0, color);
            }
            else{
                canvas1.drawBitmap(item.getIconPicture(), 100/counter,0, color);
            }
        }

        //Canvas canvas = new Canvas(icon);
        //canvas.drawText(Integer.toString(quantity), 30, 40, color);

        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
    }

    private Bitmap drawIcon(Bitmap landmarkImage) {
        landmarkImage = Bitmap.createScaledBitmap(landmarkImage, markerWidth, markerHeight, false);
        Bitmap bubble = iconGenerator.makeIcon();
        bubble = Bitmap.createScaledBitmap(bubble, markerWidth + 30, markerHeight + 60, false);
        Bitmap icon = Bitmap.createBitmap(bubble.getWidth(), bubble.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(icon);
        canvas.drawBitmap(bubble, 0, 0, null);
        int centreX = (canvas.getWidth() - landmarkImage.getWidth()) / 2;
        int centreY = (canvas.getWidth() - landmarkImage.getWidth()) / 2;
        canvas.drawBitmap(landmarkImage, centreX, centreY, null);
        return icon;
    }
}
