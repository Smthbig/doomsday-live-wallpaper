package com.devkrishna.doomsday.ui.main;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import com.devkrishna.doomsday.core.manager.GoalManager;
import com.devkrishna.doomsday.core.model.RenderResult;
import com.devkrishna.doomsday.core.renderer.CanvasRenderer;
import com.devkrishna.doomsday.core.observer.GoalObserver;

public class DotPreviewView extends View implements GoalObserver {

    private final CanvasRenderer canvasRenderer = new CanvasRenderer();

    // SINGLE SOURCE OF TRUTH (LOCAL CACHE)
    private RenderResult currentData;

    public DotPreviewView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    // =========================
    // DRAW
    // =========================
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (currentData == null) {
            currentData = GoalManager.get(getContext()).getRenderResult();
        }

        canvasRenderer.draw(
                getContext(),
                canvas,
                getWidth(),
                getHeight(),
                currentData,
                true
        );
    }

    // =========================
    // LIFECYCLE
    // =========================
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        GoalManager manager = GoalManager.get(getContext());
        manager.addObserver(this);

        // INITIAL LOAD
        currentData = manager.getRenderResult();
        invalidate();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        GoalManager.get(getContext()).removeObserver(this);
    }

    // =========================
    // OBSERVER
    // =========================
    @Override
    public void onGoalUpdated(RenderResult data) {
        if (data == null) return;

        currentData = data;

        // UI SAFE UPDATE
        postInvalidate();
    }
}