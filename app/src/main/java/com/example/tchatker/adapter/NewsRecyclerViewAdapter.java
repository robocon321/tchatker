package com.example.tchatker.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tchatker.R;
import com.example.tchatker.activity.CommentActivity;
import com.example.tchatker.model.Like;
import com.example.tchatker.model.News;
import com.example.tchatker.model.Time;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NewsRecyclerViewAdapter extends RecyclerView.Adapter<NewsRecyclerViewAdapter.ViewHolder> {
    ArrayList<News> news;
    Context context;
    FirebaseDatabase database;
    DatabaseReference reference;
    SharedPreferences sharedPreferences;
    String uname;

    public NewsRecyclerViewAdapter(ArrayList<News> news, Context context) {
        this.news = news;
        this.context = context;

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("user");
        sharedPreferences = context.getSharedPreferences("login", Context.MODE_PRIVATE);
        uname = sharedPreferences.getString("uname", "robocon321");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.layout_item_news, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        News itemNews = news.get(position);

        reference.orderByChild("uname").equalTo(itemNews.getUname()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot item: snapshot.getChildren()){
                    holder.txtName.setText(item.child("name").getValue(String.class));
                    Picasso.get().load(item.child("avatar").getValue(String.class)).into(holder.imgAvatar);
                    Time time = itemNews.getTime();
                    holder.txtTime.setText(time.toNow());

                    reference.child(item.getKey()).child("news").child(itemNews.getId()).child("likes").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.hasChild(uname)){
                                holder.txtLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.liked, 0, 0, 0);
                            }else{
                                holder.txtLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like, 0, 0, 0);
                            }
                            holder.txtLike.setText(snapshot.getChildrenCount()+"");
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("Error", error.getMessage());
                        }
                    });

                    reference.child(item.getKey()).child("news").child(itemNews.getId()).child("comments").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            holder.txtComment.setText(snapshot.getChildrenCount()+"");
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
                Log.e("Error", error.getMessage());
            }
        });

        holder.txtText.setText(itemNews.getText());
        holder.txtText.setTextColor(Color.parseColor(itemNews.getNewsStyle().getColor()));
        holder.txtLike.setText(itemNews.getLikes().size()+"");
        holder.txtComment.setText(itemNews.getComments().size()+"");
        holder.layoutMain.setBackgroundColor(Color.parseColor(itemNews.getNewsStyle().getBackground()));
    }

    @Override
    public int getItemCount() {
        return news.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvatar;
        TextView txtName, txtTime, txtText, txtLike, txtComment;
        RelativeLayout layoutMain;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            txtName = itemView.findViewById(R.id.txtName);
            txtTime = itemView.findViewById(R.id.txtTime);
            txtLike = itemView.findViewById(R.id.txtLike);
            txtComment = itemView.findViewById(R.id.txtComment);
            txtText = itemView.findViewById(R.id.txtText);
            layoutMain = itemView.findViewById(R.id.layoutMain);

            Log.d("AAAA", getAdapterPosition()+"");

            txtLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    News itemNews = news.get(getAdapterPosition());
                    Like like = new Like(uname, new Time());
                    reference.orderByChild("uname").equalTo(itemNews.getUname()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot item : snapshot.getChildren()){
                                reference.child(item.getKey()).child("news").child(itemNews.getId()).child("likes").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        DatabaseReference referenceLike = reference.child(item.getKey()).child("news").child(itemNews.getId()).child("likes");
                                        if(snapshot.hasChild(uname)){
                                            referenceLike.removeValue();
                                        }else{
                                            referenceLike.child(uname).setValue(new Time());
                                        }
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
                            Log.e("Error", error.getMessage());
                        }
                    });
                }
            });

            txtComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    News itemNews = news.get(getAdapterPosition());

                    Intent intent = new Intent(context, CommentActivity.class);
                    intent.putExtra("itemNews", itemNews);
                    context.startActivity(intent);
                }
            });
        }
    }
}
