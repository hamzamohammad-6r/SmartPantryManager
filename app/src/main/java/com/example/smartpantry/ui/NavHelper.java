package com.example.smartpantry.ui;

import android.app.Activity;
import android.content.Intent;

import com.example.smartpantry.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/** Wires the bottom navigation bar to the three top-level screens using Intents. */
public final class NavHelper {
    private NavHelper() { }

    public static void setup(final Activity current, BottomNavigationView nav, final int selectedId) {
        nav.setSelectedItemId(selectedId);
        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == selectedId) return true;
            Class<?> target;
            if (id == R.id.nav_pantry) target = MainActivity.class;
            else if (id == R.id.nav_suggested) target = SuggestedRecipesActivity.class;
            else target = SettingsActivity.class;

            Intent i = new Intent(current, target);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            current.startActivity(i);
            current.overridePendingTransition(0, 0);
            if (!(current instanceof MainActivity)) current.finish();
            return true;
        });
    }
}
