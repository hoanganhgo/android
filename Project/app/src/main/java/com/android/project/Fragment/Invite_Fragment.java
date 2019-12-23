package com.android.project.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.project.Activity.Activity_MyCircle_Home;
import com.android.project.R;

public class Invite_Fragment extends Fragment {
    private EditText edUserName;
    private Button btnInvite;
    private String nameCircle;
    private String userName;
    Activity_MyCircle_Home mainActivity;
    Context context = null;

    public Invite_Fragment(String circleName, String userName){
        this.userName = userName;
        this.nameCircle = circleName;
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
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_invite,container,false);

        /*edUserName = view.findViewById(R.id.input_newMember);
        btnInvite = view.findViewById(R.id.btn_addMember);

        btnInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/

        return view;
    }
}
