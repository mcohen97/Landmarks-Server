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

import org.w3c.dom.Text;

import java.util.List;

public class LandmarkCardAdapter extends RecyclerView.Adapter<LandmarkCardAdapter.ViewHolder> {

    private Context mContext;
    private List<Landmark> mData;

    public LandmarkCardAdapter(Context mContext, List<Landmark> mData){
        this.mContext = mContext;
        this.mData = mData;
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
        String landmarkName = mData.get(position).getName();
        holder.title.setText(landmarkName);
        int imgId = Integer.parseInt(mData.get(position).getImg());
        holder.thumbnail.setImageResource(imgId);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        ImageView thumbnail;

        public ViewHolder(View itemView){
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.landmark_card_title);
            thumbnail = (ImageView) itemView.findViewById(R.id.landmark_card_img_id);
        }
    }
}
