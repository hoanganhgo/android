package com.android.project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.sql.Time;

public class StaticLocation_Activity extends Activity {
    private String circleName;
    private EditText edCoorX;
    private EditText edCoorY;
    private Button btnCreateLocation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_static_location);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        circleName = bundle.getString("circleName");

        edCoorX = findViewById(R.id.input_coorX);
        edCoorY = findViewById(R.id.input_coorY);
        btnCreateLocation = findViewById(R.id.btn_createLocation);

        btnCreateLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Time time = new Time (1,1,1);
                float coorX = Float.parseFloat(edCoorX.getText().toString());
                float coorY = Float.parseFloat(edCoorY.getText().toString());

                Static_MyLocation static_myLocation = new Static_MyLocation(coorX, coorY, time, time);
                boolean res = Bussiness.insertStaticLocation(circleName, static_myLocation);

                if(res)
                {
                    Toast.makeText(getApplication(), "Success !", Toast.LENGTH_LONG).show();
                    finish();
                }
                else
                {
                    Toast.makeText(getApplication(), "Fail !", Toast.LENGTH_LONG).show();
                }

            }
        });

    }
}
