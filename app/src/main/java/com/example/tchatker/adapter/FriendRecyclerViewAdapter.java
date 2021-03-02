package com.example.tchatker.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tchatker.R;
import com.example.tchatker.activity.ChatMessageActivity;
import com.example.tchatker.model.Account;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FriendRecyclerViewAdapter extends RecyclerView.Adapter<FriendRecyclerViewAdapter.ViewHolder> {
    ArrayList<Account> accounts;
    Context context;

    public FriendRecyclerViewAdapter(ArrayList<Account> accounts, Context context){
        this.accounts = accounts;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inlater = LayoutInflater.from(context);
        View view = inlater.inflate(R.layout.layout_item_friend_directory, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Account account = accounts.get(position);
        String avatar = account.getAvatar();
        String name = account.getName();
        String status = account.getStatus();

        Picasso.get().load(account.getAvatar()).into(holder.imgUser);
        holder.txtNameUser.setText(account.getName());
        if (status.equals("online")){
            holder.txtStatusUser.setText("Đang hoạt động");
            holder.txtStatusUser.setCompoundDrawablesWithIntrinsicBounds(R.drawable.online, 0,0,0);
        }
        else {
            holder.txtStatusUser.setText("Không hoạt động");
            holder.txtStatusUser.setCompoundDrawablesWithIntrinsicBounds(R.drawable.offline,0,0,0);
        }

        // setEvent
        holder.imgCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Calling...", Toast.LENGTH_SHORT).show();
            }
        });

        holder.imgVideoCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Video calling...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return accounts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgUser, imgCall, imgVideoCall;
        TextView txtNameUser;
        TextView txtStatusUser;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgUser = itemView.findViewById(R.id.imgUser);
            txtNameUser = itemView.findViewById(R.id.txtNameUser);
            txtStatusUser = itemView.findViewById(R.id.txtStatusUser);
            imgCall = itemView.findViewById(R.id.imgCall);
            imgVideoCall = itemView.findViewById(R.id.imgVideoCall);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ChatMessageActivity.class);
                    intent.putExtra("account", accounts.get(getAdapterPosition()));
                    context.startActivity(intent);
                }
            });
        }
    }
}
