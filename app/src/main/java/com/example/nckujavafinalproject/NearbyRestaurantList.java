package com.example.nckujavafinalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NearbyRestaurantList extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_restaurant_list);

        RecyclerView recyclerView = findViewById(R.id.recyclerview);

        final NearbyRestaurantAdapter adapter = new NearbyRestaurantAdapter(new NearbyRestaurantAdapter.RestaurantDiff());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<String> defaultRestaurants= Arrays.asList("apple", "banana", "milk");

        adapter.submitList(defaultRestaurants);
    }
}