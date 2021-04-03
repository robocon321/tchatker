package com.example.tchatker.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.tchatker.R;
import com.example.tchatker.adapter.NewsRecyclerViewAdapter;
import com.example.tchatker.model.Account;
import com.example.tchatker.model.News;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DiaryActivity extends AppCompatActivity {
    Button btnIntro, btnDiary;
    ImageView imgAvatar, imgBackground;

    RecyclerView rcyNews;
    NewsRecyclerViewAdapter adapter;
    ArrayList<News> news;

    FirebaseDatabase database;
    DatabaseReference reference;

    String unameAccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);
        init();
        setEvents();
    }

    public void init(){
        unameAccess = getIntent().getStringExtra("uname");

        btnIntro = findViewById(R.id.btnIntro);
        btnDiary = findViewById(R.id.btnDiary);

        imgAvatar = findViewById(R.id.imgAvatar);
        imgBackground = findViewById(R.id.imgBackground);

        rcyNews = findViewById(R.id.rcyNews);
        news = new ArrayList<>();
        adapter = new NewsRecyclerViewAdapter(news, DiaryActivity.this);
        rcyNews.setLayoutManager(new LinearLayoutManager(this));
        rcyNews.setHasFixedSize(true);
        rcyNews.setAdapter(adapter);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("user");

        reference.child(unameAccess).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Account account = snapshot.getValue(Account.class);

                String avatar = account.getAvatar();
                if(avatar != null){
                    Picasso.get().load(account.getAvatar()).into(imgAvatar);
                }

                String background = account.getBackground();
                if(background != null){
                    Picasso.get().load(snapshot.child("background").getValue(String.class)).into(imgBackground);
                }

                news.clear();

                for(DataSnapshot item : snapshot.child("news").getChildren()){
                    News itemNews = item.getValue(News.class);
                    itemNews.setId(item.getKey());
                    itemNews.setUname(unameAccess);
                    news.add(itemNews);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Error", error.getMessage());
            }
        });
    }

    public void setEvents(){
        btnIntro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DiaryActivity.this, ProfileActivity.class);
                intent.putExtra("uname", unameAccess);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
}