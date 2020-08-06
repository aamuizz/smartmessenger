package com.amtechnology.smartreplyassistant;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;

public class replystore extends AppCompatActivity {
    String allreplies="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.toolbar_allreplies);
        TextView allreply = findViewById(R.id.allreplies);
        Replies_Database replies_database = new Replies_Database(this);

        Cursor all = replies_database.getAllReplies();
        while(all.moveToNext()){
            String message = all.getString(all.getColumnIndex("Message"));
            String reply = all.getString(all.getColumnIndex("Reply"));
            allreplies += "Message: "+message+"\n"+"Reply: "+reply+"\n\n";
        }
        allreply.setText(allreplies);
    }
}