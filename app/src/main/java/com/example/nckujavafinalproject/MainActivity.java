package com.example.nckujavafinalproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nckujavafinalproject.Restaurant;
import com.example.nckujavafinalproject.RestaurantListAdapter;
import com.example.nckujavafinalproject.RestaurantViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    public static final int NEW_RESTAURANT_ACTIVITY_REQUEST_CODE = 1;

    private RestaurantViewModel mRestaurantViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final RestaurantListAdapter adapter = new RestaurantListAdapter(new RestaurantListAdapter.RestaurantDiff());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

//        // Get a new or existing ViewModel from the ViewModelProvider.
//        mRestaurantViewModel = new ViewModelProvider(this).get(RestaurantViewModel.class);
//
//        // Add an observer on the LiveData returned by getAllRestaurants.
//        // The onChanged() method fires when the observed data changes and the activity is
//        // in the foreground.
//        mRestaurantViewModel.getAllRestaurants().observe(this, restaurants -> {
//            // Update the cached copy of the restaurants in the adapter.
//            adapter.submitList(restaurants);
//        });
//
//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(view -> {
//            Intent intent = new Intent(MainActivity.this, NewRestaurantActivity.class);
//            startActivityForResult(intent, NEW_RESTAURANT_ACTIVITY_REQUEST_CODE);
//        });
    }

//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == NEW_RESTAURANT_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
//            Restaurant restaurant = new Restaurant(data.getStringExtra(NewRestaurantActivity.EXTRA_REPLY));
//            mRestaurantViewModel.insert(restaurant);
//        } else {
//            Toast.makeText(
//                    getApplicationContext(),
//                    R.string.empty_not_saved,
//                    Toast.LENGTH_LONG).show();
//        }
//    }
}