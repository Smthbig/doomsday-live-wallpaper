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
        // APPLY SAVED THEME MODE
        // =========================
        applyThemeMode();

        // =========================
        // MATERIAL YOU (Dynamic Color)
        // =========================
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            DynamicColors.applyToActivitiesIfAvailable(this);
        }
    }

    // =========================
    // THEME MODE HANDLER
    // =========================
    private void applyThemeMode() {

        String mode = ThemeManager.getThemeMode(this);

        switch (mode) {
            case "LIGHT":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;

            case "DARK":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;

            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }
}