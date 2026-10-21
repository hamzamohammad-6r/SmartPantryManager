package com.example.smartpantry.ui;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantry.R;
import com.example.smartpantry.data.DatabaseHelper;
import com.example.smartpantry.model.PantryItem;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/** Add a new pantry item, or edit/delete an existing one (when EXTRA_ITEM_ID is supplied). */
public class AddEditIngredientActivity extends AppCompatActivity {

    public static final String EXTRA_ITEM_ID = "item_id";

    private DatabaseHelper db;
    private EditText editName, editQuantity, editExpiry;
    private Spinner spinnerUnit;
    private long itemId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);
        setSupportActionBar(findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        TextView toolbarTitle = findViewById(R.id.toolbarTitle);

        db = new DatabaseHelper(this);
        editName = findViewById(R.id.editName);
        editQuantity = findViewById(R.id.editQuantity);
        editExpiry = findViewById(R.id.editExpiry);
        spinnerUnit = findViewById(R.id.spinnerUnit);
        @SuppressLint("WrongViewCast") MaterialButton save = findViewById(R.id.buttonSave);
        @SuppressLint("WrongViewCast") MaterialButton delete = findViewById(R.id.buttonDeleteItem);
        @SuppressLint("WrongViewCast") MaterialButton clearDate = findViewById(R.id.buttonClearDate);

        // ---- white text everywhere on this screen ----
        UiColors.whiteInputs(editName, editQuantity, editExpiry);   // text, hint and underline
        spinnerUnit.setAdapter(UiColors.whiteSpinnerAdapter(this, Fmt.UNITS));
        clearDate.setTextColor(Color.WHITE);
        delete.setTextColor(Color.WHITE);
        delete.setStrokeColor(ColorStateList.valueOf(Color.WHITE));
        // ----------------------------------------------

        spinnerUnit.setSelection(indexOfUnit(Prefs.defaultUnit(this)));

        // Intent data: if an id was passed we are in edit mode.
        itemId = getIntent().getLongExtra(EXTRA_ITEM_ID, -1);
        if (itemId != -1) {
            PantryItem item = db.getPantryItem(itemId);
            if (item == null) { finish(); return; }
            setTitle(R.string.edit_ingredient);
            editName.setText(item.getName());
            editQuantity.setText(Fmt.qty(item.getQuantity()));
            spinnerUnit.setSelection(indexOfUnit(item.getUnit()));
            if (item.getExpiry() != null) editExpiry.setText(item.getExpiry());
            delete.setVisibility(View.VISIBLE);
        } else {
            setTitle(R.string.add_ingredient);
        }

        editExpiry.setOnClickListener(v -> pickDate());
        clearDate.setOnClickListener(v -> editExpiry.setText(""));
        save.setOnClickListener(v -> save());
        delete.setOnClickListener(v -> confirmDelete());
    }

    private int indexOfUnit(String unit) {
        for (int i = 0; i < Fmt.UNITS.length; i++) if (Fmt.UNITS[i].equalsIgnoreCase(unit)) return i;
        return 0;
    }

    private void pickDate() {
        final Calendar cal = Calendar.getInstance();
        new DatePickerDialog(this, (view, y, m, d) -> {
            cal.set(y, m, d);
            editExpiry.setText(new SimpleDateFormat(Fmt.DATE_PATTERN, Locale.US).format(cal.getTime()));
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void save() {
        // ---- input validation ----
        String name = editName.getText().toString().trim();
        String qtyText = editQuantity.getText().toString().trim();
        String expiry = editExpiry.getText().toString().trim();
        boolean ok = true;

        editName.setError(null);
        editQuantity.setError(null);
        editExpiry.setError(null);

        if (name.isEmpty()) {
            editName.setError(getString(R.string.err_name_required));
            ok = false;
        } else if (!name.matches(".*[A-Za-z].*")) {
            editName.setError(getString(R.string.err_name_letters));
            ok = false;
        }

        double qty = 0;
        if (qtyText.isEmpty()) {
            editQuantity.setError(getString(R.string.err_qty_required));
            ok = false;
        } else {
            try {
                qty = Double.parseDouble(qtyText);
                if (qty <= 0) {
                    editQuantity.setError(getString(R.string.err_qty_positive));
                    ok = false;
                }
            } catch (NumberFormatException e) {
                editQuantity.setError(getString(R.string.err_qty_number));
                ok = false;
            }
        }
        if (!expiry.isEmpty() && !Fmt.isValidDate(expiry)) {
            editExpiry.setError(getString(R.string.err_date_invalid));
            ok = false;
        }
        if (!ok) return;

        String unit = (String) spinnerUnit.getSelectedItem();
        if (itemId == -1) {
            db.addPantryItem(name, qty, unit, expiry);            // Create
            Toast.makeText(this, R.string.msg_added, Toast.LENGTH_SHORT).show();
        } else {
            db.updatePantryItem(itemId, name, qty, unit, expiry); // Update
            Toast.makeText(this, R.string.msg_updated, Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_title)
                .setMessage(getString(R.string.delete_message, editName.getText().toString()))
                .setPositiveButton(R.string.delete, (d, w) -> {
                    db.deletePantryItem(itemId);                 // Delete
                    Toast.makeText(this, R.string.msg_deleted, Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { finish(); return true; }
        return super.onOptionsItemSelected(item);
    }
}