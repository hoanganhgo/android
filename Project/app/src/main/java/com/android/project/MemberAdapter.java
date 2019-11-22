package com.android.project;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class MemberAdapter extends BaseAdapter {
    List<Member> lismember;
    Activity activity;
    public MemberAdapter(Activity activity, List<Member> lismember)
    {
        this.lismember = lismember;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return lismember.size();
    }

    @Override
    public Object getItem(int position) {
        return lismember.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = activity.getLayoutInflater();

        convertView = inflater.inflate(R.layout.member_item, null);

        TextView tvNameMember = (TextView) convertView.findViewById(R.id.tv_MemberName);
        TextView tvMemberStatus = (TextView)convertView.findViewById(R.id.tv_MemberStatus);

        Member member = lismember.get(position);

        tvNameMember.setText(member.getUserName());

        String battery = Integer.toString(member.getBattery());
        String speed = Integer.toString(member.getSpeed());

        tvMemberStatus.setText("Battery: "+ battery + "%, Speed: " + speed +"km/h");

        return convertView;
    }
}
