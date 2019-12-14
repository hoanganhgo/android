package com.android.project.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.project.Activity.Activity_MyCircle_Home;
import com.android.project.ModelDatabase.UserModel;
import com.android.project.R;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;

public class Member_Fragment extends Fragment {
    Activity_MyCircle_Home mainActivity;
    Context context = null;
    String circleName;

    public Member_Fragment(String circleName){
        this.circleName = circleName;
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


        FirebaseListAdapter<UserModel> adapter = new FirebaseListAdapter<UserModel>(mainActivity, UserModel.class,
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

        return view;
    }
}
