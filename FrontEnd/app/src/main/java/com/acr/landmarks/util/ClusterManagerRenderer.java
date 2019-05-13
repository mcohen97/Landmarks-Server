package com.acr.landmarks.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Base64;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.acr.landmarks.R;
import com.acr.landmarks.models.LandmarkClusterMarker;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import java.util.ArrayList;

public class ClusterManagerRenderer extends DefaultClusterRenderer<LandmarkClusterMarker> {
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
     * @param item
     * @param markerOptions
     */
    @Override
    protected void onBeforeClusterItemRendered(LandmarkClusterMarker item, MarkerOptions markerOptions) {
        Marker marker = getMarker(item);

        String image = item.getIconPicture();
        byte[] imageData = Base64.decode(image, Base64.DEFAULT);
        Bitmap landmark = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
        landmark = Bitmap.createScaledBitmap(landmark, markerWidth, markerHeight, false);
        Bitmap bubbleIcon = iconGenerator.makeIcon();
        bubbleIcon = Bitmap.createScaledBitmap(bubbleIcon, markerWidth + 30, markerHeight + 60, false);
        Bitmap icon = drawIcon(bubbleIcon, landmark);

            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(item.getTitle()).snippet("");
    }


    @Override
    protected boolean shouldRenderAsCluster(Cluster cluster) {
        return false;
    }

    private Bitmap drawIcon(Bitmap bubble,Bitmap landmarkImage){
        Bitmap icon = Bitmap.createBitmap(bubble.getWidth(),bubble.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(icon);
        canvas.drawBitmap(bubble,0,0,null);
        int centreX = (canvas.getWidth()  - landmarkImage.getWidth()) /2;
        int centreY = (canvas.getWidth()  - landmarkImage.getWidth()) /2;
        canvas.drawBitmap(landmarkImage, centreX, centreY, null);
        return icon;
    }

    public void RemoveItem(LandmarkClusterMarker item){
        Marker marker = getMarker(item);
        if(marker != null){
            marker.remove();
        }
    }
}
