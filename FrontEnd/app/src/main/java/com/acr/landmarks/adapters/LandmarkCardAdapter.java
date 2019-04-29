package com.acr.landmarks.adapters;

import android.content.Context;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.acr.landmarks.R;
import com.acr.landmarks.models.Landmark;
import com.acr.landmarks.service.LandmarksService;

import org.w3c.dom.Text;

import java.util.List;

public class LandmarkCardAdapter extends RecyclerView.Adapter<LandmarkCardAdapter.ViewHolder> {

    private Context mContext;
    private LandmarksService landmarksProvider;

    public LandmarkCardAdapter(Context mContext, LandmarksService landmarksService){
        this.mContext = mContext;
        this.landmarksProvider = landmarksService;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.cardview_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        List<Landmark> lastAvailableData = landmarksProvider.getAllLandmarks();
        Landmark requestedLandmark = lastAvailableData.get(position);
        String landmarkName = requestedLandmark.getName();
        holder.title.setText(landmarkName);
        int imgId = Integer.parseInt(requestedLandmark.getImg());
        holder.thumbnail.setImageResource(imgId);
    }

    @Override
    public int getItemCount() {
        return landmarksProvider.getLandmarksCount();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        ImageView thumbnail;

        public ViewHolder(View itemView){
            super(itemView);

            title = itemView.findViewById(R.id.landmark_card_title);
            thumbnail = itemView.findViewById(R.id.landmark_card_img_id);
        }
    }
}
