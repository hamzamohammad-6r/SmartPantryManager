package com.example.smartpantry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.smartpantry.logic.IngredientNormalizer;
import com.example.smartpantry.logic.RecipeMatcher;
import com.example.smartpantry.model.PantryItem;
import com.example.smartpantry.model.Recipe;
import com.example.smartpantry.model.RecipeIngredient;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecipeMatcherTest {

    private Recipe tomatoPasta() {
        Recipe r = new Recipe(1, "Tomato Pasta", "steps");
        r.getIngredients().add(new RecipeIngredient("pasta", 200, "g"));
        r.getIngredients().add(new RecipeIngredient("tomato", 4, "pcs"));
        r.getIngredients().add(new RecipeIngredient("garlic", 2, "pcs"));
        r.getIngredients().add(new RecipeIngredient("olive oil", 2, "tbsp"));
        r.getIngredients().add(new RecipeIngredient("salt", 1, "tsp"));
        return r;
    }

    private List<PantryItem> fullPantry() {
        return new ArrayList<>(Arrays.asList(
                new PantryItem(1, "Pasta", 0.5, "kg", null),
                new PantryItem(2, "Tomatoes", 4, "pcs", null),
                new PantryItem(3, "Garlic", 2, "pcs", null),
                new PantryItem(4, "Olive Oil", 30, "ml", null),
                new PantryItem(5, "salt", 1, "tsp", null)));
    }

    @Test
    public void allIngredientsPresent_isSuggested_despiteUnitsAndPlurals() {
        assertTrue(RecipeMatcher.canMake(tomatoPasta(), fullPantry()));
    }

    @Test
    public void fourOfFiveIngredients_isNotSuggested() {
        List<PantryItem> pantry = fullPantry();
        pantry.remove(4); // no salt
        assertFalse(RecipeMatcher.canMake(tomatoPasta(), pantry));
        assertTrue(RecipeMatcher.strictMatches(Arrays.asList(tomatoPasta()), pantry).isEmpty());
        assertEquals(1, RecipeMatcher.almostThere(Arrays.asList(tomatoPasta()), pantry).size());
    }

    @Test
    public void insufficientQuantity_isNotSuggested() {
        List<PantryItem> pantry = fullPantry();
        pantry.set(1, new PantryItem(2, "Tomatoes", 3, "pcs", null));
        assertFalse(RecipeMatcher.canMake(tomatoPasta(), pantry));
    }

    @Test
    public void duplicatePantryRowsAreSummed() {
        List<PantryItem> pantry = fullPantry();
        pantry.set(1, new PantryItem(2, "Tomatoes", 2, "pcs", null));
        pantry.add(new PantryItem(9, "tomato", 2, "pcs", null));
        assertTrue(RecipeMatcher.canMake(tomatoPasta(), pantry));
    }

    @Test
    public void normalizerHandlesPlurals() {
        assertEquals("tomato", IngredientNormalizer.normalize("Tomatoes "));
        assertEquals("berry", IngredientNormalizer.normalize("berries"));
        assertEquals("hummus", IngredientNormalizer.normalize("hummus"));
    }
}
