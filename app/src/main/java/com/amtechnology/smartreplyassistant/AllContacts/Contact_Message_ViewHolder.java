package com.amtechnology.smartreplyassistant.AllContacts;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amtechnology.smartreplyassistant.R;
//    View Holder for RecyclerView
public class Contact_Message_ViewHolder extends RecyclerView.ViewHolder {

    TextView name,phone_no;
    LinearLayout contact_layout;
    public Contact_Message_ViewHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.Contact_Name);
        contact_layout = itemView.findViewById(R.id.contact_layout);
        phone_no = itemView.findViewById(R.id.phone_no);

    }
}
