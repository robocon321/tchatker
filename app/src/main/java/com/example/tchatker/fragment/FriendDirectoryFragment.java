package com.example.tchatker.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tchatker.R;
import com.example.tchatker.activity.HomeActivity;
import com.example.tchatker.activity.LoginActivity;
import com.example.tchatker.adapter.FriendRecyclerViewAdapter;
import com.example.tchatker.model.Account;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FriendDirectoryFragment extends Fragment {
    RecyclerView recyclerView;
    FriendRecyclerViewAdapter adapter;
    ArrayList<Account> accounts;
    FirebaseDatabase database;
    DatabaseReference reference;
    SharedPreferences sharedPreferences;
    String uname;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_directory_friend, null);
        init(view);
        return view;
    }

    public void init(View view){
        accounts = new ArrayList<>();
        FriendRecyclerViewAdapter adapter = new FriendRecyclerViewAdapter(accounts, getActivity());

        sharedPreferences = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
        uname = sharedPreferences.getString("uname", "robocon321");

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("user");

        recyclerView = view.findViewById(R.id.recyclerViewFriend);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        reference.orderByChild("uname").equalTo(uname).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot item : snapshot.getChildren()){
                    for(DataSnapshot itemFriend : item.child("friends").getChildren()){
                        String unameFriend = itemFriend.getValue().toString();

                        reference.orderByChild("uname").equalTo(unameFriend).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshotFriend) {
                                for (DataSnapshot item : snapshotFriend.getChildren()){
                                    Account account = item.getValue(Account.class);
                                    accounts.add(account);
                                }
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError errorFriend) {
                                Log.d("Error", errorFriend.getMessage());
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Error", error.getMessage());
            }
        });

    }

}
