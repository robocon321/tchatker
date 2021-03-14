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
import com.google.android.flexbox.AlignContent;
import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayout;
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

        reference.child(itemNews.getUname()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    holder.txtName.setText(snapshot.child("name").getValue(String.class));
                    Picasso.get().load(snapshot.child("avatar").getValue(String.class)).into(holder.imgAvatar);
                    Time time = itemNews.getTime();
                    holder.txtTime.setText(time.toNow());

                    reference.child(itemNews.getUname()).child("news").child(itemNews.getId()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            // SnapshotLike
                            DataSnapshot snapshotLike = snapshot.child("likes");
                            holder.txtLike.setText(snapshotLike.getChildrenCount()+"");
                            if(snapshotLike.hasChild(uname)){
                                holder.txtLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.liked, 0, 0, 0);
                            }else{
                                holder.txtLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like, 0, 0, 0);
                            }
                            holder.txtLike.setText(snapshotLike.getChildrenCount()+"");

                            // SnapshotComment
                            DataSnapshot snapshotComment = snapshot.child("comments");
                            holder.txtComment.setText(snapshotComment.getChildrenCount()+"");

                            // set for flex content type
                            String typeContent = itemNews.getTypeContent();
                            if(typeContent.equals("IMAGE")){
                                FlexboxLayout contentLayout = new FlexboxLayout(context);
                                contentLayout.setFlexWrap(FlexWrap.WRAP);
                                contentLayout.setLayoutParams(new FlexboxLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                contentLayout.setAlignContent(AlignContent.FLEX_START);
                                contentLayout.setAlignItems(AlignItems.FLEX_START);

                                for(DataSnapshot snapshotImage : snapshot.child("images").getChildren()){
                                    ImageView imgView = new ImageView(context);
                                    Picasso.get().load(snapshotImage.getValue(String.class)).into(imgView);
                                    contentLayout.addView(imgView);
                                }

                                holder.layoutMain.addView(contentLayout);
                            }else {
                                // Nothing else
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("Error", error.getMessage());
                        }
                    });
                }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Error", error.getMessage());
            }
        });

        holder.txtText.setText(itemNews.getText());
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

            txtLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    News itemNews = news.get(getAdapterPosition());
                    DatabaseReference referenceLike = reference.child(itemNews.getUname()).child("news").child(itemNews.getId()).child("likes");
                    referenceLike.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
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
