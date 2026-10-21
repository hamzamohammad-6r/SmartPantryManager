package com.example.smartpantry.logic;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Converts quantities to a base unit per dimension (grams, millilitres, count) so that
 * "1 kg" in the pantry satisfies "500 g" in a recipe.
 */
public final class UnitConverter {

    public static final int MASS = 0;
    public static final int VOLUME = 1;
    public static final int COUNT = 2;

    private static final class Info {
        final int dimension;
        final double factor;
        Info(int dimension, double factor) { this.dimension = dimension; this.factor = factor; }
    }

    private static final Map<String, Info> UNITS = new HashMap<>();
    private static void put(int dim, double factor, String... names) {
        for (String n : names) UNITS.put(n, new Info(dim, factor));
    }
    static {
        put(MASS, 1, "g", "gram", "grams", "gr");
        put(MASS, 1000, "kg", "kilogram", "kilograms", "kgs");
        put(MASS, 0.001, "mg", "milligram", "milligrams");
        put(MASS, 28.3495, "oz", "ounce", "ounces");
        put(MASS, 453.592, "lb", "lbs", "pound", "pounds");
        put(VOLUME, 1, "ml", "millilitre", "milliliter", "millilitres", "milliliters");
        put(VOLUME, 1000, "l", "litre", "liter", "litres", "liters");
        put(VOLUME, 5, "tsp", "teaspoon", "teaspoons");
        put(VOLUME, 15, "tbsp", "tablespoon", "tablespoons");
        put(VOLUME, 240, "cup", "cups");
        put(VOLUME, 29.5735, "fl oz", "floz");
    }

    private UnitConverter() { }

    private static Info lookup(String unit) {
        if (unit == null) return new Info(COUNT, 1);
        String u = unit.toLowerCase(Locale.ROOT).trim().replace(".", "");
        Info info = UNITS.get(u);
        // Anything unknown (pcs, piece, whole, clove, can, slice, "") is treated as a count.
        return info != null ? info : new Info(COUNT, 1);
    }

    public static int dimensionOf(String unit) { return lookup(unit).dimension; }

    public static double toBase(double quantity, String unit) { return quantity * lookup(unit).factor; }
}
