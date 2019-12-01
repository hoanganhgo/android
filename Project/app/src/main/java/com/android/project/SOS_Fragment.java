package com.android.project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import pl.droidsonroids.gif.GifImageView;

public class SOS_Fragment extends Fragment {

    private Button button_SOS;
    private GifImageView image_sos;
    private ImageView image_smile;
    private DatabaseReference myRef=null;

    private boolean sos=false;
    private String userName;
    private String circleName;

    SOS_Fragment(String circleName, String userName)
    {
        this.circleName=circleName;
        this.userName=userName;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_sos,container,false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        button_SOS=(Button) view.findViewById(R.id.btn_SOS);
        image_sos=(GifImageView)view.findViewById(R.id.sos_gif);
        image_smile=(ImageView)view.findViewById(R.id.smile);

        DatabaseReference database= FirebaseDatabase.getInstance().getReference();
        myRef=database.child("Circles").child(circleName).child("SOS");

        button_SOS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sos)
                {
                    button_SOS.setText("TURN ON SOS");
                    image_sos.setVisibility(View.INVISIBLE);
                    image_smile.setVisibility(View.VISIBLE);

                    //set SOS empty
                    myRef.setValue("");
                }
                else
                {
                    button_SOS.setText("TURN OFF SOS");
                    image_smile.setVisibility(View.INVISIBLE);
                    image_sos.setVisibility(View.VISIBLE);

                    //set database
                    myRef.setValue(userName);
                }

                sos=!sos;
            }
        });
    }

    public void onDestroyView() {
        super.onDestroyView();

        button_SOS.setText("TURN ON SOS");
        image_sos.setVisibility(View.INVISIBLE);
        image_smile.setVisibility(View.VISIBLE);

        //set SOS empty
        myRef.setValue("");
        sos=false;
    }
}
