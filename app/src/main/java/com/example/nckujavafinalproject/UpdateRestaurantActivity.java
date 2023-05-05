package com.example.nckujavafinalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class UpdateRestaurantActivity extends AppCompatActivity {

    private LabelViewModel mLabelViewModel;
    private ArrayList<String>checkedLabels=new ArrayList<>();
    private String newLabelStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_restaurant);

        LinearLayout linearLayout=findViewById(R.id.label_checkbox_list);
        final LabelListAdapter adapter = new LabelListAdapter(new LabelListAdapter.LabelDiff());

        // dynamically generate label list

        mLabelViewModel = new ViewModelProvider(this).get(LabelViewModel.class);
        mLabelViewModel.getAllLabels().observe(this, labels -> {
            for (int i = 0; i < labels.size(); i++) {

                CheckBox checkBox = new CheckBox(getApplicationContext());
                checkBox.setText(labels.get(i).getName());
                // TODO: if the restaurant has the label, set it to true
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

                linearLayout.addView(checkBox);
            }

        });
    }
}