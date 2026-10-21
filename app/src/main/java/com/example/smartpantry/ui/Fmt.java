package com.example.smartpantry.ui;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/** Small formatting helpers. */
public final class Fmt {
    public static final String[] UNITS = {"pcs", "g", "kg", "ml", "l", "tsp", "tbsp", "cup"};
    public static final String DATE_PATTERN = "yyyy-MM-dd";

    private Fmt() { }

    public static String qty(double q) {
        if (q == Math.rint(q)) return String.valueOf((long) q);
        return String.format(Locale.US, "%.2f", q).replaceAll("0+$", "").replaceAll("\\.$", "");
    }

    /** Days from today until the date (negative = already expired). Null if no/invalid date. */
    public static Integer daysUntil(String yyyyMMdd) {
        if (yyyyMMdd == null || yyyyMMdd.isEmpty()) return null;
        try {
            SimpleDateFormat f = new SimpleDateFormat(DATE_PATTERN, Locale.US);
            f.setLenient(false);
            Date d = f.parse(yyyyMMdd);
            Date today = f.parse(f.format(new Date()));
            if (d == null || today == null) return null;
            return (int) TimeUnit.MILLISECONDS.toDays(d.getTime() - today.getTime());
        } catch (java.text.ParseException e) {
            return null;
        }
    }

    public static boolean isValidDate(String s) {
        try {
            SimpleDateFormat f = new SimpleDateFormat(DATE_PATTERN, Locale.US);
            f.setLenient(false);
            f.parse(s);
            return true;
        } catch (java.text.ParseException e) {
            return false;
        }
    }
}
