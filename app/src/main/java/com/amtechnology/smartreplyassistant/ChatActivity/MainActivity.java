package com.amtechnology.smartreplyassistant.ChatActivity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.amtechnology.smartreplyassistant.AllContacts.Contact;
import com.amtechnology.smartreplyassistant.AllContacts.Contact_Message_Adapter;
import com.amtechnology.smartreplyassistant.R;
import com.amtechnology.smartreplyassistant.Database.replystore;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    RecyclerView mRecyclerview;
    ArrayList<Contact>allusers;
    public static String user_uid;
    ImageView userreplies_icon;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.toolbar_chatmain);
        setTitle("Chat");
        Bundle bundle = getIntent().getExtras();
        userreplies_icon = findViewById(R.id.userreplies_icon);
        userreplies_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), replystore.class);
                startActivity(intent);
            }
        });
        user_uid = "Vb3ovYnu2lYay5gqV3bHxqi4rLK2"; /// bundle
//        user_uid = bundle.getString("uid");
//        Setting up the Recycler View
        mRecyclerview = findViewById(R.id.allcontacts_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        mRecyclerview.setLayoutManager(linearLayoutManager);
        mRecyclerview.setHasFixedSize(true);
//Getting all users from the database
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String>allkeys = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                      allkeys.add(ds.getKey());
                }
                allusers = new ArrayList<>();
                for(int i =0;i<allkeys.size();++i){
                    DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("users").child(allkeys.get(i));
                    databaseReference2.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(!snapshot.getKey().equals(user_uid)) {
                                Map<String, String> alldata = (Map<String, String>) snapshot.getValue();
                                allusers.add(new Contact(alldata.get("name"), snapshot.getKey(), alldata.get("phoneno")));
                                Contact_Message_Adapter contact_message_adapter = new Contact_Message_Adapter(allusers, getApplicationContext());
                                mRecyclerview.setAdapter(contact_message_adapter);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }


}