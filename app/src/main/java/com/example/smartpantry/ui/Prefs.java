package com.example.smartpantry.ui;

import android.content.Context;
import android.content.SharedPreferences;

/** Settings stored in SharedPreferences. */
public final class Prefs {
    private static final String FILE = "settings";
    private static final String KEY_EXPIRY_ALERTS = "expiry_alerts";
    private static final String KEY_DEFAULT_UNIT = "default_unit";

    private Prefs() { }

    private static SharedPreferences sp(Context c) {
        return c.getSharedPreferences(FILE, Context.MODE_PRIVATE);
    }

    public static boolean expiryAlerts(Context c) { return sp(c).getBoolean(KEY_EXPIRY_ALERTS, true); }
    public static void setExpiryAlerts(Context c, boolean v) { sp(c).edit().putBoolean(KEY_EXPIRY_ALERTS, v).apply(); }

    public static String defaultUnit(Context c) { return sp(c).getString(KEY_DEFAULT_UNIT, "pcs"); }
    public static void setDefaultUnit(Context c, String u) { sp(c).edit().putString(KEY_DEFAULT_UNIT, u).apply(); }
}
