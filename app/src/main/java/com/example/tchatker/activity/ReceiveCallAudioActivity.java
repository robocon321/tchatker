package com.example.tchatker.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tchatker.R;
import com.example.tchatker.model.Account;
import com.example.tchatker.model.IncomeInfo;
import com.example.tchatker.service.CallService;
import com.squareup.picasso.Picasso;

public class ReceiveCallAudioActivity extends AppCompatActivity {
    ImageView imgUser, imgAccept, imgDismiss;
    TextView txtUser;
    Account account;
    IncomeInfo incomeInfo;
    String uname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_call_audio);
        Intent intent = getIntent();
        account = (Account) intent.getSerializableExtra("account");
        incomeInfo = (IncomeInfo) intent.getSerializableExtra("incomeInfo");
        SharedPreferences sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
        uname = sharedPreferences.getString("uname", "robocon321");
        addControl();
        setEvents();
    }

    public void addControl(){
        imgUser = findViewById(R.id.imgUser);
        imgAccept = findViewById(R.id.imgAccept);
        imgDismiss = findViewById(R.id.imgDismiss);
        txtUser = findViewById(R.id.txtUser);

        Picasso.get().load(account.getAvatar()).into(imgUser);
        txtUser.setText(account.getName());
    }

    public void setEvents(){
        CallService.isStartCall = false;
        Log.d("BBBB", "1");

        WebView webView = new WebView(ReceiveCallAudioActivity.this);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        }
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onPermissionRequest(final PermissionRequest request) {
                Log.d("BBBB", "2");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    request.grant(request.getResources());
                }
            }
        });
        webView.loadUrl("https://mtbfo.csb.app/");

        imgAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    webView.evaluateJavascript("accept('"+incomeInfo.getPeerId()+"',"+incomeInfo.isVideo()+")", null);
                } else {
                    webView.loadUrl("javascript:accept('"+incomeInfo.getPeerId()+"',"+incomeInfo.isVideo()+")");
                }

                ((ViewGroup) imgAccept.getParent()).removeView(imgAccept);
                txtUser.setText("00:00");
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    int second = 0;
                    int minute = 0;

                    @Override
                    public void run() {
                        second ++;
                        if(second > 60) {
                            second = 0;
                            minute ++;
                        }

                        if(second < 10 && minute < 10){
                            txtUser.setText("0"+minute+":"+"0"+second);
                        }else if(second < 10 && minute >= 10){
                            txtUser.setText(minute+":"+"0"+second);
                        }else if(second > 10 && minute < 10){
                            txtUser.setText("0"+minute+":"+second);
                        }else {
                            txtUser.setText(minute+":"+second);
                        }
                        handler.postDelayed(this, 1000);
                    }
                }, 1000);
            }
        });

        imgDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("BBBB", "3");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    webView.evaluateJavascript("disaccept('"+uname+"')", null);
                } else {
                    webView.loadUrl("javascript:disaccept('"+uname+"')");
                }
                onBackPressed();
            }
        });
    }}