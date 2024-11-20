
package com.pxr.golf.ui.analytics;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class AnalyticsViewModelFactory implements ViewModelProvider.Factory {
    private final Application application;
    private final String userId;

    public AnalyticsViewModelFactory(Application application, String userId) {
        this.application = application;
        this.userId = userId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(AnalyticsViewModel.class)) {
            return (T) new AnalyticsViewModel(application, userId);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}