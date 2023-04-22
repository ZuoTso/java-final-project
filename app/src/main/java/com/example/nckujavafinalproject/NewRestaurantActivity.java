package com.example.nckujavafinalproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class NewRestaurantActivity extends AppCompatActivity {

    public static final String EXTRA_REPLY = "com.example.android.restaurantlistsql.REPLY";

    private EditText mEditRestaurantView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_restaurant);
        mEditRestaurantView = findViewById(R.id.edit_restaurant);

        final Button button = findViewById(R.id.button_save);
        button.setOnClickListener(view -> {
            Intent replyIntent = new Intent();
            if (TextUtils.isEmpty(mEditRestaurantView.getText())) {
                setResult(RESULT_CANCELED, replyIntent);
            } else {
                String restaurant = mEditRestaurantView.getText().toString();
                replyIntent.putExtra(EXTRA_REPLY, restaurant);
                setResult(RESULT_OK, replyIntent);
            }
            finish();
        });
    }
}
