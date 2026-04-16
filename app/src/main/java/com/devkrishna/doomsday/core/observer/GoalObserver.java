package com.devkrishna.doomsday.core.observer;

import com.devkrishna.doomsday.core.model.RenderResult;

public interface GoalObserver {
    void onGoalUpdated(RenderResult data);
}