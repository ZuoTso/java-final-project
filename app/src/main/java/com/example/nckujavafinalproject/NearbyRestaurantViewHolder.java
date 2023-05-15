package com.example.nckujavafinalproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NearbyRestaurantViewHolder extends RecyclerView.ViewHolder {
    private final TextView restaurantItemView;
    public NearbyRestaurantViewHolder(@NonNull View itemView) {
        super(itemView);
        restaurantItemView = itemView.findViewById(R.id.textView);
    }

    public void bind(String text) {
        restaurantItemView.setText(text);
    }

    static NearbyRestaurantViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_item, parent, false);
        return new NearbyRestaurantViewHolder(view);
    }
}
