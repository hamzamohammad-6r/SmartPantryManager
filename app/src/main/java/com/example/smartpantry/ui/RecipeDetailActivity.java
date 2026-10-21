package com.example.smartpantry.ui;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantry.R;
import com.example.smartpantry.data.DatabaseHelper;
import com.example.smartpantry.model.Recipe;
import com.example.smartpantry.model.RecipeIngredient;

/** Full ingredient list and method for one recipe (id arrives via Intent extra). */
public class RecipeDetailActivity extends AppCompatActivity {

    public static final String EXTRA_RECIPE_ID = "recipe_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        setSupportActionBar(findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        long id = getIntent().getLongExtra(EXTRA_RECIPE_ID, -1);
        Recipe recipe = new DatabaseHelper(this).getRecipe(id);
        if (recipe == null) { finish(); return; }

        TextView toolbarTitle = findViewById(R.id.toolbarTitle);   // may not exist in your layout
        TextView title = findViewById(R.id.textTitle);
        TextView ingredients = findViewById(R.id.textIngredients);
        TextView steps = findViewById(R.id.textSteps);

        if (toolbarTitle != null) toolbarTitle.setText(recipe.getName());
        else setTitle(recipe.getName());

        StringBuilder sb = new StringBuilder();
        for (RecipeIngredient ing : recipe.getIngredients()) {
            sb.append("\u2022 ").append(Fmt.qty(ing.getQuantity())).append(' ')
                    .append(ing.getUnit()).append("  ").append(ing.getName()).append('\n');
        }

        if (title != null) title.setText(recipe.getName());
        if (ingredients != null) ingredients.setText(sb.toString().trim());
        if (steps != null) steps.setText(recipe.getSteps());

        UiColors.white(title, ingredients, steps);   // skips any view that is null
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { finish(); return true; }
        return super.onOptionsItemSelected(item);
    }
}