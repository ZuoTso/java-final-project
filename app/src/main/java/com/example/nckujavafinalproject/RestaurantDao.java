package com.example.nckujavafinalproject;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RestaurantDao {
    // ignore inserting if already exists
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Restaurant restaurant);

    @Query("DELETE from restaurant_table")
    void deleteAll();

    // sorted alphabetically
    @Query("SELECT * FROM restaurant_table ORDER by name ASC")
    LiveData<List<Restaurant>> getAlphaSortedRestaurant();
}
