package com.example.nckujavafinalproject;

import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import java.util.List;

public class NearbyRestaurantAdapter extends ListAdapter<String, NearbyRestaurantViewHolder> {

//    TODO: change list data type into corresponding data type
    private List<String> mRestaurants; // Cached copy of restaurants

    public NearbyRestaurantAdapter(@NonNull DiffUtil.ItemCallback<String> diffCallback) {
        super(diffCallback);
    }

    @NonNull
    @Override
    public NearbyRestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return NearbyRestaurantViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull NearbyRestaurantViewHolder holder, int position) {
        String current = getItem(position);
        holder.bind(current);
    }

    // TODO: string to data type
    static class RestaurantDiff extends DiffUtil.ItemCallback<String> {

        @Override
        public boolean areItemsTheSame(@NonNull String oldItem, @NonNull String newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull String oldItem, @NonNull String newItem) {
            return oldItem.equals(newItem);
        }
    }

    void setRestaurants(List<String> restaurants) {
        mRestaurants = restaurants;
        notifyDataSetChanged();
    }
    public String getRestaurantAtPosition(int position) {
        return mRestaurants.get(position);
    }
}
