package com.devkrishna.doomsday.ui.main;

import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.devkrishna.doomsday.R;
import com.devkrishna.doomsday.core.manager.GoalManager;
import com.devkrishna.doomsday.theme.ThemeManager;
import com.devkrishna.doomsday.ui.settings.SettingsActivity;
import com.devkrishna.doomsday.wallpaper.DoomWallpaperService;

public class MainActivity extends AppCompatActivity {

    private DotPreviewView previewView;
    private ImageButton settingsBtn;
    private TutorialOverlayView tutorialView;
    private FrameLayout rootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ThemeManager.applyTheme(this);
        setContentView(R.layout.activity_main);

        initViews();

        // CRITICAL: ensure first render is correct
        refreshPipeline();

        setupTutorial();
        setupActions();
    }

    // =========================
    // INIT
    // =========================
    private void initViews() {
        previewView = findViewById(R.id.previewView);
        settingsBtn = findViewById(R.id.settingsBtn);
        tutorialView = findViewById(R.id.tutorialView);
        rootLayout = findViewById(R.id.rootLayout);
    }

    // =========================
    // PIPELINE REFRESH (CENTRAL)
    // =========================
    private void refreshPipeline() {

        GoalManager.get(this).refresh();

        if (previewView != null) {}
    }

    // =========================
    // TUTORIAL
    // =========================
    private void setupTutorial() {

        boolean firstLaunch = ThemeManager.prefs(this).getBoolean("first_launch", true);

        if (!firstLaunch) {
            if (tutorialView != null) {
                rootLayout.removeView(tutorialView);
            }
            return;
        }

        tutorialView.setOnClickListener(
                v -> {
                    tutorialView.nextStep();

                    if (tutorialView.getVisibility() == View.GONE) {
                        ThemeManager.prefs(this).edit().putBoolean("first_launch", false).apply();

                        rootLayout.post(() -> rootLayout.removeView(tutorialView));
                    }
                });
    }

    // =========================
    // ACTIONS
    // =========================
    private void setupActions() {

        settingsBtn.setOnClickListener(
                v -> startActivity(new Intent(this, SettingsActivity.class)));

        previewView.setOnClickListener(
                v -> {
                    try {
                        Intent intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
                        intent.putExtra(
                                WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                                new ComponentName(this, DoomWallpaperService.class));
                        startActivity(intent);
                    } catch (Exception e) {
                        Toast.makeText(this, "Wallpaper picker failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // =========================
    // LIFECYCLE
    // =========================
    @Override
    protected void onResume() {
        super.onResume();

        refreshPipeline();

        if (tutorialView != null) {
            tutorialView.refreshTheme();
        }
    }
}
