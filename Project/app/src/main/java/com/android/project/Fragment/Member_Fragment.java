package com.android.project.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
                TextView tvMemberStatus = (TextView)v.findViewById(R.id.tv_MemberStatus);
                Log.e("MemberFragment", "FirebaseListAdapter");

                tvNameMember.setText(model.getUsername());

                String battery = Integer.toString(model.getBattery());
                String speed = Integer.toString(model.getSpeed());

                tvMemberStatus.setText("Battery: "+ battery + "%, Speed: " + speed +"km/h");
            }
        };
        listView.setAdapter(adapter);

        return view;
    }
}
