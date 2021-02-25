package com.example.tchatker.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.tchatker.fragment.DirectoryFragment;
import com.example.tchatker.fragment.MessageFragment;
import com.example.tchatker.fragment.NewsFragment;
import com.example.tchatker.fragment.SettingFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    public ViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new MessageFragment();
            case 1:
                return new DirectoryFragment();
            case 2:
                return new NewsFragment();
            case 3:
                return new SettingFragment();
            default:
                return new MessageFragment();
        }
    }

    @Override
    public int getCount() {
        return 4;
    }
}
