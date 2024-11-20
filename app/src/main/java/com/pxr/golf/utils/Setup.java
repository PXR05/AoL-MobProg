package com.pxr.golf.utils;

import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Setup {
    public static void setMode(AppCompatActivity act) {
        act.getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
    }

    public static void setActionBar(AppCompatActivity act) {
        ActionBar bar = act.getSupportActionBar();
        if (bar != null) {
            bar.hide();
        }
    }

    public static void setInsets(View root) {
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(
                    systemBars.left,
                    insets.getInsets(WindowInsetsCompat.Type.statusBars()).top / 2,
                    systemBars.right,
                    systemBars.bottom
            );
            return insets;
        });
    }

    public static void all(AppCompatActivity act, @Nullable View root) {
        setMode(act);
        setActionBar(act);
        if (root != null) {
            setInsets(root);
        }
    }
}
