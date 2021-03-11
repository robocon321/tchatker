package com.example.tchatker.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.PermissionRequest;
import android.webkit.SslErrorHandler;
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

public class ReceiveCallVideoActivity extends AppCompatActivity {
    ImageView imgUser, imgAccept, imgDismiss;
    TextView txtUser;
    Account account;
    IncomeInfo incomeInfo;
    String uname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_call_video);
        account = (Account) getIntent().getSerializableExtra("account");
        incomeInfo = (IncomeInfo) getIntent().getSerializableExtra("incomeInfo");
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
        imgAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReceiveCallVideoActivity.this, CallVideoActivity.class);
                intent.putExtra("account", account);
                intent.putExtra("incomeInfo", incomeInfo);
                startActivity(intent);
            }
        });

        imgDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CallService.isStartCall = false;
                WebView webView = new WebView(ReceiveCallVideoActivity.this);
                webView.getSettings().setJavaScriptEnabled(true);
                webView.getSettings().setDomStorageEnabled(true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
                }
                webView.setWebChromeClient(new WebChromeClient(){
                    @Override
                    public void onPermissionRequest(final PermissionRequest request) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            request.grant(request.getResources());
                        }
                    }
                });
                webView.loadUrl("https://mtbfo.csb.app/");

                webView.setWebViewClient(new WebViewClient(){
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            webView.evaluateJavascript("disaccept('"+uname+"')", null);
                        } else {
                            webView.loadUrl("javascript:disaccept('"+uname+"')");
                        }
                    }
                });
                onBackPressed();
            }
        });
    }
}