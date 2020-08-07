package com.amtechnology.smartreplyassistant.AllContacts;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amtechnology.smartreplyassistant.ChatActivity.ChatActivity;
import com.amtechnology.smartreplyassistant.R;

import java.util.ArrayList;

public class Contact_Message_Adapter extends RecyclerView.Adapter<Contact_Message_ViewHolder> {
    private ArrayList<Contact>allcontacts;
    private Context mcontext;

    public Contact_Message_Adapter(ArrayList<Contact> allcontacts, Context mcontext) {
        this.allcontacts = allcontacts;
        this.mcontext = mcontext;
    }

    @NonNull
    @Override
    public Contact_Message_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_contact_message,parent,false);
        return new Contact_Message_ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Contact_Message_ViewHolder holder, final int position) {

//       It will show all the users
        holder.name.setText(allcontacts.get(position).getName());
        holder.phone_no.setText(allcontacts.get(position).getPhoneno());
        holder.contact_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mcontext, ChatActivity.class);
                intent.putExtra("uid", allcontacts.get(position).getUid());
                intent.putExtra("name",allcontacts.get(position).getName());
                intent.putExtra("phoneno",allcontacts.get(position).getPhoneno());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mcontext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return allcontacts.size();
    }
}
