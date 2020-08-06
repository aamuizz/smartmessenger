package com.amtechnology.smartreplyassistant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Verify_phone extends AppCompatActivity {
    private FirebaseAuth mAuth;
    EditText verify_code;
    private String mVerificationId;
    Button login_btn;
    TextView toolbar_verify,waiting_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone);
        toolbar_verify = findViewById(R.id.toolbar_verify_text);
        waiting_text = findViewById(R.id.waiting_text);

        mAuth = FirebaseAuth.getInstance();
        verify_code = findViewById(R.id.verify_Code);
        login_btn = findViewById(R.id.login_btn);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = verify_code.getText().toString().trim();
                if (code.isEmpty() || code.length() < 6) {
                    verify_code.setError("Enter valid code");
                    verify_code.requestFocus();
                    return;
                }

                //verifying the code entered manually
                verifyVerificationCode(code);
            }
        });
        Bundle bundle = getIntent().getExtras();
        String phone_no = bundle.getString("phoneno");
        sendVerificationCode(phone_no);
        toolbar_verify.setText("Verify "+phone_no);
        waiting_text.setText("Waiting to Automatically Detect an Sms to "+phone_no);
    }

    private void sendVerificationCode(String mobile) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                mobile,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(Verify_phone.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }


        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();

            //sometime the code is not detected automatically
            //in this case the code will be null
            //so user has to manually enter the code
            if (code != null) {
                verify_code.setText(code);
                //verifying the code
                verifyVerificationCode(code);
            }

        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            mVerificationId = s;

        }
    };

    private void verifyVerificationCode(String otp) {
        //creating the credential
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otp);

        //signing the user
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(Verify_phone.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //verification successful we will start the profile activity

                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                            FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                            final String uid=current_user.getUid();
                            String token_id = FirebaseInstanceId.getInstance().getToken();
                            Map userMap=new HashMap();
                            final Bundle bundle = getIntent().getExtras();
                            userMap.put("name",bundle.getString("name"));
                            userMap.put("phoneno",bundle.getString("phoneno"));
                            userMap.put("token",token_id);
                            SharedPreferences.Editor sharedPreferences = getApplicationContext().getSharedPreferences("autologin", Context.MODE_PRIVATE).edit();
                            sharedPreferences.putString("phoneno",bundle.getString("phoneno"));
                            sharedPreferences.putString("name",bundle.getString("name"));
                            sharedPreferences.putString("uid",uid);
                            sharedPreferences.putBoolean("login",true);
                            sharedPreferences.apply();

                            databaseReference.child("users").child(uid).setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task1) {
                                    if (task1.isSuccessful()) {
                                        Intent intent = new Intent(Verify_phone.this, MainActivity.class);
                                        intent.putExtra("uid",uid);
                                        intent.putExtra("name",bundle.getString("name"));
                                        intent.putExtra("phoneno",bundle.getString("phoneno"));
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    }
                                }
                            } );

                        } else {


                            String message = "Something is wrong, we will fix it soon.";
                            Snackbar snackbar = Snackbar.make(findViewById(R.id.parent), message, Snackbar.LENGTH_LONG);
                            snackbar.setAction("Dismiss", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                }
                            });
                            snackbar.show();

                            }


                        }

                });
    }
}