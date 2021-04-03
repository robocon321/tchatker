package com.example.tchatker.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tchatker.R;
import com.example.tchatker.adapter.MessageRecyclerViewAdapter;
import com.example.tchatker.model.Account;
import com.example.tchatker.model.Message;
import com.google.android.gms.tasks.OnCompleteListener;
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class ChatMessageActivity extends AppCompatActivity {
    int REQ_GET_IMAGE = 100;
    int REQ_PICK_IMAGE = 101;
    int REQ_GET_FILE = 102;

    FirebaseDatabase database;
    DatabaseReference databaseReference;

    FirebaseStorage storage;
    StorageReference storageReference;

    ImageView imgBack, imgCall, imgVideoCall;
    TextView txtName, txtStatus;

    RecyclerView recyclerViewMessage;
    ArrayList<Message> messages;
    MessageRecyclerViewAdapter adapter;

    ImageView imgEmoji, imgAttach, imgPicture, imgCamera, imgSend;
    EditText editMessage;

    Account account;
    String idBoxMessage;
    String uname;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_message);
        addControls();
        initData();
        setEvents();
    }

    public void addControls(){
        imgBack = findViewById(R.id.imgBack);
        imgCall = findViewById(R.id.imgCall);
        imgVideoCall = findViewById(R.id.imgVideoCall);
        txtName = findViewById(R.id.txtName);
        txtStatus = findViewById(R.id.txtStatus);

        recyclerViewMessage = findViewById(R.id.recyclerViewMessage);

        imgEmoji = findViewById(R.id.imgEmoji);
        imgAttach = findViewById(R.id.imgAttach);
        imgPicture = findViewById(R.id.imgPicture);
        imgCamera = findViewById(R.id.imgCamera);
        imgSend = findViewById(R.id.imgSend);

        editMessage = findViewById(R.id.editMessage);
    }

    public void initData(){
        initFirebase();
        initHeader();
        initMessageContent();
    }

    public void initFirebase() {
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReferenceFromUrl("gs://tchatker.appspot.com");
    }

    public void initHeader(){
        account = (Account) getIntent().getSerializableExtra("account");

        String name = account.getName();
        if(name == null) name = account.getUname();
        txtName.setText(name);

        String status = account.getStatus();
        if (status.equals("online")){
            txtStatus.setText("Đang hoạt động");
            txtStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.online, 0,0,0);
        }
        else {
            txtStatus.setText("Không hoạt động");
            txtStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.offline,0,0,0);
        }
    }

    public void initMessageContent(){

        sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
        uname = sharedPreferences.getString("uname","robocon321");

        messages = new ArrayList<>();
        adapter = new MessageRecyclerViewAdapter(messages, ChatMessageActivity.this);

        databaseReference.child("user").child(uname).addListenerForSingleValueEvent(new ValueEventListener() {
            boolean isExistBoxChat = false;
            long count;

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                count = snapshot.child("messages").getChildrenCount();
                for(DataSnapshot boxMessage : snapshot.child("messages").getChildren()) {
                    databaseReference.child("chat").child(boxMessage.getValue(String.class)).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshotChat) {
                            count --;
                            if (snapshotChat.child("type").getValue(String.class).equals("person")) {
                                for (DataSnapshot memberSnapshot : snapshotChat.child("member").getChildren()) {
                                    Log.d("HHH", memberSnapshot.getValue(String.class));
                                    if (memberSnapshot.getValue(String.class).equals(account.getUname())){
                                        isExistBoxChat = true;
                                        idBoxMessage = boxMessage.getValue(String.class);

                                        listenBoxMessage(idBoxMessage);
                                    }
                                }
                            }
                            if(!isExistBoxChat && count == 0){
                                String id = databaseReference.child("chat").push().getKey();
                                idBoxMessage = id;
                                databaseReference.child("chat").child(id).child("type").setValue("person");
                                databaseReference.child("chat").child(id).child("member").push().setValue(uname);
                                databaseReference.child("chat").child(id).child("member").push().setValue(account.getUname());

                                databaseReference.child("user").child(uname).child("messages").push().setValue(id);
                                databaseReference.child("user").child(account.getUname()).child("messages").push().setValue(id);
                                listenBoxMessage(idBoxMessage);
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
        recyclerViewMessage.setAdapter(adapter);
        recyclerViewMessage.setHasFixedSize(true);
        recyclerViewMessage.setLayoutManager(new LinearLayoutManager(ChatMessageActivity.this));
    }

    public void setEvents(){
        imgSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = editMessage.getText().toString().trim();
                String sender = sharedPreferences.getString("uname","robocon321");
                boolean status = false;
                String typeContent = "String";
                Message message = new Message(sender, content, status, typeContent);
                databaseReference.child("chat").child(idBoxMessage).child("message").push().setValue(message);
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        imgCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQ_GET_IMAGE);
            }
        });

        imgPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQ_PICK_IMAGE);
            }
        });

        imgAttach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(intent, REQ_GET_FILE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent dataIntent) {
        super.onActivityResult(requestCode, resultCode, dataIntent);
        if (requestCode == REQ_GET_IMAGE && resultCode == RESULT_OK && dataIntent != null) {
                Bitmap bitmap = (Bitmap) dataIntent.getExtras().get("data");
                uploadImageBitmap(bitmap);
            }
        else if (requestCode == REQ_PICK_IMAGE && resultCode == RESULT_OK && dataIntent != null) {
            Bitmap bitmap;
            try {
                InputStream inputStream = getContentResolver().openInputStream(dataIntent.getData());
                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (FileNotFoundException e) {
                Log.e("Error", e.getMessage());
                return;
            }
            uploadImageBitmap(bitmap);
        }else if(requestCode == REQ_GET_FILE && resultCode == RESULT_OK && dataIntent != null){
            try {
                String nameFile = getFileName(dataIntent.getData());
                InputStream inputStream = getContentResolver().openInputStream(dataIntent.getData());

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
                        String content = taskSnapshot.getMetadata().getName();
                        String sender = sharedPreferences.getString("uname", "robocon321");
                        boolean status = false;
                        String typeContent = "File";
                        Message message = new Message(sender, content, status, typeContent);
                        databaseReference.child("chat").child(idBoxMessage).child("message").push().setValue(message);
                    }
                });
            }
        });
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
                            String content = uri.toString();
                            String sender = sharedPreferences.getString("uname", "robocon321");
                            boolean status = false;
                            String typeContent = "Image";
                            Message message = new Message(sender, content, status, typeContent);
                            databaseReference.child("chat").child(idBoxMessage).child("message").push().setValue(message);
                        } else
                            Log.e("Error", "Uncompleted!");
                    }
                });
            }
        });
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
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

    public void listenBoxMessage(String id){
        databaseReference.child("chat").child(idBoxMessage).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messages.clear();
                for (DataSnapshot messageSnapShot : snapshot.child("message").getChildren()) {

                    Message message = messageSnapShot.getValue(Message.class);
                    messages.add(message);

                    // Change status
                    if (!message.getSender().equals(uname)) {
                        databaseReference.child("chat")
                                .child(idBoxMessage)
                                .child("message")
                                .child(messageSnapShot.getKey())
                                .child("status").setValue(true);
                    }
                }
                Collections.sort(messages);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Error", error.getMessage());
            }
        });
    }
}