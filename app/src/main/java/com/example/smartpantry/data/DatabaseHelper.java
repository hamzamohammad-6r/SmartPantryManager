package com.example.smartpantry.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.smartpantry.model.PantryItem;
import com.example.smartpantry.model.Recipe;
import com.example.smartpantry.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.List;

/** Local SQLite storage: pantry CRUD + pre-seeded recipes. */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "smart_pantry.db";
    private static final int DB_VERSION = 1;

    public static final String T_PANTRY = "pantry";
    public static final String T_RECIPES = "recipes";
    public static final String T_RECIPE_ING = "recipe_ingredients";

    public DatabaseHelper(Context context) {
        super(context.getApplicationContext(), DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + T_PANTRY + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT NOT NULL, quantity REAL NOT NULL, unit TEXT NOT NULL, expiry TEXT)");
        db.execSQL("CREATE TABLE " + T_RECIPES + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, steps TEXT NOT NULL)");
        db.execSQL("CREATE TABLE " + T_RECIPE_ING + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "recipe_id INTEGER NOT NULL, name TEXT NOT NULL, quantity REAL NOT NULL, unit TEXT NOT NULL, "
                + "FOREIGN KEY(recipe_id) REFERENCES " + T_RECIPES + "(id) ON DELETE CASCADE)");
        SeedData.seed(db); // runs once, on first launch only
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + T_RECIPE_ING);
        db.execSQL("DROP TABLE IF EXISTS " + T_RECIPES);
        db.execSQL("DROP TABLE IF EXISTS " + T_PANTRY);
        onCreate(db);
    }

    // ---------------- Pantry CRUD ----------------

    private ContentValues toValues(String name, double qty, String unit, String expiry) {
        ContentValues v = new ContentValues();
        v.put("name", name);
        v.put("quantity", qty);
        v.put("unit", unit);
        if (expiry == null || expiry.isEmpty()) v.putNull("expiry"); else v.put("expiry", expiry);
        return v;
    }

    /** Create */
    public long addPantryItem(String name, double qty, String unit, String expiry) {
        return getWritableDatabase().insert(T_PANTRY, null, toValues(name, qty, unit, expiry));
    }

    /** Update */
    public int updatePantryItem(long id, String name, double qty, String unit, String expiry) {
        return getWritableDatabase().update(T_PANTRY, toValues(name, qty, unit, expiry),
                "id = ?", new String[]{String.valueOf(id)});
    }

    /** Delete */
    public int deletePantryItem(long id) {
        return getWritableDatabase().delete(T_PANTRY, "id = ?", new String[]{String.valueOf(id)});
    }

    /** Read (list) */
    public List<PantryItem> getAllPantryItems() {
        List<PantryItem> list = new ArrayList<>();
        try (Cursor c = getReadableDatabase().query(T_PANTRY, null, null, null, null, null,
                "name COLLATE NOCASE ASC")) {
            while (c.moveToNext()) list.add(readPantry(c));
        }
        return list;
    }

    /** Read (single) */
    public PantryItem getPantryItem(long id) {
        try (Cursor c = getReadableDatabase().query(T_PANTRY, null, "id = ?",
                new String[]{String.valueOf(id)}, null, null, null)) {
            return c.moveToFirst() ? readPantry(c) : null;
        }
    }

    private PantryItem readPantry(Cursor c) {
        return new PantryItem(
                c.getLong(c.getColumnIndexOrThrow("id")),
                c.getString(c.getColumnIndexOrThrow("name")),
                c.getDouble(c.getColumnIndexOrThrow("quantity")),
                c.getString(c.getColumnIndexOrThrow("unit")),
                c.isNull(c.getColumnIndexOrThrow("expiry")) ? null : c.getString(c.getColumnIndexOrThrow("expiry")));
    }

    // ---------------- Recipes ----------------

    public List<Recipe> getAllRecipes() {
        List<Recipe> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor c = db.query(T_RECIPES, null, null, null, null, null, "name COLLATE NOCASE ASC")) {
            while (c.moveToNext()) {
                Recipe r = new Recipe(c.getLong(c.getColumnIndexOrThrow("id")),
                        c.getString(c.getColumnIndexOrThrow("name")),
                        c.getString(c.getColumnIndexOrThrow("steps")));
                loadIngredients(db, r);
                list.add(r);
            }
        }
        return list;
    }

    public Recipe getRecipe(long id) {
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor c = db.query(T_RECIPES, null, "id = ?", new String[]{String.valueOf(id)}, null, null, null)) {
            if (!c.moveToFirst()) return null;
            Recipe r = new Recipe(id, c.getString(c.getColumnIndexOrThrow("name")),
                    c.getString(c.getColumnIndexOrThrow("steps")));
            loadIngredients(db, r);
            return r;
        }
    }

    private void loadIngredients(SQLiteDatabase db, Recipe r) {
        try (Cursor c = db.query(T_RECIPE_ING, null, "recipe_id = ?",
                new String[]{String.valueOf(r.getId())}, null, null, "id ASC")) {
            while (c.moveToNext()) {
                r.getIngredients().add(new RecipeIngredient(
                        c.getString(c.getColumnIndexOrThrow("name")),
                        c.getDouble(c.getColumnIndexOrThrow("quantity")),
                        c.getString(c.getColumnIndexOrThrow("unit"))));
            }
        }
    }
}
