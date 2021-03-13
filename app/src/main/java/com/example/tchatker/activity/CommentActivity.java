package com.example.tchatker.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.tchatker.R;
import com.example.tchatker.adapter.CommentRecyclerViewAdapter;
import com.example.tchatker.model.Comment;
import com.example.tchatker.model.News;
import com.example.tchatker.model.Time;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentActivity extends AppCompatActivity {
    CircleImageView imgAvatar;
    ImageView imgComment;
    TextView txtName, txtTime,txtText;
    EditText editComment;

    Toolbar toolbar;

    FirebaseDatabase database;
    DatabaseReference reference;

    SharedPreferences sharedPreferences;
    String uname;
    News itemNews;

    RecyclerView recyclerViewComment;
    ArrayList<Comment> comments;
    CommentRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        init();
        setEvents();
    }

    public void init(){
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("user");

        sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
        uname = sharedPreferences.getString("uname", "robocon321");

        itemNews = (News) getIntent().getSerializableExtra("itemNews");

        imgAvatar = findViewById(R.id.imgAvatar);
        imgComment = findViewById(R.id.imgComment);
        txtName = findViewById(R.id.txtName);
        txtTime = findViewById(R.id.txtTime);
        txtText = findViewById(R.id.txtText);
        recyclerViewComment = findViewById(R.id.recyclerViewComment);
        editComment = findViewById(R.id.editComment);
        toolbar = findViewById(R.id.toolbar);

        comments = new ArrayList<>();
        adapter = new CommentRecyclerViewAdapter(comments, this);

        recyclerViewComment.setHasFixedSize(true);
        recyclerViewComment.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewComment.setAdapter(adapter);

        reference.orderByChild("uname").equalTo(itemNews.getUname()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot itemSnapshot: snapshot.getChildren()){
                    String avatar = itemSnapshot.child("avatar").getValue(String.class);
                    String name = itemSnapshot.child("name").getValue(String.class);

                    Picasso.get().load(avatar).into(imgAvatar);
                    txtName.setText(name);
                    txtTime.setText(itemNews.getTime().toNow());

                    reference.child(itemSnapshot.getKey()).child("news").child(itemNews.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String text = snapshot.child("text").getValue(String.class);
                            txtText.setText(text);
                            comments.clear();
                            for(DataSnapshot commentSnapshot : snapshot.child("comments").getChildren()){
                                Comment comment = commentSnapshot.getValue(Comment.class);
                                comments.add(comment);
                            }
                            adapter.notifyDataSetChanged();
                            toolbar.setTitle("Comment("+comments.size()+")");
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("Error", error.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Error", error.getMessage());
            }
        });
    }

    public void setEvents(){
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        imgComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = editComment.getText().toString();
                Comment comment = new Comment(uname, content, new Time());
                reference.orderByChild("uname").equalTo(itemNews.getUname()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot itemSnapshot : snapshot.getChildren()){
                            reference.child(itemSnapshot.getKey()).child("news").child(itemNews.getId()).child("comments").push().setValue(comment);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Error", error.getMessage());
                    }
                });
            }
        });
    }
}