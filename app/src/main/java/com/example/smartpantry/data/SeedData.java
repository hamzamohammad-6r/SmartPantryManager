package com.example.smartpantry.data;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

/** Pre-loads 20 recipes the first time the database is created. */
final class SeedData {

    private SeedData() { }

    /** Ingredient format: "name|quantity|unit". */
    private static void recipe(SQLiteDatabase db, String name, String steps, String... ingredients) {
        ContentValues rv = new ContentValues();
        rv.put("name", name);
        rv.put("steps", steps);
        long recipeId = db.insert(DatabaseHelper.T_RECIPES, null, rv);
        for (String line : ingredients) {
            String[] p = line.split("\\|");
            ContentValues iv = new ContentValues();
            iv.put("recipe_id", recipeId);
            iv.put("name", p[0]);
            iv.put("quantity", Double.parseDouble(p[1]));
            iv.put("unit", p[2]);
            db.insert(DatabaseHelper.T_RECIPE_ING, null, iv);
        }
    }

    static void seed(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            recipe(db, "Scrambled Eggs on Toast",
                    "1. Toast the bread.\n2. Whisk the eggs with the salt.\n3. Melt the butter in a pan over low heat.\n4. Stir the eggs gently until just set.\n5. Pile onto the toast and serve.",
                    "egg|3|pcs", "bread|2|pcs", "butter|10|g", "salt|0.5|tsp");
            recipe(db, "Tomato Pasta",
                    "1. Boil the pasta in salted water until al dente.\n2. Fry the chopped garlic in olive oil for 1 minute.\n3. Add the chopped tomatoes and simmer 10 minutes.\n4. Toss the drained pasta through the sauce.",
                    "pasta|200|g", "tomato|4|pcs", "garlic|2|pcs", "olive oil|2|tbsp", "salt|1|tsp");
            recipe(db, "Cheese Omelette",
                    "1. Beat the eggs.\n2. Melt butter in a non-stick pan.\n3. Pour in eggs and cook until almost set.\n4. Add grated cheese, fold and serve.",
                    "egg|3|pcs", "cheese|50|g", "butter|10|g");
            recipe(db, "Egg Fried Rice",
                    "1. Fry the diced onion in oil until soft.\n2. Push aside, scramble the eggs in the pan.\n3. Add cooked rice and soy sauce.\n4. Stir-fry for 3 minutes and serve hot.",
                    "rice|200|g", "egg|2|pcs", "onion|1|pcs", "soy sauce|2|tbsp", "oil|1|tbsp");
            recipe(db, "Simple Pancakes",
                    "1. Whisk flour, sugar, milk and egg into a smooth batter.\n2. Heat a lightly oiled pan.\n3. Pour in a ladle of batter and cook 2 minutes per side.\n4. Serve warm.",
                    "flour|200|g", "milk|300|ml", "egg|1|pcs", "sugar|1|tbsp");
            recipe(db, "Garlic Butter Pasta",
                    "1. Boil the pasta until al dente.\n2. Melt butter and gently cook sliced garlic.\n3. Toss the pasta in the garlic butter.\n4. Season with salt and serve.",
                    "pasta|200|g", "butter|30|g", "garlic|3|pcs", "salt|1|tsp");
            recipe(db, "Creamy Potato Soup",
                    "1. Saute the chopped onion in butter.\n2. Add diced potatoes and cover with water.\n3. Simmer 20 minutes until soft.\n4. Blend, stir in milk and season.",
                    "potato|4|pcs", "onion|1|pcs", "milk|250|ml", "butter|20|g", "salt|1|tsp");
            recipe(db, "Cheese Toastie",
                    "1. Butter the outside of the bread slices.\n2. Place cheese between them.\n3. Fry in a pan 2-3 minutes per side until golden.",
                    "bread|2|pcs", "cheese|40|g", "butter|10|g");
            recipe(db, "Banana Oat Porridge",
                    "1. Heat oats and milk in a pot, stirring.\n2. Cook 5 minutes until thick.\n3. Top with sliced banana.",
                    "oats|80|g", "milk|250|ml", "banana|1|pcs");
            recipe(db, "Chicken Rice Bowl",
                    "1. Season the chicken with salt and slice it.\n2. Fry in oil with chopped onion until cooked through.\n3. Serve over cooked rice.",
                    "chicken breast|300|g", "rice|200|g", "onion|1|pcs", "salt|1|tsp", "oil|1|tbsp");
            recipe(db, "Lentil Curry",
                    "1. Fry onion and garlic until soft.\n2. Add curry powder and tomatoes.\n3. Add lentils and 500 ml water.\n4. Simmer 25 minutes until tender.",
                    "lentil|200|g", "onion|1|pcs", "tomato|2|pcs", "garlic|2|pcs", "curry powder|2|tsp");
            recipe(db, "Veggie Stir-Fry",
                    "1. Slice carrots, pepper and onion.\n2. Stir-fry in hot oil for 5 minutes.\n3. Add soy sauce and toss to coat.",
                    "carrot|2|pcs", "bell pepper|1|pcs", "onion|1|pcs", "soy sauce|2|tbsp", "oil|1|tbsp");
            recipe(db, "Tuna Pasta",
                    "1. Boil the pasta.\n2. Warm olive oil with crushed garlic.\n3. Stir in the drained tuna.\n4. Toss with the pasta and serve.",
                    "pasta|200|g", "tuna|1|can", "olive oil|1|tbsp", "garlic|1|pcs");
            recipe(db, "French Toast",
                    "1. Whisk eggs, milk and sugar.\n2. Dip bread slices in the mixture.\n3. Fry in butter until golden on both sides.",
                    "bread|4|pcs", "egg|2|pcs", "milk|100|ml", "butter|10|g", "sugar|1|tbsp");
            recipe(db, "Mashed Potatoes",
                    "1. Peel, chop and boil the potatoes for 15 minutes.\n2. Drain and mash with butter and warm milk.\n3. Season with salt.",
                    "potato|500|g", "butter|30|g", "milk|100|ml", "salt|1|tsp");
            recipe(db, "Egg Noodle Stir-Fry",
                    "1. Cook the noodles and drain.\n2. Scramble the eggs in a hot pan.\n3. Add noodles, soy sauce and sliced spring onion.\n4. Toss for 2 minutes.",
                    "noodle|200|g", "egg|2|pcs", "soy sauce|2|tbsp", "spring onion|2|pcs");
            recipe(db, "Tomato and Egg Scramble",
                    "1. Chop the tomatoes and fry in oil until soft.\n2. Pour in beaten eggs and salt.\n3. Stir until the eggs are set.",
                    "egg|3|pcs", "tomato|2|pcs", "oil|1|tbsp", "salt|0.5|tsp");
            recipe(db, "Chickpea Salad",
                    "1. Drain and rinse the chickpeas.\n2. Dice cucumber and tomatoes.\n3. Mix everything with olive oil and lemon juice.",
                    "chickpea|240|g", "cucumber|1|pcs", "tomato|2|pcs", "olive oil|1|tbsp", "lemon|1|pcs");
            recipe(db, "Garlic Rice",
                    "1. Melt butter and fry minced garlic until fragrant.\n2. Add cooked rice and salt.\n3. Stir-fry 3 minutes.",
                    "rice|200|g", "garlic|4|pcs", "butter|20|g", "salt|1|tsp");
            recipe(db, "Peanut Butter Banana Toast",
                    "1. Toast the bread.\n2. Spread with peanut butter.\n3. Top with sliced banana.",
                    "bread|2|pcs", "peanut butter|2|tbsp", "banana|1|pcs");
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
}
