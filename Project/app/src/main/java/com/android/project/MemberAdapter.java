package com.android.project;

        import android.app.Activity;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.BaseAdapter;
        import android.widget.TextView;

        import java.util.List;

public class MemberAdapter extends BaseAdapter {
    private List<String> listMember;
    private Activity activity;
    // BATTERY
    // SPEED


    public MemberAdapter(Activity activity, List<String> listMember) {
        this.activity = activity;
        this.listMember = listMember;
    }

    @Override
    public int getCount() {
        return listMember.size();
    }

    @Override
    public Object getItem(int position) {
        return listMember.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = activity.getLayoutInflater();

        convertView = inflater.inflate(R.layout.member_item, null);

        TextView tv_MemberName = (TextView) convertView.findViewById(R.id.tv_MemberName);
        tv_MemberName.setText(listMember.get(position));


        // TẠM THỜI CODE CỨNG MEMBER STATUS, CẦN UPDATE LẠI
        TextView tv_MemberStatus = (TextView) convertView.findViewById(R.id.tv_MemberStatus);
        tv_MemberStatus.setText("Battery: 100%, Speed: 10km/h");


        return convertView;
    }
}
