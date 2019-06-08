package com.acr.landmarks.ui;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;

import com.acr.landmarks.R;

public class SettingsActivity extends AppCompatActivity {

    Switch darkTheme;
    boolean darkThemeActivated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        SharedPreferences preferences = getSharedPreferences("PREFS",0);
        darkThemeActivated = preferences.getBoolean("darkTheme", false);

        darkTheme = findViewById(R.id.theme_switch);
        darkTheme.setChecked(darkThemeActivated);
        darkTheme.setOnClickListener(v -> {
            darkThemeActivated = !darkThemeActivated;
            SharedPreferences.Editor preferencesEditor = preferences.edit();
            preferencesEditor.putBoolean("darkTheme",darkThemeActivated);
            preferencesEditor.apply();
        });
    }
}
