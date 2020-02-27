package com.harsh.healthcard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private TextView pid,nam,phon,dob,email,bg,he,we,ct,st,nt,addr;
    private DatabaseReference reference;
    private ValueEventListener listener;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.qrbtn && HomeActivity.myid!=null){
            Intent intent=new Intent(this,QrCode.class);
            intent.putExtra("id",HomeActivity.myid);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.profile_toolbar,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar=findViewById(R.id.profiletoolbar);
        setSupportActionBar(toolbar);

        pid=findViewById(R.id.pid);
        nam=findViewById(R.id.naam);
        phon=findViewById(R.id.phone);
        dob=findViewById(R.id.dob);
        email=findViewById(R.id.email);
        bg=findViewById(R.id.bg);
        he=findViewById(R.id.hei);
        we=findViewById(R.id.wei);
        ct=findViewById(R.id.ct);
        st=findViewById(R.id.st);
        nt=findViewById(R.id.nt);
        addr=findViewById(R.id.addr);

        reference= FirebaseDatabase.getInstance().getReference().child("patient");
        listener=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String,String>map= (Map<String, String>) dataSnapshot.getValue();
                pid.setText("P"+HomeActivity.myid);
                nam.setText(map.get("name"));
                dob.setText(map.get("dob"));
                phon.setText(map.get("phone"));
                email.setText(map.get("email"));
                bg.setText(map.get("bloodgroup"));
                he.setText(map.get("height"));
                we.setText(map.get("weight"));
                ct.setText(map.get("city"));
                st.setText(map.get("state"));
                nt.setText(map.get("nation"));
                addr.setText(map.get("address"));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        };
        reference.child(HomeActivity.myid).addValueEventListener(listener);
    }
    @Override
    protected void onDestroy() {
        reference.child(HomeActivity.myid).removeEventListener(listener);
        super.onDestroy();
    }
}
