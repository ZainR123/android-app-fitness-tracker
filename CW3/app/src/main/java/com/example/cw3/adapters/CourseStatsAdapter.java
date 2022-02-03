package com.example.cw3.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cw3.R;
import com.example.cw3.entities.CourseStats;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

//Adapter for recycler view to display the 100m splits for a course
public class CourseStatsAdapter extends RecyclerView.Adapter<CourseStatsAdapter.ViewHolder>{

    //Declare objects and list
    private final LayoutInflater inflater;
    private List<CourseStats> data;
    //Format for doubles
    private static final DecimalFormat df = new DecimalFormat("0.00");

    //Constructor for initialising data list and layout inflater
    public CourseStatsAdapter(Context context) {
        this.data = new ArrayList<>();
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    //Called when RecyclerView needs a new RecyclerView.ViewHolder of the given type to represent an item.
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.db_point_layout_view, parent, false);
        return new ViewHolder(view);
    }

    //Called by RecyclerView to display the data at the specified position.
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(data.get(position), position);
    }

    //Get the amount of elements in the data list
    @Override
    public int getItemCount() {
        return data.size();
    }

    //If the data is not empty then clear it and add the newly given list, otherwise set data to the new data
    //Notify dataset has been changed
    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<CourseStats> newData) {
        if(data != null) {
            data.clear();
            data.addAll(newData);
            notifyDataSetChanged();
        } else {
            data = newData;
        }
    }

    //Describes an item view and metadata about its place within the RecyclerView
    public static class ViewHolder extends RecyclerView.ViewHolder {

        //Declare view items
        TextView pointID;
        TextView distance;
        TextView pace;
        TextView time;

        //Constructor assign ids to view items
        ViewHolder(View itemView) {
            super(itemView);
            pointID = itemView.findViewById(R.id.pointIDViewValue);
            distance = itemView.findViewById(R.id.distancePerViewValue);
            pace = itemView.findViewById(R.id.pacePerViewValue);
            time = itemView.findViewById(R.id.timeChangeViewValue);
        }

        //Bind view items to the associated data values
        void bind(CourseStats courseStats, int position) {
            if(courseStats != null) {
                pointID.setText(String.valueOf(position + 1));
                distance.setText(df.format(courseStats.getDistance()));
                pace.setText(df.format(courseStats.getPace()));
                int minutes = courseStats.getTime() / 60;
                int seconds = courseStats.getTime() % 60;
                time.setText(String.format(Locale.UK,"%02d:%02d", minutes, seconds));
            }
        }
    }
}
