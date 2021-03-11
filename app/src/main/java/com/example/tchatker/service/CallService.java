package com.example.tchatker.service;

import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.tchatker.R;
import com.example.tchatker.activity.ReceiveCallAudioActivity;
import com.example.tchatker.activity.ReceiveCallVideoActivity;
import com.example.tchatker.model.Account;
import com.example.tchatker.model.IncomeInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.content.Context.NOTIFICATION_SERVICE;

public class CallService extends Service {
    FirebaseDatabase database;
    DatabaseReference reference;
    public static boolean isStartCall = false;

    @Override
    public void onCreate() {
        super.onCreate();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("user");
        Toast.makeText(this, "onCreate", Toast.LENGTH_SHORT).show();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Toast.makeText(this, "onBind", Toast.LENGTH_SHORT).show();
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String uname = intent.getStringExtra("uname");
        reference.orderByChild("uname").equalTo(uname).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot item : snapshot.getChildren()){
                    if(item.hasChild("income") && !isStartCall){
                        isStartCall = true;
                        IncomeInfo incomeInfo = item.child("income").getValue(IncomeInfo.class);
                        reference.orderByChild("uname").equalTo(incomeInfo.getUname()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot item : snapshot.getChildren()){
                                    Account account = item.getValue(Account.class);
                                    if(incomeInfo.isVideo()){
                                        Intent intentX = new Intent(CallService.this, ReceiveCallVideoActivity.class);
                                        intentX.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intentX.putExtra("account", account);
                                        intentX.putExtra("incomeInfo", incomeInfo);
                                        startActivity(intentX);
                                    }else{
                                        Intent intentX = new Intent(CallService.this, ReceiveCallAudioActivity.class);
                                        intentX.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intentX.putExtra("account", account);
                                        intentX.putExtra("incomeInfo", incomeInfo);
                                        startActivity(intentX);
                                    }
                                }
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
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "onDestroy", Toast.LENGTH_SHORT).show();
    }
}
