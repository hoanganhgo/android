package com.android.project.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.android.project.ModelDatabase.JoinModel;
import com.android.project.ModelDatabase.MemberModel;
import com.android.project.Object.Member;
import com.android.project.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MemberAdapter extends BaseAdapter {
    private Activity activity;
    private List<MemberModel> memberModels;
    private String circleName;
    private View view;

    public MemberAdapter(Activity activity, String circleName)
    {
        this.circleName = circleName;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return memberModels.size();
    }

    @Override
    public Object getItem(int position) {
        return memberModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        view = convertView;
        final int pos = position;

        FirebaseDatabase.getInstance().getReference().child("join").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final List<String> usernames = new ArrayList<String>();

                for(DataSnapshot datajoin : dataSnapshot.getChildren())
                {
                    if(circleName.contentEquals(datajoin.getValue(JoinModel.class).getCirclename()))
                    {
                        usernames.add(datajoin.getValue(JoinModel.class).getUsername());
                    }
                }

                FirebaseDatabase.getInstance().getReference().child("Account").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot dataaccount : dataSnapshot.getChildren())
                        {
                            MemberModel memberModel = dataaccount.getValue(MemberModel.class);
                            for(String username : usernames)
                            {
                                if(username.contentEquals(memberModel.getUserName()))
                                {
                                    memberModels.add(memberModel);
                                }
                            }
                        }

                        LayoutInflater inflater = activity.getLayoutInflater();

                        view = inflater.inflate(R.layout.member_item, null);

                        TextView tvNameMember = (TextView) view.findViewById(R.id.tv_MemberName);
                        TextView tvMemberStatus = (TextView)view.findViewById(R.id.tv_MemberStatus);

                        MemberModel member = memberModels.get(pos);

                        tvNameMember.setText(member.getUserName());

                        String battery = Integer.toString(member.getBattery());
                        String speed = Integer.toString(member.getSpeed());

                        tvMemberStatus.setText("Battery: "+ battery + "%, Speed: " + speed +"km/h");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return convertView;
    }
}
