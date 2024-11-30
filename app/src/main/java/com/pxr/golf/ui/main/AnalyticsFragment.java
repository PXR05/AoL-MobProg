package com.pxr.golf.ui.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pxr.golf.R;
import com.pxr.golf.adapters.CourseAdapter;
import com.pxr.golf.databinding.FragmentAnalyticsBinding;
import com.pxr.golf.db.DBManager;
import com.pxr.golf.models.Course;
import com.pxr.golf.models.User;
import com.pxr.golf.ui.LandingActivity;
import com.pxr.golf.utils.Auth;

import java.util.List;

public class AnalyticsFragment extends Fragment {

    private FragmentAnalyticsBinding binding;

    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAnalyticsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ImageView logoutBtn = binding.analyticsLogoutBtn;
        logoutBtn.setOnClickListener(v -> {
            Auth.logout(root.getContext());
            Intent intent = new Intent(root.getContext(), LandingActivity.class);
            startActivity(intent);
            requireActivity().finish();
        });

        User user = Auth.loadUser(root.getContext());
        TextView nameText = binding.analyticsNameText;
        nameText.setText(user.getName());
        ImageView profileImage = binding.analyticsProfileImage;
        profileImage.setImageResource(R.drawable.baseline_person_24);

        DBManager db = new DBManager(root.getContext());
        List<Course> courses = db.getHistories(user.getId());
        if (courses != null && !courses.isEmpty()) {
            displayCourses(courses);
            TextView totalPlayedText = binding.analyticsTotalPlayedText;
            totalPlayedText.setText("TOTAL COURSE PLAYED: " + courses.size());
        }

        return root;
    }

    private void displayCourses(List<Course> courses) {
        RecyclerView courseRV = binding.analyticsHistoryRV;
        courseRV.setLayoutManager(new GridLayoutManager(getContext(), 2, GridLayoutManager.HORIZONTAL, false));
        courseRV.setAdapter(new CourseAdapter(courses, true));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}