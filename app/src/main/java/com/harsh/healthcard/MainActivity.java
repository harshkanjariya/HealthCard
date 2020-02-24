package com.harsh.healthcard;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.NumberPicker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final FirebaseUser usr= FirebaseAuth.getInstance().getCurrentUser();
        if (usr!=null){
            Intent intent=new Intent(MainActivity.this,HomeActivity.class);
            startActivity(intent);
            finish();
        }
        NumberPicker picker=findViewById(R.id.languagelist);
        picker.setMinValue(0);
        picker.setMaxValue(4);
        picker.setDisplayedValues(new String[]{"English","हिंदी","ગુજરાતી","español","日本語"});
    }

    public void nextclick(View view) {
        Intent intent=new Intent(this,LoginActivity.class);
        startActivity(intent);
        finish();
    }
}