package com.example.tchatker.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tchatker.R;
import com.example.tchatker.adapter.SearchFriendRecyclerViewAdapter;
import com.example.tchatker.model.Account;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchActivity extends AppCompatActivity {
    ImageView imgBack;
    EditText editSearch;

    RecyclerView rcyFriend;
    ArrayList<Account> accounts;
    SearchFriendRecyclerViewAdapter adapter;

    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        init();
        setEvent();
    }

    public void init(){
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("user");

        imgBack = findViewById(R.id.imgBack);
        editSearch = findViewById(R.id.editSearch);
        rcyFriend = findViewById(R.id.rcyFriend);

        accounts = new ArrayList<>();
        adapter = new SearchFriendRecyclerViewAdapter(this, accounts);
        rcyFriend.setAdapter(adapter);
        rcyFriend.setLayoutManager(new LinearLayoutManager(this));
        rcyFriend.setHasFixedSize(true);
    }

    public void setEvent(){
       imgBack.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               onBackPressed();
           }
       });

       editSearch.addTextChangedListener(new TextWatcher() {
           @Override
           public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

           @Override
           public void onTextChanged(CharSequence s, int start, int before, int count) {
               String str = editSearch.getText().toString();
               if(str.trim().length()==0) return;
               accounts.clear();
               reference.addListenerForSingleValueEvent(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot item : snapshot.getChildren()){
                            if(item.child("name").getValue(String.class).toLowerCase().indexOf(str.toLowerCase()) > -1){
                                Account account = item.getValue(Account.class);
                                accounts.add(account);
                            }
                        }
                        adapter.notifyDataSetChanged();
                   }

                   @Override
                   public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Error", error.getMessage());
                   }
               });
           }

           @Override
           public void afterTextChanged(Editable s) {}
       });
    }
}