# Smart Pantry Manager

A Java Android app that helps reduce food waste. The user records the ingredients they
actually have at home, and the app suggests **only** recipes they can cook right now with
those ingredients - no shopping trip required.

## Features
- Pantry CRUD: add, edit, delete ingredients (name, quantity, unit, optional expiry date)
- Pantry list (RecyclerView + custom adapter) bound to SQLite
- 20 pre-seeded recipes (loaded on first run)
- **Suggested Recipes** screen using the strict-matching rule
- Optional "Almost There" list (recipes missing exactly one ingredient), clearly separated
- Recipe detail screen (ingredients + method)
- Settings: expiring-soon alerts toggle, default unit
- Friendly empty state when no recipe matches
- Input validation on the add/edit form
- No maps, GPS or location services

## Strict-matching rule
A recipe is suggested only if **every** ingredient is in the pantry in at least the required
quantity (see `logic/RecipeMatcher.java`). Matching is tolerant of simple real-world messiness:
- singular/plural and case ("Tomatoes" = "tomato") - `IngredientNormalizer`
- unit differences within the same dimension (1 kg satisfies 500 g; 30 ml satisfies 2 tbsp) - `UnitConverter`
- several pantry rows for the same ingredient are added together

Unit tests: `app/src/test/.../RecipeMatcherTest.java`

## Database: SQLite (SQLiteOpenHelper)
Chosen because the app is single-user and must work offline in the kitchen; SQLite is built
into Android, needs no server or account, persists across restarts, and is covered by the module.
Tables: `pantry`, `recipes`, `recipe_ingredients` (FK to recipes, ON DELETE CASCADE).

## Setup / run
1. Install Android Studio (Hedgehog or newer) with an Android SDK (API 34).
2. `git clone <your-repo-url>` and open the folder in Android Studio (File > Open).
3. Let Gradle sync finish (needs internet the first time).
4. Create an emulator (e.g. Pixel 6, API 34) or plug in a device with USB debugging.
5. Press **Run**. To run the unit tests: right-click `RecipeMatcherTest` > Run.

Min SDK 24, target SDK 34, Java 8, Material Components, RecyclerView, ConstraintLayout.
