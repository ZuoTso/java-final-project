package com.example.nckujavafinalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
    }

    // to restaurant page button
    public void EditRestaurant_onclick(View view){
        // switch to restaurant page
        Intent intent = new Intent();
        intent.setClass(StartActivity.this, RestaurantListActivity.class);
        startActivity(intent);
    }

    // switch to label page
    public void EditLabel_onclick(View view){
        Intent intent = new Intent();
        intent.setClass(StartActivity.this, LabelListActivity.class);
        startActivity(intent);
    }
}