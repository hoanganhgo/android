package com.android.project.Activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.project.Bussiness;
import com.android.project.Fragment.Maps_Fragment;
import com.android.project.Fragment.AddLocation_Fragment;
import com.android.project.Fragment.History_Fragment;
import com.android.project.Fragment.Member_Fragment;
import com.android.project.Fragment.SOS_Fragment;
import com.android.project.ModelDatabase.HistoryModel;
import com.android.project.ModelDatabase.UserModel;
import com.android.project.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

@SuppressLint("Registered")
public class Activity_MyCircle_Home extends AppCompatActivity{
    private AddLocation_Fragment addLocation_fragment;
    private SOS_Fragment sos_fragment;
    private History_Fragment history_fragment;

    private Member_Fragment member_fragment;
    private Maps_Fragment maps_fragment;
    private ImageButton btnChat;
    private ImageButton btnLeave;

    private ArrayList<String> lowBatteries=new ArrayList<String>();
    private ArrayList<String> overSpeeds= new ArrayList<String>();
    private Context context;

    private String circleName;
    private String userName;
    private String admin;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mycircle);
        context = this.getApplicationContext();

        BottomNavigationView bottomNav =  findViewById(R.id.bottomNavigationView);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        //Nhận dữ liệu từ Activity_Home
        Intent callingIntent=getIntent();
        Bundle bundle=callingIntent.getExtras();
        circleName=bundle.getString("nameCircle");
        userName=bundle.getString("userName");
        admin = bundle.getString("admin");

        btnChat = findViewById(R.id.ib_chat);
        btnLeave = findViewById(R.id.ib_leave);

        //Read data from database.
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference sosRef=database.child("Circles");
        //Listen event SOS
        ValueEventListener sos_event=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String status= Objects.requireNonNull(dataSnapshot.getValue().toString());
                String parent=dataSnapshot.getKey();
                if (!status.contentEquals("") && !status.contentEquals(userName))
                {
                    Bussiness.notify_SOS(Activity_MyCircle_Home.this, parent, status);
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
                    if (speed>80 && !Bussiness.checkInList(memberName,overSpeeds))  //80km/h
                    {
                        Log.e("test1234","Speed; "+speed+", Battery: "+battery);
                        Bussiness.notify_OverSpeed(Activity_MyCircle_Home.this,circleName,memberName);
                        overSpeeds.add(memberName);
                    }
                    if (battery<10 && !Bussiness.checkInList(memberName,lowBatteries))  //level < 10%
                    {
                        Bussiness.notify_LowBattery(Activity_MyCircle_Home.this,circleName,memberName);
                        lowBatteries.add(memberName);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        //Create notify
        sosRef.child(circleName).child("SOS").addValueEventListener(sos_event);
        sosRef.child(circleName).child("Members").addValueEventListener(help_event);

        final boolean isAdmin = admin.contentEquals(userName);

        sos_fragment = new SOS_Fragment(circleName, userName);
        addLocation_fragment = new AddLocation_Fragment(circleName);
        member_fragment = new Member_Fragment(circleName, userName, admin);
        maps_fragment = new Maps_Fragment();
        history_fragment = new History_Fragment(circleName, userName);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Maps_Fragment()).commit();

        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("userName", userName);
                bundle.putString("nameCircle", circleName);
                Intent intent = new Intent(Activity_MyCircle_Home.this, Activity_Chat.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        btnLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Kiểm tra xem có phải là admin hay không?
                if(!isAdmin) {
                    //Nếu không phải admin thì có thể rời khỏi circle tự do
                    new AlertDialog.Builder(Activity_MyCircle_Home.this)
                            .setTitle("Leave")
                            .setMessage("Leave this circle?")

                            //Specifying a listener allows you to take an action before dismissing the dialog.
                            // The dialog is automatically dismissed when a dialog button is clicked.
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(context, "Leave", Toast.LENGTH_SHORT).show();
                                    FirebaseDatabase.getInstance().getReference().child("Joining").child(userName).child(circleName).removeValue();
                                    FirebaseDatabase.getInstance().getReference().child("Circles").child(circleName).child("Members").child(userName).removeValue();

                                    //Tạo một lịch sử
                                    HistoryModel historyModel = new HistoryModel(userName, String.format("%s left circle", userName));

                                    //Đẩy lịch sử lên firebase
                                    FirebaseDatabase.getInstance().getReference().child("Circles").child(circleName).child("History").push().setValue(historyModel);
                                    finish();
                                }
                            })

                            // A null listener allows the button to dismiss the dialog and take no further action.
                            .setNegativeButton(android.R.string.no, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }else
                {
                    //Nếu là admin thì phải chỉ ra được một admin mới để quản lý circle
                    //Hệ quả: Nếu circle có 1 thành viên duy nhất thì thành viên đó chính là admin, và thành viên đó sẽ không thể rời khỏi circle
                    final Dialog customDialog = new Dialog(Activity_MyCircle_Home.this);
                    customDialog.setContentView(R.layout.dialog_input_username);

                    ((TextView)customDialog.findViewById(R.id.tvTitle)).setText("You are the admin of this circle. If you want to leave this circle, tell us know who is the new admin?");

                    //Tạo một customdialog để cho người dùng nhập admin mới
                    //Ở đây dùng lại layout của invite nên có hơi lỗi về các id của view
                    //Một admin mới phải nằm trong circle thì mới có thể làm admin mới
                    customDialog.findViewById(R.id.btnInviteOK).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final String newadmin = ((EditText) customDialog.findViewById(R.id.edInviteName)).getText().toString();

                            //
                            if (newadmin.contentEquals("") == false) {
                                //Kiểm tra admin mới có nằm trong circle hay không?
                                FirebaseDatabase.getInstance().getReference().child("Circles").child(circleName).child("Members").child(newadmin)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if(dataSnapshot.exists()){
                                                    //bus number exists in Database

                                                    //Nếu đã chỉ ra được admin mới thì tiến hành xóa thông tin
                                                    //Xóa thông tin của bảng Joining

                                                    //Tạo một lịch sử
                                                    HistoryModel historyModel = new HistoryModel(userName, String.format("%s left circle", userName));

                                                    //Đẩy lịch sử lên firebase
                                                    FirebaseDatabase.getInstance().getReference().child("Circles").child(circleName).child("History").push().setValue(historyModel);

                                                    //Tạo một lịch sử
                                                    historyModel = new HistoryModel(userName, String.format("%s is new admin", newadmin));

                                                    //Đẩy lịch sử lên firebase
                                                    FirebaseDatabase.getInstance().getReference().child("Circles").child(circleName).child("History").push().setValue(historyModel);


                                                    FirebaseDatabase.getInstance().getReference().child("Joining").child(userName).child(circleName).removeValue();
                                                    //Xóa khỏi Member
                                                    FirebaseDatabase.getInstance().getReference().child("Circles").child(circleName).child("Members").child(userName).removeValue();

                                                    //Cập nhật lại admin mới
                                                    FirebaseDatabase.getInstance().getReference().child("Circles").child(circleName).child("admin")
                                                            .setValue(newadmin);
                                                    FirebaseDatabase.getInstance().getReference().child("Circles").child(circleName).child("Members").addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            if(dataSnapshot.exists())
                                                            {
                                                                for(DataSnapshot data : dataSnapshot.getChildren()){
                                                                    UserModel user = data.getValue(UserModel.class);

                                                                    FirebaseDatabase.getInstance().getReference().child("Joining").child(user.getUsername())
                                                                            .child(circleName).child("admin").setValue(newadmin);
                                                                }
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                        }
                                                    });

                                                    finish();

                                                    Toast.makeText(Activity_MyCircle_Home.this,"Success !!", Toast.LENGTH_LONG).show();
                                                } else {
                                                    //bus number doesn't exists.
                                                    Toast.makeText(Activity_MyCircle_Home.this,"Account not exits in circle!!", Toast.LENGTH_LONG).show();
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
            }
        });
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.nav_History:
                    InitializeFragment(history_fragment);
                    return true;
                case R.id.nav_Members:
                    InitializeFragment(member_fragment);
                    return true;
                case R.id.nav_Sos:
                    InitializeFragment(sos_fragment);
                    return true;
                case R.id.nav_addLocation:
                    InitializeFragment(addLocation_fragment);
                    return true;
                case R.id.nav_Maps:
                    InitializeFragment(maps_fragment);
                    return true;
            }
            return false;
        }
    };
    private void InitializeFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }
}
