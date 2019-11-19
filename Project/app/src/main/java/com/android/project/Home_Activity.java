package com.android.project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.annotation.Nullable;

public class Home_Activity extends Activity{
    ImageButton imgbtnIbMenu;
    ListView listView;

    private String userName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.circle_home);

        listView = (ListView) findViewById(R.id.listCircle);

        //ArrayAdapter<String> data = new ArrayAdapter<String>(t);

        imgbtnIbMenu = (ImageButton) findViewById(R.id.ib_menu);
        imgbtnIbMenu.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                // bắt gói Intent bundle từ MainActivity, lấy user name và gửi đến ChatActivity
                Intent callingIntent = getIntent();
                Bundle myBundle = callingIntent.getExtras();
                userName = myBundle.getString("userName");

                Bundle bundle = new Bundle();
                bundle.putString("userName", userName);

                Intent intent = new Intent(Home_Activity.this, ChatActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    public void personal_Click(View view) {
       // Intent intent = new Intent(this, Profile_Activity.class);
        Intent intent = new Intent(this, MyCircle_Activity.class);
        Bundle bundle = new Bundle();
        bundle.putString("userName", userName);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void create_Circle_Click(View view) {
        Intent intent = new Intent(this, Create_Circle_Activity.class);
        Bundle bundle = new Bundle();
        bundle.putString("userName", userName);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
