package com.example.tchatker.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tchatker.R;
import com.example.tchatker.model.Account;
import com.squareup.picasso.Picasso;

public class SendCallAudioActivity extends AppCompatActivity {
    ImageView imgUser, imgClose;
    TextView txtUser;
    Account account;
    String uname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_call_audio);
        init();
        setEvents();
    }

    public void init(){
        imgUser = findViewById(R.id.imgUser);
        imgClose = findViewById(R.id.imgClose);
        txtUser = findViewById(R.id.txtUser);

        Intent intent = getIntent();
        account = (Account) intent.getSerializableExtra("account");

        SharedPreferences sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
        uname = sharedPreferences.getString("uname", "robocon321");

        txtUser.setText(account.getName());
        Picasso.get().load(account.getAvatar()).into(imgUser);
    }

    public void setEvents(){

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SendCallAudioActivity.this, "Close", Toast.LENGTH_SHORT).show();
            }
        });
    }
}