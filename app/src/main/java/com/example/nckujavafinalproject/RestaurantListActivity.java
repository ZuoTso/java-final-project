package com.example.nckujavafinalproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Update;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class RestaurantListActivity extends AppCompatActivity {

    public static final int NEW_RESTAURANT_ACTIVITY_REQUEST_CODE = 1;
    public static final int UPDATE_RESTAURANT_ACTIVITY_REQUEST_CODE=2;

    private RestaurantViewModel mRestaurantViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_list);

        // NOTE: un-comment this line to reset database
//        getApplicationContext().deleteDatabase("database");

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final RestaurantListAdapter adapter = new RestaurantListAdapter(new RestaurantListAdapter.RestaurantDiff());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get a new or existing ViewModel from the ViewModelProvider.
        mRestaurantViewModel = new ViewModelProvider(this).get(RestaurantViewModel.class);

        // Add an observer on the LiveData returned by getAllRestaurants.
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground.
        mRestaurantViewModel.getAllRestaurants().observe(this, restaurants -> {
            // Update the cached copy of the restaurants in the adapter.
            adapter.submitList(restaurants);
            adapter.setRestaurants(restaurants);
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(RestaurantListActivity.this, NewRestaurantActivity.class);
            startActivityForResult(intent, NEW_RESTAURANT_ACTIVITY_REQUEST_CODE);
        });

    // Add the functionality to swipe right in the
    // recycler view to delete that item
        ItemTouchHelper deleteHelper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(0,
                        ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView,
                                          RecyclerView.ViewHolder viewHolder,
                                          RecyclerView.ViewHolder target) {
                        return false;
                    }
                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder,
                                         int direction) {
                        int position = viewHolder.getAdapterPosition();
                        Restaurant myRestaurant = adapter.getRestaurantAtPosition(position);
                        Toast.makeText(RestaurantListActivity.this, "Deleting " +
                                myRestaurant.getName(), Toast.LENGTH_LONG).show();

                        // Delete the restaurant
                        mRestaurantViewModel.deleteRestaurant(myRestaurant);
                    }
                });
        deleteHelper.attachToRecyclerView(recyclerView);

        // swipe left to update
        ItemTouchHelper updateHelper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(0,
                        ItemTouchHelper.LEFT) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView,
                                          RecyclerView.ViewHolder viewHolder,
                                          RecyclerView.ViewHolder target) {
                        return false;
                    }
                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder,
                                         int direction) {
                        int position = viewHolder.getAdapterPosition();
                        Restaurant myRestaurant = adapter.getRestaurantAtPosition(position);
                        Toast.makeText(RestaurantListActivity.this, "Updating " +
                                myRestaurant.getName(), Toast.LENGTH_LONG).show();


                        // TODO: call function to update the restaurant

                        // switch to new activity
                        Intent intent = new Intent(RestaurantListActivity.this, UpdateRestaurantActivity.class);
                        // pass restaurant to update activity
                        intent.putExtra("restaurant",myRestaurant);
                        startActivityForResult(intent, UPDATE_RESTAURANT_ACTIVITY_REQUEST_CODE);
                    }
                });
        updateHelper.attachToRecyclerView(recyclerView);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NEW_RESTAURANT_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            Restaurant restaurant = new Restaurant(data.getStringExtra(NewRestaurantActivity.EXTRA_REPLY),"");
            mRestaurantViewModel.insert(restaurant);
        } else if(requestCode==UPDATE_RESTAURANT_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK) {
            String name=data.getStringExtra(UpdateRestaurantActivity.EXTRA_REPLY_NAME);
            String labels=data.getStringExtra(UpdateRestaurantActivity.EXTRA_REPLY_LABELS);

            Restaurant updatedRestaurant=new Restaurant(name,labels);
            mRestaurantViewModel.insert(updatedRestaurant); // old one will be replaced
        }else {
            Toast.makeText(
                    getApplicationContext(),
                    R.string.empty_not_saved,
                    Toast.LENGTH_LONG).show();
        }
    }
}