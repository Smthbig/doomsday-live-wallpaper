package com.devkrishna.doomsday;

import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private DotPreviewView previewView;
    private ImageButton settingsBtn;
    private TutorialOverlayView tutorialView;
    private FrameLayout rootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        previewView = findViewById(R.id.previewView);
        settingsBtn = findViewById(R.id.settingsBtn);
        tutorialView = findViewById(R.id.tutorialView);
        rootLayout = findViewById(R.id.rootLayout);

        SharedPreferences prefs = getSharedPreferences("doom_prefs", MODE_PRIVATE);
        boolean firstLaunch = prefs.getBoolean("first_launch", true);

        // ✅ tutorial only first time
        if (!firstLaunch) {
            if (tutorialView != null) {
                rootLayout.removeView(tutorialView);
            }
        } else {
            tutorialView.setOnClickListener(v -> {
                tutorialView.nextStep();

                if (tutorialView.getVisibility() == View.GONE) {
                    prefs.edit().putBoolean("first_launch", false).apply();

                    // ✅ completely remove overlay
                    rootLayout.post(() -> rootLayout.removeView(tutorialView));
                }
            });
        }

        settingsBtn.setOnClickListener(v ->
                startActivity(new Intent(this, SettingsActivity.class)));

        previewView.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
                intent.putExtra(
                        WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                        new ComponentName(this, DoomWallpaperService.class)
                );
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, "Wallpaper picker failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}