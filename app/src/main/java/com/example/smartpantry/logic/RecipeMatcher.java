package com.example.smartpantry.logic;

import com.example.smartpantry.model.PantryItem;
import com.example.smartpantry.model.Recipe;
import com.example.smartpantry.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * THE core business logic: the strict-matching rule.
 *
 * A recipe is "suggested" only if EVERY ingredient it needs is in the pantry in at least
 * the required quantity. 4 out of 5 ingredients = not suggested.
 */
public final class RecipeMatcher {

    private static final double EPSILON = 1e-9;

    /** A recipe together with the ingredient names that are missing / insufficient. */
    public static final class Match {
        public final Recipe recipe;
        public final List<String> missing;
        Match(Recipe recipe, List<String> missing) { this.recipe = recipe; this.missing = missing; }
    }

    private RecipeMatcher() { }

    /** Recipes the user can cook right now (nothing missing). */
    public static List<Recipe> strictMatches(List<Recipe> recipes, List<PantryItem> pantry) {
        Map<String, double[]> stock = buildStock(pantry);
        List<Recipe> result = new ArrayList<>();
        for (Recipe r : recipes) {
            if (missingFor(r, stock).isEmpty()) result.add(r);
        }
        return result;
    }

    /** Optional stretch: recipes missing exactly ONE ingredient. Never mixed into strict list. */
    public static List<Match> almostThere(List<Recipe> recipes, List<PantryItem> pantry) {
        Map<String, double[]> stock = buildStock(pantry);
        List<Match> result = new ArrayList<>();
        for (Recipe r : recipes) {
            List<String> missing = missingFor(r, stock);
            if (missing.size() == 1) result.add(new Match(r, missing));
        }
        return result;
    }

    public static boolean canMake(Recipe recipe, List<PantryItem> pantry) {
        return missingFor(recipe, buildStock(pantry)).isEmpty();
    }

    /** normalised name -> base-unit totals indexed by dimension (mass, volume, count). */
    static Map<String, double[]> buildStock(List<PantryItem> pantry) {
        Map<String, double[]> stock = new HashMap<>();
        for (PantryItem item : pantry) {
            String key = IngredientNormalizer.normalize(item.getName());
            if (key.isEmpty()) continue;
            double[] totals = stock.get(key);
            if (totals == null) {
                totals = new double[3];
                stock.put(key, totals);
            }
            // Several pantry rows for the same ingredient are added together.
            totals[UnitConverter.dimensionOf(item.getUnit())] += UnitConverter.toBase(item.getQuantity(), item.getUnit());
        }
        return stock;
    }

    static List<String> missingFor(Recipe recipe, Map<String, double[]> stock) {
        List<String> missing = new ArrayList<>();
        for (RecipeIngredient need : recipe.getIngredients()) {
            String key = IngredientNormalizer.normalize(need.getName());
            double[] have = stock.get(key);
            int dim = UnitConverter.dimensionOf(need.getUnit());
            double required = UnitConverter.toBase(need.getQuantity(), need.getUnit());
            // A different dimension (have grams, need pieces) counts as NOT having enough.
            if (have == null || have[dim] + EPSILON < required) missing.add(need.getName());
        }
        return missing;
    }
}
