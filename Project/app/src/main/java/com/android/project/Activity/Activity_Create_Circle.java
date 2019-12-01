package com.android.project.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.project.Bussiness;
import com.android.project.Object.Circle;
import com.android.project.Object.Member;
import com.android.project.R;

public class Activity_Create_Circle extends Activity {

    private String userName;
    private EditText circleName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_circle);

        circleName =(EditText) findViewById(R.id.input_circleName);

        //Bắt username từ Activity_Home
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
