package com.devkrishna.doomsday;

import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DoomWallpaperService extends WallpaperService {

    @Override
    public Engine onCreateEngine() {
        return new DoomEngine();
    }

    class DoomEngine extends Engine {

        private final Paint completedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint remainingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint currentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        @Override
        public void onVisibilityChanged(boolean visible) {
            if (visible) drawWallpaper();
        }

        private void drawWallpaper() {
            SurfaceHolder holder = getSurfaceHolder();
            Canvas canvas = holder.lockCanvas();
            if (canvas == null) return;

            canvas.drawColor(Color.BLACK);

            SharedPreferences prefs =
                    ThemeManager.prefs(DoomWallpaperService.this);

            String mode = prefs.getString("mode", "YEAR");
            String layoutType = prefs.getString("layout_type", "GRID");
            String dotSize = prefs.getString("dot_size", "AUTO FIT");
            String goalName = prefs.getString("goal_name", "Goal");
            int goalDays = prefs.getInt("goal_days", 365);

            int filledColor = prefs.getInt("filled_color", Color.WHITE);
            int emptyColor = prefs.getInt("empty_color", Color.parseColor("#252525"));
            int currentColor = prefs.getInt("current_color", Color.parseColor("#FF9800"));

            String fontStyle = prefs.getString("font_style", "DEFAULT");
            String dateStyle = prefs.getString("date_style", "Days Left + %");

            completedPaint.setColor(filledColor);
            remainingPaint.setColor(emptyColor);
            currentPaint.setColor(currentColor);

            textPaint.setColor(currentColor);
            textPaint.setTextSize(44);
            textPaint.setTextAlign(Paint.Align.CENTER);

            if ("BOLD".equals(fontStyle)) {
                textPaint.setTypeface(Typeface.DEFAULT_BOLD);
            } else if ("MONO".equals(fontStyle)) {
                textPaint.setTypeface(Typeface.MONOSPACE);
            } else {
                textPaint.setTypeface(Typeface.DEFAULT);
            }

            int totalDays;
            int currentDay;

            if ("GOAL".equals(mode)) {
                totalDays = goalDays;
                currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                currentDay = Math.min(currentDay, totalDays);
            } else {
                totalDays = 365;
                currentDay = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
            }

            int daysLeft = totalDays - currentDay;
            int percent = (currentDay * 100) / totalDays;

            // ===== DOT SIZE =====
            int dotRadius;
            switch (dotSize) {
                case "SMALL":
                    dotRadius = 5;
                    break;
                case "MEDIUM":
                    dotRadius = 8;
                    break;
                case "LARGE":
                    dotRadius = 12;
                    break;
                default:
                    dotRadius = 7;
                    break;
            }

            int columns;
            switch (layoutType) {
                case "LINEAR":
                    columns = totalDays;
                    break;
                case "CIRCLE":
                    columns = Math.min(15, totalDays);
                    break;
                default:
                    columns = Math.min(20, totalDays);
                    break;
            }

            int spacing = dotRadius * 4;
            int rows = (int) Math.ceil(totalDays / (float) columns);

            float gridWidth = columns * spacing;
            float gridHeight = rows * spacing;

            float startX = (canvas.getWidth() - gridWidth) / 2f;
            float startY = (canvas.getHeight() - gridHeight) / 2f - 120;

            // ===== DRAW DOTS =====
            for (int i = 0; i < totalDays; i++) {
                int row = i / columns;
                int col = i % columns;

                float x = startX + col * spacing;
                float y = startY + row * spacing;

                Paint paint;
                if (i < currentDay - 1) {
                    paint = completedPaint;
                } else if (i == currentDay - 1) {
                    paint = currentPaint;
                } else {
                    paint = remainingPaint;
                }

                canvas.drawCircle(x, y, dotRadius, paint);
            }

            // ===== TEXT STYLE =====
            String bottomText;

            switch (dateStyle) {
                case "Current Date + %":
                    String date = new SimpleDateFormat("dd MMM", Locale.getDefault())
                            .format(Calendar.getInstance().getTime());
                    bottomText = date + " • " + percent + "%";
                    break;

                case "% Only":
                    bottomText = percent + "% complete";
                    break;

                default:
                    if ("GOAL".equals(mode)) {
                        bottomText = goalName + " • " + daysLeft + "d left • " + percent + "%";
                    } else {
                        bottomText = daysLeft + "d left • " + percent + "%";
                    }
                    break;
            }

            canvas.drawText(
                    bottomText,
                    canvas.getWidth() / 2f,
                    startY + gridHeight + 100,
                    textPaint
            );

            holder.unlockCanvasAndPost(canvas);
        }
    }
}