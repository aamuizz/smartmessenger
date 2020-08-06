package com.amtechnology.smartreplyassistant;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class login extends AppCompatActivity {
    EditText phone_no,name;
    Button verify_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.toolbar_login);
        phone_no = findViewById(R.id.phone_no);
        name = findViewById(R.id.name);
        verify_btn = findViewById(R.id.verify_btn);

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("autologin", Context.MODE_PRIVATE);
        if(sharedPreferences.getBoolean("login",false)){
            Intent intent = new Intent(login.this,MainActivity.class);
            intent.putExtra("uid",sharedPreferences.getString("uid",null));
            intent.putExtra("name",sharedPreferences.getString("name",null));
            intent.putExtra("phoneno",sharedPreferences.getString("phoneno",null));
            startActivity(intent);


        }

        verify_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = phone_no.getText().toString().trim();

                if(phone.isEmpty()){
                    phone_no.setError("Enter a valid mobile");
                    phone_no.requestFocus();
                    return;
                }
                Intent intent = new Intent(login.this,Verify_phone.class);
                intent.putExtra("phoneno",phone);
                intent.putExtra("name",name.getText().toString());
                startActivity(intent);
            }
        });
    }
}