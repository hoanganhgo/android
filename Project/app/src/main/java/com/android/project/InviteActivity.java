package com.android.project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class InviteActivity extends Activity {
    private EditText edUserName;
    private Button btnInvite;
    private String nameCircle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invite);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        nameCircle = bundle.getString("nameCircle");

        edUserName = findViewById(R.id.input_newMember);
        btnInvite = findViewById(R.id.btn_addMember);

        btnInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userNameMember = edUserName.getText().toString();
                boolean isSuccess = Bussiness.insertMember(nameCircle,userNameMember);
                if(isSuccess)
                {
                    Toast.makeText(getApplication(),"Invite Success", Toast.LENGTH_LONG).show();
                    Intent backIntent = new Intent(InviteActivity.this, MyCircle_Activity.class);
                    Bundle backBundle = new Bundle();
                    backBundle.putString("nameCircle", nameCircle);
                    backIntent.putExtras(backBundle);
                    startActivity(backIntent);
                }
                else
                {
                    Toast.makeText(getApplication(),"Account not exits", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
