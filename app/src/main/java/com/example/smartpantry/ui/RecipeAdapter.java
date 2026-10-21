package com.example.smartpantry.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantry.R;
import com.example.smartpantry.model.Recipe;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

/** Shows recipes; each row can carry an optional subtitle (used by the "Almost There" list). */
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.VH> {

    public interface Listener { void onRecipeClick(Recipe recipe); }

    private final List<Recipe> recipes = new ArrayList<>();
    private final List<String> subtitles = new ArrayList<>();
    private final Listener listener;

    public RecipeAdapter(Listener listener) { this.listener = listener; }

    /** subtitles may be null; otherwise it must be the same length as recipes. */
    public void submit(List<Recipe> newRecipes, List<String> newSubtitles) {
        recipes.clear();
        subtitles.clear();
        recipes.addAll(newRecipes);
        for (int i = 0; i < newRecipes.size(); i++) {
            subtitles.add(newSubtitles != null ? newSubtitles.get(i) : null);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        final Recipe r = recipes.get(position);

        UiColors.styleCard((MaterialCardView) h.itemView);

        h.name.setText(r.getName());
        String sub = subtitles.get(position);
        if (sub == null) {
            sub = h.itemView.getContext().getResources()
                    .getQuantityString(R.plurals.ingredient_count, r.getIngredients().size(), r.getIngredients().size());
        }
        h.subtitle.setText(sub);
        h.name.setTextColor(UiColors.CARD_TEXT);
        h.subtitle.setTextColor(UiColors.CARD_TEXT_SOFT);

        h.itemView.setOnClickListener(v -> listener.onRecipeClick(r));
    }

    @Override
    public int getItemCount() { return recipes.size(); }

    static class VH extends RecyclerView.ViewHolder {
        final TextView name, subtitle;
        VH(View v) {
            super(v);
            name = v.findViewById(R.id.textRecipeName);
            subtitle = v.findViewById(R.id.textRecipeSubtitle);
        }
    }
}
