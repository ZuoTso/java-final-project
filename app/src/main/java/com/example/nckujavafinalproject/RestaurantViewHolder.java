package com.example.nckujavafinalproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

class RestaurantViewHolder extends RecyclerView.ViewHolder {
    private final TextView restaurantItemView;
    private RestaurantViewHolder(View itemView) {
        super(itemView);
        restaurantItemView = itemView.findViewById(R.id.textView);
    }

    public void bind(String text) {
        restaurantItemView.setText(text);
    }

    static RestaurantViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_item, parent, false);
        return new RestaurantViewHolder(view);
    }
}
