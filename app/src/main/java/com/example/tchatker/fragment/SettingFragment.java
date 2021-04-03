package com.example.tchatker.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.tchatker.R;
import com.example.tchatker.model.Account;
import com.example.tchatker.model.Message;
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
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;

import br.com.sapereaude.maskedEditText.MaskedEditText;

import static android.app.Activity.RESULT_OK;

public class SettingFragment extends Fragment {
    final int REQ_GET_AVATAR = 100;
    final int REQ_GET_BACKGROUND = 101;

    ImageView imgBackground, imgEditBackgound, imgAvatar, imgEditAvatar;
    EditText editName, editPhone, editEmail, editPwd, editRePwd;
    Spinner spDay, spMonth, spYear;
    Button btnSave;

    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseStorage storage;
    StorageReference storageReference;

    SharedPreferences sharedPreferences;
    String uname;
    String urlUploadAvatar;
    String urlUploadBackground;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, null);
        init(view);
        setEvents();
        return view;
    }

    public void init(View view){
        imgBackground = view.findViewById(R.id.imgBackground);
        imgEditBackgound = view.findViewById(R.id.imgEditBackground);
        imgAvatar = view.findViewById(R.id.imgAvatar);
        imgEditAvatar = view.findViewById(R.id.imgEditAvatar);

        editName = view.findViewById(R.id.editName);
        editPhone = view.findViewById(R.id.editPhone);
        editEmail = view.findViewById(R.id.editEmail);
        editPwd = view.findViewById(R.id.editPwd);
        editRePwd = view.findViewById(R.id.editRePwd);

        spDay = view.findViewById(R.id.spDay);
        ArrayList<Integer> days = new ArrayList<>();
        for(int i=1; i<=31; i++){
            days.add(i);
        }
        ArrayAdapter adapterDay = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, days);
        spDay.setAdapter(adapterDay);

        spMonth = view.findViewById(R.id.spMonth);
        ArrayList<Integer> months = new ArrayList<>();
        for(int i=1; i<=12;i++){
            months.add(i);
        }
        ArrayAdapter adapterMonth = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, months);
        spMonth.setAdapter(adapterMonth);

        spYear = view.findViewById(R.id.spYear);
        ArrayList<Integer> years = new ArrayList<>();
        for(int i=1980; i<= Calendar.getInstance().get(Calendar.YEAR);i++){
            years.add(i);
        }
        ArrayAdapter adapterYear = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, years);
        spYear.setAdapter(adapterYear);

        btnSave = view.findViewById(R.id.btnSave);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("user");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReferenceFromUrl("gs://tchatker.appspot.com");

        sharedPreferences = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
        uname = sharedPreferences.getString("uname", "robocon321");

        reference.child(uname).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Account account = snapshot.getValue(Account.class);
                editName.setText(account.getName());
                editPhone.setText(account.getPhoneNumber().substring(3));
                editEmail.setText(account.getEmail());
                editPwd.setText(account.getPwd());
                editRePwd.setText(account.getPwd());

                String avatar = account.getAvatar();
                if(avatar != null){
                    Picasso.get().load(account.getAvatar()).into(imgAvatar);
                }

                String background = account.getBackground();
                if(background != null){
                    Picasso.get().load(snapshot.child("background").getValue(String.class)).into(imgBackground);
                }

                spDay.setSelection(account.getBirthday().getDay()-1);
                spMonth.setSelection(account.getBirthday().getMonth()-1);
                spYear.setSelection(account.getBirthday().getYear()- 1980);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Error", error.getMessage());
            }
        });
    }

    public void setEvents(){
        imgEditAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQ_GET_AVATAR);
            }
        });

        imgEditBackgound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQ_GET_BACKGROUND);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get data
                String name = editName.getText().toString().trim();
                int day = (int) spDay.getSelectedItem();
                int month = (int) spMonth.getSelectedItem();
                int year = (int) spYear.getSelectedItem();
                String phone = editPhone.getText().toString().trim().replaceAll("[() ]", "");
                String email = editEmail.getText().toString().trim();
                String pwd = editPwd.getText().toString().trim();
                String rePwd = editRePwd.getText().toString().trim();

                // Validate data
                if(name.equals("")) {
                    editName.setError("Not empty");
                    editName.requestFocus();
                    return ;
                }
                if(phone.length() < 12){
                    editPhone.setError("Error format phone number");
                    editPhone.requestFocus();
                    return;
                }
                if(!email.matches("^\\S+@\\S+\\.\\S+$")){
                    editEmail.setError("Error format email");
                    editEmail.requestFocus();
                    return ;
                }
                if(pwd.length()<6){
                    editPwd.setError("Length password must >= 6");
                    editPwd.requestFocus();
                    return ;
                }

                if(!rePwd.equals(pwd)){
                    editRePwd.setError("Re-password don't match with password");
                    editRePwd.requestFocus();
                    return ;
                }

                // update data

                Account account = new Account(uname, pwd, name, phone, email, new Time(year, month, day), "", "online", "");
                // upload avatar

                Bitmap bitmap = ((BitmapDrawable) imgAvatar.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();

                UploadTask uploadTask = storageReference.child("image").child("image" + System.currentTimeMillis()).putBytes(data);
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
                                    account.setAvatar(uri.toString());
                                    // upload background

                                    Bitmap bitmap = ((BitmapDrawable) imgBackground.getDrawable()).getBitmap();
                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                    byte[] data = baos.toByteArray();

                                    UploadTask uploadTask = storageReference.child("image").child("image" + System.currentTimeMillis()).putBytes(data);
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
                                                        account.setBackground(uri.toString());
                                                        reference.child(uname).setValue(account);
                                                        Toast.makeText(getActivity(), "Completed!", Toast.LENGTH_SHORT).show();
                                                    } else
                                                        Log.e("Error", "Uncompleted!");
                                                }
                                            });
                                        }
                                    });
                                } else
                                    Log.e("Error", "Uncompleted!");
                            }
                        });
                    }
                });
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQ_GET_AVATAR && resultCode == getActivity().RESULT_OK && data != null){
            try {
                InputStream inputStream = getActivity().getContentResolver().openInputStream(data.getData());
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imgAvatar.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }else if(requestCode == REQ_GET_BACKGROUND && resultCode == getActivity().RESULT_OK && data != null){
            try {
                InputStream inputStream = getActivity().getContentResolver().openInputStream(data.getData());
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imgBackground.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
