package com.example.tchatker.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tchatker.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class OTPActivity extends AppCompatActivity {
    String phone, code, uname;
    Button btnEnter, btnCancel;
    EditText editOTP;
    TextView txtNotify;
    FirebaseAuth mAuth;
    int timeout = 60;
    SharedPreferences sharedPreferences;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_o_t_p);

        mAuth = ForgotPasswordActivity.mAuth;
        mAuth.setLanguageCode("vi");

        addControls();
        setEvents();
    }

    public void addControls(){
        btnEnter = findViewById(R.id.btnEnter);
        btnCancel = findViewById(R.id.btnCancel);
        editOTP = findViewById(R.id.editOTP);
        txtNotify = findViewById(R.id.txtNotify);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                timeout --;
                String str = "We just sent a number \n to your phone. Enter it \n before "+timeout+"s";
                if(timeout >= 0) {
                    txtNotify.setText(str);
                    handler.postDelayed(this, 1000);
                }
            }
        }, 1000);
    }

    public void setEvents(){
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OTPActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otp = editOTP.getText().toString();
                Log.d("OTP", otp+"\t"+code);
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(code, otp);
                signInWithPhoneAuthCredential(credential);
            }
        });
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            sharedPreferences = getSharedPreferences("login",MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("uname", uname);
                            editor.commit();

                            reference.child(uname).child("phoneNumber").setValue(phone);

                            Intent intent = new Intent(OTPActivity.this, HomeActivity.class);
                            startActivity(intent);
                        } else {
                            Log.d("Result","Fail!");
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        phone = intent.getStringExtra("phoneNumber");
        code = intent.getStringExtra("code");
        uname = intent.getStringExtra("uname");

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("user");
    }
}