package com.example.tchatker.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.tchatker.R;
import com.example.tchatker.adapter.DirectoryViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;

public class DirectoryFragment extends Fragment {
    TabLayout tabsDirectory;
    ViewPager viewPagerDirectory;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_directory, null);

        tabsDirectory = view.findViewById(R.id.tabsDirectory);
        viewPagerDirectory = view.findViewById(R.id.viewPagerDirectory);

        DirectoryViewPagerAdapter adapter = new DirectoryViewPagerAdapter(getFragmentManager());
        viewPagerDirectory.setAdapter(adapter);

        tabsDirectory.setupWithViewPager(viewPagerDirectory);
        tabsDirectory.getTabAt(0).setIcon(R.drawable.user);
        tabsDirectory.getTabAt(1).setIcon(R.drawable.group);

        return view;
    }
}
