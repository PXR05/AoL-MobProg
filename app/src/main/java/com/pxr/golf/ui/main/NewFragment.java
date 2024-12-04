package com.pxr.golf.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.pxr.golf.R;

import com.pxr.golf.databinding.FragmentNewBinding;
import com.pxr.golf.db.DBManager;
import com.pxr.golf.models.Course;
import com.pxr.golf.models.User;
import com.pxr.golf.ui.CourseDetailsActivity;
import com.pxr.golf.ui.LoginActivity;
import com.pxr.golf.utils.Auth;

public class NewFragment extends Fragment {

    private FragmentNewBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNewBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        EditText newCourseName = binding.newCourseNameET;
        EditText newTotalHole = binding.newTotalHoleET;
        Button newCourse = binding.newCourseBTN;

        newCourse.setOnClickListener(v -> {
            String courseName = newCourseName.getText().toString();
            String totalHole = newTotalHole.getText().toString();
            if(courseName.isEmpty() || totalHole.isEmpty()){
                Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            }else{
                User user = Auth.loadUser(root.getContext());
                createCourse(courseName, R.drawable.course1, Integer.parseInt(totalHole), user.getId());
            }
        });

        return root;
    }

    private void createCourse(String name, int image, int holeCount, String uid){
        Log.d("NF", "createCourse: " + name + ", " + holeCount);
        DBManager db = new DBManager(getContext());
        String course = db.addCourse(name, image, holeCount, uid);
        if (course == null) {
            Toast.makeText(getContext(), "Invalid input", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(getContext(), CourseDetailsActivity.class);
        intent.putExtra("id", course);
        intent.putExtra("name", name);
        intent.putExtra("image", image);
        intent.putExtra("holeCount", holeCount);
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}