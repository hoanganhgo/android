package com.android.project.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.project.Bussiness;
import com.android.project.R;

public class Activity_MyCircle extends Activity {
    private String nameCircle;
    private String userName;
    private TextView tvNameCircle;
    private ImageButton btnmaps;
    private ImageButton btnmembers;
    private ImageButton btninvite;
    private ImageButton btnchatCircle;
    private ImageButton btnstaticLocation;
    private ImageButton btnleaveCircle;
    private ImageButton btnsos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_circle);

        final Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        nameCircle = bundle.getString("nameCircle");
        userName = bundle.getString("userName");

        btnmaps = findViewById(R.id.maps);
        btnmembers = findViewById(R.id.members);
        btninvite = findViewById(R.id.invite);
        btnchatCircle = findViewById(R.id.chatcircle);
        btnstaticLocation = findViewById(R.id.staticlocation);
        btnleaveCircle = findViewById(R.id.leavecircle);
        btnsos = findViewById(R.id.sos);
        tvNameCircle = findViewById(R.id.tv_mycircle);

        tvNameCircle.setText(nameCircle);

        btnchatCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("userName", userName);
                Intent intent = new Intent(Activity_MyCircle.this, Activity_Chat.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        btninvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentInvite = new Intent(Activity_MyCircle.this, Activity_Invite.class);
                Bundle bundleInvite = new Bundle();
                bundleInvite.putString("nameCircle", nameCircle);
                intentInvite.putExtras(bundleInvite);
                startActivity(intentInvite);
            }
        });

        btnleaveCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String result = Bussiness.deleteMember(nameCircle, userName)? "Success" : "Fail";
/*                Toast.makeText(getApplication(), result, Toast.LENGTH_LONG).show();
                Log.e("Circle17", "Leave: " + result);*/
                finish();
            }
        });

        btnmembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentInvite = new Intent(Activity_MyCircle.this, Activity_CircleMember.class);
                Bundle bundleInvite = new Bundle();
                bundleInvite.putString("userName", userName);
                bundleInvite.putString("nameCircle", nameCircle);
                intentInvite.putExtras(bundleInvite);
                startActivity(intentInvite);
            }
        });



        //Log.e("Circle17", nameCircle);
       /* List<String> nametest = new ArrayList<String>();
        nametest.add("Test 1");
        nametest.add("Test 3");
        nametest.add("Test 4");
        nametest.add("Test 5");
        nametest.add("Test 2");

        tvNameCircle = findViewById(R.id.tv_mycircle);
        btnInvite = findViewById(R.id.btnInvite);
        lvMember = findViewById(R.id.list_member);

        tvNameCircle.setText(nameCircle);
        CircleAddapter addapter = new CircleAddapter(this, nametest);
        lvMember.setAdapter(addapter);

        btnInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentInvite = new Intent(Activity_MyCircle.this, Activity_Invite.class);
                Bundle bundleInvite = new Bundle();
                bundleInvite.putString("nameCircle", nameCircle);
                intentInvite.putExtras(bundleInvite);
                startActivity(intentInvite);
            }
        });*/
    }

    public void sos_Click(View view) {
        Intent intent = new Intent(this, Activity_SOS.class);
        startActivity(intent);
    }

    public void maps_Click(View view) {
        Intent intent = new Intent(this, Activity_Maps.class);
        startActivity(intent);
    }

    public void insert_new_location(View view) {
    }
}
