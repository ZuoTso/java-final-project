package com.example.nckujavafinalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NearbyRestaurantList extends AppCompatActivity {


    private double currentLat = 0;
    private double currentLng = 0;

    private RecyclerView recyclerView;
    private RestaurantViewModel mRestaurantViewModel;
    private NearbyRestaurantAdapter adapter = new NearbyRestaurantAdapter(new NearbyRestaurantAdapter.RestaurantDiff());
    private ArrayList<String> Restaurantinformation= new ArrayList<>();
    public static final int UPDATE_RESTAURANT_ACTIVITY_REQUEST_CODE = 2;
    private int position;
    private ArrayList<String> allRestaurantNames=new ArrayList<>();
    private Toast tutorialToast =null;

    private Toast noRestaurantToast=null;

    private TextView stateText=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_restaurant_list);

        noRestaurantToast=Toast.makeText(getApplicationContext(),"附近沒有餐廳",Toast.LENGTH_LONG);

        tutorialToast=Toast.makeText(getApplicationContext(), "左右滑將餐廳加入清單", Toast.LENGTH_LONG);
        tutorialToast.show();

        stateText=findViewById(R.id.stateText);

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRestaurantViewModel = new ViewModelProvider(this).get(RestaurantViewModel.class);
        mRestaurantViewModel.getAllRestaurants().observe(this, restaurants -> {
            for(int i=0;i<restaurants.size();i++){
                allRestaurantNames.add(restaurants.get(i).getName());
            }
        });

        currentLat = getIntent().getDoubleExtra("lat", 0.0);
        currentLng = getIntent().getDoubleExtra("lng", 0.0);
        // request data with current location
        fetchData();

        //左滑將餐廳加入清單
        ItemTouchHelper leftHelper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(0,
                        ItemTouchHelper.LEFT) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView,
                                          RecyclerView.ViewHolder viewHolder,
                                          RecyclerView.ViewHolder target) {
                        return false;
                    }
                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder,
                                         int direction) {
                        position = viewHolder.getAdapterPosition();
                        String newRestaurantString = adapter.getRestaurantAtPosition(position);
                        String newRestaurantName = (newRestaurantString.split("\n"))[0]; //get name
                        Restaurant myRestaurant = new Restaurant(newRestaurantName,""); //creat restaurant,label is null

                        // switch to new activity
                        Intent intent = new Intent(NearbyRestaurantList.this, UpdateRestaurantActivity.class);
                        // pass restaurant to update activity
                        intent.putExtra("restaurant", myRestaurant);
                        startActivityForResult(intent, UPDATE_RESTAURANT_ACTIVITY_REQUEST_CODE);
                    }
                    @Override
                    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                        setLeftEditIcon(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                    }
                });
        leftHelper.attachToRecyclerView(recyclerView);

        //右滑將餐廳加入清單
        ItemTouchHelper rightHelper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(0,
                        ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView,
                                          RecyclerView.ViewHolder viewHolder,
                                          RecyclerView.ViewHolder target) {
                        return false;
                    }
                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder,
                                         int direction) {
                        position = viewHolder.getAdapterPosition();
                        String newRestaurantString = adapter.getRestaurantAtPosition(position);
                        String newRestaurantName = (newRestaurantString.split("\n"))[0]; //get name
                        Restaurant myRestaurant = new Restaurant(newRestaurantName,""); //creat restaurant,label is null

                        // switch to new activity
                        Intent intent = new Intent(NearbyRestaurantList.this, UpdateRestaurantActivity.class);
                        // pass restaurant to update activity
                        intent.putExtra("restaurant", myRestaurant);
                        startActivityForResult(intent, UPDATE_RESTAURANT_ACTIVITY_REQUEST_CODE);
                    }

                    @Override
                    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                        setRightEditIcon(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                    }
                });
        rightHelper.attachToRecyclerView(recyclerView);
    }

    private void fetchData() {
        // SECTION get api key from local.properties
        String apiKey = BuildConfig.MAPS_API_KEY;

        // SECTION test fetching
        final int radius = 1500;

        String url = String.format(
                "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=%f,%f&radius=%d&type=restaurant|food|cafe&opennow=true&language=language=zh-TW&key=%s",
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
                stateText.setText("獲得餐廳失敗，請檢查你的網路");
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // Handle the response on the background thread
                String responseBody = response.body().string();
                JSONArray filteredResults=new JSONArray();

                try {
                    JSONObject json = new JSONObject(responseBody);
                    JSONArray results = json.getJSONArray("results");

                    // filter existed restaurants
                    for(int i=0;i<results.length();i++){
                        JSONObject obj = results.getJSONObject(i);

                        // filter restaurants with the same name
                        final String name=obj.getString("name");
                        if(allRestaurantNames.contains(name)){
                            continue;
                        }
                        filteredResults.put(obj);
                    }

                    // add distance to every object
                    try {
                        for (int i = 0; i < filteredResults.length(); i++) {
                            JSONObject obj = filteredResults.getJSONObject(i);

                            JSONObject geometry = obj.getJSONObject("geometry");
                            JSONObject location = geometry.getJSONObject("location");
                            double lat = location.getDouble("lat");
                            double lng = location.getDouble("lng");

                            double curLat = currentLat;
                            double curLong = currentLng;
                            long distance=Math.round(calculateDistance(curLat, curLong, lat, lng));
                            obj.put("distance", distance);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // sort array by distance
                    try {
                        List<JSONObject> jsonValues = new ArrayList<>();
                        for (int i = 0; i < filteredResults.length(); i++) {
                            jsonValues.add(filteredResults.getJSONObject(i));
                        }

                        Collections.sort(jsonValues, new Comparator<JSONObject>() {
                            private static final String KEY_DISTANCE = "distance";

                            @Override
                            public int compare(JSONObject a, JSONObject b) {
                                int distanceA = a.optInt(KEY_DISTANCE, 0);
                                int distanceB = b.optInt(KEY_DISTANCE, 0);
                                return Integer.compare(distanceA, distanceB);
                            }
                        });

                        // Clear the original JSONArray and add the sorted objects back
                        filteredResults = new JSONArray();
                        for (JSONObject jsonObject : jsonValues) {
                            filteredResults.put(jsonObject);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // format the string to show

                    for (int i = 0; i < filteredResults.length(); i++) {
                        JSONObject obj = filteredResults.getJSONObject(i);

                        // name and rating
                        String name = obj.getString("name");
                        String rating="";
                        try {
                            rating = String.valueOf(obj.getDouble("rating"));
                        }catch(JSONException e){
                            Log.e("Place API","One of the restaurants has no rating");
                            rating="?";
                        }

                        // rating count
                        double ratingCount=0.0;
                        String ratingCountText="";

                        try {
                            ratingCount = obj.getInt("user_ratings_total");
                            if (ratingCount > 999) {
                                ratingCountText = String.format("%.1fk", ratingCount / 1000);
                            } else {
                                ratingCountText = String.format("%.0f", ratingCount);
                            }
                        }catch(JSONException e){
                            Log.e("Place API","restaurant doesn't have user_ratings_total");
                            ratingCount=0.0;
                            ratingCountText="?";
                        }

                        String distance = obj.getInt("distance") + "m";

                        Restaurantinformation.add( String.format("%s\n %s/5.0 ( %s ) %16.5s", name, rating,ratingCountText, distance));
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                JSONArray finalFilteredResults = filteredResults;
                // Update the UI if needed (switch to the main thread)
                runOnUiThread(() -> {
                    // Update UI here
                    adapter.submitList(Restaurantinformation);
                    adapter.setRestaurants(Restaurantinformation);

                    stateText.setText(""); // hide the loading text

                    // show no restaurant toast
                    if(finalFilteredResults.length()==0){
                        tutorialToast.cancel();
                        noRestaurantToast.show();
                    }
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


    private void setLeftEditIcon(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        Paint mClearPaint = new Paint();
        mClearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        ColorDrawable mBackground = new ColorDrawable();
        int backgroundColor = Color.parseColor("#FF03DAC5");
        Drawable deleteDrawable = ContextCompat.getDrawable(this, R.drawable.baseline_add_24);
        int intrinsicWidth = deleteDrawable.getIntrinsicWidth();
        int intrinsicHeight = deleteDrawable.getIntrinsicHeight();

        View itemView = viewHolder.itemView;
        int itemHeight = itemView.getHeight();

        boolean isCancelled = dX == 0 && !isCurrentlyActive;

        if (isCancelled) {
            c.drawRect(itemView.getRight() + dX, (float) itemView.getTop(),
                    (float) itemView.getRight(), (float) itemView.getBottom(), mClearPaint);
            return;
        }
        mBackground.setColor(backgroundColor);
        mBackground.setBounds(itemView.getRight(),
                itemView.getTop(), itemView.getRight() + (int) dX, itemView.getBottom());
        mBackground.draw(c);

        int deleteIconTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
        int deleteIconMargin = (itemHeight - intrinsicHeight) / 2;
        int deleteIconLeft = itemView.getRight() - deleteIconMargin - intrinsicWidth;
        int deleteIconRight = itemView.getRight() - deleteIconMargin;
        int deleteIconBottom = deleteIconTop + intrinsicHeight;
        deleteDrawable.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom);
        deleteDrawable.draw(c);

    }
    private void setRightEditIcon(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        Paint mClearPaint = new Paint();
        mClearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        ColorDrawable mBackground = new ColorDrawable();
        int backgroundColor = Color.parseColor("#FF03DAC5");
        Drawable deleteDrawable = ContextCompat.getDrawable(this, R.drawable.baseline_add_24);
        int intrinsicWidth = deleteDrawable.getIntrinsicWidth();
        int intrinsicHeight = deleteDrawable.getIntrinsicHeight();

        View itemView = viewHolder.itemView;
        int itemHeight = itemView.getHeight();

        boolean isCancelled = dX == 0 && !isCurrentlyActive;

        if (isCancelled) {
            c.drawRect((float) itemView.getRight(), (float) itemView.getTop(),
                    itemView.getRight() + dX, (float) itemView.getBottom(), mClearPaint);
            return;
        }
        mBackground.setColor(backgroundColor);
        mBackground.setBounds(itemView.getLeft(),
                itemView.getTop(), itemView.getLeft() + (int) dX, itemView.getBottom());
        mBackground.draw(c);

        int deleteIconTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
        int deleteIconMargin = (itemHeight - intrinsicHeight) / 2;
        int deleteIconLeft = itemView.getLeft() + deleteIconMargin;
        int deleteIconRight = itemView.getLeft() + deleteIconMargin + intrinsicWidth;
        int deleteIconBottom = deleteIconTop + intrinsicHeight;

        deleteDrawable.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom);
        deleteDrawable.draw(c);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == UPDATE_RESTAURANT_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            String name = data.getStringExtra(UpdateRestaurantActivity.EXTRA_REPLY_NAME);
            String labels = data.getStringExtra(UpdateRestaurantActivity.EXTRA_REPLY_LABELS);

            Restaurant addRestaurant = new Restaurant(name, labels);
            mRestaurantViewModel.insert(addRestaurant);
            //如果餐廳被加入清單中，將不在顯示
            Restaurantinformation.remove(position);
        } else {
            adapter.notifyDataSetChanged();
            return;
        }
        //只要左右滑動就刷新頁面
        adapter.submitList(Restaurantinformation);
        adapter.setRestaurants(Restaurantinformation);
    }

    @Override
    protected void onStop() {
        super.onStop();
        tutorialToast.cancel();
        noRestaurantToast.cancel();
    }
}