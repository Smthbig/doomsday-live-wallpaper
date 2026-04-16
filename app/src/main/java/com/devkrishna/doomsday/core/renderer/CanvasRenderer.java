package com.devkrishna.doomsday.core.renderer;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.BatteryManager;
import android.util.TypedValue;

import com.devkrishna.doomsday.core.model.RenderResult;
import com.devkrishna.doomsday.theme.ThemeManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CanvasRenderer {

    private final Paint donePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint leftPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint currentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private int backgroundColor;

    public void draw(
            Context context,
            Canvas canvas,
            int width,
            int height,
            RenderResult data,
            boolean drawBackground) {

        if (width == 0 || height == 0 || data == null) return;

        loadTheme(context);
        applyFont(context);

        if (drawBackground) {
            canvas.drawColor(backgroundColor);
        }

        int total = data.getTotal();
        int current = data.getPassed();

        int left = Math.max(0, total - current);
        int percent = total == 0 ? 0 : (int) ((current * 100f) / total);
        int battery = getBattery(context);

        String date =
                new SimpleDateFormat("dd MMM", Locale.getDefault())
                        .format(Calendar.getInstance().getTime());

        String topText = battery >= 0 ? date + " • " + battery + "%" : date;

        Paint.FontMetrics fmTop = textPaint.getFontMetrics();
        float topY = dp(context, 24) - (fmTop.ascent + fmTop.descent) / 2;

        canvas.drawText(topText, width / 2f, topY, textPaint);

        int cols = 20;

        float gap = getGap(context, width, cols);
        float radius = dp(context, 4);

        int rows = (int) Math.ceil(total / (float) cols);

        float gridWidth = (cols - 1) * gap;
        float gridHeight = (rows - 1) * gap;

        float startX = (width - gridWidth) / 2f;
        float startY = (height - gridHeight) / 2f + dp(context, 10);

        for (int i = 0; i < total; i++) {

            int row = i / cols;
            int col = i % cols;

            float x = startX + col * gap;
            float y = startY + row * gap;

            Paint p;
            float r = radius;

            if (i < current) {
                p = donePaint;
                p.setAlpha(220);
            } else if (i == current && current < total) {
                p = currentPaint;
                r = radius * 1.6f;
                p.setAlpha(255);
            } else {
                p = leftPaint;
                p.setAlpha(100);
            }

            canvas.drawCircle(x, y, r, p);
        }

        drawText(context, canvas, width, startY + gridHeight + dp(context, 32), percent, left);
    }

    private int getBattery(Context context) {
        try {
            IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = context.registerReceiver(null, filter);

            if (batteryStatus == null) return -1;

            int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

            if (level < 0 || scale <= 0) return -1;

            return (int) ((level / (float) scale) * 100);

        } catch (Exception e) {
            return -1;
        }
    }

    private float getGap(Context context, int width, int cols) {
        float availableWidth = width * 0.8f;
        float gap = availableWidth / cols;
        return Math.max(dp(context, 10), Math.min(gap, dp(context, 20)));
    }

    private void loadTheme(Context context) {
        donePaint.setColor(ThemeManager.getFilledColor(context));
        leftPaint.setColor(ThemeManager.getEmptyColor(context));
        currentPaint.setColor(ThemeManager.getCurrentColor(context));

        textPaint.setColor(resolveAttr(context, android.R.attr.textColorPrimary));
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(sp(context, 16));

        backgroundColor = ThemeManager.getBackgroundColor(context);
    }

    private void applyFont(Context context) {
        textPaint.setFakeBoldText(false);

        String font = ThemeManager.getFont(context);

        if ("BOLD".equals(font)) {
            textPaint.setFakeBoldText(true);
            textPaint.setTypeface(Typeface.DEFAULT_BOLD);
        } else if ("MONO".equals(font)) {
            textPaint.setTypeface(Typeface.MONOSPACE);
        } else {
            textPaint.setTypeface(Typeface.DEFAULT);
        }
    }

    private void drawText(
            Context context, Canvas canvas, int width, float y, int percent, int left) {
        String style = ThemeManager.getDateStyle(context);
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

        canvas.drawText(text, width / 2f, baseline, textPaint);
    }

    private int resolveAttr(Context context, int attr) {
        android.util.TypedValue value = new android.util.TypedValue();
        context.getTheme().resolveAttribute(attr, value, true);
        return value.data;
    }

    private float dp(Context c, float v) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, v, c.getResources().getDisplayMetrics());
    }

    private float sp(Context c, float v) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, v, c.getResources().getDisplayMetrics());
    }
}
