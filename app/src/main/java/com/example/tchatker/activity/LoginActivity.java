package com.example.tchatker.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.tchatker.R;

public class LoginActivity extends AppCompatActivity {
    Button btnLogin, btnNewAccount;
    EditText editUname, editPwd;
    TextView txtForgotAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        addControls();
        setEvents();
    }

    public void addControls(){
        btnLogin = findViewById(R.id.btnLogin);
        btnNewAccount = findViewById(R.id.btnNewAccount);
        editUname = findViewById(R.id.editUname);
        editPwd = findViewById(R.id.editPwd);
        txtForgotAccount = findViewById(R.id.txtForgotPwd);
    }

    public void setEvents(){
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