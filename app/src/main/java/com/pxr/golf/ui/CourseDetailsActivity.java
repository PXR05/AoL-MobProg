package com.pxr.golf.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pxr.golf.R;
import com.pxr.golf.adapters.HoleAdapter;
import com.pxr.golf.db.DBManager;
import com.pxr.golf.models.Course;
import com.pxr.golf.models.Hole;
import com.pxr.golf.models.User;
import com.pxr.golf.utils.Auth;
import com.pxr.golf.utils.Generate;
import com.pxr.golf.utils.Setup;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CourseDetailsActivity extends AppCompatActivity {
    private static final String TAG = "CourseDetailsActivity";
    private User user;
    private Course course;
    private DBManager db;
    private ExecutorService executor;
    private boolean isLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_course_details);
        View root = findViewById(R.id.main);
        Setup.all(this, root);

        db = new DBManager(this);
        executor = Executors.newSingleThreadExecutor();

        ImageView backBtn = findViewById(R.id.courseDetailBackBtn);
        backBtn.setOnClickListener(v -> {
            if (isLoading) {
                isLoading = false;
                executor.shutdown();
            }
            finish();
        });

        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        String name = intent.getStringExtra("name");
        int image = intent.getIntExtra("image", 0);
        int holeCount = intent.getIntExtra("holeCount", 0);
        String hid = intent.getStringExtra("hid");
        Log.d(TAG, "onCreate: " +
                ", id: " + id +
                ", name: " + name +
                ", image: " + image +
                ", holeCount: " + holeCount +
                ", hid: " + hid
        );
        user = Auth.loadUser(root.getContext());

        if (hid != null && !hid.isBlank()) {
            course = new Course(id, name, image, hid, db.getHoles(hid));
        } else {
            course = new Course(id, name, image, Generate.holes(holeCount, 72));
        }
        displayCourse(course);
    }

    @SuppressLint("DefaultLocale")
    private void displayCourse(Course course) {
        if (course == null) return;

        ImageView courseImage = findViewById(R.id.courseDetailsImage);
        courseImage.setImageResource(course.getImage());

        TextView courseName = findViewById(R.id.courseDetailsNameText);
        courseName.setText(course.getName());

        TextView courseInfo = findViewById(R.id.courseDetailsInfoText);
        courseInfo.setText(
                String.format(
                        "%d holes, %d par",
                        course.getHoles().size(),
                        course.getHoles().stream().mapToInt(Hole::getPar).sum()
                )
        );

        RecyclerView courseHoles = findViewById(R.id.courseDetailsHolesRV);
        courseHoles.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        courseHoles.setAdapter(new HoleAdapter(course.getHoles(), this));

        int score = course.getHoles().stream().map(Hole::getScore).reduce(0, Integer::sum);
        TextView courseScore = findViewById(R.id.courseDetailsScoreText);
        courseScore.setText(String.valueOf(score));

        int handicap = 72 - score;
        TextView courseHandicap = findViewById(R.id.courseDetailsHandicapText);
        courseHandicap.setText(String.valueOf(handicap));

        Button courseSave = findViewById(R.id.courseDetailsSaveBtn);
        courseSave.setOnClickListener(v -> handleSave(course.getId()));
    }

    private void handleSave(String cid) {
        if (isLoading || course == null) {
            Log.d(TAG, "handleSave: is loading");
            return;
        }
        RecyclerView courseHoles = findViewById(R.id.courseDetailsHolesRV);
        HoleAdapter adapter = (HoleAdapter) courseHoles.getAdapter();
        if (adapter == null) {
            Log.d(TAG, "handleSave: adapter null");
            return;
        }
        List<Hole> holes = adapter.getHoles();
        Log.d(TAG, "handleSave: saving holes " + holes + " with hid " + course.getHid());
        String hid = db.saveHistory(cid, user.getId(), course.getHid());
        db.saveHoles(holes, hid);
        course.setHid(hid);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!executor.isShutdown() || !executor.isTerminated()) {
            executor.shutdown();
        }
        course = null;
    }
}