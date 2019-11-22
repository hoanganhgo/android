package com.android.project;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;

public class signup extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().hide();
        setContentView(R.layout.activity_signup);
    }
}

