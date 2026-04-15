package com.devkrishna.doomsday.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.devkrishna.doomsday.R;
import com.devkrishna.doomsday.ui.main.MainActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeManager.applyTheme(this);

        // Apply theme before UI
        setTheme(R.style.Theme_App);

        setContentView(R.layout.activity_splash);

        TextView logo = findViewById(R.id.logo);

        // =========================
        // FADE ANIMATION
        // =========================
        AlphaAnimation fade = new AlphaAnimation(0.3f, 1f);
        fade.setDuration(1000);
        fade.setRepeatMode(AlphaAnimation.REVERSE);
        fade.setRepeatCount(1);

        logo.startAnimation(fade);

        // =========================
        // NAVIGATION
        // =========================
        logo.postDelayed(() -> {
            startActivity(new Intent(this, MainActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, 1600);
    }
}