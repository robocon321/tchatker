package com.example.tchatker.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.tchatker.R;
import com.example.tchatker.adapter.HomeViewPagerAdapter;
import com.example.tchatker.service.CallService;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {
    BottomNavigationView bottomNav;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        addControls();
        setEvents();
    }

    public void addControls(){
        bottomNav = findViewById(R.id.bottomNav);
        viewPager = findViewById(R.id.viewPagerHome);

        HomeViewPagerAdapter adapter = new HomeViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(1); // Giới hạn page được load trước và giá trị 1 cũng là mặc định
    }

    public void setEvents(){
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        bottomNav.getMenu().findItem(R.id.mnMessage).setChecked(true);
                        break;
                    case 1:
                        bottomNav.getMenu().findItem(R.id.mnDirectory).setChecked(true);
                        break;
                    case 2:
                        bottomNav.getMenu().findItem(R.id.mnNews).setChecked(true);
                        break;
                    case 3:
                        bottomNav.getMenu().findItem(R.id.mnSetting).setChecked(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.mnMessage:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.mnDirectory:
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.mnNews:
                        viewPager.setCurrentItem(2);
                        break;
                    case R.id.mnSetting:
                        viewPager.setCurrentItem(3);
                        break;
                }
                return true;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
        String uname = sharedPreferences.getString("uname", "roobocon321");
        Intent intent = new Intent(this, CallService.class);
        intent.putExtra("uname", uname);
        startService(intent);
    }
}