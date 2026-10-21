package com.example.smartpantry.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantry.R;
import com.example.smartpantry.data.DatabaseHelper;
import com.example.smartpantry.logic.RecipeMatcher;
import com.example.smartpantry.model.PantryItem;
import com.example.smartpantry.model.Recipe;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

/** Runs the strict matcher against the current pantry every time the screen is shown. */
public class SuggestedRecipesActivity extends AppCompatActivity implements RecipeAdapter.Listener {

    private DatabaseHelper db;
    private RecipeAdapter strictAdapter, almostAdapter;
    private TextView emptyView, almostHeader;
    private RecyclerView almostList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested);
        UiColors.white(emptyView, almostHeader);
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        db = new DatabaseHelper(this);
        emptyView = findViewById(R.id.textEmpty);
        almostHeader = findViewById(R.id.textAlmostHeader);
        UiColors.white(emptyView, almostHeader);

        RecyclerView strictList = findViewById(R.id.recyclerSuggested);
        strictList.setLayoutManager(new LinearLayoutManager(this));
        strictList.setNestedScrollingEnabled(false);
        strictAdapter = new RecipeAdapter(this);
        strictList.setAdapter(strictAdapter);

        almostList = findViewById(R.id.recyclerAlmost);
        almostList.setLayoutManager(new LinearLayoutManager(this));
        almostList.setNestedScrollingEnabled(false);
        almostAdapter = new RecipeAdapter(this);
        almostList.setAdapter(almostAdapter);

        NavHelper.setup(this, (BottomNavigationView) findViewById(R.id.bottomNav), R.id.nav_suggested);
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<PantryItem> pantry = db.getAllPantryItems();
        List<Recipe> recipes = db.getAllRecipes();

        // STRICT list - every ingredient present in sufficient quantity.
        List<Recipe> strict = RecipeMatcher.strictMatches(recipes, pantry);
        strictAdapter.submit(strict, null);
        emptyView.setVisibility(strict.isEmpty() ? View.VISIBLE : View.GONE);

        // Optional bonus: separate "Almost There" list (exactly one ingredient missing).
        List<RecipeMatcher.Match> almost = RecipeMatcher.almostThere(recipes, pantry);
        List<Recipe> almostRecipes = new ArrayList<>();
        List<String> subtitles = new ArrayList<>();
        for (RecipeMatcher.Match m : almost) {
            almostRecipes.add(m.recipe);
            subtitles.add(getString(R.string.missing_one, m.missing.get(0)));
        }
        almostAdapter.submit(almostRecipes, subtitles);
        int vis = almost.isEmpty() ? View.GONE : View.VISIBLE;
        almostHeader.setVisibility(vis);
        almostList.setVisibility(vis);
    }

    @Override
    public void onRecipeClick(Recipe recipe) {
        Intent i = new Intent(this, RecipeDetailActivity.class);
        i.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, recipe.getId());
        startActivity(i);
    }
}
