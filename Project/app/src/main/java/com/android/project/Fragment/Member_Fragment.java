package com.android.project.Fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.project.Activity.Activity_MyCircle_Home;
import com.android.project.ModelDatabase.JoinModel;
import com.android.project.ModelDatabase.UserModel;
import com.android.project.R;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

public class Member_Fragment extends Fragment {
    Activity_MyCircle_Home mainActivity;
    Context context = null;
    String circleName;
    boolean isAdmin;
    String admin;

    public Member_Fragment(String circleName, boolean isAdmin, String admin){
        this.isAdmin = isAdmin;
        this.circleName = circleName;
        this.admin = admin;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            context = this.getActivity();
            mainActivity = (Activity_MyCircle_Home) getActivity();
        } catch (IllegalStateException e) {
            throw new IllegalStateException(
                    "MainActivity must implement callbacks");
        }
        Log.e("MemberFragment", "onCreate Member_Fragment");
        Log.e("MemberFragment", "CircleName: " + circleName);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.activity_member,container,false);

        ListView listView = (ListView) view.findViewById(R.id.list_member);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Circles").child(circleName).child("Members");

        final FirebaseListAdapter<UserModel> adapter = new FirebaseListAdapter<UserModel>(mainActivity, UserModel.class,
                R.layout.member_item, databaseReference) {
            @Override
            protected void populateView(View v, UserModel model, int position) {
                TextView tvNameMember = (TextView) v.findViewById(R.id.tv_MemberName);
                TextView tvBattery=(TextView)v.findViewById(R.id.tv_Battery);
                TextView tvSpeed=(TextView)v.findViewById(R.id.tv_Speed);
                ImageView imgStatus=(ImageView)v.findViewById(R.id.image_status);
                TextView textStatus=(TextView)v.findViewById(R.id.text_status);

                Log.e("MemberFragment", "FirebaseListAdapter");

                long present=(long)(new Date().getTime());
                long time=(present-model.getRealtime())/1000;  //second
                if (time < 60)
                {
                    imgStatus.setVisibility(View.VISIBLE);
                    textStatus.setVisibility(View.INVISIBLE);
                }
                else {
                    imgStatus.setVisibility(View.INVISIBLE);
                    long timeAgo=time/60;      //minutes
                    if (timeAgo<60)
                    {
                        textStatus.setVisibility(View.VISIBLE);
                        textStatus.setText(timeAgo+" min ago");
                    }
                }

                tvNameMember.setText(model.getUsername());

                String battery = Integer.toString(model.getBattery());
                String speed = Integer.toString(model.getSpeed());

                if (model.getShare_speed()==1)
                {
                    tvSpeed.setText("Speed: "+speed+"km/h");
                    tvSpeed.setVisibility(View.VISIBLE);
                }
                else
                {
                    tvSpeed.setVisibility(View.INVISIBLE);
                }

                if (model.getShare_battery()==1)
                {
                    tvBattery.setText("Battery: "+battery+"%");
                    tvBattery.setVisibility(View.VISIBLE);
                }
                else
                {
                    tvBattery.setVisibility(View.INVISIBLE);
                }
            }
        };
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final UserModel userModel = adapter.getItem(position);
                long present = (long) (new Date().getTime());
                long time = (present - userModel.getRealtime()) / 1000;  //second
                String status = "offline";
                if (time < 60)
                    status = "online";

                final Dialog customDialog = new Dialog(mainActivity);
                customDialog.setContentView(R.layout.dialog_detail_member);
                Button btnRemove = customDialog.findViewById(R.id.btnRemove);

                ((TextView) customDialog.findViewById(R.id.detailUsername)).setText(String.format("                      Name:          %s", userModel.getUsername()));
                ((TextView) customDialog.findViewById(R.id.detailBattery)).setText(String.format("                    Battery:          %d %%", userModel.getBattery()));
                ((TextView) customDialog.findViewById(R.id.detailSpeed)).setText(String.format("                      Speed:          %d km", userModel.getSpeed()));
                ((TextView) customDialog.findViewById(R.id.detailCoordinates)).setText(String.format("(%s, %s)", Double.toString(userModel.getCoor_x()), Double.toString(userModel.getCoor_y())));
                ((TextView) customDialog.findViewById(R.id.detailStatus)).setText(String.format("                      Status:          %s", status));


                if (isAdmin && !userModel.getUsername().contentEquals(admin)) {
                    btnRemove.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new AlertDialog.Builder(context)
                                    .setTitle("Remove")
                                    .setMessage("Are you sure you want to remove this user?")

                                    // Specifying a listener allows you to take an action before dismissing the dialog.
                                    // The dialog is automatically dismissed when a dialog button is clicked.
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            FirebaseDatabase.getInstance().getReference().child("Joining").child(userModel.getUsername()).child(circleName).removeValue();
                                            FirebaseDatabase.getInstance().getReference().child("Circles").child(circleName).child("Members").child(userModel.getUsername()).removeValue();
                                        }
                                    })

                                    // A null listener allows the button to dismiss the dialog and take no further action.
                                    .setNegativeButton(android.R.string.no, null)
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();


                            customDialog.dismiss();
                        }
                    });
                } else {
                    btnRemove.setVisibility(View.INVISIBLE);
                }

                customDialog.show();
                Toast.makeText(mainActivity, "Hello", Toast.LENGTH_SHORT).show();
            }
        });


        ImageButton addMember = view.findViewById(R.id.btnAddmember);
        addMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog customDialog = new Dialog(view.getContext());
                customDialog.setContentView(R.layout.dialog_input_username);

                ((TextView)customDialog.findViewById(R.id.tvTitle)).setText("Add member");

                customDialog.findViewById(R.id.btnInviteOK).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String userName = ((EditText) customDialog.findViewById(R.id.edInviteName)).getText().toString();

                        if (userName.contentEquals("") == false) {
                            //Thêm bạn vào circle
                            FirebaseDatabase.getInstance().getReference().child("Account").child(userName)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.exists()){
                                                //bus number exists in Database
                                                FirebaseDatabase.getInstance().getReference().child("Circles").child(circleName).child("Members")
                                                        .child(userName).setValue(dataSnapshot.getValue(UserModel.class));

                                                FirebaseDatabase.getInstance().getReference().child("Circles").child(circleName).child("admin").addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        FirebaseDatabase.getInstance().getReference().child("Joining").child(userName).child(circleName)
                                                                .setValue(new JoinModel(circleName, dataSnapshot.getValue(String.class)));
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });


                                                Toast.makeText(mainActivity,"Invite Success !!", Toast.LENGTH_LONG).show();
                                            } else {
                                                //bus number doesn't exists.
                                                Toast.makeText(mainActivity,"Account not exits !!", Toast.LENGTH_LONG).show();
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
        });

        return view;
    }
}
