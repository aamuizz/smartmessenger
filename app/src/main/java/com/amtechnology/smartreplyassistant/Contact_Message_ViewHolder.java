package com.amtechnology.smartreplyassistant;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

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
