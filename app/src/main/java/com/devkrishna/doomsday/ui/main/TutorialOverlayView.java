package com.devkrishna.doomsday.ui.main;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;

import com.devkrishna.doomsday.theme.ThemeManager;

public class TutorialOverlayView extends View {

    private final Paint dimPaint = new Paint();
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint arrowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private int step = 0;

    public TutorialOverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TutorialOverlayView(Context context) {
        super(context);
        init();
    }

    // =========================
    // INIT
    // =========================
    private void init() {

        dimPaint.setColor(Color.parseColor("#B3000000")); // semi transparent

        textPaint.setColor(ThemeManager.getCurrentColor(getContext()));
        textPaint.setTextSize(48);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setFakeBoldText(true);

        arrowPaint.setColor(ThemeManager.getCurrentColor(getContext()));
        arrowPaint.setStrokeWidth(8);
        arrowPaint.setStyle(Paint.Style.STROKE);
    }

    // =========================
    // STEP CONTROL
    // =========================
    public void nextStep() {
        step++;
        if (step > 2) {
            setVisibility(GONE);
        } else {
            invalidate();
        }
    }

    // =========================
    // DRAW
    // =========================
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // background dim
        canvas.drawRect(0, 0, getWidth(), getHeight(), dimPaint);

        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;

        if (step == 0) {

            // arrow pointing to center preview
            drawArrow(canvas, centerX, 200, centerX, centerY - 120);

            drawCenteredText(canvas,
                    "Tap preview to set wallpaper",
                    centerX,
                    140);

        } else if (step == 1) {

            float targetX = getWidth() - 120;
            float targetY = 140;

            drawArrow(canvas,
                    targetX - 180, targetY + 120,
                    targetX, targetY);

            drawCenteredText(canvas,
                    "Use settings here",
                    centerX,
                    200);

        } else if (step == 2) {

            drawCenteredText(canvas,
                    "Wallpaper updates daily automatically",
                    centerX,
                    centerY - 40);

            drawCenteredText(canvas,
                    "Tap anywhere to finish",
                    centerX,
                    centerY + 40);
        }
    }

    // =========================
    // HELPERS
    // =========================

    private void drawCenteredText(Canvas canvas, String text, float x, float y) {
        canvas.drawText(text, x, y, textPaint);
    }

    private void drawArrow(Canvas canvas, float startX, float startY, float endX, float endY) {

        canvas.drawLine(startX, startY, endX, endY, arrowPaint);

        float angle = (float) Math.atan2(endY - startY, endX - startX);
        float arrowSize = 30f;

        float x1 = endX - arrowSize * (float) Math.cos(angle - Math.PI / 6);
        float y1 = endY - arrowSize * (float) Math.sin(angle - Math.PI / 6);

        float x2 = endX - arrowSize * (float) Math.cos(angle + Math.PI / 6);
        float y2 = endY - arrowSize * (float) Math.sin(angle + Math.PI / 6);

        canvas.drawLine(endX, endY, x1, y1, arrowPaint);
        canvas.drawLine(endX, endY, x2, y2, arrowPaint);
    }

    // =========================
    // THEME REFRESH
    // =========================
    public void refreshTheme() {
        textPaint.setColor(ThemeManager.getCurrentColor(getContext()));
        arrowPaint.setColor(ThemeManager.getCurrentColor(getContext()));
        invalidate();
    }
}