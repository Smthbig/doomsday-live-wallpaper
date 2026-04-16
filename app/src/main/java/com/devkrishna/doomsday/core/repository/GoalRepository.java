package com.devkrishna.doomsday.core.repository;

import android.content.Context;

import com.devkrishna.doomsday.core.model.Goal;
import com.devkrishna.doomsday.core.storage.GoalStorage;

public class GoalRepository {

    private final Context context;

    public GoalRepository(Context context) {
        this.context = context.getApplicationContext();
    }

    public Goal getGoal() {
        return GoalStorage.getGoal(context);
    }

    public void saveGoal(Goal goal) {
        GoalStorage.saveGoal(context, goal);
    }

    public void clear() {
        GoalStorage.clear(context);
    }
}