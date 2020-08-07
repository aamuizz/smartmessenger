package com.amtechnology.smartreplyassistant.ChatActivity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.amtechnology.smartreplyassistant.Messages;
import com.amtechnology.smartreplyassistant.R;
import com.amtechnology.smartreplyassistant.Database.Replies_Database;
import com.amtechnology.smartreplyassistant.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.smartreply.FirebaseSmartReply;
import com.google.firebase.ml.naturallanguage.smartreply.FirebaseTextMessage;
import com.google.firebase.ml.naturallanguage.smartreply.SmartReplySuggestion;
import com.google.firebase.ml.naturallanguage.smartreply.SmartReplySuggestionResult;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ChatActivity extends AppCompatActivity {
    String mCurrentUserId;
    String mChatUser;
    public static final int TOTAL_ITEM_TO_LOAD = 10;
    private int mCurrentPage = 1;
    MessagesList messagesList;
    MessageInput messageInput;
    String lastmessageid;
    boolean autoreply = false;
    boolean replysent;
    private int itemPos = 0;
    private String mLastKey = "";
    private String mPrevKey = "";
    private String lastmsgid="";
    private String message,reply;
    private ArrayList<FirebaseTextMessage>conversation = new ArrayList<>();
    public MessagesListAdapter<Messages> adapter;
    private Handler handler;
    ImageView threesuggestions;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.toolbar_user_chat);

//        Message List is for Messenger which shows all the messages
        messagesList = findViewById(R.id.messagesList);
//        MessageInput is the input field at the bottom of Chat
        messageInput = findViewById(R.id.input);
        handler = new Handler();

//        Switch to Turn on AutoReply
        Switch autoreply_switch = findViewById(R.id.autoreply_switch);
        autoreply_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    autoreply = true;
                }
                else{
                    autoreply = false;
                }
            }
        });
        Bundle bundle = getIntent().getExtras();

//        Getting suggestions of the messsages by clicking the toolbar icon
        threesuggestions = findViewById(R.id.three_suggestions);
        threesuggestions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generatesuggestions_dialog(v.getContext());
            }
        });
/////////////////////////////////////////////////////////////////////////////

//Initializing Text View
        TextView name = findViewById(R.id.users_name);
        TextView phoneno = findViewById(R.id.phone_no);
/////////////////////////////////////////////////////////////////////////////

//Setting the name in toolbar
        name.setText(bundle.getString("name"));
        phoneno.setText(bundle.getString("phoneno"));
/////////////////////////////////////////////////////////////////////////////

        //        mChatUser = bundle.getString("uid");

        mChatUser = "Vb3ovYnu2lYay5gqV3bHxqi4rLK2";
        mCurrentUserId = "7Mu9cFd5l4cuUWBFeQoxluPTEb33";;
//// Fetching all Messages from database
        getallmessages();
////////////////////////////////////////////////////////////////////////////


//When the message will be sent then it will saved in the realtime database
        messageInput.setInputListener(new MessageInput.InputListener() {
            @Override
            public boolean onSubmit(CharSequence input) {
//                Database is saved in this pattern in database
                String current_user_ref = "messages/" + mCurrentUserId + "/" + mChatUser;
                String chat_user_ref = "messages/" + mChatUser + "/" + mCurrentUserId;

                DatabaseReference user_message_push = FirebaseDatabase.getInstance().getReference().child("messages")
                        .child(mCurrentUserId).child(mChatUser).push();

                String push_id = user_message_push.getKey();
//Parameters for the message
                Map messageMap = new HashMap();
                messageMap.put("message", input.toString());
                messageMap.put("seen", false);
                messageMap.put("type", "text");
                messageMap.put("time", new Date().toString());
                messageMap.put("from", mCurrentUserId);

                Map messageUserMap = new HashMap();
                messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
                messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);

                FirebaseDatabase.getInstance().getReference().updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {

                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError != null) {

                            Log.e("CHAT_ACTIVITY", "Cannot add message to database");
                        } else {
                            Toast.makeText(ChatActivity.this, "Message sent", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                return true;
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void getallmessages() {

//        Creating Adapter and Adding all Messages in the list
        adapter = new MessagesListAdapter<>(mCurrentUserId, null);
        DatabaseReference messageRef = FirebaseDatabase.getInstance().getReference().child("messages").child(mCurrentUserId).child(mChatUser);
        Query messageQuery = messageRef.limitToLast(mCurrentPage * TOTAL_ITEM_TO_LOAD);
//This listener is called firstly with all messages then with when new message is added
        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map<String, String> allmessage = (Map<String, String>) dataSnapshot.getValue();
                User user = new User(allmessage.get("from"), "Mubarak", "");
                Messages messages = new Messages(allmessage.get("from"), allmessage.get("message"), user, allmessage.get("time"));
                itemPos++;

//Passing the message to an array list which can later be used for ML Machine Learning Model
                if(allmessage.get("from").equals(mCurrentUserId)) {
                    addMessage(allmessage.get("message"));
                }
                else {
                    addLocalMessage(allmessage.get("message"));
                }
/////////////////////////////////////////////////////////////////////////////////////////////

//// Saving the message and replies in the database
                if(itemPos>10){
                    if(allmessage.get("from").equals(mCurrentUserId)){
                        message = allmessage.get("message");
                    }
                    else{
                        reply = allmessage.get("message");
                    }
                    if(message!=null&reply!=null) {
                        Replies_Database replies_database = new Replies_Database(ChatActivity.this);
                        replies_database.insert_Reply(message,reply);
                        message = null;
                        reply = null;
                    }
                }
////////////////////////////////////////////////////////////////////////////////////////////
//                if (itemPos == 1) {
//                    String mMessageKey = dataSnapshot.getKey();
//                    mLastKey = mMessageKey;
//                    mPrevKey = mMessageKey;
//                }

////// Auto Reply is enabled then
                if(autoreply){
                      if(!allmessage.get("from").equals(mCurrentUserId)) {
                          generate_auto_reply(allmessage.get("message"));
                      }
                }

                adapter.addToStart(messages, true);
                messagesList.setAdapter(adapter);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
//    public void gettotalmessages(){
//        DatabaseReference messageRef = FirebaseDatabase.getInstance().getReference().child("messages").child(mCurrentUserId).child(mChatUser);
//        messageRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for(DataSnapshot ds:snapshot.getChildren()){
//                    lastmessageid = ds.getKey();
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//    }



    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void generate_auto_reply(String message) {
        ///Checking if the reply is already is present in our database
        Replies_Database replies_database = new Replies_Database(this);
        Cursor all = replies_database.getAllReplies();
        while (all.moveToNext()) {
//            Getting the messages
            String message2 = all.getString(all.getColumnIndex("Message"));
            String reply = all.getString(all.getColumnIndex("Reply"));
//            if message is found then send the reply to the person
            if (message2.equals(message)) {
                replysent=true;
                String current_user_ref = "messages/" + mCurrentUserId + "/" + mChatUser;
                String chat_user_ref = "messages/" + mChatUser + "/" + mCurrentUserId;

                DatabaseReference user_message_push = FirebaseDatabase.getInstance().getReference().child("messages")
                        .child(mCurrentUserId).child(mChatUser).push();

                String push_id = user_message_push.getKey();

                Map messageMap = new HashMap();
                messageMap.put("message", reply);
                messageMap.put("seen", false);
                messageMap.put("type", "text");
                messageMap.put("time", new Date().toString());
                messageMap.put("from", mCurrentUserId);

                Map messageUserMap = new HashMap();
                messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
                messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);

                FirebaseDatabase.getInstance().getReference().updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {

                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError != null) {

                            Log.e("CHAT_ACTIVITY", "Cannot add message to database");
                        } else {
                        }

                    }
                });
                break;
            }
        }

// if the reply is not found then the message will be sent to Firebase Smart Reply Machine Learning Model and it will suggest three replies
        if(!replysent){
            FirebaseSmartReply smartReply = FirebaseNaturalLanguage.getInstance().getSmartReply();
            smartReply.suggestReplies(conversation)
                    .addOnSuccessListener(new OnSuccessListener<SmartReplySuggestionResult>() {
                        @Override
                        public void onSuccess(SmartReplySuggestionResult result) {
                            if (result.getStatus() == SmartReplySuggestionResult.STATUS_NOT_SUPPORTED_LANGUAGE) {
                                // The conversation's language isn't supported, so the
                                // the result doesn't contain any suggestions.
                            } else if (result.getStatus() == SmartReplySuggestionResult.STATUS_SUCCESS) {
                                ArrayList<String>allreplies = new ArrayList<>(); //three replies
                                for (SmartReplySuggestion suggestion : result.getSuggestions()) {
                                    String replyText = suggestion.getText();
                                    allreplies.add(replyText);
                                }
                                String current_user_ref = "messages/" + mCurrentUserId + "/" + mChatUser;
                                String chat_user_ref = "messages/" + mChatUser + "/" + mCurrentUserId;

                                DatabaseReference user_message_push = FirebaseDatabase.getInstance().getReference().child("messages")
                                        .child(mCurrentUserId).child(mChatUser).push();

                                String push_id = user_message_push.getKey();

                                Map messageMap = new HashMap();
                                messageMap.put("message", allreplies.get(getRandomNumberInRange(0,2)));
                                messageMap.put("seen", false);
                                messageMap.put("type", "text");
                                messageMap.put("time", new Date().toString());
                                messageMap.put("from", mCurrentUserId);

                                Map messageUserMap = new HashMap();
                                messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
                                messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);

                                FirebaseDatabase.getInstance().getReference().updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {

                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                        if (databaseError != null) {

                                            Log.e("CHAT_ACTIVITY", "Cannot add message to database");
                                        } else {
//                                        autoreply_sent = true;
//                                        Toast.makeText(ChatActivity.this, "Message sent", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });


                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Task failed with an exception
                            // ...
                        }
                    });

        }
    }
//    get a random number as we get three replies from the Machine Learning Model
    private static int getRandomNumberInRange(int min, int max) {

        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }
//////////////////////////////////////////////////////////////////////////////////

//Adding the message for the receiver side for ML Model
    private void addMessage(String text){
        conversation.add(
                FirebaseTextMessage.createForRemoteUser(
                        text, System.currentTimeMillis(), mChatUser));
    }
//Adding the message for the sender side for ML Model
    private void addLocalMessage(String text){
        conversation.add(
                FirebaseTextMessage.createForLocalUser(
                        text, System.currentTimeMillis()));
    }
    ///Generate Suggestion based on ML Model
    public void generatesuggestions_dialog(Context context){
        LayoutInflater li = LayoutInflater.from(context);
        final View promptsView = li.inflate(R.layout.suggestions_dialoguebox, null);

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);
        alertDialogBuilder.setView(promptsView);

        final TextView reply1 = promptsView.findViewById(R.id.reply1);
        final TextView reply2 = promptsView.findViewById(R.id.reply2);
        final TextView reply3 = promptsView.findViewById(R.id.reply3);

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        FirebaseSmartReply smartReply = FirebaseNaturalLanguage.getInstance().getSmartReply();
        smartReply.suggestReplies(conversation)
                .addOnSuccessListener(new OnSuccessListener<SmartReplySuggestionResult>() {
                    @Override
                    public void onSuccess(SmartReplySuggestionResult result) {
                        if (result.getStatus() == SmartReplySuggestionResult.STATUS_NOT_SUPPORTED_LANGUAGE) {
                            // The conversation's language isn't supported, so the
                            // the result doesn't contain any suggestions.
                        } else if (result.getStatus() == SmartReplySuggestionResult.STATUS_SUCCESS) {
                            ArrayList<String>allreplies = new ArrayList<>();
                            for (SmartReplySuggestion suggestion : result.getSuggestions()) {
                                String replyText = suggestion.getText();
                                allreplies.add(replyText);
                            }
                            reply1.setText(allreplies.get(0));
                            reply2.setText(allreplies.get(1));
                            reply3.setText(allreplies.get(2));

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Task failed with an exception
                        // ...
                    }
                });
    }

}