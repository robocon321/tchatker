package com.example.tchatker.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.webkit.PermissionRequest;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.example.tchatker.R;
import com.example.tchatker.model.Account;
import com.example.tchatker.model.IncomeInfo;

public class CallVideoActivity extends AppCompatActivity {
    WebView webView;
    ImageView imgClose;
    Account account;
    IncomeInfo incomeInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_video);
        account = (Account) getIntent().getSerializableExtra("account");
        incomeInfo = (IncomeInfo) getIntent().getSerializableExtra("incomeInfo");
        addControls();
        setEvents();
    }

    public void addControls(){
        webView = findViewById(R.id.webViewideoCall);
        imgClose = findViewById(R.id.imgClose);

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
                    webView.evaluateJavascript("accept('"+incomeInfo.getPeerId()+"',true)", null);
                } else {
                    webView.loadUrl("javascript:accept('"+incomeInfo.getPeerId()+"', true);");
                }
            }
        });
    }

    public void setEvents(){

    }
}