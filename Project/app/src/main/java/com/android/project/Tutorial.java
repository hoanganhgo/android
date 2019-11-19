package com.android.project;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

public class Tutorial extends AppCompatActivity {
    private ViewPager viewPager;
    private SwipeAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_tutorial);
        viewPager = findViewById(R.id.pager);
        adapter = new SwipeAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
    }
}
