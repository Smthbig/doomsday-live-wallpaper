package com.devkrishna.doomsday;

import android.app.Application;
import android.os.Build;

import androidx.appcompat.app.AppCompatDelegate;

import com.devkrishna.doomsday.theme.ThemeManager;
import com.google.android.material.color.DynamicColors;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // =========================
        // APPLY THEME MODE (Single Source)
        // =========================
        ThemeManager.applyTheme(this);

        // =========================
        // MATERIAL YOU (Dynamic Color)
        // =========================
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            DynamicColors.applyToActivitiesIfAvailable(this);
        }
    }

    private void applyThemeMode() {
        String mode = ThemeManager.getThemeMode(this);

        if ("LIGHT".equals(mode)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else if ("DARK".equals(mode)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
    }
}