package com.example.tchatker.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.tchatker.fragment.FriendDirectoryFragment;
import com.example.tchatker.fragment.GroupDirectoryFragment;

public class DirectoryViewPagerAdapter extends FragmentPagerAdapter {
    public DirectoryViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if(position == 0)
            return new FriendDirectoryFragment();
        else
            return new GroupDirectoryFragment();
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if(position == 0)
            return "Friend";
        else
            return "Group";
    }
}
