package com.devkrishna.doomsday;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DotPreviewView extends View {

    private final Paint donePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint leftPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint currentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public DotPreviewView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // 🎨 load theme colors
        donePaint.setColor(ThemeManager.getFilledColor(context));
        leftPaint.setColor(ThemeManager.getEmptyColor(context));
        currentPaint.setColor(ThemeManager.getCurrentColor(context));

        textPaint.setColor(ThemeManager.getCurrentColor(context));
        textPaint.setTextSize(60);
        textPaint.setTextAlign(Paint.Align.CENTER);

        // 🔤 font style support
        String font = ThemeManager.getFont(context);
        if ("BOLD".equals(font)) {
            textPaint.setFakeBoldText(true);
        } else if ("MONO".equals(font)) {
            textPaint.setTypeface(Typeface.MONOSPACE);
        } else {
            textPaint.setTypeface(Typeface.DEFAULT);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(Color.BLACK);

        int total = 365;
        int current = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
        int left = total - current;
        int percent = (current * 100) / total;

        int cols = 20;
        float dotRadius = 7f;
        float gap = 22f;

        int rows = (int) Math.ceil(total / (float) cols);

        float gridWidth = (cols - 1) * gap;
        float gridHeight = (rows - 1) * gap;

        // 🎯 proper perfect centering
        float startX = (getWidth() - gridWidth) / 2f;
        float startY = (getHeight() - gridHeight) / 2f - 120;

        for (int i = 0; i < total; i++) {
            int row = i / cols;
            int col = i % cols;

            float x = startX + col * gap;
            float y = startY + row * gap;

            Paint p;
            if (i < current - 1) {
                p = donePaint;
            } else if (i == current - 1) {
                p = currentPaint;
            } else {
                p = leftPaint;
            }

            canvas.drawCircle(x, y, dotRadius, p);
        }

        // 📅 date style support
        String style = ThemeManager.getDateStyle(getContext());
        String bottomText;

        switch (style) {
            case "DATE_PERCENT":
                String date = new SimpleDateFormat("dd MMM", Locale.getDefault())
                        .format(Calendar.getInstance().getTime());
                bottomText = date + " • " + percent + "%";
                break;

            case "PERCENT_ONLY":
                bottomText = percent + "% completed";
                break;

            default:
                bottomText = left + "d left • " + percent + "%";
                break;
        }

        canvas.drawText(
                bottomText,
                getWidth() / 2f,
                startY + gridHeight + 100,
                textPaint
        );
    }
}