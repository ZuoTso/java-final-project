package com.example.nckujavafinalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

public class RestaurantResultActivity extends AppCompatActivity {

    private TextView pickedLabelsText;
    private TextView restaurantNameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_result);

        pickedLabelsText=findViewById(R.id.pickedLabels);
        restaurantNameText=findViewById(R.id.RestaurantName);

        // get restaurantName and pickedLabels
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            final String restaurantName=extras.getString("restaurantName");
            final String pickedLabels=extras.getString("pickedLabels");

            restaurantNameText.setText(restaurantName);
            pickedLabelsText.setText(pickedLabels);

        }

        // TODO: event listeners

    }
}