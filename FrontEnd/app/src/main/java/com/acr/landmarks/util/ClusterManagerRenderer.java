package com.acr.landmarks.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
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
import java.util.List;


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
        // Draw multiple landmarks.
        // Note: this method runs on the UI thread. Don't spend too much time in here (like in this example).
        List<Drawable> landmarkPhotos = new ArrayList<Drawable>(Math.min(4, cluster.getSize()));
        int width = markerWidth;
        int height = markerHeight;

        for (LandmarkClusterMarker item : cluster.getItems()) {
            // Draw 4 at most.
            if (landmarkPhotos.size() == 4) break;
            Drawable drawable = new BitmapDrawable(item.getIconPicture());
            drawable.setBounds(0, 0, width, height);
            landmarkPhotos.add(drawable);
        }
        MultiDrawable multiDrawable = new MultiDrawable(landmarkPhotos);
        multiDrawable.setBounds(0, 0, width, height);

        imageView.setImageDrawable(multiDrawable);
        Bitmap icon = iconGenerator.makeIcon(String.valueOf(cluster.getSize()));
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
