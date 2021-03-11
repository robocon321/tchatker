package com.example.tchatker.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tchatker.R;
import com.example.tchatker.activity.ChatMessageActivity;
import com.example.tchatker.activity.ReceiveCallVideoActivity;
import com.example.tchatker.activity.SendCallAudioActivity;
import com.example.tchatker.activity.SendCallVideoActivity;
import com.example.tchatker.model.Account;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FriendRecyclerViewAdapter extends RecyclerView.Adapter<FriendRecyclerViewAdapter.ViewHolder> {
    ArrayList<Account> accounts;
    Context context;

    public FriendRecyclerViewAdapter(ArrayList<Account> accounts, Context context){
        this.accounts = accounts;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inlater = LayoutInflater.from(context);
        View view = inlater.inflate(R.layout.layout_item_friend_directory, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Account account = accounts.get(position);
        String avatar = account.getAvatar();
        String name = account.getName();
        String status = account.getStatus();

        Picasso.get().load(account.getAvatar()).into(holder.imgUser);
        holder.txtNameUser.setText(account.getName());
        if (status.equals("online")){
            holder.txtStatusUser.setText("Đang hoạt động");
            holder.txtStatusUser.setCompoundDrawablesWithIntrinsicBounds(R.drawable.online, 0,0,0);
        }
        else {
            holder.txtStatusUser.setText("Không hoạt động");
            holder.txtStatusUser.setCompoundDrawablesWithIntrinsicBounds(R.drawable.offline,0,0,0);
        }
    }

    @Override
    public int getItemCount() {
        return accounts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgUser, imgCall, imgVideoCall;
        TextView txtNameUser;
        TextView txtStatusUser;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgUser = itemView.findViewById(R.id.imgUser);
            txtNameUser = itemView.findViewById(R.id.txtNameUser);
            txtStatusUser = itemView.findViewById(R.id.txtStatusUser);
            imgCall = itemView.findViewById(R.id.imgCall);
            imgVideoCall = itemView.findViewById(R.id.imgVideoCall);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ChatMessageActivity.class);
                    intent.putExtra("account", accounts.get(getAdapterPosition()));
                    context.startActivity(intent);
                }
            });

            imgVideoCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, SendCallVideoActivity.class);
                    intent.putExtra("unameDes", accounts.get(getAdapterPosition()).getUname());
                    context.startActivity(intent);
                }
            });

            imgCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Account account = accounts.get(getAdapterPosition());
                    String unameDes = account.getUname();

                    SharedPreferences sharedPreferences = context.getSharedPreferences("login", Context.MODE_PRIVATE);
                    String unameSrc = sharedPreferences.getString("uname", "robocon321");

                    WebView webView = new WebView(context);

                    webView.getSettings().setJavaScriptEnabled(true);
                    webView.getSettings().setDomStorageEnabled(true);

                    webView.loadUrl("https://mtbfo.csb.app/");

                    webView.setWebViewClient(new WebViewClient(){
                        @Override
                        public void onPageFinished(WebView view, String url) {
                            super.onPageFinished(view, url);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                webView.evaluateJavascript("callAudioTo('"+unameSrc+"','"+unameDes+"')", null);
                            } else {
                                webView.loadUrl("javascript:callAudioTo('"+unameSrc+"','"+unameDes+"')");
                            }
                        }
                    });

                    Intent intent = new Intent(context, SendCallAudioActivity.class);
                    intent.putExtra("account", account);
                    context.startActivity(intent);
                }
            });
        }
    }
}
