package com.example.tchatker.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tchatker.R;
import com.example.tchatker.model.Account;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class SearchFriendRecyclerViewAdapter extends RecyclerView.Adapter<SearchFriendRecyclerViewAdapter.ViewHolder> {
    Context context;
    ArrayList<Account> accounts;

    FirebaseDatabase database;
    DatabaseReference reference;
    ArrayList<String> friends;

    String uname;

    public SearchFriendRecyclerViewAdapter(Context context, ArrayList<Account> accounts){
        this.context = context;
        this.accounts = accounts;

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("user");
        friends = new ArrayList<>();

        SharedPreferences sharedPreferences = context.getSharedPreferences("login", Context.MODE_PRIVATE);
        uname = sharedPreferences.getString("uname","robocon321");
        reference.child(uname).child("friends").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot item : snapshot.getChildren()){
                    friends.add(item.getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Error", error.getMessage());
            }
        });
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.layout_item_friend_search, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Account account = accounts.get(position);
        Picasso.get().load(account.getAvatar()).into(holder.imgUser);
        holder.txtNameUser.setText(account.getName());

        reference.child(account.getUname()).child("friends").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = 0;
                for(DataSnapshot item : snapshot.getChildren()){
                    if(friends.contains(item.getValue(String.class))){
                        count ++;
                    }
                }

                holder.txtManualFriend.setText(count+" bạn chung");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Error", error.getMessage());
            }
        });

        if(friends.contains(account.getUname())){
            holder.imgAddFriend.setImageResource(R.drawable.remove_friend);
        }else{
            holder.imgAddFriend.setImageResource(R.drawable.add_friend);
        }

        holder.imgAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reference.child(uname).child("friends").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot item : snapshot.getChildren()){
                            String friend = item.getValue(String.class);
                            if(friend.equals(account.getUname())){
                                reference.child(uname).child("friends").child(item.getKey()).removeValue();
                                holder.imgAddFriend.setImageResource(R.drawable.add_friend);
                                return ;
                            }
                        }
                        reference.child(uname).child("friends").push().setValue(account.getUname());
                        reference.child(account.getUname()).child("friends").push().setValue(uname);
                        holder.imgAddFriend.setImageResource(R.drawable.remove_friend);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Error", error.getMessage());
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return accounts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgUser, imgAddFriend;
        TextView txtNameUser, txtManualFriend;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgUser = itemView.findViewById(R.id.imgUser);
            imgAddFriend = itemView.findViewById(R.id.imgAddFriend);
            txtNameUser = itemView.findViewById(R.id.txtNameUser);
            txtManualFriend = itemView.findViewById(R.id.txtManualFriend);
        }
    }
}
