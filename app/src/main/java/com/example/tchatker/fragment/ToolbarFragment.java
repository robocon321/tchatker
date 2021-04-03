package com.example.tchatker.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.tchatker.R;
import com.example.tchatker.activity.LoginActivity;
import com.example.tchatker.activity.ProfileActivity;
import com.example.tchatker.activity.SearchActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ToolbarFragment extends Fragment {
    EditText editSearch;
    ImageView imgBonus;
    PopupMenu popupMenu;
    SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_tool, container, false);
        init(view);
        setEvents();
        return view;
    }

    public void init(View view){
        sharedPreferences = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
        editSearch = view.findViewById(R.id.editSearch);
        imgBonus = view.findViewById(R.id.imgBonus);

        popupMenu = new PopupMenu(getActivity(), imgBonus);
        popupMenu.inflate(R.menu.more_menu);
    }

    public void setEvents() {
        editSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
            }
        });
        imgBonus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupMenu.show();
            }
        });

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.mnProfile){
                    String uname = sharedPreferences.getString("uname", "robocon321");
                    Intent intent = new Intent(getActivity(), ProfileActivity.class);
                    intent.putExtra("uname", uname);
                    startActivity(intent);
                }else if(item.getItemId() == R.id.mnCreateGroup){
                    Toast.makeText(getActivity(), "Create group", Toast.LENGTH_SHORT).show();
                }else{
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove("uname");
                    editor.commit();

                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
                return false;
            }
        });
    }
}
