package com.android.project.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
import com.android.project.ModelDatabase.UserModel;
import com.android.project.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Activity_Home extends Activity implements LocationListener {
    ImageButton imgbtnIbMenu;
    ListView listView;

    private String userName;
    private DataSnapshot dataUser = null;
    public static boolean isDisplay = true;

    private DatabaseReference sosRef = null;
    private DatabaseReference myRef = null;
    private ValueEventListener sos_event = null;

    public static String circle_Selected=null;
    public static String user=null;
    public static LatLng myLocation=null;

    private ArrayList<String> lowBatteries=new ArrayList<String>();
    private ArrayList<String> overSpeeds= new ArrayList<String>();

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
        myRef = database.child("Account").child(userName);
        //Listen event sharing
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataUser = dataSnapshot;
                Log.e("notify", "Run");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("notify", "Read sharing fail");
            }
        };
        myRef.addListenerForSingleValueEvent(valueEventListener);

        //Listen event SOS
        sosRef = database.child("Circles");
        sos_event = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String status = Objects.requireNonNull(dataSnapshot.getValue().toString());
                String parent = dataSnapshot.getKey();
                if (!status.contentEquals("") && isDisplay) {
                    Log.e("notify", "Data change Home");
                    Bussiness.notify_SOS(Activity_Home.this, parent, status);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        ValueEventListener help_event=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot member : dataSnapshot.getChildren())
                {
                    String circleName=dataSnapshot.getRef().getParent().getKey();
                    String memberName=member.getKey();
                    int speed= Integer.parseInt(member.child("speed").getValue().toString());
                    int battery=Integer.parseInt(member.child("battery").getValue().toString());
                    if (speed>80 && !Bussiness.checkInList(member.getKey().toString(),overSpeeds))  //80km/h
                    {
                        Bussiness.notify_OverSpeed(Activity_Home.this,circleName,memberName);
                        overSpeeds.add(memberName);
                    }
                    if (battery<10 && !Bussiness.checkInList(member.getKey().toString(),lowBatteries))  //level < 10%
                    {
                        Bussiness.notify_LowBattery(Activity_Home.this,circleName,memberName);
                        lowBatteries.add(memberName);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        final List<String> listCircleName = Bussiness.getCircleUserJoinning(userName);

        for (String circle : listCircleName) {
            Log.e("circle", circle);
            sosRef.child(circle).child("SOS").addValueEventListener(sos_event);
            sosRef.child(circle).child("Members").addValueEventListener(help_event);
        }

        Log.e("AccountChange", userName + " change");

        FirebaseDatabase.getInstance().getReference().child("Account").child(userName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e("AccountChange", userName + " change");
                UserModel userModel = dataSnapshot.getValue(UserModel.class);
                for (String circle : listCircleName) {
                    FirebaseDatabase.getInstance().getReference().child("Circles").child(circle).child("Members").child(userName).setValue(userModel);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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

        //Luồng cập nhật pin
        Thread thread_Battery = new Thread() {
            @Override
            public void run() {
                try {
                    while(true) {
                        //Lấy lượng pin
                        int level= Bussiness.getBatteryPercentage(Activity_Home.this);
                        myRef.child("battery").setValue(level);
                        sleep(60000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread_Battery.start();

        //Luồng cập nhật thời gian thực
        Thread thread_RealTime=new Thread(){
            @Override
            public void run(){
                try{
                    while(true)
                    {
                        long realTime=(long)(new Date().getTime());
                        //Log.e("test123","realtime: "+realTime);
                        myRef.child("realtime").setValue(realTime);
                        sleep(10000);
                    }
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        };
        thread_RealTime.start();

        //GPS
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        assert locationManager != null;
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,  this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        final List<String> listCircleName = Bussiness.getCircleUserJoinning(userName);
        CircleAddapter circleAddapter = new CircleAddapter(this, listCircleName);

        listView.setAdapter(circleAddapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                circle_Selected=listCircleName.get(position);
                user=userName;
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
        Log.e("isDisplay","false");
        isDisplay=false;
    }

    public void personal_Click(View view) {
       // Intent intent = new Intent(this, Activity_Profile.class);
        //Cài đặt giá trị sharing lên màn hình
        String status_Location = Objects.requireNonNull(dataUser.child("share_location").getValue()).toString();
        String status_Battery = Objects.requireNonNull(dataUser.child("share_battery").getValue()).toString();
        String status_Speed = Objects.requireNonNull(dataUser.child("share_speed").getValue()).toString();

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

    @Override
    public void onLocationChanged(Location location) {
        Log.e("LocationGPS", "lat: " + location.getLatitude());
        Log.e("LocationGPS", "lng: " + location.getLongitude());
        //myLocation=new MyLocation((float)location.getLatitude(),(float)location.getLongitude());

        int speed =(int)Math.round(location.getSpeed()*3.6);
        //Log.e("LocationGPS","Speed: " + this.speed + "km/h");

        //set firebase
        myRef.child("coor_x").setValue(location.getLatitude());
        myRef.child("coor_y").setValue(location.getLongitude());
        myLocation=new LatLng(location.getLatitude(),location.getLongitude());
        myRef.child("speed").setValue(speed);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
