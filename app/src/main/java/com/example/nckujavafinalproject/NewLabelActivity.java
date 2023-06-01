package com.example.nckujavafinalproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class NewLabelActivity extends AppCompatActivity {

    public static final String EXTRA_REPLY = "com.example.android.restaurantlistsql.REPLY";

    private EditText mEditLabelView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_label);
        mEditLabelView = findViewById(R.id.edit_label);

        final Button button = findViewById(R.id.button_save);
        button.setOnClickListener(view -> {
            Intent replyIntent = new Intent();
            final String labelName=mEditLabelView.getText().toString().trim();

            // validation
            if (labelName.length()==0) {
                Toast emptyToast=Toast.makeText(getApplicationContext(),"標籤名稱不可為空白",Toast.LENGTH_SHORT);
                emptyToast.show();
                return;
            } else if(labelName.contains("`")) {
                Toast noTickToast=Toast.makeText(getApplicationContext(),"標籤名稱不可含有`",Toast.LENGTH_SHORT);
                noTickToast.show();
                return;
            } else{
                    String label = mEditLabelView.getText().toString();
                    replyIntent.putExtra(EXTRA_REPLY, label);
                    setResult(RESULT_OK, replyIntent);
                }
            finish();
        });
    }
}