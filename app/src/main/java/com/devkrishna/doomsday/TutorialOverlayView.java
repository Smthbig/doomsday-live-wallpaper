package com.devkrishna.doomsday;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;

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

    private void init() {
        dimPaint.setColor(Color.parseColor("#B3000000"));

        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(48);
        textPaint.setFakeBoldText(true);

        arrowPaint.setColor(Color.parseColor("#FF9800"));
        arrowPaint.setStrokeWidth(8);
        arrowPaint.setStyle(Paint.Style.STROKE);
    }

    public void nextStep() {
        step++;
        if (step > 2) {
            setVisibility(GONE);
        } else {
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawRect(0, 0, getWidth(), getHeight(), dimPaint);

        if (step == 0) {
            // arrow to preview center
            float cx = getWidth() / 2f;
            float cy = getHeight() / 2f;

            canvas.drawLine(cx, 220, cx, cy - 100, arrowPaint);
            canvas.drawLine(cx, cy - 100, cx - 25, cy - 130, arrowPaint);
            canvas.drawLine(cx, cy - 100, cx + 25, cy - 130, arrowPaint);

            canvas.drawText("Tap preview to set wallpaper", 80, 180, textPaint);

        } else if (step == 1) {
            // arrow to top-right settings
            float x = getWidth() - 100;
            float y = 120;

            canvas.drawLine(x - 150, y + 100, x, y, arrowPaint);
            canvas.drawLine(x, y, x - 20, y + 35, arrowPaint);
            canvas.drawLine(x, y, x - 40, y, arrowPaint);

            canvas.drawText("Use settings here", 80, 220, textPaint);

        } else if (step == 2) {
            canvas.drawText("Wallpaper updates daily automatically", 60, getHeight() / 2f, textPaint);
            canvas.drawText("Tap anywhere for Done", 120, getHeight() / 2f + 80, textPaint);
        }
    }
}