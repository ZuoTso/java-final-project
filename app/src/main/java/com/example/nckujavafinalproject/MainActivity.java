package com.example.nckujavafinalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.location.Location;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FusedLocationProviderClient fusedLocationClient;
    private static final int LABEL_CHOOSE_REQUEST_CODE = 1;

    private ArrayList<String> currentLabel=new ArrayList<String>();

    private RestaurantViewModel mRestaurantViewModel;

    private List<Restaurant> mAllRestaurants = new ArrayList<Restaurant>();
    private List<Restaurant> filteredRestaurants=new ArrayList<Restaurant>(); // after filtered with labels

    private AnimationDrawable lottery; //轉盤

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // remove dark mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        String savedLabels=sharedPref.getString("selectedLabels","");
        if(savedLabels.equals("")) {
            currentLabel=new ArrayList<>();
        }else{
            currentLabel = new ArrayList<>(Arrays.asList(savedLabels.split("`")));
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        mRestaurantViewModel = new ViewModelProvider(this).get(RestaurantViewModel.class);
        mRestaurantViewModel.getAllRestaurants().observe(this,restaurants -> {
            mAllRestaurants = restaurants;
            filteredRestaurants=restaurants;
        });

        ImageView image = (ImageView) findViewById(R.id.image);
        image.setBackgroundResource(R.drawable.animation);
        lottery = (AnimationDrawable) image.getBackground();
        lottery.start(); //開始轉動
    }

    // Receive the data returned by LabelChooseActivity
    private void launchLabelChooseActivity() {
        Intent intent = new Intent(MainActivity.this, LabelChooseActivity.class);
        intent.putStringArrayListExtra("currentLabel", currentLabel);
        startActivityForResult(intent, LABEL_CHOOSE_REQUEST_CODE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LABEL_CHOOSE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null && data.hasExtra("currentLabel")) {
                currentLabel = data.getStringArrayListExtra("currentLabel");

                // save currentLabel to saved preference
                SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("selectedLabels", String.join("`",currentLabel));
                editor.apply();

            }
        }
    }

    // to restaurant page button
    public void EditRestaurant_onclick(View view) {
        // switch to restaurant page
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, RestaurantListActivity.class);
        startActivity(intent);
    }

    // switch to label page
    public void EditLabel_onclick(View view) {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, LabelListActivity.class);
        startActivity(intent);
    }

    // switch to map page
    public void NearbyRestaurant_onclick(View view) {
        // if we don't have permission, request for permission, else get current location

        // if no permission
        if (ActivityCompat.checkSelfPermission(MainActivity.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(MainActivity.this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // request location permissions
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    2);
            return;
        } else {
            goToNearbyActivity();
        }
    }

    // switch to choose label page
    public void LabelChoose_onclick(View view) {
        launchLabelChooseActivity();
    }

    // update current location and go to nearbyActivity
    private void goToNearbyActivity() {
        // if no permission, return
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {

                    @Override
                    public void onSuccess(Location location) {

                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object

                            Intent intent = new Intent();
                            intent.putExtra("lat",location.getLatitude());
                            intent.putExtra("lng",location.getLongitude());


                            intent.setClass(MainActivity.this, NearbyRestaurantList.class);
                            startActivity(intent);
                        }
                    }
                });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // permission is granted
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            goToNearbyActivity();
        } else {
        // declined
            Log.v("INFO", "permission declined");
        }
        return;
    }

    public void pickRestaurant_onclick(View view){
        int pick;
        String restaurantName;
        String pickedLabels=String.join(",",currentLabel);

        // SECTION filter restaurants
        // if currentLabel is empty, filteredRestaurants= allRestaurants
        if(currentLabel.size()==0){
            filteredRestaurants=mAllRestaurants;
        }else {
            // reset array list
            filteredRestaurants = new ArrayList<Restaurant>();

            // filter restaurants by labels
            for (Restaurant restaurant : mAllRestaurants) {
                List<String> restaurantLabels = Arrays.asList(restaurant.getLabels().split("`"));
                boolean containLabels = true;

                for (String labelName : currentLabel) {
                    if (!restaurantLabels.contains(labelName)) {
                        containLabels = false;
                        break;
                    }
                }
                if (containLabels) {
                    filteredRestaurants.add(restaurant);
                }
            }
        }

        // SECTION pick restaurant

        if (filteredRestaurants.size()==0){
            restaurantName="無符合標準的餐廳";
        }

        else {
            pick = (int) (Math.random() * (filteredRestaurants.size())); //隨機選取餐廳index
            restaurantName = filteredRestaurants.get(pick).getName(); //取得中選餐廳的名字
        }

        try {
            Thread.sleep(100); //點級按鈕後，延遲0.1秒跳轉
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        // switch activity
        Intent intent = new Intent();

        intent.putExtra("restaurantName",restaurantName);
        intent.putExtra("pickedLabels",pickedLabels);

        intent.setClass(MainActivity.this, RestaurantResultActivity.class);
        startActivity(intent);
    }
}