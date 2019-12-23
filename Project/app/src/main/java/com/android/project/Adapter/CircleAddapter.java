package com.android.project.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.project.R;
import java.util.List;

public class CircleAddapter extends BaseAdapter {
    private List<String> listCircle;
    private Activity activity;

    public CircleAddapter(Activity activity, List<String> listCircle) {
        this.activity = activity;
        this.listCircle = listCircle;
    }

    @Override
    public int getCount() {
        return listCircle.size();
    }

    @Override
    public Object getItem(int position) {
        return listCircle.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = activity.getLayoutInflater();

        convertView = inflater.inflate(R.layout.line_adapter_circle, null);

        TextView txNameCircle = (TextView) convertView.findViewById(R.id.txNameCircle);

        txNameCircle.setText(listCircle.get(position));

        return convertView;
    }
}