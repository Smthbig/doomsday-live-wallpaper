package com.devkrishna.doomsday.theme;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.TypedValue;

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
    // THEME TYPE
    // =========================
    public static String getThemeType(Context c) {
        SharedPreferences p = prefs(c);
        return p != null ? p.getString("theme_type", "LIGHT") : "LIGHT";
    }

    public static void setThemeType(Context c, String type) {
        SharedPreferences p = prefs(c);
        if (p != null) {
            p.edit().putString("theme_type", type).apply();
        }
    }

    // =========================
    // APPLY THEME
    // =========================
    public static void applyTheme(Activity activity) {

        if (activity == null) return;

        String type = getThemeType(activity);

        switch (type) {
            case "DARK":
                activity.setTheme(com.devkrishna.doomsday.R.style.Theme_App_Dark);
                break;

            case "GLASSY_LIGHT":
                activity.setTheme(com.devkrishna.doomsday.R.style.Theme_App_GlassyLight);
                break;

            case "GLASSY_DARK":
                activity.setTheme(com.devkrishna.doomsday.R.style.Theme_App_GlassyDark);
                break;

            case "EXPERIMENTAL":
                activity.setTheme(com.devkrishna.doomsday.R.style.Theme_App_Experimental);
                break;

            default:
                activity.setTheme(com.devkrishna.doomsday.R.style.Theme_App_Light);
                break;
        }
    }

    // =========================
    // COLOR RESOLUTION (CORE FIX)
    // =========================
    private static int resolveAttr(Context context, int attr) {
        TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(attr, value, true);
        return value.data;
    }

    // =========================
    // COLORS (FIXED)
    // =========================

    public static int getBackgroundColor(Context c) {
        return resolveAttr(c, android.R.attr.colorBackground);
    }

    public static int getFilledColor(Context c) {
        SharedPreferences p = prefs(c);

        if (p != null && p.contains("filled_color")) {
            return p.getInt("filled_color", 0);
        }

        return resolveAttr(c, com.google.android.material.R.attr.colorOnSurface);
    }

    public static int getEmptyColor(Context c) {
        SharedPreferences p = prefs(c);

        if (p != null && p.contains("empty_color")) {
            return p.getInt("empty_color", 0);
        }

        return resolveAttr(c, com.google.android.material.R.attr.colorOutline);
    }

    public static int getCurrentColor(Context c) {
        SharedPreferences p = prefs(c);

        if (p != null && p.contains("current_color")) {
            return p.getInt("current_color", 0);
        }

        return resolveAttr(c, com.google.android.material.R.attr.colorPrimary);
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
    // WALLPAPER
    // =========================
    public static void setWallpaper(Context c, String uriString) {
        SharedPreferences p = prefs(c);
        if (p != null) {
            p.edit().putString("wallpaper_path", uriString).apply();
        }
    }

    public static String getWallpaper(Context c) {
        SharedPreferences p = prefs(c);
        if (p == null) return null;

        String path = p.getString("wallpaper_path", null);
        return (path == null || path.trim().isEmpty()) ? null : path;
    }

    public static void clearWallpaper(Context c) {
        SharedPreferences p = prefs(c);
        if (p != null) {
            p.edit().remove("wallpaper_path").apply();
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