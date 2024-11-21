package com.pxr.golf.ui.home;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pxr.golf.R;
import com.pxr.golf.adapters.CourseAdapter;
import com.pxr.golf.adapters.PostAdapter;
import com.pxr.golf.databinding.FragmentHomeBinding;
import com.pxr.golf.db.DBManager;
import com.pxr.golf.models.Course;
import com.pxr.golf.models.Post;
import com.pxr.golf.models.User;
import com.pxr.golf.utils.Auth;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Collectors;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private FragmentHomeBinding binding;
    private List<Course> courses;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel = new ViewModelProvider(this,
                new HomeViewModelFactory(requireActivity().getApplication()))
                .get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        DBManager db = new DBManager(root.getContext());

        setGreetings();
        User user = Auth.loadUser(root.getContext());
        TextView nameText = binding.homeNameText;
        nameText.setText(user.getName());
        ImageView profileImage = binding.homeProfileImage;
        profileImage.setImageResource(R.drawable.baseline_person_24);

        courses = db.getCourses(user.getId());
        displayCourses(courses);

        homeViewModel.getPosts().observe(getViewLifecycleOwner(), this::displayPosts);

        EditText courseSearchInput = binding.homeCourseSearchInput;
        courseSearchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d(TAG, "onTextChanged: " + charSequence);
                if (charSequence == null || charSequence.length() == 0) {
                    displayCourses(courses);
                    return;
                }
                List<Course> filtered = handleSearch(courses, charSequence.toString());
                displayCourses(filtered);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        return root;
    }

    private void displayPosts(List<Post> posts) {
        if (posts == null) return;
        RecyclerView postRV = binding.homeProductRV;
        postRV.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        postRV.setAdapter(new PostAdapter(posts));
    }

    private void displayCourses(List<Course> courses) {
        if (courses == null || courses.isEmpty()) return;
        RecyclerView courseRV = binding.homeCourseRV;
        courseRV.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        courseRV.setAdapter(new CourseAdapter(courses));
    }

    private List<Course> handleSearch(List<Course> courses, String query) {
        if (courses == null) return new ArrayList<>();
        return courses.stream().filter(
                course -> course.getName().toLowerCase().contains(query.toLowerCase())
        ).collect(Collectors.toList());
    }

    private void setGreetings() {
        Date now = new Date();
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(now);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        if (hour < 12) {
            binding.homeGreetingsText.setText("Good Morning,");
        } else if (hour < 18) {
            binding.homeGreetingsText.setText("Good Afternoon,");
        } else {
            binding.homeGreetingsText.setText("Good Evening,");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}