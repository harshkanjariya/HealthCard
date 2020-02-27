package com.harsh.healthcard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

public class NotificationActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    public NotificationAdapter myadapter;
    private DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        recyclerView=findViewById(R.id.notificationlistview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        myadapter=new NotificationAdapter();
        recyclerView.setAdapter(myadapter);
        HomeActivity.adapter=myadapter;
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,1);
        recyclerView.addItemDecoration(dividerItemDecoration);
        reference= FirebaseDatabase.getInstance().getReference();
    }
    class NotificationAdapter extends RecyclerView.Adapter<NotificationViewHolder>{
        @NonNull
        @Override
        public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater=getLayoutInflater();
            View v=inflater.inflate(R.layout.notification_single_view,parent,false);
            return new NotificationViewHolder(v);
        }
        @Override
        public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
            final Pair<String, Map<String,String>>pair=HomeActivity.notificationlist.get(position);
            holder.title.setText(pair.second.get("title"));
            holder.description.setText(pair.second.get("description"));
            reference.child("/notifications/"+HomeActivity.myid+"/"+pair.first+"/seen").setValue("true");
            if (pair.second.get("type").equals("docreq")){
                holder.btn1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        reference.child("permissions/"+pair.second.get("docid")+"/"+HomeActivity.myid).setValue(false);
                        reference.child("/notifications/"+HomeActivity.myid+"/"+pair.first).removeValue();
                    }
                });
                holder.btn2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        reference.child("permissions/"+pair.second.get("docid")+"/"+HomeActivity.myid).setValue(true);
                        reference.child("/notifications/"+HomeActivity.myid+"/"+pair.first).removeValue();
                    }
                });
            }
        }
        @Override
        public int getItemCount() {
            return HomeActivity.notificationlist.size();
        }
    }
    class NotificationViewHolder extends RecyclerView.ViewHolder{
        public TextView title,description;
        public TextView btn1,btn2;
        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            title=itemView.findViewById(R.id.single_not_title);
            description=itemView.findViewById(R.id.single_not_description);
            btn1=itemView.findViewById(R.id.single_not_btn1);
            btn2=itemView.findViewById(R.id.single_not_btn2);
        }
    }
}
