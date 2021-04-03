package com.example.tchatker.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tchatker.R;
import com.example.tchatker.model.Account;
import com.example.tchatker.model.Time;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

public class CreateAccountActivity extends AppCompatActivity {
    Button btnNewAccount, btnCancel;
    EditText editUname, editPwd, editRePwd;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        init();
        addControls();
        setEvents();
    }

    public void init(){
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("user");
    }

    public void addControls(){
        btnNewAccount = findViewById(R.id.btnNewAccount);
        btnCancel = findViewById(R.id.btnCancel);
        editUname = findViewById(R.id.editUname);
        editPwd = findViewById(R.id.editPwd);
        editRePwd = findViewById(R.id.editRePwd);
    }

    public void setEvents(){
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btnNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uname = editUname.getText().toString();
                String pwd = editPwd.getText().toString();
                String rePwd = editRePwd.getText().toString();

                if(uname.length()<6){
                    editUname.setError("Length username must >= 6");
                    editUname.requestFocus();
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

                reference.orderByChild("uname").equalTo(uname).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.getChildrenCount()>0){
                            editUname.setError("Already exist this username");
                            editUname.requestFocus();
                            return ;
                        }else{
                            reference.child(uname).setValue(new Account(uname,pwd,"","","",new Time(1990,1,1), "","",""))
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(CreateAccountActivity.this, "Completed", Toast.LENGTH_SHORT).show();
                                            }else{
                                                Toast.makeText(CreateAccountActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                            }
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
    }
}