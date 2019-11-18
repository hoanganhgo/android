package com.android.project;

import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.Nullable;

public class Home_Activity extends Activity{
    ListView listView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.circle_home);

        listView = (ListView) findViewById(R.id.listCircle);

        //ArrayAdapter<String> data = new ArrayAdapter<String>(t);

    }
}
