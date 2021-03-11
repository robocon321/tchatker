package com.example.tchatker.activity;

import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.Toast;

import com.example.tchatker.R;

public class SendCallVideoActivity extends AppCompatActivity {
    WebView webView;
    ImageView imgClose;
    String unameDes;
    String unameSrc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_call_video);
        unameDes = getIntent().getStringExtra("unameDes");
        SharedPreferences sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
        unameSrc = sharedPreferences.getString("uname", "robocon321");
        addControls();
        setEvents();
    }

    public void addControls(){
        webView = findViewById(R.id.webViewideoCall);
        imgClose = findViewById(R.id.imgClose);
    }

    public void setEvents(){
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
                    webView.evaluateJavascript("callVideoTo('"+unameSrc+"','"+unameDes+"')", null);
                } else {
                    webView.loadUrl("javascript:callVideoTo('"+unameSrc+"','"+unameDes+"')");
                }
            }
        });

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    webView.evaluateJavascript("disconnectCall('"+unameDes+"')", null);
                } else {
                    webView.loadUrl("javascript:disconnectCall('"+unameDes+"')");
                }
                onBackPressed();
            }
        });
    }
}