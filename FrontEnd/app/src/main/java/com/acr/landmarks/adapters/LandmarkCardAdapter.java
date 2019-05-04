package com.acr.landmarks.adapters;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.acr.landmarks.R;
import com.acr.landmarks.models.Landmark;
import com.acr.landmarks.services.contracts.ILandmarksService;
import com.acr.landmarks.view_models.LandmarksViewModel;

import java.util.ArrayList;
import java.util.List;

public class LandmarkCardAdapter extends RecyclerView.Adapter<LandmarkCardAdapter.ViewHolder>{

    private Context mContext;
    private LandmarkCardClickListener clickListener;
    private LandmarksViewModel viewModel;
    private List<Landmark> lastAvailableData;

    public LandmarkCardAdapter(Context mContext, LandmarkCardClickListener clickListener){
        this.mContext = mContext;
        this.clickListener = clickListener;
        viewModel = ViewModelProviders.of((FragmentActivity) mContext).get(LandmarksViewModel.class);
        lastAvailableData = new ArrayList<Landmark>();
        viewModel.getLandmarks().observe((FragmentActivity) mContext, landmarks -> {
            lastAvailableData = landmarks;
            notifyDataSetChanged();
        });
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
        Landmark requestedLandmark = lastAvailableData.get(position);
        String landmarkName = requestedLandmark.getName();
        holder.title.setText(landmarkName);
        String image = requestedLandmark.getImg();
        byte[] imageData = android.util.Base64.decode(image, Base64.DEFAULT);
        Bitmap landmark = BitmapFactory.decodeByteArray(imageData,0,imageData.length);
        holder.thumbnail.setImageBitmap(landmark);
    }

    @Override
    public int getItemCount() {
        return lastAvailableData.size();
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

