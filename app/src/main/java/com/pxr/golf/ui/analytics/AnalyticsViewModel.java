package com.pxr.golf.ui.analytics;

import android.app.Application;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.pxr.golf.db.DBManager;
import com.pxr.golf.models.Course;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AnalyticsViewModel extends ViewModel {
    private final DBManager db;
    private final MutableLiveData<List<Course>> histories;
    private final ExecutorService executorService;
    private final String userId;

    public AnalyticsViewModel(Application app, String userId) {
        this.userId = userId;
        db = new DBManager(app);
        histories = new MutableLiveData<>();
        executorService = Executors.newSingleThreadExecutor();
        loadHistories();
    }

    public MutableLiveData<List<Course>> getHistories() {
        if (histories.getValue() == null || histories.getValue().isEmpty()) {
            loadHistories();
        }
        return histories;
    }

    private void loadHistories() {
        executorService.execute(() -> {
            List<Course> fetchedHistories = db.getHistory(userId);
            histories.postValue(fetchedHistories);
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown();
        db.close();
    }
}
