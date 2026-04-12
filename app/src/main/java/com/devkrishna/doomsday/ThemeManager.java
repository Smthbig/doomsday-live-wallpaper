package com.devkrishna.doomsday;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;

public class ThemeManager {
    public static SharedPreferences prefs(Context c) {
        return c.getSharedPreferences("doom_prefs", Context.MODE_PRIVATE);
    }

    public static int getFilledColor(Context c) {
        return prefs(c).getInt("filled_color", Color.WHITE);
    }

    public static int getEmptyColor(Context c) {
        return prefs(c).getInt("empty_color", Color.parseColor("#252525"));
    }

    public static int getCurrentColor(Context c) {
        return prefs(c).getInt("current_color", Color.parseColor("#FF9800"));
    }

    public static String getFont(Context c) {
        return prefs(c).getString("font_style", "DEFAULT");
    }

    public static String getDateStyle(Context c) {
        return prefs(c).getString("date_style", "DAYS_PERCENT");
    }
}