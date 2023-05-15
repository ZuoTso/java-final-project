package com.example.nckujavafinalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    // to restaurant page button
    public void EditRestaurant_onclick(View view){
        // switch to restaurant page
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, RestaurantListActivity.class);
        startActivity(intent);
    }

    // switch to label page
    public void EditLabel_onclick(View view){
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, LabelListActivity.class);
        startActivity(intent);
    }

    // switch to map page
    public void NearbyRestaurant_onclick(View view){
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, NearbyRestaurantList.class);
        startActivity(intent);
    }
}