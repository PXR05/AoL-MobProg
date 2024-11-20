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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pxr.golf.R;
import com.pxr.golf.adapters.HoleAdapter;
import com.pxr.golf.db.DBManager;
import com.pxr.golf.models.Course;
import com.pxr.golf.models.Hole;
import com.pxr.golf.models.User;
import com.pxr.golf.utils.Auth;
import com.pxr.golf.utils.Setup;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CourseDetailsActivity extends AppCompatActivity {
    private static final String TAG = "CourseDetailsActivity";
    private User user;
    private MutableLiveData<Course> course;
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
        course = new MutableLiveData<>();

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
        user = Auth.loadUser(root.getContext());

        getCourse(id, user.getId()).observe(this, this::displayCourse);
    }

    private MutableLiveData<Course> getCourse(String cid, @Nullable String uid) {
        loadCourse(cid, uid);
        return course;
    }

    private void loadCourse(String cid, @Nullable String uid) {
        if (isLoading) return;
        isLoading = true;
        executor.execute(() -> {
            try {
                Log.d(TAG, "loadCourse: loading course");
                Course c = db.getCourse(cid, uid);
                Log.d(TAG, "loadCourse: course loaded\n" +
                        c.getId() + "\n" +
                        c.getName() + "\n" +
                        c.getHoles().stream().map(
                                h -> h.getId() + ", " + h.getScore()
                        ).reduce(
                                "[id, score]: ", (a, b) -> a + " | " + b
                        )
                );
                course.postValue(c);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            isLoading = false;
        });
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
        courseHoles.setAdapter(new HoleAdapter(course.getHoles()));

        int score = course.getHoles().stream().map(Hole::getScore).reduce(0, Integer::sum);
        TextView courseScore = findViewById(R.id.courseDetailsScoreText);
        courseScore.setText(String.valueOf(score));

        TextView courseHandicap = findViewById(R.id.courseDetailsHandicapText);
        courseHandicap.setText("0");
        // TODO: idk cara itungnya

        Button courseSave = findViewById(R.id.courseDetailsSaveBtn);
        courseSave.setOnClickListener(v -> handleSave(course.getId()));
    }

    private void handleSave(String cid) {
        if (isLoading) {
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
        Log.d(TAG, "handleSave: saving holes " + holes);
        db.saveHoles(holes, cid, user.getId());
        db.saveHistory(cid, user.getId());
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
        db.close();
        course = null;
    }
}