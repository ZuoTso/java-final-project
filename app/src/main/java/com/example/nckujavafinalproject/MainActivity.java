package com.example.nckujavafinalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private FusedLocationProviderClient fusedLocationClient;
    private static final int LABEL_CHOOSE_REQUEST_CODE = 1;

    private ArrayList<String> currentLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        currentLabel = new ArrayList<>();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
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

}