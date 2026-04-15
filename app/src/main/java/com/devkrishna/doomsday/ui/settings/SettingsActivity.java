package com.devkrishna.doomsday.ui.settings;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.devkrishna.doomsday.R;
import com.devkrishna.doomsday.theme.ThemeManager;

public class SettingsActivity extends AppCompatActivity {

    private int filledColor;
    private int emptyColor;
    private int currentColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeManager.applyTheme(this);
        setContentView(R.layout.activity_settings);

        // =========================
        // VIEW BINDING + NULL SAFETY
        // =========================
        Spinner themeMode = findViewById(R.id.themeModeSpinner);
        Spinner fontSpinner = findViewById(R.id.fontSpinner);
        Spinner dateSpinner = findViewById(R.id.dateSpinner);

        LinearLayout filledRow = findViewById(R.id.filledColorRow);
        LinearLayout emptyRow = findViewById(R.id.emptyColorRow);
        LinearLayout currentRow = findViewById(R.id.currentColorRow);

        Button saveBtn = findViewById(R.id.saveBtn);

        if (themeMode == null || fontSpinner == null || dateSpinner == null ||
                filledRow == null || emptyRow == null || currentRow == null || saveBtn == null) {
            Toast.makeText(this, "Layout error", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // =========================
        // SAFE DEFAULT VALUES
        // =========================
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
        // SPINNERS SETUP
        // =========================
        ArrayAdapter<String> themeAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"SYSTEM", "LIGHT", "DARK"});

        ArrayAdapter<String> fontAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"DEFAULT", "BOLD", "MONO"});

        ArrayAdapter<String> dateAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"DAYS_PERCENT", "DATE_PERCENT", "PERCENT_ONLY"});

        themeMode.setAdapter(themeAdapter);
        fontSpinner.setAdapter(fontAdapter);
        dateSpinner.setAdapter(dateAdapter);

        // =========================
        // RESTORE VALUES SAFELY
        // =========================
        try {
            setSpinnerSelection(themeMode, ThemeManager.getThemeMode(this));
            setSpinnerSelection(fontSpinner, ThemeManager.getFont(this));
            setSpinnerSelection(dateSpinner, ThemeManager.getDateStyle(this));
        } catch (Exception ignored) {}

        // =========================
        // COLOR PICKERS
        // =========================
        setupColorRow(filledRow, c -> filledColor = c);
        setupColorRow(emptyRow, c -> emptyColor = c);
        setupColorRow(currentRow, c -> currentColor = c);

        // =========================
        // SAVE ACTION
        // =========================
        saveBtn.setOnClickListener(v -> {
            try {
                ThemeManager.setThemeMode(this, themeMode.getSelectedItem().toString());
                ThemeManager.setFont(this, fontSpinner.getSelectedItem().toString());
                ThemeManager.setDateStyle(this, dateSpinner.getSelectedItem().toString());

                ThemeManager.setFilledColor(this, filledColor);
                ThemeManager.setEmptyColor(this, emptyColor);
                ThemeManager.setCurrentColor(this, currentColor);

                Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
                finish();

            } catch (Exception e) {
                Toast.makeText(this, "Error saving settings", Toast.LENGTH_SHORT).show();
            }
        });
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

    private void setupColorRow(LinearLayout row, ColorPick listener) {

        int[] colors = {
                Color.WHITE, Color.RED, Color.GREEN,
                Color.BLUE, Color.YELLOW, Color.CYAN,
                Color.MAGENTA, Color.parseColor("#FF9800")
        };

        for (int c : colors) {
            ImageView dot = new ImageView(this);

            GradientDrawable bg = new GradientDrawable();
            bg.setShape(GradientDrawable.OVAL);
            bg.setColor(c);

            dot.setBackground(bg);

            LinearLayout.LayoutParams lp =
                    new LinearLayout.LayoutParams(80, 80);
            lp.setMargins(10, 10, 10, 10);

            dot.setLayoutParams(lp);

            dot.setOnClickListener(v -> {
                listener.pick(c);
                row.setTag(c);
            });

            row.addView(dot);
        }
    }

    interface ColorPick {
        void pick(int color);
    }
}