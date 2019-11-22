package com.android.project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.annotation.Nullable;

import java.util.List;

public class Activity_Home extends Activity{
    ImageButton imgbtnIbMenu;
    ListView listView;

    private String userName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Intent callingIntent = getIntent();
        Bundle myBundle = callingIntent.getExtras();
        userName = myBundle.getString("userName");
        listView = (ListView) findViewById(R.id.listCircle);



        imgbtnIbMenu = (ImageButton) findViewById(R.id.ib_menu);
        imgbtnIbMenu.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // bắt gói Intent bundle từ MainActivity, lấy user name và gửi đến Activity_Chat
                Bundle bundle = new Bundle();
                bundle.putString("userName", userName);

                Intent intent = new Intent(Activity_Home.this, Activity_Chat.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        final List<String> listCircleName = Bussiness.getListCircleFromDatabase(userName);

        CircleAddapter circleAddapter = new CircleAddapter(this, listCircleName);

        listView.setAdapter(circleAddapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Activity_Home.this, Activity_MyCircle.class);
                Bundle bundle = new Bundle();
                bundle.putString("nameCircle", listCircleName.get(position));
                bundle.putString("userName", userName);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    public void personal_Click(View view) {
       // Intent intent = new Intent(this, Activity_Profile.class);
        Intent intent = new Intent(this, Activity_Profile.class);
        Bundle bundle = new Bundle();
        bundle.putString("userName", userName);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void create_Circle_Click(View view) {
        Intent intent = new Intent(this, Activity_Create_Circle.class);
        Bundle bundle = new Bundle();
        bundle.putString("userName", userName);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
