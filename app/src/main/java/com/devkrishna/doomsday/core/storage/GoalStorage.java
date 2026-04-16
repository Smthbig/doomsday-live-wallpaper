package com.devkrishna.doomsday.core.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.devkrishna.doomsday.core.model.Goal;

public class GoalStorage {

    private static final String PREF = "doom_pref";
    private static final String KEY_DAYS = "goal_days";
    private static final String KEY_START = "goal_start";

    // =========================
    // SAVE
    // =========================
    public static void saveGoal(Context context, Goal goal) {

        if (context == null || goal == null) return;

        SharedPreferences sp =
                context.getSharedPreferences(PREF, Context.MODE_PRIVATE);

        // Preserve existing start time if already exists
        long existingStart = sp.getLong(KEY_START, 0);

        long startTime =
                existingStart != 0 ? existingStart : goal.getStartTime();

        sp.edit()
                .putInt(KEY_DAYS, goal.getTotalDays())
                .putLong(KEY_START, startTime)
                .apply();
    }

    // =========================
    // GET
    // =========================
    public static Goal getGoal(Context context) {

        if (context == null) return null;

        SharedPreferences sp =
                context.getSharedPreferences(PREF, Context.MODE_PRIVATE);

        int days = sp.getInt(KEY_DAYS, -1);
        long start = sp.getLong(KEY_START, -1);

        // strict validation
        if (days <= 0 || start <= 0) {
            return null;
        }

        return new Goal(days, start);
    }

    // =========================
    // CLEAR
    // =========================
    public static void clear(Context context) {

        if (context == null) return;

        SharedPreferences sp =
                context.getSharedPreferences(PREF, Context.MODE_PRIVATE);

        sp.edit().clear().apply();
    }
}