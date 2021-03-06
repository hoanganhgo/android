package com.android.project.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.project.Bussiness;
import com.android.project.ModelDatabase.HistoryModel;
import com.android.project.ModelDatabase.JoinModel;
import com.android.project.ModelDatabase.StaticLocationModel;
import com.android.project.ModelDatabase.UserModel;
import com.android.project.R;
import com.firebase.ui.database.FirebaseListAdapter;
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

    //Ứng dụng đang hiện hành và cho phép cập nhật thông tin lên firebase
    //Nếu bằng false thì có nghĩa là người dùng đã thoát ra màn hình đăng nhập, không cập nhật thông tin nữa
    private boolean isUpdate = true;

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

        final ValueEventListener help_event = new ValueEventListener() {
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

        //listCircleName = Bussiness.getCircleUserJoinning(userName);

        FirebaseDatabase.getInstance().getReference().child("Joining").child(userName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot data : dataSnapshot.getChildren()){
                    String circle = data.getValue(JoinModel.class).getCirclename();
                    Log.e("circle", circle);
                    sosRef.child(circle).child("SOS").removeEventListener(sos_event);
                    sosRef.child(circle).child("Members").removeEventListener(help_event);
                    sosRef.child(circle).child("SOS").addValueEventListener(sos_event);
                    sosRef.child(circle).child("Members").addValueEventListener(help_event);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Log.e("AccountChange", userName + " change");

        final ValueEventListener syncAccount = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e("AccountChange", userName + " change");
                final UserModel userModel = dataSnapshot.getValue(UserModel.class);


                FirebaseDatabase.getInstance().getReference().child("Joining").child(userName)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists())
                        {
                            for(DataSnapshot data : dataSnapshot.getChildren()){
                                String circle = data.getValue(JoinModel.class).getCirclename();
                                FirebaseDatabase.getInstance().getReference().child("Circles").child(circle).child("Members").child(userName).setValue(userModel);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        FirebaseDatabase.getInstance().getReference().child("Joining").child(userName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                FirebaseDatabase.getInstance().getReference().child("Account").child(userName).removeEventListener(syncAccount);
                FirebaseDatabase.getInstance().getReference().child("Account").child(userName).addValueEventListener(syncAccount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        //Đồng bộ thông tin của user tới các nhánh của circle mà user đó tham gia



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
                        if(isUpdate)
                        {
                            myRef.child("realtime").setValue(realTime);
                        }
                        else
                            break;
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
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, this);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0,  this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        progressBar.setVisibility(View.INVISIBLE);

        final FirebaseListAdapter<JoinModel> adapter = new FirebaseListAdapter<JoinModel>(this, JoinModel.class,
                R.layout.line_adapter_circle, FirebaseDatabase.getInstance().getReference().child("Joining").child(userName)
        ) {
            @Override
            protected void populateView(View v, JoinModel model, int position) {
                TextView txNameCircle = (TextView) v.findViewById(R.id.txNameCircle);
                TextView txNameAdmin = (TextView) v.findViewById(R.id.txNameAdmin);

                txNameCircle.setText(Integer.toString(position + 1) + ". " + model.getCirclename());
                txNameAdmin.setText("Admin: " + model.getAdmin());
            }
    };

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                progressBar.setVisibility(View.VISIBLE);
                circle_Selected = adapter.getItem(position).getCirclename();
                user=userName;
                Intent intent = new Intent(Activity_Home.this, Activity_MyCircle_Home.class);
                Bundle bundle = new Bundle();
                bundle.putString("nameCircle", circle_Selected);
                bundle.putString("userName", userName);
                bundle.putString("admin", adapter.getItem(position).getAdmin());
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
        final Dialog customDialog = new Dialog(view.getContext());
        customDialog.setContentView(R.layout.dialog_input_username);

        ((TextView)customDialog.findViewById(R.id.tvTitle)).setText("Create circle");

        ((EditText) customDialog.findViewById(R.id.edInviteName)).setHint("Enter circle name");
        customDialog.findViewById(R.id.btnInviteOK).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String circlename = ((EditText) customDialog.findViewById(R.id.edInviteName)).getText().toString();

                if (circlename.contentEquals("") == false) {
                    //Thêm bạn vào circle
                    final DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Circles").child(circlename);

                    db.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists())
                            {
                                //Toast.makeText(Activity_Home.this, "Circle is exit!", Toast.LENGTH_SHORT).show();
                                new AlertDialog.Builder(Activity_Home.this)
                                        .setTitle("Circle existed")
                                        .setMessage("This circle name existed, please use another name...")

                                        //Specifying a listener allows you to take an action before dismissing the dialog.
                                        // The dialog is automatically dismissed when a dialog button is clicked.
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        })

                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .show();
                            }
                            else
                            {
                                db.child("admin").setValue(userName);
                                db.child("SOS").setValue("");

                                //Cách mới: tạo một nhánh trong Joining, lấy tên là chính user này
                                //Mỗi mẫu tin sẽ chứa thông tin về tên circle tham gia, ai làm admin, mẫu tin nhắn cuối cùng.

                                FirebaseDatabase.getInstance().getReference().child("Joining").child(userName).child(circlename).setValue(new JoinModel(circlename, userName));

                                Toast.makeText(Activity_Home.this, "Create circle success!", Toast.LENGTH_SHORT).show();
                                //Tạo một lịch sử
                                HistoryModel historyModel = new HistoryModel(userName, String.format("%s created circle", userName));

                                //Đẩy lịch sử lên firebase
                                FirebaseDatabase.getInstance().getReference().child("Circles").child(circlename).child("History").push().setValue(historyModel);

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
                customDialog.dismiss();
            }
        });

        customDialog.findViewById(R.id.btnInviteCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog.dismiss();
            }
        });

        customDialog.show();
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.e("LocationGPS", "lat: " + location.getLatitude());
        Log.e("LocationGPS", "lng: " + location.getLongitude());
        //myLocation=new MyLocation((float)location.getLatitude(),(float)location.getLongitude());

        int speed =(int)Math.round(location.getSpeed()*3.6);
        //Log.e("LocationGPS","Speed: " + this.speed + "km/h");

        final double coor_x = location.getLatitude();
        final double coor_y = location.getLongitude();

/*        double coor_x = 10.762913;
        double coor_y = 106.6821717;*/

        if(isUpdate)
        {
            //set firebase
        myRef.child("coor_x").setValue(coor_x);
        myRef.child("coor_y").setValue(coor_y);
        myLocation=new LatLng(coor_x, coor_y);
        myRef.child("speed").setValue(speed);
        }

        /*HistoryModel historyModel = new HistoryModel(userName, String.format("%s has arrived %s", userName, "KHTN"));

        //Đẩy lịch sử lên firebase
        FirebaseDatabase.getInstance().getReference().child("Circles").child("abc").child("History").push().setValue(historyModel);

        historyModel = new HistoryModel(userName, String.format("%s has arrived %s", "hoanganh", "KHTN"));

        //Đẩy lịch sử lên firebase
        FirebaseDatabase.getInstance().getReference().child("Circles").child("abc").child("History").push().setValue(historyModel);*/
        //Kiểm tra xem có tới địa điểm nào trong StaticLocation hay không
        //Nếu có thì tiến hành đẩy history lên.

        final Calendar calendar_now = Calendar.getInstance();

        //Duyệt trong cơ sở dữ liệu để tìm ra các circle mà người này tham gia để kiểm tra có đến circle nào hay không?
        FirebaseDatabase.getInstance().getReference().child("Joining").child(userName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot data : dataSnapshot.getChildren()){
                    String circle = data.getValue(JoinModel.class).getCirclename();
                    checkInCheckOut(coor_x, coor_y, calendar_now, userName, circle);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

  /*      for(String circle : listCircleName)
        {
            checkInCheckOut(coor_x, coor_y, calendar_now, userName, circle);
        }*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isUpdate = false;
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

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
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

                        if ((checkin.compareTo(calendar) < 1) && (checkout.compareTo(calendar) > -1))
                        {
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
