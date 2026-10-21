package com.example.smartpantry.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantry.R;
import com.example.smartpantry.model.PantryItem;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

/** Binds pantry rows (from SQLite) to the RecyclerView. */
public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.VH> {

    public interface Listener {
        void onEdit(PantryItem item);
        void onDelete(PantryItem item);
    }

    private final List<PantryItem> items = new ArrayList<>();
    private final Listener listener;
    private boolean showExpiryAlerts = true;

    public PantryAdapter(Listener listener) { this.listener = listener; }

    public void submit(List<PantryItem> newItems, boolean showExpiryAlerts) {
        items.clear();
        items.addAll(newItems);
        this.showExpiryAlerts = showExpiryAlerts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pantry, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        final PantryItem item = items.get(position);
        Context c = h.itemView.getContext();

        UiColors.styleCard((MaterialCardView) h.itemView);

        h.name.setText(item.getName());
        h.amount.setText(Fmt.qty(item.getQuantity()) + " " + item.getUnit());
        h.name.setTextColor(UiColors.CARD_TEXT);
        h.amount.setTextColor(UiColors.CARD_TEXT_SOFT);

        String expiryText = "";
        int color = UiColors.CARD_TEXT_SOFT;
        Integer days = Fmt.daysUntil(item.getExpiry());
        if (days != null) {
            expiryText = c.getString(R.string.expires_on, item.getExpiry());
            if (showExpiryAlerts) {
                if (days < 0) {
                    expiryText = c.getString(R.string.expired);
                    color = 0xFFB71C1C;   // red
                } else if (days <= 3) {
                    expiryText = c.getString(R.string.expires_soon, item.getExpiry());
                    color = 0xFFBF360C;   // deep orange
                }
            }
        }
        h.expiry.setText(expiryText);
        h.expiry.setTextColor(color);
        h.expiry.setVisibility(expiryText.isEmpty() ? View.GONE : View.VISIBLE);

        h.delete.setColorFilter(UiColors.CARD_TEXT);

        h.itemView.setOnClickListener(v -> listener.onEdit(item));
        h.delete.setOnClickListener(v -> listener.onDelete(item));
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        final TextView name, amount, expiry;
        final ImageButton delete;
        VH(View v) {
            super(v);
            name = v.findViewById(R.id.textName);
            amount = v.findViewById(R.id.textAmount);
            expiry = v.findViewById(R.id.textExpiry);
            delete = v.findViewById(R.id.buttonDelete);
        }
    }
}
