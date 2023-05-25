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
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class LabelListActivity extends AppCompatActivity {

    private enum SwipeDirection {
        LEFT,
        RIGHT
    }

    private Toast tutorialToast =null;

    public static final int NEW_LABEL_ACTIVITY_REQUEST_CODE = 1;

    private LabelViewModel mLabelViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_label_list);

        tutorialToast =Toast.makeText(getApplicationContext(), "左右滑刪除", Toast.LENGTH_LONG);
        tutorialToast.show();

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final LabelListAdapter adapter = new LabelListAdapter(new LabelListAdapter.LabelDiff());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get a new or existing ViewModel from the ViewModelProvider.
        mLabelViewModel = new ViewModelProvider(this).get(LabelViewModel.class);

        // Add an observer on the LiveData returned by getAllLabels.
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground.
        mLabelViewModel.getAllLabels().observe(this, labels -> {
            // Update the cached copy of the labels in the adapter.
            adapter.submitList(labels);
            adapter.setLabels(labels);
        });

        FloatingActionButton fab = findViewById(R.id.labelListFab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(LabelListActivity.this, NewLabelActivity.class);
            startActivityForResult(intent, NEW_LABEL_ACTIVITY_REQUEST_CODE);
        });

        // swipe to delete label
        // Add the functionality to swipe items in the
// recycler view to delete that item
        ItemTouchHelper helper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(0,
                        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
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
                        Label myLabel = adapter.getLabelAtPosition(position);

                        // Delete the word
                        mLabelViewModel.deleteLabel(myLabel);

                        // Show undo button
                        Snackbar.make(recyclerView, String.format("刪除標籤: %s", myLabel.getName()),
                                Snackbar.LENGTH_LONG).setAction("取消", new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                mLabelViewModel.insert(myLabel);
                                adapter.notifyDataSetChanged();
                                recyclerView.scrollToPosition(position);
                            }
                        }).show();
                    }

                    @Override
                    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                        if (dX > 0) {
                            // RIGHT swipe
                            setDeleteIcon(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive, SwipeDirection.RIGHT);

                        } else {
                            // LEFT swipe
                            setDeleteIcon(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive, SwipeDirection.LEFT);
                        }
                        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                    }
                });

        helper.attachToRecyclerView(recyclerView);
    }

    private void setDeleteIcon(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive, SwipeDirection swipeDirection) {
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
            if (swipeDirection == SwipeDirection.LEFT) {
                c.drawRect(itemView.getRight() + dX, (float) itemView.getTop(),
                        (float) itemView.getRight(), (float) itemView.getBottom(), mClearPaint);
            } else if (swipeDirection == SwipeDirection.RIGHT) {
                c.drawRect((float) itemView.getRight(), (float) itemView.getTop(),
                        itemView.getRight() + dX, (float) itemView.getBottom(), mClearPaint);
            }

            return;

        }

        mBackground.setColor(backgroundColor);


        if (swipeDirection == SwipeDirection.LEFT) {
            mBackground.setBounds(itemView.getRight(),
                    itemView.getTop(), itemView.getRight() + (int) dX, itemView.getBottom());
        } else {
            mBackground.setBounds(itemView.getLeft(),
                    itemView.getTop(), itemView.getLeft() + (int) dX, itemView.getBottom());
        }
        mBackground.draw(c);

        // NOTE: code for swipe left
        int deleteIconTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
        int deleteIconMargin = (itemHeight - intrinsicHeight) / 2;
        int deleteIconLeft = itemView.getRight() - deleteIconMargin - intrinsicWidth;
        int deleteIconRight = itemView.getRight() - deleteIconMargin;
        int deleteIconBottom = deleteIconTop + intrinsicHeight;

        // NOTE: code for swipe right
        if (swipeDirection == SwipeDirection.RIGHT) {
            deleteIconLeft = itemView.getLeft() + deleteIconMargin;
            deleteIconRight = itemView.getLeft() + deleteIconMargin + intrinsicWidth;
        }

        deleteDrawable.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom);
        deleteDrawable.draw(c);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NEW_LABEL_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            Label label = new Label(data.getStringExtra(NewLabelActivity.EXTRA_REPLY));
            mLabelViewModel.insert(label);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        tutorialToast.cancel();
    }
}