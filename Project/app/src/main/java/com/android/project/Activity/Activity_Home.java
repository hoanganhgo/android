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
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.project.Adapter.CircleAddapter;
import com.android.project.Bussiness;
import com.android.project.ModelDatabase.HistoryModel;
import com.android.project.ModelDatabase.StaticLocationModel;
import com.android.project.ModelDatabase.UserModel;
import com.android.project.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Activity_Home extends Activity implements LocationListener {
    ImageButton imgbtnIbMenu;
    ListView listView;
    ProgressBar progressBar;

    private String userName;
    private DataSnapshot dataUser = null;
    public static boolean isDisplay = true;

    private DatabaseReference sosRef = null;
    private DatabaseReference myRef = null;
    private ValueEventListener sos_event = null;

    public static String circle_Selected=null;
    public static String user=null;
    public static LatLng myLocation=null;

    private List<String> listCircleName = null;
    private List<String> listLocationArrived = new ArrayList<String>();

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
        progressBar=(ProgressBar)findViewById(R.id.progressBar_cyclic2);

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
        myRef.addValueEventListener(valueEventListener);

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

        ValueEventListener help_event = new ValueEventListener() {
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

        listCircleName = Bussiness.getCircleUserJoinning(userName);

        for (String circle : listCircleName) {
            Log.e("circle", circle);
            sosRef.child(circle).child("SOS").addValueEventListener(sos_event);
            sosRef.child(circle).child("Members").addValueEventListener(help_event);
        }

        Log.e("AccountChange", userName + " change");



        //Đồng bộ thông tin của user tới các nhánh của circle mà user đó tham gia
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

        //Mở danh sách các phòng chat
        imgbtnIbMenu = (ImageButton) findViewById(R.id.ib_menu);
        imgbtnIbMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // bắt gói Intent bundle từ MainActivity, lấy user name và gửi đến Activity_Chat
                Bundle bundle = new Bundle();
                bundle.putString("userName", userName);

                Intent intent = new Intent(Activity_Home.this, Activity_List_Conversations.class);
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
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, this);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0,  this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        progressBar.setVisibility(View.INVISIBLE);

        final List<String> listCircleName = Bussiness.getCircleUserJoinning(userName);
        CircleAddapter circleAddapter = new CircleAddapter(this, listCircleName);

        listView.setAdapter(circleAddapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                progressBar.setVisibility(View.VISIBLE);
                circle_Selected = listCircleName.get(position);
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
    protected void onStop() {
        super.onStop();
        Log.e("isDisplay", "false");
        isDisplay = false;
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

        double coor_x = location.getLatitude();
        double coor_y = location.getLongitude();

/*        double coor_x = 10.762913;
        double coor_y = 106.6821717;*/

        //set firebase
        myRef.child("coor_x").setValue(coor_x);
        myRef.child("coor_y").setValue(coor_y);
        myLocation=new LatLng(coor_x, coor_y);
        myRef.child("speed").setValue(speed);

        /*HistoryModel historyModel = new HistoryModel(userName, String.format("%s has arrived %s", userName, "KHTN"));

        //Đẩy lịch sử lên firebase
        FirebaseDatabase.getInstance().getReference().child("Circles").child("abc").child("History").push().setValue(historyModel);

        historyModel = new HistoryModel(userName, String.format("%s has arrived %s", "hoanganh", "KHTN"));

        //Đẩy lịch sử lên firebase
        FirebaseDatabase.getInstance().getReference().child("Circles").child("abc").child("History").push().setValue(historyModel);*/
        //Kiểm tra xem có tới địa điểm nào trong StaticLocation hay không
        //Nếu có thì tiến hành đẩy history lên.

        Calendar calendar_now = Calendar.getInstance();
        for(String circle : listCircleName)
        {
            checkInCheckOut(coor_x, coor_y, calendar_now, userName, circle);
        }
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
    @Override
    public void onBackPressed() {
        progressBar.setVisibility(View.VISIBLE);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void checkInCheckOut(final double coor_x, final double coor_y, final Calendar calendar, final String user, final String nameCircle){
        Log.e("checkInCheckOut", "checkInCheckOut " + nameCircle);

        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);

        calendar.set(0,0,0, hour, minute, 0);

        FirebaseDatabase.getInstance().getReference().child("Circles").child(nameCircle).child("StaticLocation").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    StaticLocationModel staticLocationModel = data.getValue(StaticLocationModel.class);

                    Calendar checkin = StaticLocationModel.stringToCalendar(staticLocationModel.getCheckin());
                    Calendar checkout = StaticLocationModel.stringToCalendar(staticLocationModel.getCheckout());
                    String nameLocation = staticLocationModel.getName();

                    Log.e("checkInCheckOut", "checkInCheckOut " + staticLocationModel.getName());

                    //Nếu thời điểm checkin nhỏ hơn checkout
                    if (checkin.compareTo(checkout) == -1) {
                        //Kiểm tra nếu thời điểm người dùng thay đổi vị trí có nằm trong thời gian checkin checkout thì tiến hành kiểm tra
                        //vị trí của người dùng so với vị trí của Địa điểm. Nếu bán kính dưới 50m thì tiến hành đẩy một thông báo lên.
                        if ((checkin.compareTo(calendar) < 1) && (checkout.compareTo(calendar) > -1)) {
                            double locationCoor_x = staticLocationModel.getCoor_x();
                            double locationCoor_y = staticLocationModel.getCoor_y();
                            Log.e("HistoryCompare", "true");
                            if (distanceBetween2Points(coor_x, coor_y, locationCoor_x, locationCoor_y) < 0.05) {
                                boolean arrived = false;
                                for (String locationArrived : listLocationArrived) {
                                    if (locationArrived.contentEquals(staticLocationModel.getName())) {
                                        arrived = true;
                                        break;
                                    }
                                }

                                if (arrived == false)
                                {
                                    listLocationArrived.add(staticLocationModel.getName());

                                    //Tạo một lịch sử
                                    HistoryModel historyModel = new HistoryModel(user, String.format("%s has arrived %s", user, nameLocation));

                                    //Đẩy lịch sử lên firebase
                                    FirebaseDatabase.getInstance().getReference().child("Circles").child(nameCircle).child("History").push().setValue(historyModel);
                                }
                            } else {
                                boolean arrived = false;
                                for (String locationArrived : listLocationArrived) {
                                    if (locationArrived.contentEquals(staticLocationModel.getName())) {
                                        arrived = true;
                                        listLocationArrived.remove(locationArrived);
                                        break;
                                    }
                                }

                                if (arrived) {
                                    //Tạo một lịch sử
                                    HistoryModel historyModel = new HistoryModel(user, String.format("%s has left %s", user, nameLocation));

                                    //Đẩy lịch sử lên firebase
                                    FirebaseDatabase.getInstance().getReference().child("Circles").child(nameCircle).child("History").push().setValue(historyModel);
                                }
                            }
                        }

                    } else {
                        if ((checkin.compareTo(calendar) > -1) && (checkout.compareTo(calendar) < 1)) {
                            double locationCoor_x = staticLocationModel.getCoor_x();
                            double locationCoor_y = staticLocationModel.getCoor_y();

                            if(distanceBetween2Points(coor_x, coor_y, locationCoor_x, locationCoor_y) < 0.05){
                                //Tạo một lịch sử
                                HistoryModel historyModel = new HistoryModel(user, String.format("%s has arrived %s", user, nameLocation));

                                //Đẩy lịch sử lên firebase
                                FirebaseDatabase.getInstance().getReference().child("Circles").child(nameCircle).child("History").push().setValue(historyModel);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public double distanceBetween2Points(double la1, double lo1, double la2, double lo2) {
        double R = 6.371;
        double dLat = (la2 - la1) * (Math.PI / 180);
        double dLon = (lo2 - lo1) * (Math.PI / 180);
        double la1ToRad = la1 * (Math.PI / 180);
        double la2ToRad = la2 * (Math.PI / 180);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(la1ToRad)
                * Math.cos(la2ToRad) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = R * c;
        return d;
    }
}
