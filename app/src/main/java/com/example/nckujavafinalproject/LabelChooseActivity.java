package com.example.nckujavafinalproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDeepLinkSaveStateControl;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import kotlin.jvm.internal.markers.KMutableList;

public class LabelChooseActivity extends AppCompatActivity {
    private LiveData<List<Label>> labelListLiveData;
    private LinearLayout checkboxContainer;
    private RecyclerView recyclerView;
    private ArrayList<String> currentLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_label);
        final Button saveButton = findViewById(R.id.button_save);

        checkboxContainer = findViewById(R.id.label_checkbox_list);
        recyclerView = findViewById(R.id.recyclerview);

        List<CheckBox> theCheckBoxList = new ArrayList<>();

        // incoming current labels let them be chosen
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("currentLabel")) {
            currentLabel = intent.getStringArrayListExtra("currentLabel");
        }
        // Pass in all label data
        LabelViewModel labelViewModel = new ViewModelProvider(this).get(LabelViewModel.class);
        labelListLiveData = labelViewModel.getAllLabels();

        labelListLiveData.observe(this, labels -> {

            // using names of labels set text of checkbox
            checkboxContainer.removeAllViews(); // remove previous checkbox

            for (Label label : labels) {
                CheckBox checkBox = new CheckBox(LabelChooseActivity.this);
                checkBox.setText(label.getName());
                // incoming current labels let them be chosen
                if(currentLabel.contains(checkBox.getText().toString())){
                    checkBox.setChecked((true));
                }
                theCheckBoxList.add(checkBox);

                checkboxContainer.addView(checkBox);
            }
            checkboxContainer.addView(saveButton); // display save bottom
        });

        saveButton.setOnClickListener(view -> {
            Intent replyIntent = new Intent();

            // add a string arraylist, and traverse all checkbox,if it have been elected to, add it into replyChosenLabel.
            ArrayList<String> replyChosenLabel = new ArrayList<>();
            for(CheckBox item : theCheckBoxList){
                if(item.isChecked()){
                    replyChosenLabel.add(item.getText().toString());
                }
            }
            replyIntent.putExtra("currentLabel", replyChosenLabel);
            setResult(RESULT_OK, replyIntent);
            finish();
        });
    }
}