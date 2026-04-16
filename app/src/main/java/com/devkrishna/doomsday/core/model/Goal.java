package com.devkrishna.doomsday.core.model;

import java.util.Calendar;

public class Goal {

    private final int totalDays;
    private final long startTime;

    public Goal(int totalDays, long startTime) {
        this.totalDays = totalDays;
        this.startTime = startTime;
    }

    public int getTotalDays() {
        return totalDays;
    }

    public long getStartTime() {
        return startTime;
    }

    // =========================
    // FIXED DAY CALCULATION
    // =========================
    public int getDaysPassed() {

        long now = System.currentTimeMillis();

        // Normalize start date to midnight
        Calendar startCal = Calendar.getInstance();
        startCal.setTimeInMillis(startTime);
        resetToMidnight(startCal);

        // Normalize current date to midnight
        Calendar nowCal = Calendar.getInstance();
        nowCal.setTimeInMillis(now);
        resetToMidnight(nowCal);

        long diff = nowCal.getTimeInMillis() - startCal.getTimeInMillis();

        int days = (int) (diff / (1000L * 60 * 60 * 24));

        // SAFETY CLAMP
        if (days < 0) return 0;
        if (days > totalDays) return totalDays;

        return days;
    }

    // =========================
    // HELPER
    // =========================
    private void resetToMidnight(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
    }
}