package com.android.project.Activity;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.android.project.Bussiness;
import com.android.project.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.Objects;

public class Activity_Maps extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap=null;
    private boolean begin=false;
    SupportMapFragment mapFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_maps,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.fragment_map);
        mapFragment.getMapAsync(this);

        final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference gpsRef=database.child("Circles");
        //Listen event GPS
        ValueEventListener gps_event=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (mMap==null) {
                    return;
                }
                String x = null,y=null;
                if (Activity_Home.myLocation!=null && !begin)
                {
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(Activity_Home.myLocation));
                    mMap.moveCamera(CameraUpdateFactory.zoomTo(10.0f));
                    begin=true;
                }
                mMap.clear();
                for (DataSnapshot user : dataSnapshot.getChildren())
                {
                    if (user.child("share_location").getValue().toString().contentEquals("0"))
                    {
                        continue;
                    }
                    long realTime=Long.parseLong(user.child("realtime").getValue().toString());
                    long present=(long)(new Date().getTime());
                    long time = present-realTime;         //miliseconds
                    if (time>60000)
                    {
                        continue;            //Vượt quá thời gian để định vị
                    }

                    x=user.child("coor_x").getValue().toString();
                    y=user.child("coor_y").getValue().toString();
                    String name=user.getKey();
                    assert name != null;
                    if (name.contentEquals(Activity_Home.user))
                    {
                        name="you";
                    }
                    Log.e("gps123","listen: "+name+" => "+x+"   "+y);
                    LatLng yourLocation = new LatLng(Double.parseDouble(x),Double.parseDouble(y));
                    mMap.addMarker(new MarkerOptions().position(yourLocation).title(name));
                }

                if (!begin && x!=null)
                {
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(Double.parseDouble(x),Double.parseDouble(y))));
                    mMap.moveCamera(CameraUpdateFactory.zoomTo(10.0f));
                    begin=true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        //Create notify
        gpsRef.child(Activity_Home.circle_Selected).child("Members").addValueEventListener(gps_event);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }
}
