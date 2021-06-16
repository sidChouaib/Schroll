package com.example.schroll;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FeaturedAdapter extends RecyclerView.Adapter<FeaturedAdapter.FeaturedViewHolder> {

    private ArrayList<FeaturedHelperClass> featuredLocations;
    private onCourseListener mOnCourseListener;

    public FeaturedAdapter(ArrayList<FeaturedHelperClass> featuredLocations, onCourseListener oncourseListener) {
        this.featuredLocations = featuredLocations;
        this.mOnCourseListener = oncourseListener;
    }

    @NonNull
    @Override
    public FeaturedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.featured_card_design,parent,false);
       FeaturedViewHolder featuredViewHolder = new FeaturedViewHolder(view, mOnCourseListener);
        return featuredViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FeaturedViewHolder holder, int position) {
        FeaturedHelperClass featuredHelperClass = featuredLocations.get(position);
        holder.courseImage.setImageResource(featuredHelperClass.getCourseImage());
        holder.courseTitle.setText(featuredHelperClass.getCourseTitle());
        holder.courseDesc.setText(featuredHelperClass.getCourseDesc());
    }

    @Override
    public int getItemCount() {
        return featuredLocations.size();
    }


    public static class FeaturedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView courseImage;
        TextView courseTitle, courseDesc;
        onCourseListener mOncourseListener;
        public FeaturedViewHolder(@NonNull View itemView, onCourseListener oncourseListener) {
            super(itemView);
            //Hooks of the recycler view
            courseImage = itemView.findViewById(R.id.courseImage01);
            courseTitle = itemView.findViewById(R.id.courseTitle01);
            courseDesc = itemView.findViewById(R.id.courseDesc01);

            mOncourseListener = oncourseListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mOncourseListener.onCourseClick(getAdapterPosition());
        }
    }

    public interface onCourseListener{
        void onCourseClick(int position);
    }

}