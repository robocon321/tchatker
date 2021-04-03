package com.example.tchatker.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tchatker.R;
import com.example.tchatker.model.Account;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {
    String unameAccess;
    FirebaseDatabase database;
    DatabaseReference reference;

    Button btnIntro, btnDiary;
    TextView txtName, txtBirthday, txtPhone, txtEmail;
    ImageView imgAvatar, imgBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        init();
        setEvents();
    }

    public void init(){
        btnIntro = findViewById(R.id.btnIntro);
        btnDiary = findViewById(R.id.btnDiary);

        imgAvatar = findViewById(R.id.imgAvatar);
        imgBackground = findViewById(R.id.imgBackground);

        txtName = findViewById(R.id.txtName);
        txtBirthday = findViewById(R.id.txtBirthday);
        txtPhone = findViewById(R.id.txtPhone);
        txtEmail = findViewById(R.id.txtEmail);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("user");

        Intent intent = getIntent();
        unameAccess = intent.getStringExtra("uname");

        reference.child(unameAccess).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Account account = snapshot.getValue(Account.class);
                txtName.setText(account.getName());
                txtBirthday.setText(account.getBirthday().toString());
                txtPhone.setText(account.getPhoneNumber());
                txtEmail.setText(account.getEmail());

                Picasso.get().load(account.getAvatar()).into(imgAvatar);
                Picasso.get().load(account.getBackground()).into(imgBackground);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Error", error.getMessage());
            }
        });
    }

    public void setEvents(){
        btnDiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}