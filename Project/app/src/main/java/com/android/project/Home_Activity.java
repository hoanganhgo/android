package com.android.project;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;

public class Home_Activity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.circle_home);
    }
}
