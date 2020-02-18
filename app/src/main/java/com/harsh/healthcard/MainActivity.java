package com.harsh.healthcard;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.NumberPicker;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NumberPicker picker=findViewById(R.id.languagelist);
        picker.setMinValue(0);
        picker.setMaxValue(4);
        picker.setDisplayedValues(new String[]{"English","हिंदी","ગુજરાતી","español","日本語"});
    }

    public void nextclick(View view) {
    }
}