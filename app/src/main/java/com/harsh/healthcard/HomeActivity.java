package com.harsh.healthcard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    private DatabaseReference reference;
    private ValueEventListener notificationlistener;
    public static RecyclerView.Adapter adapter;
    public static String myid,num;
    public static ArrayList<Pair<String,Map<String,String>>>notificationlist=new ArrayList<>();
    private TextView notcount;
    private ProgressDialog loading;
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.logoutbtn){
            AlertDialog.Builder builder=new AlertDialog.Builder(HomeActivity.this)
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout ?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FirebaseAuth.getInstance().signOut();
                            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .setNegativeButton("No",null);
            builder.create().show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_toolbar,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar=findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);

        loading=new ProgressDialog(this);
        loading.setMessage("Loading Data...");
        loading.setCancelable(false);
        loading.show();
        notcount=findViewById(R.id.notificationcount);
        FirebaseUser usr= FirebaseAuth.getInstance().getCurrentUser();
        num=usr.getPhoneNumber();
        reference= FirebaseDatabase.getInstance().getReference();
        reference.child("patient").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (loading.isShowing())
                    loading.dismiss();
                boolean found=false;
                for (DataSnapshot snap:dataSnapshot.getChildren()){
                    Map<String,String> map= (Map<String, String>) snap.getValue();
                    if (map.get("phone").equals(num)){
                        found=true;
                        myid=snap.getKey();
                        break;
                    }
                }
                if(!found){
                    Intent intent=new Intent(HomeActivity.this,LoginActivity.class);
                    startActivity(intent);
                }else{
                    reference.child("notifications/"+myid).addValueEventListener(notificationlistener);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
        notificationlistener=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long count = 0;
                notificationlist.clear();
                for (DataSnapshot snapshot:dataSnapshot.getChildren()) {
                    Map<String,String>map= (Map<String, String>) snapshot.getValue();
                    if(map.get("seen").equals("false"))
                        count++;
                    notificationlist.add(new Pair(snapshot.getKey(), map));
                }
                if (count > 0){
                    notcount.setText("" + count);
                    notcount.setVisibility(View.VISIBLE);
                }else
                    notcount.setVisibility(View.GONE);
                if (adapter!=null)
                    adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        };
    }

    @Override
    protected void onDestroy() {
        reference.child("notifications/"+myid).removeEventListener(notificationlistener);
        super.onDestroy();
    }

    public void openprofile(View view){
        Intent intent=new Intent(this,ProfileActivity.class);
        startActivity(intent);
    }
    public void opennotification(View view) {
        Intent intent=new Intent(this,NotificationActivity.class);
        startActivity(intent);
    }
    public void openhistory(View view) {
        Intent intent=new Intent(this,HistoryActivity.class);
        startActivity(intent);
    }
    public void openmedication(View view) {
        Intent intent=new Intent(this,MedicationActivity.class);
        startActivity(intent);
    }
}
