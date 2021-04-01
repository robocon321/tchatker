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
    String newsId;

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
        storageReference = storage.getReferenceFromUrl("gs://tchatker.appspot.com");

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

        databaseReference.child(uname).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                news.clear();
                    // My news
                    for(DataSnapshot newsSnapshot:snapshot.child("news").getChildren()){
                        News itemNews = newsSnapshot.getValue(News.class);
                        itemNews.setId(newsSnapshot.getKey());
                        itemNews.setUname(snapshot.child("uname").getValue(String.class));
                        Log.d("EEE", itemNews.toString());
                        news.add(itemNews);
                        adapter.notifyDataSetChanged();
                    }
                    // Friend news
                    for(DataSnapshot friendSnapshot: snapshot.child("friends").getChildren()){
                        String friendUname = friendSnapshot.getValue(String.class);
                        databaseReference.child(friendUname).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for(DataSnapshot newsSnapshot : snapshot.child("news").getChildren()){
                                        News itemNews = new News();
                                        itemNews.setTime(newsSnapshot.child("time").getValue(Time.class));
                                        itemNews.setTypeContent(newsSnapshot.child("typeContent").getValue(String.class));
                                        itemNews.setText(newsSnapshot.child("text").getValue(String.class));

                                        itemNews.setId(newsSnapshot.getKey());
                                        itemNews.setUname(snapshot.child("uname").getValue(String.class));
                                        news.add(itemNews);
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
                uploadNews("NORMAL");
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

        btnPostVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Video"),REQ_GET_VIDEO);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == REQ_GET_MULTI_IMG && resultCode == RESULT_OK && data!=null){
            if(data.getClipData() != null){
                uploadNews("IMAGE");

                int count = data.getClipData().getItemCount();

                for (int i=0; i<count; i++){

                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    try {
                        InputStream inputStream = getActivity().getContentResolver().openInputStream(imageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        uploadImageBitmap(bitmap);
                    } catch (IOException e) {
                        Log.e("Error", e.getMessage());
                    }
                }
            }
            else if(data.getData() != null){

                Uri imgUri = data.getData();
                try {
                    InputStream inputStream = getActivity().getContentResolver().openInputStream(imgUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    uploadImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    Log.e("Error", e.getMessage());
                }
            }
        }else if(requestCode == REQ_GET_VIDEO && resultCode == RESULT_OK && data !=null){
            try {
                String nameFile = getFileName(data.getData());
                InputStream inputStream = getActivity().getContentResolver().openInputStream(data.getData());

                uploadFile(inputStream, nameFile);
            } catch (IOException e) {
                Log.e("Error", e.getMessage());
            }
        }
    }

    public void uploadFile(InputStream inputStream, String nameFile){
        UploadTask uploadTask = storageReference.child("file").child(nameFile).putStream(inputStream);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Error", e.getMessage());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

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
                                    uploadNews("VIDEO");
                                    databaseReference.child(uname).child("news").child(newsId).child("content").setValue(path);
                                } else
                                    Log.e("Error", "Uncompleted!");
                            }
                        });
                    }
                });
            }
        });
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
                            databaseReference.child(uname).child("news").child(newsId).child("images").push().setValue(path);
                        } else
                            Log.e("Error", "Uncompleted!");
                    }
                });
            }
        });
    }

    public void uploadNews(String typeContent){
        String text = editText.getText().toString();
        Time time = new Time();
        News itemNews = new News(time, text, typeContent);

        DatabaseReference newsReference = databaseReference.child(uname).child("news").push();
        newsId = newsReference.getKey();
        newsReference.setValue(itemNews);
    }
}
