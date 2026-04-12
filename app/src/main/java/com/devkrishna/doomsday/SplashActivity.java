package com.devkrishna.doomsday;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView logo = new TextView(this);
        logo.setText("D");
        logo.setTextSize(72);
        logo.setTextColor(0xFFFFFFFF);
        logo.setGravity(android.view.Gravity.CENTER);
        logo.setBackgroundColor(0xFF000000);

        AlphaAnimation fade = new AlphaAnimation(0.2f, 1f);
        fade.setDuration(1200);
        fade.setRepeatMode(AlphaAnimation.REVERSE);
        fade.setRepeatCount(1);

        logo.startAnimation(fade);

        setContentView(logo);

        new Handler().postDelayed(() -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }, 1800);
    }
}