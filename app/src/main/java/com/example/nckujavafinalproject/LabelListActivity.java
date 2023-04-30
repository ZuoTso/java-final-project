package com.example.nckujavafinalproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nckujavafinalproject.Label;
import com.example.nckujavafinalproject.LabelListAdapter;
import com.example.nckujavafinalproject.LabelViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class LabelListActivity extends AppCompatActivity {

    public static final int NEW_LABEL_ACTIVITY_REQUEST_CODE = 1;

    private LabelViewModel mLabelViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_label_list);

        // NOTE: un-comment this line to reset database
//        getApplicationContext().deleteDatabase("database");

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
        });

        FloatingActionButton fab = findViewById(R.id.labelListFab);
//        fab.setOnClickListener(view -> {
//            Intent intent = new Intent(LabelListActivity.this, NewLabelActivity.class);
//            startActivityForResult(intent, NEW_LABEL_ACTIVITY_REQUEST_CODE);
//        });
    }

//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == NEW_LABEL_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
//            Label label = new Label(data.getStringExtra(NewLabelActivity.EXTRA_REPLY),"");
//            mLabelViewModel.insert(label);
//        } else {
//            Toast.makeText(
//                    getApplicationContext(),
//                    R.string.empty_not_saved,
//                    Toast.LENGTH_LONG).show();
//        }
//    }
}