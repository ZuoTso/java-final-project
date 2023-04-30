package com.example.nckujavafinalproject;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class RestaurantViewModel extends AndroidViewModel {

    private RestaurantRepository mRepository;

    private final LiveData<List<Restaurant>> mAllRestaurants;

    public RestaurantViewModel(Application application) {
        super(application);
        mRepository = new RestaurantRepository(application);
        mAllRestaurants = mRepository.getAllRestaurants();
    }

    LiveData<List<Restaurant>> getAllRestaurants() {
        return mAllRestaurants;
    }

    public void insert(Restaurant restaurant) {
        mRepository.insert(restaurant);
    }

    public void deleteRestaurant(Restaurant restaurant) {mRepository.deleteRestaurant(restaurant);}
}
