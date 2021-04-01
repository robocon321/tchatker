package com.example.tchatker.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tchatker.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class ForgotPasswordActivity extends AppCompatActivity {
    Button btnOTP, btnCancel;
    EditText editPhone;
    public static FirebaseAuth mAuth;
    String uname;
    String phone;

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        phone = intent.getStringExtra("phoneNumber");
        uname = intent.getStringExtra("uname");
        if(phone != null){
            sendOTP(phone);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mAuth = FirebaseAuth.getInstance();
        mAuth.setLanguageCode("vi");

        addControls();
        setEvents();
    }

    public void addControls(){
        btnOTP = findViewById(R.id.btnOTP);
        btnCancel = findViewById(R.id.btnCancel);
        editPhone = findViewById(R.id.editPhone);
    }

    public void setEvents(){
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btnOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = editPhone.getText().toString();

                if(phone.isEmpty()){
                    editPhone.setError("Phone number is required");
                    editPhone.requestFocus();
                    return ;
                }else{
                    sendOTP(phone);
                }
            }
        });
    }

    public void sendOTP(String phone){
        PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                String code = phoneAuthCredential.getSmsCode();
                Log.d("code",code);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Log.e("Error",e.getMessage());
                editPhone.setError("Incorrect format phone number");
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                Intent intent = new Intent(ForgotPasswordActivity.this, OTPActivity.class);
                intent.putExtra("code", s);
                intent.putExtra("phoneNumber", phone);
                intent.putExtra("uname", uname);
                startActivity(intent);
            }
        };

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phone)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(ForgotPasswordActivity.this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

    }

}