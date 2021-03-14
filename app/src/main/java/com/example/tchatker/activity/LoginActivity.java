package com.example.tchatker.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tchatker.R;
import com.example.tchatker.model.Account;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    Button btnLogin, btnNewAccount;
    EditText editUname, editPwd;
    TextView txtForgotAccount;
    FirebaseDatabase database;
    DatabaseReference reference;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        addControls();
        setEvents();
    }

    @Override
    protected void onStart() {
        super.onStart();
        sharedPreferences = this.getSharedPreferences("login", MODE_PRIVATE);
        if(sharedPreferences.contains("uname")) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        }
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("user");
    }

    public void addControls(){
        btnLogin = findViewById(R.id.btnLogin);
        btnNewAccount = findViewById(R.id.btnNewAccount);
        editUname = findViewById(R.id.editUname);
        editPwd = findViewById(R.id.editPwd);
        txtForgotAccount = findViewById(R.id.txtForgotPwd);
    }

    public void setEvents(){
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uname = editUname.getText().toString();
                String pwd = editPwd.getText().toString();

                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(!snapshot.hasChild(uname)){
                            editUname.setError("Not exists this username");
                            editUname.requestFocus();
                        }else{
                                Account account = snapshot.child("uname").getValue(Account.class);
                                if (!pwd.equals(account.getPwd())) {
                                    editPwd.setError("Incorrect password");
                                    editPwd.requestFocus();
                                }else{
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("uname", account.getUname());
                                    editor.commit();

                                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                    startActivity(intent);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Error", error.getMessage());
                    }
                });
            }
        });
        btnNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, CreateAccountActivity.class);
                startActivity(intent);
            }
        });

        txtForgotAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });
    }

}