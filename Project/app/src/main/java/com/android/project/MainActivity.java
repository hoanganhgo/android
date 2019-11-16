package com.android.project;


import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;


public class MainActivity extends Activity {

    ListView lv;
    ArrayList<String> array;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.circle_member);
        lv = (ListView) findViewById(R.id.list_circleMember);
        array = new ArrayList<>();
        array.add("Ha Noi");
        array.add("TP HCM");
        array.add("Hai Phong");
        array.add("Can Tho");
        array.add("Da Nang");
        array.add("Khanh Hoa");
        array.add("Nghe An");

        ArrayAdapter adapter = new ArrayAdapter(MainActivity.this,android.R.layout.simple_list_item_1, array);
        lv.setAdapter(adapter);
    }
}
