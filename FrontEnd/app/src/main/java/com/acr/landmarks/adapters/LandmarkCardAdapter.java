package com.acr.landmarks.adapters;

import android.content.Context;
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

import java.util.List;

public class LandmarkCardAdapter extends RecyclerView.Adapter<LandmarkCardAdapter.ViewHolder>  {

    private Context mContext;
    private LandmarksService landmarksProvider;
    private LandmarkCardClickListener clickListener;

    public LandmarkCardAdapter(Context mContext, LandmarkCardClickListener clickListener, LandmarksService landmarksService){
        this.mContext = mContext;
        this.landmarksProvider = landmarksService;
        this.clickListener = clickListener;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.cardview_item,parent,false);
        return new ViewHolder(view, clickListener);
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

    public List<Landmark> getLandmarks(){
        return landmarksProvider.getAllLandmarks();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title;
        ImageView thumbnail;
        LandmarkCardClickListener clickListener;

        public ViewHolder(View itemView,  LandmarkCardClickListener clickListener){
            super(itemView);

            this.title = itemView.findViewById(R.id.landmark_card_title);
            this.thumbnail = itemView.findViewById(R.id.landmark_card_img_id);
            this.clickListener = clickListener;
            itemView.setOnClickListener(this);

        }
        @Override
        public void onClick(View v) {
            clickListener.onLandmarkClicked(getAdapterPosition());
        }
    }

    public interface LandmarkCardClickListener {
        void onLandmarkClicked(int position);
    }

}


