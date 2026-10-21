package com.example.smartpantry.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantry.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.materialswitch.MaterialSwitch;

import android.widget.TextView;

/** Expiring-soon alerts toggle + default unit preference (SharedPreferences). */
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        MaterialSwitch alerts = findViewById(R.id.switchExpiry);
        alerts.setChecked(Prefs.expiryAlerts(this));
        alerts.setOnCheckedChangeListener((b, checked) -> Prefs.setExpiryAlerts(this, checked));
        UiColors.white(alerts);
        UiColors.white((TextView) findViewById(R.id.textExpiryDesc), (TextView) findViewById(R.id.textUnitLabel));

        final Spinner unit = findViewById(R.id.spinnerDefaultUnit);
        unit.setAdapter(UiColors.whiteSpinnerAdapter(this, Fmt.UNITS));
        String current = Prefs.defaultUnit(this);
        for (int i = 0; i < Fmt.UNITS.length; i++) if (Fmt.UNITS[i].equals(current)) unit.setSelection(i);
        unit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                Prefs.setDefaultUnit(SettingsActivity.this, Fmt.UNITS[pos]);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        NavHelper.setup(this, (BottomNavigationView) findViewById(R.id.bottomNav), R.id.nav_settings);
    }
}
