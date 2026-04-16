package com.devkrishna.doomsday.ui.settings;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.TypedValue;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.devkrishna.doomsday.R;
import com.devkrishna.doomsday.theme.ThemeManager;

public class SettingsActivity extends AppCompatActivity {

    private int filledColor;
    private int emptyColor;
    private int currentColor;

    private static final int REQ_WALLPAPER = 101;
    private String wallpaperPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeManager.applyTheme(this);
        setContentView(R.layout.activity_settings);

        Spinner themeMode = findViewById(R.id.themeModeSpinner);
        Spinner fontSpinner = findViewById(R.id.fontSpinner);
        Spinner dateSpinner = findViewById(R.id.dateSpinner);

        LinearLayout filledRow = findViewById(R.id.filledColorRow);
        LinearLayout emptyRow = findViewById(R.id.emptyColorRow);
        LinearLayout currentRow = findViewById(R.id.currentColorRow);

        Button saveBtn = findViewById(R.id.saveBtn);
        Button wallpaperBtn = findViewById(R.id.wallpaperBtn);

        if (themeMode == null || fontSpinner == null || dateSpinner == null ||
                filledRow == null || emptyRow == null || currentRow == null ||
                saveBtn == null || wallpaperBtn == null) {
            Toast.makeText(this, "Layout error", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // =========================
        // LOAD EXISTING
        // =========================
        wallpaperPath = ThemeManager.prefs(this).getString("wallpaper_path", null);

        try {
            filledColor = ThemeManager.getFilledColor(this);
            emptyColor = ThemeManager.getEmptyColor(this);
            currentColor = ThemeManager.getCurrentColor(this);
        } catch (Exception e) {
            filledColor = Color.WHITE;
            emptyColor = Color.GRAY;
            currentColor = Color.parseColor("#FF9800");
        }

        // =========================
        // SPINNERS
        // =========================
        themeMode.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"SYSTEM", "LIGHT", "DARK"}));

        fontSpinner.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"DEFAULT", "BOLD", "MONO"}));

        dateSpinner.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"DAYS_PERCENT", "DATE_PERCENT", "PERCENT_ONLY"}));

        setSpinnerSelection(themeMode, ThemeManager.getThemeMode(this));
        setSpinnerSelection(fontSpinner, ThemeManager.getFont(this));
        setSpinnerSelection(dateSpinner, ThemeManager.getDateStyle(this));

        // =========================
        // COLOR PICKERS (UPGRADED)
        // =========================
        setupColorRow(filledRow, c -> filledColor = c);
        setupColorRow(emptyRow, c -> emptyColor = c);
        setupColorRow(currentRow, c -> currentColor = c);

        // =========================
        // WALLPAPER PICKER
        // =========================
        wallpaperBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, REQ_WALLPAPER);
        });

        // =========================
        // SAVE
        // =========================
        saveBtn.setOnClickListener(v -> {
            try {
                ThemeManager.setThemeMode(this, themeMode.getSelectedItem().toString());
                ThemeManager.setFont(this, fontSpinner.getSelectedItem().toString());
                ThemeManager.setDateStyle(this, dateSpinner.getSelectedItem().toString());

                ThemeManager.setFilledColor(this, filledColor);
                ThemeManager.setEmptyColor(this, emptyColor);
                ThemeManager.setCurrentColor(this, currentColor);

                if (wallpaperPath != null) {
                    ThemeManager.setWallpaper(this, wallpaperPath);
                }

                recreate(); // instant apply
                Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                Toast.makeText(this, "Error saving", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // =========================
    // WALLPAPER RESULT
    // =========================
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_WALLPAPER && resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                wallpaperPath = uri.toString();
                Toast.makeText(this, "Wallpaper selected", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // =========================
    // COLOR ROW (FIXED)
    // =========================
    private void setupColorRow(LinearLayout row, ColorPick listener) {

        int size = (int) dp(40);

        int[] colors = {
                Color.WHITE, Color.RED, Color.GREEN,
                Color.BLUE, Color.YELLOW, Color.CYAN,
                Color.MAGENTA, Color.parseColor("#FF9800")
        };

        for (int c : colors) {

            FrameLayout container = new FrameLayout(this);

            ImageView dot = new ImageView(this);

            GradientDrawable bg = new GradientDrawable();
            bg.setShape(GradientDrawable.OVAL);
            bg.setColor(c);

            dot.setBackground(bg);

            FrameLayout.LayoutParams dotLp =
                    new FrameLayout.LayoutParams(size, size);

            dot.setLayoutParams(dotLp);
            container.setPadding(6,6,6,6);
            container.addView(dot);

            LinearLayout.LayoutParams lp =
                    new LinearLayout.LayoutParams(size + 20, size + 20);
            lp.setMargins(8, 8, 8, 8);

            container.setLayoutParams(lp);

            container.setOnClickListener(v -> {

                listener.pick(c);

                // clear previous
                for (int i = 0; i < row.getChildCount(); i++) {
                    row.getChildAt(i).setBackground(null);
                }

                GradientDrawable border = new GradientDrawable();
                border.setShape(GradientDrawable.OVAL);
                border.setStroke(4, Color.WHITE);

                container.setBackground(border);
            });

            row.addView(container);
        }
    }

    private float dp(float v) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                v,
                getResources().getDisplayMetrics()
        );
    }

    private void setSpinnerSelection(Spinner spinner, String value) {
        if (value == null) return;

        ArrayAdapter adapter = (ArrayAdapter) spinner.getAdapter();
        if (adapter == null) return;

        for (int i = 0; i < adapter.getCount(); i++) {
            if (value.equals(adapter.getItem(i))) {
                spinner.setSelection(i);
                return;
            }
        }
    }

    interface ColorPick {
        void pick(int color);
    }
}