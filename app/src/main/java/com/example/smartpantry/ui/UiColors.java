package com.example.smartpantry.ui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import com.google.android.material.card.MaterialCardView;

import androidx.annotation.NonNull;

public final class UiColors {
    public static final int HINT = 0xB3FFFFFF;      // white at 70%
    public static final int SOFT_WHITE = 0xFFDDDDDD;

    // ---- shared card style (pantry + recipes) ----
    public static final int CARD_BG = 0xFF5C3A2E;        // cocoa cupcake brown
    public static final int CARD_STROKE = 0xFFFFFFFF;    // white outline
    public static final int CARD_TEXT = 0xFFFFFFFF;      // titles
    public static final int CARD_TEXT_SOFT = 0xFFD8F3DC; // second lines (soft mint)

    public static void styleCard(MaterialCardView card) {
        card.setCardBackgroundColor(CARD_BG);
        card.setStrokeColor(CARD_STROKE);
        card.setStrokeWidth((int) (2 * card.getResources().getDisplayMetrics().density)); // 2dp
    }

    private UiColors() { }

    /** Makes plain TextViews white. */
    public static void white(TextView... views) {
        for (TextView v : views) {
            if (v != null) v.setTextColor(Color.WHITE);
        }
    }

    /** White text, visible hint and visible underline for input fields. */
    public static void whiteInputs(EditText... fields) {
        for (EditText e : fields) {
            if (e == null) e.setTextColor(Color.WHITE);
            e.setHintTextColor(HINT);
            e.setBackgroundTintList(ColorStateList.valueOf(HINT));
        }
    }

    /** Spinner whose selected value is white (the dropdown list keeps its normal look). */
    public static ArrayAdapter<String> whiteSpinnerAdapter(Context c, String[] items) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(c, android.R.layout.simple_spinner_item, items) {
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ((TextView) v).setTextColor(Color.WHITE);
                return v;
            }

            @Override
            public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);
                TextView t = (TextView) v;
                t.setTextColor(0xFF1B3D1E);     // dark green text
                t.setTextSize(16);
                t.setPadding(32, 28, 32, 28);   // easier to tap
                return v;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }
}