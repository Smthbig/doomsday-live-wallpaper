package com.devkrishna.doomsday.wallpaper;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

import com.devkrishna.doomsday.core.manager.GoalManager;
import com.devkrishna.doomsday.core.model.RenderResult;
import com.devkrishna.doomsday.core.observer.GoalObserver;
import com.devkrishna.doomsday.core.renderer.CanvasRenderer;
import com.devkrishna.doomsday.theme.ThemeManager;

import java.util.Calendar;

public class DoomWallpaperService extends WallpaperService {

    @Override
    public Engine onCreateEngine() {
        return new DoomEngine();
    }

    class DoomEngine extends Engine implements GoalObserver {

        private final Handler handler = new Handler();
        private final CanvasRenderer canvasRenderer = new CanvasRenderer();

        private SharedPreferences prefs;
        private boolean visible = false;

        private Bitmap cachedWallpaper;

        private final Runnable minuteTick = this::onMinuteTick;

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            prefs = ThemeManager.prefs(DoomWallpaperService.this);
            loadWallpaperOnce();
        }

        // =========================
        // VISIBILITY
        // =========================
        @Override
        public void onVisibilityChanged(boolean visible) {
            this.visible = visible;

            if (visible) {
                GoalManager.get(DoomWallpaperService.this).addObserver(this);
                requestDraw();
                scheduleNextMinute();
            } else {
                GoalManager.get(DoomWallpaperService.this).removeObserver(this);
                handler.removeCallbacks(minuteTick);
            }
        }

        // =========================
        // OBSERVER
        // =========================
        @Override
        public void onGoalUpdated(RenderResult data) {
            requestDraw();
        }

        // =========================
        // TIME TICK
        // =========================
        private void onMinuteTick() {
            if (!visible) return;

            requestDraw();
            scheduleNextMinute();
        }

        private void scheduleNextMinute() {

            handler.removeCallbacks(minuteTick);

            long now = System.currentTimeMillis();

            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(now);

            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            cal.add(Calendar.MINUTE, 1);

            long delay = cal.getTimeInMillis() - now;

            handler.postDelayed(minuteTick, delay);
        }

        // =========================
        // DRAW
        // =========================
        private void requestDraw() {
            draw();
        }

        private void draw() {

            if (!visible) return;

            SurfaceHolder holder = getSurfaceHolder();
            Canvas canvas = null;

            try {
                canvas = holder.lockCanvas();
                if (canvas == null) return;

                int width = canvas.getWidth();
                int height = canvas.getHeight();

                // =========================
                // BACKGROUND
                // =========================
                if (cachedWallpaper != null) {
                    Rect dst = new Rect(0, 0, width, height);
                    canvas.drawBitmap(cachedWallpaper, null, dst, null);
                } else {
                    int bg = ThemeManager.getBackgroundColor(DoomWallpaperService.this);
                    canvas.drawColor(bg);
                }

                // =========================
                // DATA (NO refresh())
                // =========================
                RenderResult data =
                        GoalManager.get(DoomWallpaperService.this).getRenderResult();

                // =========================
                // DRAW
                // =========================
                canvasRenderer.draw(
                        DoomWallpaperService.this,
                        canvas,
                        width,
                        height,
                        data,
                        false
                );

            } catch (Exception ignored) {

            } finally {
                if (canvas != null) {
                    holder.unlockCanvasAndPost(canvas);
                }
            }
        }

        // =========================
        // WALLPAPER CACHE
        // =========================
        private void loadWallpaperOnce() {
            try {
                String path = prefs.getString("wallpaper_path", null);
                if (path == null) {
                    cachedWallpaper = null;
                    return;
                }

                cachedWallpaper = BitmapFactory.decodeFile(path);

            } catch (Exception e) {
                cachedWallpaper = null;
            }
        }
    }
}