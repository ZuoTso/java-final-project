package com.example.nckujavafinalproject;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class RestaurantRepository {
    private RestaurantDao mRestaurantDao;
    private LiveData<List<Restaurant>> mAllRestaurants;

    RestaurantRepository(Application application) {
        AppRoomDatabase db = AppRoomDatabase.getDatabase(application);
        mRestaurantDao = db.restaurantDao();
        mAllRestaurants = mRestaurantDao.getAlphaSortedRestaurant();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    LiveData<List<Restaurant>> getAllRestaurants() {
        return mAllRestaurants;
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    void insert(Restaurant restaurant) {
        AppRoomDatabase.databaseWriteExecutor.execute(() -> {
            mRestaurantDao.insert(restaurant);
        });
    }
}
