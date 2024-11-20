package com.pxr.golf.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.pxr.golf.R;
import com.pxr.golf.models.Course;
import com.pxr.golf.ui.CourseDetailsActivity;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {
    private final List<Course> courses;
    private boolean isHistory = false;
    private Context ctx;

    public CourseAdapter(List<Course> courses) {
        this.courses = courses;
    }

    public CourseAdapter(List<Course> courses, boolean isHistory) {
        this.courses = courses;
        this.isHistory = isHistory;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ctx = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View item = inflater.inflate(R.layout.course_item, parent, false);
        return new CourseViewHolder(item);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course course = courses.get(position);
        holder.courseImage.setImageResource(course.getImage());
        holder.courseName.setText(course.getName().toUpperCase());
        if (isHistory && course.getDate() != null) {
            DateTimeFormatter df = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);
            holder.courseHoles.setText(df.format(course.getDate()));
        } else {
            holder.courseHoles.setText(course.getHoleCount() + " HOLES");
        }
        int width = ctx.getResources().getDisplayMetrics().widthPixels;
        holder.courseContainer.setLayoutParams(new ViewGroup.LayoutParams((int) (width * 0.6f), ViewGroup.LayoutParams.MATCH_PARENT));
        holder.courseContainer.setOnClickListener(e -> {
            Intent details = new Intent(ctx, CourseDetailsActivity.class);
            details.putExtra("id", course.getId());
            ctx.startActivity(details);
        });
    }

    @Override
    public int getItemCount() {
        if (courses == null) {
            return 0;
        }
        return courses.size();
    }

    public static class CourseViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout courseContainer;
        ImageView courseImage;
        TextView courseName, courseHoles;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            courseContainer = itemView.findViewById(R.id.courseItemContainer);
            courseImage = itemView.findViewById(R.id.courseItemImage);
            courseName = itemView.findViewById(R.id.courseItemNameText);
            courseHoles = itemView.findViewById(R.id.courseItemHolesText);
        }
    }
}
