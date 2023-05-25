package com.example.nckujavafinalproject;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Update;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class RestaurantListActivity extends AppCompatActivity {

    public static final int NEW_RESTAURANT_ACTIVITY_REQUEST_CODE = 1;
    public static final int UPDATE_RESTAURANT_ACTIVITY_REQUEST_CODE = 2;

    private RestaurantViewModel mRestaurantViewModel;
    private final RestaurantListAdapter adapter = new RestaurantListAdapter(new RestaurantListAdapter.RestaurantDiff());

    private Toast tutorialToast =null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_list);

        tutorialToast=Toast.makeText(getApplicationContext(), "左滑編輯、右滑刪除", Toast.LENGTH_LONG);
        tutorialToast.show();

        RecyclerView recyclerView = findViewById(R.id.recyclerview);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get a new or existing ViewModel from the ViewModelProvider.
        mRestaurantViewModel = new ViewModelProvider(this).get(RestaurantViewModel.class);

        // Add an observer on the LiveData returned by getAllRestaurants.
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground.
        mRestaurantViewModel.getAllRestaurants().observe(this, restaurants -> {
            // Update the cached copy of the restaurants in the adapter.
            adapter.submitList(restaurants);
            adapter.setRestaurants(restaurants);
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(RestaurantListActivity.this, NewRestaurantActivity.class);
            startActivityForResult(intent, NEW_RESTAURANT_ACTIVITY_REQUEST_CODE);
        });

        // swipe right to delete
        ItemTouchHelper deleteHelper = new ItemTouchHelper(
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
                        int position = viewHolder.getAdapterPosition();
                        Restaurant myRestaurant = adapter.getRestaurantAtPosition(position);


                        // Delete the restaurant
                        mRestaurantViewModel.deleteRestaurant(myRestaurant);

                        // Show undo button
                        Snackbar.make(recyclerView, String.format("刪除餐廳: %s",myRestaurant.getName()),
                                Snackbar.LENGTH_LONG).setAction("取消",new View.OnClickListener(){

                            @Override
                            public void onClick(View v) {
                                mRestaurantViewModel.insert(myRestaurant);
                                adapter.notifyDataSetChanged();
                                recyclerView.scrollToPosition(position);
                            }
                        }).show();
                    }

                    @Override
                    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                        setDeleteIcon(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                    }
                });
        deleteHelper.attachToRecyclerView(recyclerView);

        // swipe left to update
        ItemTouchHelper updateHelper = new ItemTouchHelper(
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
                        int position = viewHolder.getAdapterPosition();
                        Restaurant myRestaurant = adapter.getRestaurantAtPosition(position);

                        // switch to new activity
                        Intent intent = new Intent(RestaurantListActivity.this, UpdateRestaurantActivity.class);
                        // pass restaurant to update activity
                        intent.putExtra("restaurant", myRestaurant);
                        startActivityForResult(intent, UPDATE_RESTAURANT_ACTIVITY_REQUEST_CODE);
                    }

                    @Override
                    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                        setEditIcon(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                    }
                });
        updateHelper.attachToRecyclerView(recyclerView);
    }

    // for swipe left
    private void setEditIcon(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        Paint mClearPaint = new Paint();
        mClearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        ColorDrawable mBackground = new ColorDrawable();
        int backgroundColor = Color.parseColor("#4ca825");
        Drawable deleteDrawable = ContextCompat.getDrawable(this, R.drawable.baseline_edit_24);
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

    // for swipe right
    private void setDeleteIcon(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        Paint mClearPaint = new Paint();
        mClearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        ColorDrawable mBackground = new ColorDrawable();
        int backgroundColor = Color.parseColor("#b80f0a");
        Drawable deleteDrawable = ContextCompat.getDrawable(this, R.drawable.baseline_delete_24);
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

        if (requestCode == NEW_RESTAURANT_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            final ArrayList<String> checkedLabels=data.getStringArrayListExtra(NewRestaurantActivity.CHECKBOX_REPLY);
            final String newLabel=String.join("`",checkedLabels);

            Restaurant restaurant = new Restaurant(data.getStringExtra(NewRestaurantActivity.EXTRA_REPLY), newLabel);
            mRestaurantViewModel.insert(restaurant);
        } else if (requestCode == UPDATE_RESTAURANT_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            String name = data.getStringExtra(UpdateRestaurantActivity.EXTRA_REPLY_NAME);
            String labels = data.getStringExtra(UpdateRestaurantActivity.EXTRA_REPLY_LABELS);

            Restaurant updatedRestaurant = new Restaurant(name, labels);
            mRestaurantViewModel.insert(updatedRestaurant); // old one will be replaced
        } else {
            adapter.notifyDataSetChanged();
            return;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        tutorialToast.cancel();
    }
}