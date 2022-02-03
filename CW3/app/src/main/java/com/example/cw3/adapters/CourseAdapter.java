package com.example.cw3.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cw3.R;
import com.example.cw3.entities.Course;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

//Adapter for recycler view to display all course data
public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ViewHolder> {

    //Declare objects and list
    private final LayoutInflater inflater;
    private ItemClickListener clickListener;
    private List<Course> data;
    //Format for doubles
    private static final DecimalFormat df = new DecimalFormat("0.00");

    //Constructor for initialising data list and layout inflater
    public CourseAdapter(Context context) {
        this.data = new ArrayList<>();
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    //Called when RecyclerView needs a new RecyclerView.ViewHolder of the given type to represent an item.
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.db_track_layout_view, parent, false);
        return new ViewHolder(view);
    }

    //Called by RecyclerView to display the data at the specified position.
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(data.get(position));
    }

    //Get the amount of elements in the data list
    @Override
    public int getItemCount() {
        return data.size();
    }

    //Get the ID of a selected item in the list
    @Override
    public long getItemId(int position) {
        return data.get(position).getCourseID();
    }

    //If the data is not empty then clear it and add the newly given list, otherwise set data to the new data
    //Notify dataset has been changed
    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<Course> newData) {
        if(data != null) {
            data.clear();
            data.addAll(newData);
            notifyDataSetChanged();
        } else {
            data = newData;
        }
    }

    //Describes an item view and metadata about its place within the RecyclerView
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        //Declare view items
        TextView name;
        TextView startDate;
        TextView endDate;
        TextView distance;
        TextView time;
        TextView pace;
        TextView calories;
        TextView weight;
        RatingBar ratingBar;

        //Constructor assign ids to view items
        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.courseNameMain);
            startDate = itemView.findViewById(R.id.startDatetimeMainValue);
            endDate = itemView.findViewById(R.id.endDatetimeMainVal);
            distance = itemView.findViewById(R.id.distanceValueMain);
            time = itemView.findViewById(R.id.courseTime);
            pace = itemView.findViewById(R.id.averagePaceValue);
            calories = itemView.findViewById(R.id.caloriesValueMain);
            weight = itemView.findViewById(R.id.weightValueMain);
            ratingBar = itemView.findViewById(R.id.ratingBarMain);
            itemView.setOnClickListener(this);
        }

        //Bind view items to the associated data values
        void bind(Course course) {
            if(course != null) {
                name.setText(course.getCourseName());
                startDate.setText(course.getStartDateTime());
                endDate.setText(course.getEndDateTime());
                distance.setText(df.format(course.getDistance()));
                int hours = course.getTime() / 3600;
                int minutes = (course.getTime() % 3600) / 60;
                int seconds = course.getTime() % 60;
                time.setText(String.format(Locale.UK,"%02d:%02d:%02d", hours, minutes, seconds));
                pace.setText(df.format(course.getPace()));
                calories.setText(df.format(course.getCalories()));
                weight.setText(String.valueOf(course.getWeight()));
                ratingBar.setRating(course.getRating());
            }
        }

        //When an item is clicked get position
        @Override
        public void onClick(View view) {
            if (clickListener != null) {
                try {
                    clickListener.onItemClick(view, getAdapterPosition());
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //Set click listener
    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    //Declare click listener interface
    public interface ItemClickListener {
        void onItemClick(View view, int position) throws ExecutionException, InterruptedException;
    }
}
