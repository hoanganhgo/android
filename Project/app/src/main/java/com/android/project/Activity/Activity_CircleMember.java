package com.android.project.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.android.project.Bussiness;
import com.android.project.Object.Member;
import com.android.project.Adapter.MemberAdapter;
import com.android.project.R;

import java.util.List;

public class Activity_CircleMember extends Activity {
    private String nameCircle;
    private TextView tvNameCircle;
    private Button btnInvite;
    private ListView lvMember;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);
        final Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        nameCircle = bundle.getString("nameCircle");

        //Log.e("Circle17", nameCircle);
        List<Member> memberList = Bussiness.getMembers(nameCircle);

        tvNameCircle = findViewById(R.id.tv_mycircle);
        btnInvite = findViewById(R.id.btnInvite);
        lvMember = findViewById(R.id.list_member);

        tvNameCircle.setText(nameCircle);
        //MemberAdapter addapter = new MemberAdapter(this, memberList);
        //lvMember.setAdapter(addapter);

        btnInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentInvite = new Intent(Activity_CircleMember.this, Activity_Invite.class);
                Bundle bundleInvite = new Bundle();
                bundleInvite.putString("nameCircle", nameCircle);
                intentInvite.putExtras(bundleInvite);
                startActivity(intentInvite);
            }
        });
    }
}
