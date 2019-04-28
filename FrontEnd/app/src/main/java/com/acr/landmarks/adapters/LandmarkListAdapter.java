package com.acr.landmarks.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.acr.landmarks.R;
import com.acr.landmarks.models.Landmark;

import java.util.ArrayList;

public class LandmarkListAdapter extends RecyclerView.Adapter<LandmarkListAdapter.ViewHolder>{

    private ArrayList<Landmark> mLandmarks = new ArrayList<>();
    private LandmarkListRecyclerClickListener mClickListener;

    public LandmarkListAdapter(ArrayList<Landmark> lms, LandmarkListRecyclerClickListener clickListener) {
        mLandmarks = lms;
        mClickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_landmark_list_item, parent, false);
        final ViewHolder holder = new ViewHolder(view, mClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ((ViewHolder)holder).name.setText(mLandmarks.get(position).getName());
        ((ViewHolder)holder).description.setText(mLandmarks.get(position).getDescription());
    }

    @Override
    public int getItemCount() {
        return mLandmarks.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView name , description;
        LandmarkListRecyclerClickListener mClickListener;

        public ViewHolder(View itemView, LandmarkListRecyclerClickListener clickListener) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            description = itemView.findViewById(R.id.description);

            mClickListener = clickListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mClickListener.onLandmarkClicked(getAdapterPosition());
        }
    }

    public interface LandmarkListRecyclerClickListener{
        void onLandmarkClicked(int position);
    }

}
