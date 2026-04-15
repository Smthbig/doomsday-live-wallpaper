package com.devkrishna.doomsday.theme;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.TypedValue;

import androidx.appcompat.app.AppCompatDelegate;

public class ThemeManager {

    private static final String PREF_NAME = "doom_prefs";

    // =========================
    // PREF
    // =========================
    public static SharedPreferences prefs(Context c) {
        if (c == null) return null;
        return c.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // =========================
    // SAFE ATTR RESOLVER
    // =========================
    private static int resolveAttr(Context context, int attr) {
        if (context == null) return Color.TRANSPARENT;

        TypedValue value = new TypedValue();
        boolean found = context.getTheme().resolveAttribute(attr, value, true);

        if (!found) return Color.TRANSPARENT;

        if (value.type >= TypedValue.TYPE_FIRST_COLOR_INT &&
                value.type <= TypedValue.TYPE_LAST_COLOR_INT) {
            return value.data;
        }

        if (value.resourceId != 0) {
            try {
                return context.getResources().getColor(value.resourceId, context.getTheme());
            } catch (Exception ignored) {}
        }

        return Color.TRANSPARENT;
    }

    // =========================
    // SAFE COLOR READ
    // =========================
    private static int safeColor(int color, int fallback) {
        if (color == 0) return fallback;
        return color;
    }

    // =========================
    // COLORS
    // =========================

    public static int getFilledColor(Context c) {
        SharedPreferences p = prefs(c);
        if (p != null && p.contains("filled_color")) {
            return safeColor(p.getInt("filled_color", Color.WHITE), Color.WHITE);
        }
        return resolveAttr(c, com.google.android.material.R.attr.colorPrimary);
    }

    public static int getEmptyColor(Context c) {
        SharedPreferences p = prefs(c);
        if (p != null && p.contains("empty_color")) {
            return safeColor(p.getInt("empty_color", Color.GRAY), Color.GRAY);
        }
        return resolveAttr(c, com.google.android.material.R.attr.colorOutline);
    }

    public static int getCurrentColor(Context c) {
        SharedPreferences p = prefs(c);
        if (p != null && p.contains("current_color")) {
            return safeColor(p.getInt("current_color", Color.YELLOW), Color.YELLOW);
        }
        return resolveAttr(c, com.google.android.material.R.attr.colorTertiary);
    }

    public static int getBackgroundColor(Context c) {
        return resolveAttr(c, android.R.attr.colorBackground);
    }

    // =========================
    // FONT
    // =========================
    public static String getFont(Context c) {
        SharedPreferences p = prefs(c);
        return p != null ? p.getString("font_style", "DEFAULT") : "DEFAULT";
    }

    public static void setFont(Context c, String value) {
        SharedPreferences p = prefs(c);
        if (p != null) {
            p.edit().putString("font_style", value).apply();
        }
    }

    // =========================
    // DATE STYLE
    // =========================
    public static String getDateStyle(Context c) {
        SharedPreferences p = prefs(c);
        return p != null ? p.getString("date_style", "DAYS_PERCENT") : "DAYS_PERCENT";
    }

    public static void setDateStyle(Context c, String value) {
        SharedPreferences p = prefs(c);
        if (p != null) {
            p.edit().putString("date_style", value).apply();
        }
    }

    // =========================
    // THEME MODE
    // =========================
    public static String getThemeMode(Context c) {
        SharedPreferences p = prefs(c);
        return p != null ? p.getString("theme_mode", "SYSTEM") : "SYSTEM";
    }

    public static void setThemeMode(Context c, String mode) {
        SharedPreferences p = prefs(c);
        if (p != null) {
            p.edit().putString("theme_mode", mode).apply();
        }
    }

    // =========================
    // APPLY THEME
    // =========================
    public static void applyTheme(Activity activity) {
        if (activity == null) return;

        String mode = getThemeMode(activity);

        if ("LIGHT".equals(mode)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else if ("DARK".equals(mode)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
    }

    // =========================
    // CUSTOM COLORS
    // =========================
    public static void setFilledColor(Context c, int color) {
        SharedPreferences p = prefs(c);
        if (p != null) {
            p.edit().putInt("filled_color", color).apply();
        }
    }

    public static void setEmptyColor(Context c, int color) {
        SharedPreferences p = prefs(c);
        if (p != null) {
            p.edit().putInt("empty_color", color).apply();
        }
    }

    public static void setCurrentColor(Context c, int color) {
        SharedPreferences p = prefs(c);
        if (p != null) {
            p.edit().putInt("current_color", color).apply();
        }
    }

    public static void clearCustomColors(Context c) {
        SharedPreferences p = prefs(c);
        if (p != null) {
            p.edit()
                    .remove("filled_color")
                    .remove("empty_color")
                    .remove("current_color")
                    .apply();
        }
    }
}