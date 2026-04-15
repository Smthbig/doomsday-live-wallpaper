package com.devkrishna.doomsday.ui.main;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.devkrishna.doomsday.theme.ThemeManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DotPreviewView extends View {

    private final Paint donePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint leftPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint currentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private int backgroundColor;

    public DotPreviewView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    // =========================
    // INIT
    // =========================
    private void init() {

        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(sp(16));

        loadTheme();
        applyFont();
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
    // THEME LOADING (FIXED)
    // =========================
    private void loadTheme() {

        // priority: user preference → fallback theme

        donePaint.setColor(ThemeManager.getFilledColor(getContext()));
        leftPaint.setColor(ThemeManager.getEmptyColor(getContext()));
        currentPaint.setColor(ThemeManager.getCurrentColor(getContext()));

        textPaint.setColor(resolveAttr(android.R.attr.textColorPrimary));
        backgroundColor = ThemeManager.getBackgroundColor(getContext());
    }

    private int resolveAttr(int attr) {
        TypedValue value = new TypedValue();
        getContext().getTheme().resolveAttribute(attr, value, true);
        return value.data;
    }

    // =========================
    // FONT
    // =========================
    private void applyFont() {

        textPaint.setFakeBoldText(false);

        String font = ThemeManager.getFont(getContext());

        if ("BOLD".equals(font)) {
            textPaint.setFakeBoldText(true);
        } else if ("MONO".equals(font)) {
            textPaint.setTypeface(Typeface.MONOSPACE);
        } else {
            textPaint.setTypeface(Typeface.DEFAULT);
        }
    }

    // =========================
    // DRAW
    // =========================
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(backgroundColor);

        int total = 365;
        int current = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
        int left = total - current;
        int percent = (current * 100) / total;

        int cols = 20;

        float dotRadius = dp(4);
        float gap = dp(16);

        int rows = (int) Math.ceil(total / (float) cols);

        float gridWidth = (cols - 1) * gap;
        float gridHeight = (rows - 1) * gap;

        float startX = (getWidth() - gridWidth) / 2f;
        float startY = (getHeight() - gridHeight) / 2f - dp(40);

        for (int i = 0; i < total; i++) {

            int row = i / cols;
            int col = i % cols;

            float x = startX + col * gap;
            float y = startY + row * gap;

            Paint p;
            float radius = dotRadius;

            if (i < current - 1) {
                p = donePaint;
                p.setAlpha(220);
            } else if (i == current - 1) {
                p = currentPaint;
                radius = dotRadius * 1.6f;
                p.setAlpha(255);
            } else {
                p = leftPaint;
                p.setAlpha(100);
            }

            canvas.drawCircle(x, y, radius, p);
        }

        drawBottomText(canvas, startY + gridHeight + dp(32), percent, left);
    }

    // =========================
    // TEXT
    // =========================
    private void drawBottomText(Canvas canvas, float y, int percent, int left) {

        String style = ThemeManager.getDateStyle(getContext());
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

        canvas.drawText(text, getWidth() / 2f, baseline, textPaint);
    }

    // =========================
    // PUBLIC API
    // =========================
    public void refreshTheme() {
        loadTheme();
        applyFont();
        invalidate();
    }
}