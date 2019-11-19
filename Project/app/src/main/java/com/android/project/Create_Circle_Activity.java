package com.android.project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Create_Circle_Activity extends Activity {

    private String userName;
    private EditText circleName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_new_circle);

        circleName =(EditText) findViewById(R.id.input_circleName);

        //Bắt username từ Home_Activity
        Intent callingIntent = getIntent();
        Bundle myBundle = callingIntent.getExtras();
        userName = myBundle.getString("userName");
    }

    public void create_Circle_Click(View view) {
        Member admin=new Member(userName);
        Circle newCircle=new Circle(circleName.getText().toString(),admin);
        Bussiness.insertCircleToDatabase(newCircle);
        Toast.makeText(this, "Create circle success!", Toast.LENGTH_SHORT).show();
        finish();
    }
}
