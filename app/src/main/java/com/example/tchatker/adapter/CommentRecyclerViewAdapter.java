package com.example.tchatker.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tchatker.R;
import com.example.tchatker.model.Comment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentRecyclerViewAdapter extends RecyclerView.Adapter<CommentRecyclerViewAdapter.ViewHolder> {
    ArrayList<Comment> comments;
    Context context;
    FirebaseDatabase database;
    DatabaseReference reference;

    public CommentRecyclerViewAdapter(ArrayList<Comment> comments, Context context) {
        this.comments = comments;
        this.context = context;

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("user");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.layout_item_comment, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.txtComment.setText(comment.getContent());
        holder.txtTime.setText(comment.getTime().toNow());
        reference.orderByChild("uname").equalTo(comment.getUname()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot itemSnapshot: snapshot.getChildren()){
                    Picasso.get().load(itemSnapshot.child("avatar").getValue(String.class)).into(holder.imgUser);
                    holder.txtUser.setText(itemSnapshot.child("name").getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Error", error.getMessage());
            }
        });
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView imgUser;
        TextView txtUser, txtTime, txtComment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgUser = itemView.findViewById(R.id.imgUser);
            txtUser = itemView.findViewById(R.id.txtUser);
            txtTime = itemView.findViewById(R.id.txtTime);
            txtComment = itemView.findViewById(R.id.txtComment);
        }
    }
}
