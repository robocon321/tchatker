package com.example.tchatker.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tchatker.R;
import com.example.tchatker.model.Account;
import com.example.tchatker.model.Message;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MessageRecyclerViewAdapter extends RecyclerView.Adapter<MessageRecyclerViewAdapter.ViewHolder> {
    final private int TYPE_TEXT_VIEW = 0;
    final private int TYPE_IMAGE_VIEW = 2;
    final private int TYPE_EMOJI_VIEW = 4;
    final private int TYPE_FILE_VIEW = 8;
    final private int SENDER = 16;
    final private int RECEIVER = 32;

    final FirebaseDatabase database;
    final DatabaseReference reference;
    final String uname;


    private ArrayList<Message> messages;
    private Context context;

    public MessageRecyclerViewAdapter(ArrayList<Message> messages, Context context) {
        this.messages = messages;
        this.context = context;

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("user");

        SharedPreferences sharedPreferences = context.getSharedPreferences("login", Context.MODE_PRIVATE);
        uname = sharedPreferences.getString("uname","robocon321");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view;
        if(viewType >= RECEIVER){
            view = layoutInflater.inflate(R.layout.layout_message_left, parent, false);
            viewType -= RECEIVER;
        }else{
            view = layoutInflater.inflate(R.layout.layout_message_right, parent, false);
            viewType -= SENDER;
        }


        ViewHolder holder = new ViewHolder(view, viewType);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message message = messages.get(position);
        if(message.getStatus())
            holder.txtStatus.setText("Đã xem");
        else
            holder.txtStatus.setText("Chưa xem");

        reference.orderByChild("uname").equalTo(message.getSender()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot item : snapshot.getChildren()){
                    Account account = item.getValue(Account.class);
                    Picasso.get().load(account.getAvatar()).into(holder.imgAvatar);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Error", error.getMessage());
            }
        });

        String content = message.getContent();
        View view;

        switch (holder.viewType){
            case TYPE_TEXT_VIEW:
                view = new TextView(context);
                ((TextView) view).setText(content);
                break;
            case TYPE_IMAGE_VIEW:
                view = new ImageView(context);
                Picasso.get().load(content).into((ImageView) view);
                break;
            case TYPE_FILE_VIEW:
                view = new TextView(context);
                ((TextView) view).setText(content);
                break;
            case TYPE_EMOJI_VIEW:
                view = new TextView(context);
                ((TextView) view).setText("TYPE_VIDEO_VIEW");
                break;
            default:
                view = new TextView(context);
                ((TextView) view).setText("DEFAULT");
                break;
        }

        holder.layoutAddComponent.addView(view,
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        String typeContent = message.getTypeContent();
        String sender = message.getSender();
        int result = 0;
        if(sender.equals(uname)){
            result += SENDER;
        }else{
            result += RECEIVER;
        }

        switch (typeContent){
            case "String":
                return result += TYPE_TEXT_VIEW;
            case "Image":
                return result += TYPE_IMAGE_VIEW;
            case "Video":
                return result += TYPE_EMOJI_VIEW;
            case "File":
                return result += TYPE_FILE_VIEW;
            default:
                return -1;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvatar;
        TextView txtStatus;
        RelativeLayout layoutAddComponent;
        int viewType;

        public ViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            layoutAddComponent = itemView.findViewById(R.id.layoutAddComponent);
            this.viewType = viewType;
        }
    }
}
