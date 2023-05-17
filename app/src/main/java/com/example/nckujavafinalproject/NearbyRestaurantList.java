package com.example.nckujavafinalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NearbyRestaurantList extends AppCompatActivity {


    private double currentLat = 0;
    private double currentLng = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_restaurant_list);

        RecyclerView recyclerView = findViewById(R.id.recyclerview);

        final NearbyRestaurantAdapter adapter = new NearbyRestaurantAdapter(new NearbyRestaurantAdapter.RestaurantDiff());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<String> defaultRestaurants = Arrays.asList("apple", "banana", "milk");

        adapter.submitList(defaultRestaurants);

        currentLat = getIntent().getDoubleExtra("lat", 0.0);
        currentLng = getIntent().getDoubleExtra("lng", 0.0);
        // request data with current location
        fetchData();
    }

    private void fetchData() {
        // SECTION get api key from local.properties
        String apiKey = BuildConfig.MAPS_API_KEY;

        // SECTION test fetching
        final int radius = 1500;

        String url = String.format(
                "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=%f,%f&radius=%d&type=restaurant&key=%s",
                currentLat,
                currentLng,
                radius,
                apiKey);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // Handle the response on the background thread
                String responseBody = response.body().string();
                Log.v("INFO", responseBody);

                try {
                    JSONObject json = new JSONObject(responseBody);
                    JSONArray results = json.getJSONArray("results");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                // Update the UI if needed (switch to the main thread)
                runOnUiThread(() -> {
                    // Update UI here

                });
            }
        });
    }

    // return meters between two locations
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // Earth's radius in kilometers
        double radius = 6371;

        // Convert latitude and longitude from degrees to radians
        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);

        // Calculate the differences between the latitude and longitude values
        double latDiff = lat2Rad - lat1Rad;
        double lonDiff = lon2Rad - lon1Rad;

        // Calculate the distance using the Haversine formula
        double a = Math.sin(latDiff / 2) * Math.sin(latDiff / 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                        Math.sin(lonDiff / 2) * Math.sin(lonDiff / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = radius * c * 1000;

        return distance;
    }
}