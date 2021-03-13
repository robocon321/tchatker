package com.example.tchatker.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tchatker.R;
import com.example.tchatker.adapter.NewsRecyclerViewAdapter;
import com.example.tchatker.model.Comment;
import com.example.tchatker.model.Like;
import com.example.tchatker.model.Message;
import com.example.tchatker.model.News;
import com.example.tchatker.model.NewsStyle;
import com.example.tchatker.model.Time;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import static android.app.Activity.RESULT_OK;

public class NewsFragment extends Fragment {
    ImageView imgUser;
    EditText editText;
    Button btnPostImage, btnPostVideo, btnBackground, btnPost;
    RecyclerView recyclerViewNews;
    ArrayList<News> news;
    NewsRecyclerViewAdapter adapter;
    int REQ_GET_MULTI_IMG = 100;
    int REQ_GET_VIDEO = 101;

    FirebaseDatabase database;
    DatabaseReference databaseReference;
    FirebaseStorage storage;
    StorageReference storageReference;

    SharedPreferences sharedPreferences;
    String uname;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, null);
        init(view);
        setEvents(view);
        return view;
   }

    public void init(View view){
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("user");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference("image");

        sharedPreferences = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
        uname = sharedPreferences.getString("uname","robocon321");

        imgUser = view.findViewById(R.id.imgUser);
        editText = view.findViewById(R.id.editText);
        btnPostImage = view.findViewById(R.id.btnPostImage);
        btnPostVideo = view.findViewById(R.id.btnPostVideo);
        btnBackground = view.findViewById(R.id.btnPostBackground);
        recyclerViewNews = view.findViewById(R.id.recyclerViewNews);
        btnPost = view.findViewById(R.id.btnPost);

        news = new ArrayList<>();
        adapter = new NewsRecyclerViewAdapter(news, getContext());
        recyclerViewNews.setAdapter(adapter);
        recyclerViewNews.setHasFixedSize(true);
        recyclerViewNews.setLayoutManager(new LinearLayoutManager(getContext()));

        databaseReference.orderByChild("uname").equalTo(uname).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                news.clear();
                for(DataSnapshot itemSnapshot:snapshot.getChildren()){
                    // My news
                    for(DataSnapshot newsSnapshot:itemSnapshot.child("news").getChildren()){
                        News itemNews = newsSnapshot.getValue(News.class);
                        itemNews.setId(newsSnapshot.getKey());
                        itemNews.setUname(itemSnapshot.child("uname").getValue(String.class));
                        itemNews.setLikes(new ArrayList<Like>());
                        itemNews.setComments(new ArrayList<Comment>());
                        news.add(itemNews);
                    }
                    // Friend news
                    for(DataSnapshot friendSnapshot: itemSnapshot.child("friends").getChildren()){
                        String friendUname = friendSnapshot.getValue(String.class);
                        databaseReference.orderByChild("uname").equalTo(friendUname).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot itemSnapshot : snapshot.getChildren()){
                                    for(DataSnapshot newsSnapshot : itemSnapshot.child("news").getChildren()){
                                        News itemNews = newsSnapshot.getValue(News.class);
                                        itemNews.setId(newsSnapshot.getKey());
                                        itemNews.setUname(itemSnapshot.child("uname").getValue(String.class));
                                        itemNews.setLikes(new ArrayList<Like>());
                                        itemNews.setComments(new ArrayList<Comment>());
                                        news.add(itemNews);
                                    }
                                }
                                Collections.sort(news);
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e("Error", error.getMessage());
                            }
                        });
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Error", error.getMessage());
            }
        });

        adapter.notifyDataSetChanged();
    }

    public void setEvents(View view){
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = editText.getText().toString();
                Time time = new Time();
                String content = "";
                String typeContent = "normal";
                NewsStyle newsStyle = new NewsStyle();
                News news = new News(time, text, content, typeContent, newsStyle);

                databaseReference.orderByChild("uname").equalTo(uname).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot itemSnapShot: snapshot.getChildren()){
                            databaseReference.child(itemSnapShot.getKey()).child("news").push().setValue(news);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Error", error.getMessage());
                    }
                });
            }
        });

        btnPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"), REQ_GET_MULTI_IMG);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == REQ_GET_MULTI_IMG && resultCode == RESULT_OK && data!=null){
            if(data.getClipData() != null){

                int count = data.getClipData().getItemCount();

                for (int i=0; i<count; i++){

                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    try {
                        InputStream inputStream = getActivity().getContentResolver().openInputStream(imageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            else if(data.getData() != null){
                Uri imgUri = data.getData();
                try {
                    InputStream inputStream = getActivity().getContentResolver().openInputStream(imgUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public void uploadImageBitmap(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        Calendar calendar = Calendar.getInstance();

        UploadTask uploadTask = storageReference.child("image").child("image" + calendar.getTimeInMillis()).putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("Error", exception.getMessage());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task task = taskSnapshot.getMetadata().getReference().getDownloadUrl();

                task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        if (task.isSuccessful()) {
                            String path = uri.toString();
                            String uname = sharedPreferences.getString("uname", "robocon321");
                            Log.d("AAA", path);
                        } else
                            Log.e("Error", "Uncompleted!");
                    }
                });
            }
        });
    }
}
