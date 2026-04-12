package com.devkrishna.doomsday;

import android.graphics.Color;
import android.widget.Toast;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private int filledColor = Color.WHITE;
    private int emptyColor = Color.parseColor("#252525");
    private int currentColor = Color.parseColor("#FF9800");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(Color.BLACK);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(40, 60, 40, 60);
        scroll.addView(root);

        // ===== TITLE =====
        TextView title = new TextView(this);
        title.setText("STUDIO");
        title.setTextSize(28);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setTextColor(Color.WHITE);
        title.setGravity(Gravity.CENTER);
        root.addView(title);

        // ===== MODE =====
        root.addView(makeSection("MODE"));
        Spinner modeSpinner = new Spinner(this);
        modeSpinner.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"AUTO", "YEAR", "GOAL"}));
        root.addView(modeSpinner);

        // ===== LAYOUT TYPE =====
        root.addView(makeSection("LAYOUT TYPE"));
        Spinner typeSpinner = new Spinner(this);
        typeSpinner.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"GRID", "CIRCLE", "LINEAR"}));
        root.addView(typeSpinner);

        // ===== DOT SIZE =====
        root.addView(makeSection("DOT SIZE"));
        Spinner sizeSpinner = new Spinner(this);
        sizeSpinner.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"AUTO FIT", "SMALL", "MEDIUM", "LARGE"}));
        root.addView(sizeSpinner);

        // ===== GOAL NAME =====
        root.addView(makeSection("GOAL NAME"));
        EditText goalName = new EditText(this);
        goalName.setHint("Weight loss / Coding / Gym");
        goalName.setTextColor(Color.WHITE);
        goalName.setHintTextColor(Color.GRAY);
        root.addView(goalName);

        // ===== GOAL DAYS =====
        root.addView(makeSection("GOAL DAYS"));
        EditText goalDays = new EditText(this);
        goalDays.setHint("365");
        goalDays.setInputType(InputType.TYPE_CLASS_NUMBER);
        goalDays.setTextColor(Color.WHITE);
        goalDays.setHintTextColor(Color.GRAY);
        root.addView(goalDays);

        // ===== THEME =====
        root.addView(makeSection("THEME PRESETS"));
        root.addView(makePresetButtons());

        // ===== COLORS =====
        root.addView(makeSection("FILLED DOTS"));
        root.addView(makeColorPicker(color -> filledColor = color));

        root.addView(makeSection("EMPTY DOTS"));
        root.addView(makeColorPicker(color -> emptyColor = color));

        root.addView(makeSection("CURRENT DAY RING"));
        root.addView(makeColorPicker(color -> currentColor = color));

        // ===== FONT =====
        root.addView(makeSection("FONT"));
        Spinner fontSpinner = new Spinner(this);
        fontSpinner.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"DEFAULT", "BOLD", "MONO"}));
        root.addView(fontSpinner);

        // ===== DATE STYLE =====
        root.addView(makeSection("DATE STYLE"));
        Spinner dateSpinner = new Spinner(this);
        dateSpinner.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{
                        "Days Left + %",
                        "Current Date + %",
                        "% Only"
                }));
        root.addView(dateSpinner);

        // ===== SAVE =====
        Button save = new Button(this);
        save.setText("SAVE");
        save.setOnClickListener(v -> {
            ThemeManager.prefs(this).edit()
                    .putString("mode", modeSpinner.getSelectedItem().toString())
                    .putString("layout_type", typeSpinner.getSelectedItem().toString())
                    .putString("dot_size", sizeSpinner.getSelectedItem().toString())
                    .putString("goal_name", goalName.getText().toString())
                    .putInt("goal_days",
                            Integer.parseInt(goalDays.getText().toString().isEmpty()
                                    ? "365"
                                    : goalDays.getText().toString()))
                    .putInt("filled_color", filledColor)
                    .putInt("empty_color", emptyColor)
                    .putInt("current_color", currentColor)
                    .putString("font_style", fontSpinner.getSelectedItem().toString())
                    .putString("date_style", dateSpinner.getSelectedItem().toString())
                    .apply();

            Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show();
            finish();
        });
        root.addView(save);

        setContentView(scroll);
    }

    private TextView makeSection(String text) {
        TextView tv = new TextView(this);
        tv.setText("\n" + text);
        tv.setTextColor(Color.GRAY);
        tv.setTextSize(14);
        return tv;
    }

    private LinearLayout makePresetButtons() {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);

        row.addView(makePreset("Dark",
                Color.WHITE,
                Color.parseColor("#252525"),
                Color.parseColor("#FF9800")));

        row.addView(makePreset("Ocean",
                Color.CYAN,
                Color.parseColor("#102030"),
                Color.BLUE));

        row.addView(makePreset("Neon",
                Color.GREEN,
                Color.parseColor("#101010"),
                Color.MAGENTA));

        return row;
    }

    private Button makePreset(String name, int fill, int empty, int current) {
        Button b = new Button(this);
        b.setText(name);
        b.setOnClickListener(v -> {
            filledColor = fill;
            emptyColor = empty;
            currentColor = current;
            Toast.makeText(this, name + " preset selected", Toast.LENGTH_SHORT).show();
        });
        return b;
    }

    interface ColorSelect {
        void pick(int color);
    }

    private LinearLayout makeColorPicker(ColorSelect listener) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);

        int[] colors = {
                Color.WHITE,
                Color.RED,
                Color.GREEN,
                Color.BLUE,
                Color.YELLOW,
                Color.CYAN,
                Color.MAGENTA,
                Color.parseColor("#FF9800")
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
            dot.setOnClickListener(v -> listener.pick(c));

            row.addView(dot);
        }

        return row;
    }
}