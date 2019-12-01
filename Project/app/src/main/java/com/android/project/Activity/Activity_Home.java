package com.android.project.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.project.Bussiness;
import com.android.project.Adapter.CircleAddapter;
import com.android.project.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Objects;

public class Activity_Home extends Activity{
    ImageButton imgbtnIbMenu;
    ListView listView;

    private String userName;
    private DataSnapshot dataUser=null;
    public static boolean isDisplay=true;

    private DatabaseReference sosRef=null;
    private DatabaseReference myRef=null;
    private ValueEventListener sos_event=null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Intent callingIntent = getIntent();
        Bundle myBundle = callingIntent.getExtras();
        userName = myBundle.getString("userName");
        listView = (ListView) findViewById(R.id.listCircle);

        //Read data from database.
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        myRef=database.child("Account").child(userName);
        //Listen event sharing
        ValueEventListener valueEventListener=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataUser=dataSnapshot;
                Log.e("notify","Run");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("notify","Read sharing fail");
            }
        };
        myRef.addListenerForSingleValueEvent(valueEventListener);

        //Listen event SOS
        sosRef=database.child("Circles");
        sos_event=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String status= Objects.requireNonNull(dataSnapshot.child("SOS").getValue()).toString();
                String parent=dataSnapshot.getKey();
                if (!status.contentEquals("") && isDisplay)
                {
                    Log.e("notify","Data change Home");
                    Bussiness.notify_SOS(Activity_Home.this, parent, status);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        String[] listCircleName={"circle test 1","circle test 2", "MyCircle", "newCircle"};
        for (String circle : listCircleName)
        {
            Log.e("circle", circle);
            sosRef.child(circle).addValueEventListener(sos_event);
        }

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
                Intent intent = new Intent(Activity_Home.this, Activity_MyCircle_Home.class);
                Bundle bundle = new Bundle();
                bundle.putString("nameCircle", listCircleName.get(position));
                bundle.putString("userName", userName);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        isDisplay=true;
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        isDisplay=false;
    }

    public void personal_Click(View view) {
       // Intent intent = new Intent(this, Activity_Profile.class);
        //Cài đặt giá trị sharing lên màn hình
        String status_Location = Objects.requireNonNull(dataUser.child("Share_Location").getValue()).toString();
        String status_Battery = Objects.requireNonNull(dataUser.child("Share_Battery").getValue()).toString();
        String status_Speed = Objects.requireNonNull(dataUser.child("Share_Speed").getValue()).toString();

        Intent intent = new Intent(this, Activity_Profile.class);
        Bundle bundle = new Bundle();
        bundle.putString("userName", userName);
        bundle.putString("status_Location",status_Location);
        bundle.putString("status_Battery",status_Battery);
        bundle.putString("status_Speed",status_Speed);
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
