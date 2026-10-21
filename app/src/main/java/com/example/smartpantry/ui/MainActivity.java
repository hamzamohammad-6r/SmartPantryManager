package com.example.smartpantry.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantry.R;
import com.example.smartpantry.data.DatabaseHelper;
import com.example.smartpantry.model.PantryItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

/** Pantry list screen (launcher). Reloads from SQLite every time it comes back into view. */
public class MainActivity extends AppCompatActivity implements PantryAdapter.Listener {

    private DatabaseHelper db;
    private PantryAdapter adapter;
    private TextView emptyView;

    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        db = new DatabaseHelper(this);
        emptyView = findViewById(R.id.textEmpty);
        UiColors.white(emptyView);

        RecyclerView list = findViewById(R.id.recyclerPantry);
        list.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PantryAdapter(this);
        list.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fabAdd);
        fab.setOnClickListener(v -> startActivity(new Intent(this, AddEditIngredientActivity.class)));

        bottomNav = findViewById(R.id.bottomNav);
        NavHelper.setup(this, bottomNav, R.id.nav_pantry);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bottomNav.setSelectedItemId(R.id.nav_pantry);
        refresh();
    }

    private void refresh() {
        List<PantryItem> items = db.getAllPantryItems();
        adapter.submit(items, Prefs.expiryAlerts(this));
        emptyView.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onEdit(PantryItem item) {
        Intent i = new Intent(this, AddEditIngredientActivity.class);
        i.putExtra(AddEditIngredientActivity.EXTRA_ITEM_ID, item.getId());
        startActivity(i);
    }

    @Override
    public void onDelete(final PantryItem item) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_title)
                .setMessage(getString(R.string.delete_message, item.getName()))
                .setPositiveButton(R.string.delete, (d, w) -> {
                    db.deletePantryItem(item.getId());
                    refresh();
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }
}