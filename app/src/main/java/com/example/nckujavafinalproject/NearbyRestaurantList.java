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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_restaurant_list);

        tutorialToast=Toast.makeText(getApplicationContext(), "左右滑將餐廳加入清單", Toast.LENGTH_LONG);
        tutorialToast.show();

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

                        Log.v("pick_name",myRestaurant.getName());
                        //如果餐廳已存在於清單中，會顯示提醒
                        if(allRestaurantNames.contains(myRestaurant.getName())){
                            Toast toast=Toast.makeText(getApplicationContext(),"餐廳已存在清單中",Toast.LENGTH_SHORT);
                            toast.show();}
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

                        Log.v("pick_name",myRestaurant.getName());
                        //如果餐廳已存在於清單中，會顯示提醒
                        if(allRestaurantNames.contains(myRestaurant.getName())){
                            Toast toast=Toast.makeText(getApplicationContext(),"餐廳已存在清單中",Toast.LENGTH_SHORT);
                            toast.show();}
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
                    for (int i = 0; i < results.length(); i++) {
                        JSONObject obj = results.getJSONObject(i);
                        String name = obj.getString("name");
                        String rating = String.valueOf(obj.getDouble("rating"));
                        float rating_total_ = obj.getInt("user_ratings_total");
                        String rating_total;
                        if(rating_total_>999){
                            rating_total_ =rating_total_/1000;
                            rating_total = String.valueOf(rating_total_);
                            rating_total=String.format("%.3sk",rating_total);
                        }
                        else{ rating_total = String.valueOf(rating_total_);
                            rating_total=String.format("%.3s",rating_total);
                        }
                        JSONObject geometry = obj.getJSONObject("geometry");
                        JSONObject location = geometry.getJSONObject("location");
                        double lat = location.getDouble("lat");
                        double lng = location.getDouble("lng");

                        double curLat = currentLat;
                        double curLong = currentLng;

                        String distance = String.valueOf(Math.round(calculateDistance(curLat, curLong, lat, lng)))
                                + "m";


                        Restaurantinformation.add( String.format("%s\n %s/5.0(%s) %16.5s", name, rating,rating_total, distance));
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                // Update the UI if needed (switch to the main thread)
                runOnUiThread(() -> {
                    // Update UI here
                    adapter.submitList(Restaurantinformation);
                    adapter.setRestaurants(Restaurantinformation);

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
        Log.v("left","check");
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

}