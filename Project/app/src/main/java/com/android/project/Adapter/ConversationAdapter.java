package com.android.project.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.project.ModelDatabase.ConversationModel;
import com.android.project.R;

import java.util.List;

public class ConversationAdapter extends BaseAdapter {
    private List<ConversationModel> listConversation;
    private Activity activity;

    public ConversationAdapter(Activity activity, List<ConversationModel> listConversation) {
        this.activity = activity;
        this.listConversation = listConversation;
    }

    @Override
    public int getCount() {
        return listConversation.size();
    }

    @Override
    public Object getItem(int position) {
        return listConversation.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = activity.getLayoutInflater();

        convertView = inflater.inflate(R.layout.conversation_item_adapter, null);

        TextView nameConversation = (TextView) convertView.findViewById(R.id.nameConversation);
        TextView txtLastMessage = (TextView) convertView.findViewById(R.id.txtLastMessage);

        nameConversation.setText(listConversation.get(position).getCircleNameConversation());
        txtLastMessage.setText(listConversation.get(position).getLastMessageConversation());

        return convertView;
    }
}
