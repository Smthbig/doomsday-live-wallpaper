package com.devkrishna.doomsday.ui.settings;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.devkrishna.doomsday.R;
import com.devkrishna.doomsday.core.manager.GoalManager;
import com.devkrishna.doomsday.core.model.Goal;
import com.devkrishna.doomsday.core.model.RenderResult;
import com.devkrishna.doomsday.core.observer.GoalObserver;
import com.devkrishna.doomsday.theme.ThemeManager;

public class SettingsActivity extends AppCompatActivity {

    private int filledColor;
    private int emptyColor;
    private int currentColor;

    private static final int REQ_WALLPAPER = 101;
    private String wallpaperPath;

    private EditText goalInput;

    private Spinner themeTypeSpinner;
    private Spinner fontSpinner;
    private Spinner dateSpinner;

    private GoalObserver observer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        bindViews();
        loadState();
        setupSpinners();
        setupGoalLive();
        setupColorPickers();
        setupWallpaperPicker();
        setupDoneButton();
    }

    // =========================
    private void bindViews() {

        themeTypeSpinner = findViewById(R.id.themeModeSpinner);
        fontSpinner = findViewById(R.id.fontSpinner);
        dateSpinner = findViewById(R.id.dateSpinner);
        goalInput = findViewById(R.id.goalInput);

        if (themeTypeSpinner == null
                || fontSpinner == null
                || dateSpinner == null
                || goalInput == null) {

            Toast.makeText(this, "Layout error", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    // =========================
    private void loadState() {

        wallpaperPath = ThemeManager.getWallpaper(this);

        filledColor = ThemeManager.getFilledColor(this);
        emptyColor = ThemeManager.getEmptyColor(this);
        currentColor = ThemeManager.getCurrentColor(this);

        GoalManager manager = GoalManager.get(this);
        manager.refresh();

        Goal goal = manager.getCurrentGoal();

        if (goal != null) {
            goalInput.setText(String.valueOf(goal.getTotalDays()));
        }
    }

    // =========================
    private void setupGoalLive() {

        goalInput.addTextChangedListener(
                new TextWatcher() {

                    @Override
                    public void afterTextChanged(Editable s) {
                        try {
                            int total = Integer.parseInt(s.toString());

                            if (total > 0) {

                                GoalManager manager = GoalManager.get(SettingsActivity.this);
                                Goal existing = manager.getCurrentGoal();

                                long start =
                                        existing != null
                                                ? existing.getStartTime()
                                                : System.currentTimeMillis();

                                manager.setGoal(new Goal(total, start));
                            }

                        } catch (Exception ignored) {
                        }
                    }

                    @Override
                    public void beforeTextChanged(
                            CharSequence s, int start, int count, int after) {}

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {}
                });
    }

    // =========================
    private boolean isUserInteraction = false;

    private void setupSpinners() {

        themeTypeSpinner.setAdapter(
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        new String[] {
                            "LIGHT", "DARK", "GLASSY_LIGHT", "GLASSY_DARK", "EXPERIMENTAL"
                        }));

        fontSpinner.setAdapter(
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        new String[] {"DEFAULT", "BOLD", "MONO"}));

        dateSpinner.setAdapter(
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        new String[] {"DAYS_PERCENT", "DATE_PERCENT", "PERCENT_ONLY"}));

        // prevent auto-trigger during setup
        isUserInteraction = false;

        setSpinnerSelection(themeTypeSpinner, ThemeManager.getThemeType(this));
        setSpinnerSelection(fontSpinner, ThemeManager.getFont(this));
        setSpinnerSelection(dateSpinner, ThemeManager.getDateStyle(this));

        // enable after setup
        themeTypeSpinner.post(() -> isUserInteraction = true);

        // =========================
        // THEME SPINNER (FIXED)
        // =========================
        themeTypeSpinner.setOnItemSelectedListener(
                new SimpleItemListener(
                        pos -> {
                            if (!isUserInteraction) return;

                            String selected = (String) themeTypeSpinner.getItemAtPosition(pos);
                            String current = ThemeManager.getThemeType(this);

                            if (!selected.equals(current)) {
                                ThemeManager.setThemeType(this, selected);
                                recreate(); // now SAFE (no loop)
                            }
                        }));

        // =========================
        // FONT
        // =========================
        fontSpinner.setOnItemSelectedListener(
                new SimpleItemListener(
                        pos -> {
                            if (!isUserInteraction) return;

                            ThemeManager.setFont(this, (String) fontSpinner.getItemAtPosition(pos));
                        }));

        // =========================
        // DATE STYLE
        // =========================
        dateSpinner.setOnItemSelectedListener(
                new SimpleItemListener(
                        pos -> {
                            if (!isUserInteraction) return;

                            ThemeManager.setDateStyle(
                                    this, (String) dateSpinner.getItemAtPosition(pos));
                        }));
    }

    // =========================
    private void setupColorPickers() {

        setupColorRow(
                findViewById(R.id.filledColorRow),
                c -> ThemeManager.setFilledColor(this, c),
                filledColor);

        setupColorRow(
                findViewById(R.id.emptyColorRow),
                c -> ThemeManager.setEmptyColor(this, c),
                emptyColor);

        setupColorRow(
                findViewById(R.id.currentColorRow),
                c -> ThemeManager.setCurrentColor(this, c),
                currentColor);
    }

    // =========================
    private void setupWallpaperPicker() {

        Button wallpaperBtn = findViewById(R.id.wallpaperBtn);

        wallpaperBtn.setOnClickListener(
                v -> {
                    Intent intent =
                            new Intent(
                                    Intent.ACTION_PICK,
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, REQ_WALLPAPER);
                });
    }

    // =========================
    private void setupDoneButton() {

        Button saveBtn = findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(v -> finish());
    }

    // =========================
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    // =========================
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_WALLPAPER && resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                ThemeManager.setWallpaper(this, uri.toString());
                Toast.makeText(this, "Wallpaper selected", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // =========================
    private void setupColorRow(LinearLayout row, ColorPick listener, int selectedColor) {

        if (row == null) return;

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
            container.addView(dot, new FrameLayout.LayoutParams(size, size));

            container.setPadding(6, 6, 6, 6);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(size + 20, size + 20);
            lp.setMargins(8, 8, 8, 8);
            container.setLayoutParams(lp);

            if (c == selectedColor) highlight(container);

            container.setOnClickListener(
                    v -> {
                        listener.pick(c);

                        for (int i = 0; i < row.getChildCount(); i++) {
                            row.getChildAt(i).setBackground(null);
                        }

                        highlight(container);
                    });

            row.addView(container);
        }
    }

    private void highlight(FrameLayout container) {
        GradientDrawable border = new GradientDrawable();
        border.setShape(GradientDrawable.OVAL);
        border.setStroke(4, Color.WHITE);
        container.setBackground(border);
    }

    private float dp(float v) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, v, getResources().getDisplayMetrics());
    }

    private void setSpinnerSelection(Spinner spinner, String value) {
        if (spinner == null || value == null) return;

        ArrayAdapter adapter = (ArrayAdapter) spinner.getAdapter();
        if (adapter == null) return;

        for (int i = 0; i < adapter.getCount(); i++) {
            if (value.equals(adapter.getItem(i))) {
                spinner.setSelection(i);
                return;
            }
        }
    }

    private static class SimpleItemListener implements AdapterView.OnItemSelectedListener {

        private final OnSelect callback;

        SimpleItemListener(OnSelect cb) {
            this.callback = cb;
        }

        @Override
        public void onItemSelected(
                AdapterView<?> parent, android.view.View view, int position, long id) {
            callback.onSelect(position);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {}

        interface OnSelect {
            void onSelect(int position);
        }
    }

    interface ColorPick {
        void pick(int color);
    }
}
