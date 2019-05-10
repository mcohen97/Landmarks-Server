package com.acr.landmarks.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.acr.landmarks.R;
import com.acr.landmarks.models.Tour;

import java.util.List;

public class TourCardAdapter extends RecyclerView.Adapter<TourCardAdapter.ViewHolder> {
    private Context mContext;
    private TourCardAdapter.TourClickedListener clickListener;
    private List<Tour> lastAvailableTourData;

    public TourCardAdapter(Context mContext, TourCardAdapter.TourClickedListener clickListener, List<Tour> data){
        this.mContext = mContext;
        this.clickListener = clickListener;
        lastAvailableTourData = data;
    }

    @NonNull
    @Override
    public TourCardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.cardview_tour_item,parent,false);
        return new TourCardAdapter.ViewHolder(view, clickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull TourCardAdapter.ViewHolder holder, int position) {
        Tour requestedTour = lastAvailableTourData.get(position);
        String tourName = requestedTour.getName();
        holder.title.setText(tourName);
        String tourTheme = requestedTour.getTematica();
        holder.theme.setText(tourTheme);
        String tourDescription = requestedTour.getDescripcion();
        holder.description.setText(tourDescription);
        String image = requestedTour.getIconBase64();
        byte[] imageData = android.util.Base64.decode(image, Base64.DEFAULT);
        Bitmap landmark = BitmapFactory.decodeByteArray(imageData,0,imageData.length);
        holder.thumbnail.setImageBitmap(landmark);
    }

    @Override
    public int getItemCount() {
        return lastAvailableTourData.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title;
        TextView theme;
        ImageView thumbnail;
        TextView description;
        TourCardAdapter.TourClickedListener clickListener;

        public ViewHolder(View itemView, TourCardAdapter.TourClickedListener clickListener){
            super(itemView);

            this.title = itemView.findViewById(R.id.tour_card_title);
            this.theme = itemView.findViewById(R.id.tour_card_theme);
            this.thumbnail = itemView.findViewById(R.id.tour_card_img_id);
            this.description = itemView.findViewById(R.id.tour_card_description);
            this.clickListener = clickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            int position =getAdapterPosition();
            clickListener.onTourClicked(lastAvailableTourData.get(position));
        }
    }

    public interface TourClickedListener {
        void onTourClicked(Tour clicked);
    }
}
