package com.example.nckujavafinalproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;

public class NewRestaurantActivity extends AppCompatActivity {

    public static final String EXTRA_REPLY = "com.example.android.restaurantlistsql.REPLY";
    public static final String CHECKBOX_REPLY="com.example.android.restaurantlistsql.REPLY.checkboxlist";
    private LinearLayout checkboxList;

    private EditText mEditRestaurantView;
    private LabelViewModel mLabelViewModel;
    private ArrayList<String> checkedLabels=new ArrayList<>();;

    private RestaurantViewModel mRestaurantViewModel;

    private ArrayList<String> allRestaurantNames=new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_restaurant);
        mEditRestaurantView = findViewById(R.id.edit_restaurant);

        checkboxList=findViewById(R.id.checkbox_list);

        // Get a new or existing ViewModel from the ViewModelProvider.
        mRestaurantViewModel = new ViewModelProvider(this).get(RestaurantViewModel.class);

        // get all restaurants
        mRestaurantViewModel.getAllRestaurants().observe(this, restaurants -> {
            for(int i=0;i<restaurants.size();i++){
                allRestaurantNames.add(restaurants.get(i).getName());
            }
        });

        final Button button = findViewById(R.id.button_save);
        button.setOnClickListener(view -> {
            // trimmed
            final String restaurantName=mEditRestaurantView.getText().toString().trim();

            if (restaurantName.equals("")) {
                Toast toast=Toast.makeText(getApplicationContext(),"餐廳名稱不可為空白",Toast.LENGTH_SHORT);
                toast.show();
                return;
            }else if(allRestaurantNames.contains(restaurantName)){
                Toast toast=Toast.makeText(getApplicationContext(),"餐廳名稱已存在",Toast.LENGTH_SHORT);
                toast.show();
                return;
            } else {
                Intent replyIntent = new Intent();
                String restaurant = mEditRestaurantView.getText().toString();
                replyIntent.putExtra(EXTRA_REPLY, restaurant);
                replyIntent.putExtra(CHECKBOX_REPLY,checkedLabels);

                setResult(RESULT_OK, replyIntent);
            }
            finish();
        });

        // dynamically generate label list

        mLabelViewModel = new ViewModelProvider(this).get(LabelViewModel.class);
        mLabelViewModel.getAllLabels().observe(this, labels -> {
            for (int i = 0; i < labels.size(); i++) {
                String text=labels.get(i).getName();
                CheckBox checkBox = new CheckBox(getApplicationContext());
                checkBox.setText(text);

                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            checkedLabels.add((String) checkBox.getText());
                        } else {
                            checkedLabels.remove((String) checkBox.getText());
                        }
                    }
                });

                checkboxList.addView(checkBox);
            }
        });
    }

}
