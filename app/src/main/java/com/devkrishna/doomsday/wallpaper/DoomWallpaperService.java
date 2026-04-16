package com.devkrishna.doomsday.wallpaper;

import android.content.SharedPreferences;
import android.graphics.*;
import android.os.Handler;
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

        private final Handler handler = new Handler(android.os.Looper.getMainLooper());
        private final Runnable drawRunner = this::draw;

        private SharedPreferences prefs;

        // =========================
        // LIFECYCLE
        // =========================
        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);

            prefs = ThemeManager.prefs(DoomWallpaperService.this);
            prefs.registerOnSharedPreferenceChangeListener(prefListener);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            stopLoop();

            if (prefs != null) {
                prefs.unregisterOnSharedPreferenceChangeListener(prefListener);
            }
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            if (visible) startLoop();
            else stopLoop();
        }

        private final SharedPreferences.OnSharedPreferenceChangeListener prefListener =
                (sp, key) -> draw();

        // =========================
        // LOOP
        // =========================
        private void startLoop() {
            handler.removeCallbacks(drawRunner);
            handler.post(drawRunner);
        }

        private void stopLoop() {
            handler.removeCallbacks(drawRunner);
        }

        // =========================
        // DIMENSIONS
        // =========================
        private float dp(float v) {
            return TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, v, getResources().getDisplayMetrics());
        }

        private float sp(float v) {
            return TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_SP, v, getResources().getDisplayMetrics());
        }

        // =========================
        // DRAW
        // =========================
        private void draw() {

            SurfaceHolder holder = getSurfaceHolder();
            Canvas canvas = null;

            try {
                canvas = holder.lockCanvas();
                if (canvas == null) return;

                // =========================
                // BACKGROUND
                // =========================
                Bitmap wallpaper = loadWallpaper();

                if (wallpaper != null) {
                    Rect dst = new Rect(0, 0, canvas.getWidth(), canvas.getHeight());
                    canvas.drawBitmap(wallpaper, null, dst, null);
                } else {
                    canvas.drawColor(ThemeManager.getBackgroundColor(DoomWallpaperService.this));
                }

                // =========================
                // COLORS
                // =========================
                donePaint.setColor(ThemeManager.getFilledColor(DoomWallpaperService.this));
                leftPaint.setColor(ThemeManager.getEmptyColor(DoomWallpaperService.this));
                currentPaint.setColor(ThemeManager.getCurrentColor(DoomWallpaperService.this));
                textPaint.setColor(ThemeManager.getCurrentColor(DoomWallpaperService.this));
                textPaint.setTextAlign(Paint.Align.CENTER);
                textPaint.setTextSize(sp(18));

                applyFont();

                // =========================
                // DATA
                // =========================
                String mode = prefs.getString("mode", "YEAR");

                int total = "GOAL".equals(mode) ? prefs.getInt("goal_days", 365) : 365;

                int current = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
                current = Math.min(current, total);

                int left = total - current;
                int percent = (current * 100) / total;

                // =========================
                // GRID
                // =========================
                int cols = Math.min(20, total);

                float gap = Math.max(dp(12), canvas.getWidth() / (cols + 2f));
                float radius = gap * 0.25f;

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
                drawText(canvas, startY + gridHeight + dp(36), percent, left);

            } finally {
                if (canvas != null) {
                    holder.unlockCanvasAndPost(canvas);
                }
            }

            handler.postDelayed(drawRunner, 1000);
        }

        // =========================
        // TEXT
        // =========================
        private void drawText(Canvas canvas, float y, int percent, int left) {

            String style = ThemeManager.getDateStyle(DoomWallpaperService.this);;
            String text;

            switch (style) {
                case "DATE_PERCENT":
                    String date =
                            new SimpleDateFormat("dd MMM", Locale.getDefault())
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
                textPaint.setTypeface(Typeface.DEFAULT_BOLD);
            } else if ("MONO".equals(font)) {
                textPaint.setTypeface(Typeface.MONOSPACE);
            } else {
                textPaint.setTypeface(Typeface.DEFAULT);
            }
        }

        // =========================
        // WALLPAPER
        // =========================
        private Bitmap loadWallpaper() {

            String path = prefs.getString("wallpaper_path", null);
            if (path == null) return null;

            return BitmapFactory.decodeFile(path);
        }
    }
}
