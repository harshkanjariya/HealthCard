package com.harsh.healthcard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {
    private CountryCodePicker code;
    private TextInputEditText phone;
    private EditText otpin;
    private String pid;
    ProgressDialog pd;
    private TextView countview;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mcall=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            if (phoneAuthCredential.getSmsCode()==null){
                FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()){
                            Snackbar.make(findViewById(R.id.login_layout), Objects.requireNonNull(Objects.requireNonNull(task.getException()).getMessage()), Snackbar.LENGTH_LONG).show();
                            return;
                        }
                        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                        prefs.edit().putString("uid",pid).apply();
                        Intent intent=new Intent(LoginActivity.this,HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        }
        @Override
        public void onVerificationFailed(FirebaseException e) {
            pd.dismiss();
            Snackbar.make(findViewById(R.id.login_layout), Objects.requireNonNull(e.getMessage()),Snackbar.LENGTH_LONG).show();
            Log.e("error",e.getMessage());
            e.printStackTrace();
        }
        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verifycode=s;
            findViewById(R.id.number_screen).animate().translationY(pixel(-1000)).setDuration(1000).start();
            findViewById(R.id.otp_screen).animate().translationY(200).setDuration(1000).start();
            if (pd.isShowing())
                pd.dismiss();
            new CountDownTimer(60000,1000){
                @Override
                public void onTick(long millisUntilFinished) {
                    countview.setText(String.format(Locale.US,"%d",millisUntilFinished/1000));
                }
                @Override
                public void onFinish() {
                    countview.setText("0");
                }
            }.start();
        }
    };
    private String verifycode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar=findViewById(R.id.logintoolbar);
        setSupportActionBar(toolbar);

        code=findViewById(R.id.contrypicker);
        phone=findViewById(R.id.phone_input);
        otpin=findViewById(R.id.otp_input);
        countview=findViewById(R.id.sec_txt);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)!= PackageManager.PERMISSION_GRANTED||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_CONTACTS,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},0);
        pd=new ProgressDialog(this);
        pd.setMessage("Please wait...");
        pd.create();
    }
    public float pixel(float dp){
        return dp * ((float)getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
    public void loginfunction(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)!= PackageManager.PERMISSION_GRANTED||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_CONTACTS,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},0);
        else
            switch (view.getId()){
                case R.id.submitbtn:
                    InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    final String p=Objects.requireNonNull(phone.getText()).toString();
                    final String c=code.getSelectedCountryCodeWithPlus();
                    if (p.isEmpty()){
                        Snackbar.make(findViewById(R.id.login_layout),"Enter Phone Number",Snackbar.LENGTH_LONG).show();
                        return;
                    }
                    Log.e("num",c+p);
                    FirebaseDatabase.getInstance().getReference().child("patient").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            boolean found=false;
                            for (DataSnapshot snap:dataSnapshot.getChildren()){
                                Map<String,String>map= (Map<String, String>) snap.getValue();
                                if (map.get("phone").equals(c+p)){
                                    PhoneAuthProvider.getInstance().verifyPhoneNumber(c+p,60, TimeUnit.SECONDS,LoginActivity.this,mcall);
                                    pid=snap.getKey();
                                    found=true;
                                    break;
                                }
                            }
                            if(!found){
                                pd.dismiss();
                                Snackbar.make(findViewById(R.id.login_layout),"Patient Not Found!",Snackbar.LENGTH_LONG).show();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {}
                    });
                    pd.show();
                    break;
                case R.id.loginbtn:
                    String o=otpin.getText().toString();
                    PhoneAuthCredential credential=PhoneAuthProvider.getCredential(verifycode,o);
                    pd.show();
                    FirebaseAuth.getInstance().signInWithCredential(credential)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()){
                                        Snackbar.make(findViewById(R.id.login_layout), Objects.requireNonNull(Objects.requireNonNull(task.getException()).getMessage()),Snackbar.LENGTH_LONG).show();
                                        return;
                                    }
                                    SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                                    prefs.edit().putString("uid",pid).apply();
                                    Intent intent=new Intent(LoginActivity.this,HomeActivity.class);
                                    startActivity(intent);
                                    finish();
                                    pd.dismiss();
                                }
                            });
                    break;
            }
    }
}
