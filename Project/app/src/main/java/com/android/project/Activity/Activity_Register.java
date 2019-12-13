package com.android.project.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.project.Bussiness;
import com.android.project.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class Activity_Register extends Activity {

    private EditText userName;
    private EditText passWord;
    private EditText rePassWord;

    private DatabaseReference database;
    private DatabaseReference myRef;
    private DataSnapshot listAccount=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        userName=(EditText)findViewById(R.id.register_userName);
        passWord=(EditText)findViewById(R.id.register_passWord);
        rePassWord=(EditText)findViewById(R.id.register_repassWord);

        //firebase - get list account in database
        database = FirebaseDatabase.getInstance().getReference();
        myRef=database.child("Account");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listAccount = dataSnapshot;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w("firebase123", "Failed to read value.", error.toException());
            }
        });

    }


    public void register_Click(View view) {
        if (!checkUserName(this.userName.getText().toString()))
        {
            userName.setText("");
            Toast.makeText(this,"UserName is not invalid!",Toast.LENGTH_LONG).show();
            userName.setFocusable(true);
            return;
        }

        if (!checkRePassWord(passWord.getText().toString(), rePassWord.getText().toString()))
        {
            rePassWord.setText("");
            Toast.makeText(this,"Password does not match!",Toast.LENGTH_LONG).show();
            rePassWord.setFocusable(true);
            return;
        }

        //Kiểm tra sự tồn tại của Username
        if (existUserName(listAccount, this.userName.getText().toString()))
        {
            Toast.makeText(this,"UserName is exist! Please choose other username!",Toast.LENGTH_LONG).show();
            userName.setText("");
            userName.setFocusable(true);
            return;
        }

        //Lấy lượng pin
        int level= Bussiness.getBatteryPercentage(Activity_Register.this);

        if (!Bussiness.register(userName.getText().toString(),passWord.getText().toString(), level))
        {
            Toast.makeText(this,"Register faill",Toast.LENGTH_LONG).show();
            return;
        }

        //Chuyển sang màn hình Circle home
        Intent intent = new Intent(this, Activity_Home.class);
        Bundle bundle = new Bundle();
        bundle.putString("userName", userName.getText().toString());
        intent.putExtras(bundle);
        startActivity(intent);
    }

    //helper
    private boolean checkRePassWord(String pass1, String pass2)
    {
        if (pass1.contentEquals(pass2))
        {
            return true;
        }else
        {
            return false;
        }
    }

    public static boolean checkUserName(String userName)
    {
        int len=userName.length();

        if (len==0)
        {
            return false;
        }

        for (int i=0;i<len;i++)
        {
            if (userName.charAt(i)==' ')
            {
                return false;
            }
        }

        return true;
    }

    public static boolean existUserName(DataSnapshot listAccount, String userName)
    {
        long len=listAccount.getChildrenCount();

        for (DataSnapshot account:listAccount.getChildren())
        {
            if (userName.contentEquals(Objects.requireNonNull(account.getKey())))
            {
                return true;
            }
        }
        return false;
    }
}
