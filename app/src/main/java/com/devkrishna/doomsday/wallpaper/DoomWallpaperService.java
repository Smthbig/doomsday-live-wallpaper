package com.devkrishna.doomsday.wallpaper;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.service.wallpaper.WallpaperService;
import android.util.TypedValue;
import android.view.SurfaceHolder;

import com.devkrishna.doomsday.theme.ThemeManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DoomWallpaperService extends WallpaperService {

    @Override
    public Engine onCreateEngine() {
        return new DoomEngine();
    }

    class DoomEngine extends Engine {

        private final Paint donePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint leftPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint currentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        @Override
        public void onVisibilityChanged(boolean visible) {
            if (visible) draw();
        }

        // =========================
        // DIMENSIONS
        // =========================
        private float dp(float v) {
            return TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    v,
                    getResources().getDisplayMetrics()
            );
        }

        private float sp(float v) {
            return TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_SP,
                    v,
                    getResources().getDisplayMetrics()
            );
        }

        // =========================
        // DRAW
        // =========================
        private void draw() {

            SurfaceHolder holder = getSurfaceHolder();
            Canvas canvas = holder.lockCanvas();
            if (canvas == null) return;

            // =========================
            // THEME
            // =========================
            int bg = ThemeManager.getBackgroundColor(DoomWallpaperService.this);

            donePaint.setColor(ThemeManager.getFilledColor(DoomWallpaperService.this));
            leftPaint.setColor(ThemeManager.getEmptyColor(DoomWallpaperService.this));
            currentPaint.setColor(ThemeManager.getCurrentColor(DoomWallpaperService.this));

            textPaint.setColor(ThemeManager.getCurrentColor(DoomWallpaperService.this));
            textPaint.setTextAlign(Paint.Align.CENTER);
            textPaint.setTextSize(sp(14));

            applyFont();

            canvas.drawColor(bg);

            // =========================
            // DATA
            // =========================
            String mode = ThemeManager.prefs(DoomWallpaperService.this)
                    .getString("mode", "YEAR");

            int total = "GOAL".equals(mode)
                    ? ThemeManager.prefs(DoomWallpaperService.this).getInt("goal_days", 365)
                    : 365;

            int current = "GOAL".equals(mode)
                    ? Math.min(Calendar.getInstance().get(Calendar.DAY_OF_MONTH), total)
                    : Calendar.getInstance().get(Calendar.DAY_OF_YEAR);

            int left = total - current;
            int percent = (current * 100) / total;

            // =========================
            // LAYOUT
            // =========================
            int cols = Math.min(20, total);

            float radius = dp(4);
            float gap = dp(16);

            int rows = (int) Math.ceil(total / (float) cols);

            float gridWidth = (cols - 1) * gap;
            float gridHeight = (rows - 1) * gap;

            float startX = (canvas.getWidth() - gridWidth) / 2f;
            float startY = (canvas.getHeight() - gridHeight) / 2f - dp(40);

            // =========================
            // DOTS
            // =========================
            for (int i = 0; i < total; i++) {

                int row = i / cols;
                int col = i % cols;

                float x = startX + col * gap;
                float y = startY + row * gap;

                Paint p;
                float r = radius;

                if (i < current - 1) {
                    p = donePaint;
                    p.setAlpha(220);
                } else if (i == current - 1) {
                    p = currentPaint;
                    r = radius * 1.6f;
                    p.setAlpha(255);
                } else {
                    p = leftPaint;
                    p.setAlpha(100);
                }

                canvas.drawCircle(x, y, r, p);
            }

            // =========================
            // TEXT
            // =========================
            drawText(canvas, startY + gridHeight + dp(32), percent, left, mode);

            holder.unlockCanvasAndPost(canvas);
        }

        // =========================
        // TEXT DRAW
        // =========================
        private void drawText(Canvas canvas, float y, int percent, int left, String mode) {

            String style = ThemeManager.getDateStyle(DoomWallpaperService.this);
            String text;

            switch (style) {
                case "DATE_PERCENT":
                    String date = new SimpleDateFormat("dd MMM", Locale.getDefault())
                            .format(Calendar.getInstance().getTime());
                    text = date + " • " + percent + "%";
                    break;

                case "PERCENT_ONLY":
                    text = percent + "% completed";
                    break;

                default:
                    text = left + "d left • " + percent + "%";
                    break;
            }

            Paint.FontMetrics fm = textPaint.getFontMetrics();
            float baseline = y - (fm.ascent + fm.descent) / 2;

            canvas.drawText(text, canvas.getWidth() / 2f, baseline, textPaint);
        }

        // =========================
        // FONT
        // =========================
        private void applyFont() {

            textPaint.setFakeBoldText(false);

            String font = ThemeManager.getFont(DoomWallpaperService.this);

            if ("BOLD".equals(font)) {
                textPaint.setFakeBoldText(true);
            } else if ("MONO".equals(font)) {
                textPaint.setTypeface(Typeface.MONOSPACE);
            } else {
                textPaint.setTypeface(Typeface.DEFAULT);
            }
        }
    }
}