package com.android.project.Fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.project.Activity.Activity_MyCircle_Home;
import com.android.project.HttpDataHander;
import com.android.project.ModelDatabase.MessageModel;
import com.android.project.ModelDatabase.StaticLocationModel;
import com.android.project.R;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.maps.CameraUpdate;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AddLocation_Fragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap = null;
    private ImageButton btnAddlocation;
    private ImageButton btnListLocation;
    private Activity_MyCircle_Home mainActivity;
    private String nameCircle;
    private LatLng newLocation = null;

    private final int ADD_MODE = 1;
    private final int UPDATE_MODE = 2;
    private final int DELETE_MODE = 3;

    public AddLocation_Fragment(String nameCircle){
        this.nameCircle = nameCircle;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            mainActivity = (Activity_MyCircle_Home) getActivity();
        } catch (IllegalStateException e) {
            throw new IllegalStateException(
                    "MainActivity must implement callbacks");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_map_addlocation, container, false);
        btnAddlocation = view.findViewById(R.id.btnAddlocation);
        btnAddlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog customDialog = new Dialog(mainActivity);
                customDialog.setContentView(R.layout.dialog_add_location);
                customDialog.setCanceledOnTouchOutside(false);

                customDialog.findViewById(R.id.btnOK).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String nameLocaion = ((EditText) customDialog.findViewById(R.id.edName)).getText().toString();
                        String address = ((EditText) customDialog.findViewById(R.id.edAddress)).getText().toString();
                        if (address.contentEquals("") == false && nameLocaion.contentEquals("") == false) {
                            findLocation(nameLocaion, address);
                        }
                        customDialog.dismiss();
                    }
                });
                customDialog.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        customDialog.dismiss();
                    }
                });
                customDialog.show();
            }
        });


        btnListLocation = view.findViewById(R.id.btnListlocation);
        btnListLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog customDialog = new Dialog(mainActivity);
                customDialog.setContentView(R.layout.dialog_list_location);

                final FirebaseListAdapter<StaticLocationModel> adapter = new FirebaseListAdapter<StaticLocationModel>(mainActivity, StaticLocationModel.class,
                        R.layout.location_in_line, FirebaseDatabase.getInstance().getReference().child("Circles").child(nameCircle).child("StaticLocation")) {
                    @Override
                    protected void populateView(View v, StaticLocationModel model, int position) {
                        // Get references to the views of message.xml
                        TextView tvName = (TextView) v.findViewById(R.id.tvName);
                        TextView tvAddress = (TextView) v.findViewById(R.id.tvAddress);

                        // Set their text
                        tvName.setText(model.getName());
                        tvAddress.setText(String.format("Address: %s", model.getAddress()));
                    }
                };

                ListView listView = customDialog.findViewById(R.id.lvStaticLocation);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        customDialog.dismiss();

                        final Dialog detailDialog = new Dialog(mainActivity);
                        detailDialog.setContentView(R.layout.dialog_detail_location);
                        final StaticLocationModel staticLocationModel = adapter.getItem(position);
                        final EditText edName = (EditText)detailDialog.findViewById(R.id.edDetailName);
                        edName.setText(staticLocationModel.getName());

                        final EditText edAddress = (EditText)detailDialog.findViewById(R.id.edDetailAddress);
                        edAddress.setText(staticLocationModel.getAddress());

                        ((Button)detailDialog.findViewById(R.id.btnDetailOK)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                boolean isChangeName = !(edName.getText().toString().contentEquals(staticLocationModel.getName())) &
                                        !(edName.getText().toString().contentEquals(""));
                                boolean isChangeAddress = !(edAddress.getText().toString().contentEquals(staticLocationModel.getAddress())) &
                                        !(edAddress.getText().toString().contentEquals(""));

                                if(isChangeName){
                                    staticLocationModel.setName(edName.getText().toString());
                                    updateStaticLocation(staticLocationModel, UPDATE_MODE);
                                }
                                if(isChangeAddress){
                                    Log.e("isChangeAddress", "isChangeAddress");
                                    updateStaticLocation(staticLocationModel, DELETE_MODE);
                                    findLocation(edName.getText().toString(), edAddress.getText().toString());
                                }
                                detailDialog.dismiss();
                            }
                        });
                        ((Button)detailDialog.findViewById(R.id.btnDetailCancel)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                detailDialog.dismiss();
                            }
                        });
                        ((Button)detailDialog.findViewById(R.id.btnMoveCamera)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                detailDialog.dismiss();
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(staticLocationModel.getCoor_x(), staticLocationModel.getCoor_y()), 10f));
                            }
                        });
                        ((Button)detailDialog.findViewById(R.id.btnDelete)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                detailDialog.dismiss();
                                updateStaticLocation(staticLocationModel, DELETE_MODE);
                            }
                        });

                        detailDialog.show();
                    }
                });
                customDialog.show();
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.fragment_map);
        mapFragment.getMapAsync(this);

        final DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference().child("Circles").child(nameCircle);

        dataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    if (data.child("StaticLocation").exists()) {
                        //drawLocation();
                    } else {
                        //do something if not exists
                        drawLocation(newLocation);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    public void findLocation(String name, String address){
        Pair<String, String> location = new Pair<String, String>(name, address);
        new AddLocation_Fragment.GetCoordinates().execute(location);
    }

    private class GetCoordinates extends AsyncTask<Pair, Void, String> {
        ProgressDialog dialog = new ProgressDialog(mainActivity);
        String address;
        String name;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Loading...");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(Pair... pairs) {
            String response = "";
            String API_KEY = getText(R.string.google_maps_key).toString();
            Log.e("API_KEY", "API_KEY: " + API_KEY);

            try {
                Pair location = pairs[0];
                name = location.first.toString();
                address = location.second.toString();
                HttpDataHander httpDataHander = new HttpDataHander();
                String url = String.format
                        ("https://maps.googleapis.com/maps/api/geocode/json?address=%s&key=%s", address, API_KEY);
                response = httpDataHander.getHTTPData(url);
                Log.e("doInBackground", String.format("response: %s", response));
                return response;
            } catch (Exception ex) {
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                Log.e("onPostExecute", "is running");
                JSONObject jsonObject = new JSONObject(s);

                String status = jsonObject.get("status").toString();

                Log.e("onPostExecute", String.format("status: %s", status));

                if (status.contentEquals("OK")) {
                    String lat = ((JSONArray) jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry")
                            .getJSONObject("location").get("lat").toString();

                    String lng = ((JSONArray) jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry")
                            .getJSONObject("location").get("lng").toString();

                    String address = ((JSONArray) jsonObject.get("results")).getJSONObject(0).get("formatted_address").toString();


                    Log.e("onPostExecute", String.format("lat: %s", lat));
                    Log.e("onPostExecute", String.format("lng: %s", lng));

                    LatLng location = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                    newLocation = location;
                    //mMap.addMarker(new MarkerOptions().position(location).title(address));
/*                    mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
                    mMap.moveCamera(CameraUpdateFactory.zoomTo(10.0f));*/

                    updateStaticLocation(new StaticLocationModel(name, address, Double.parseDouble(lat), Double.parseDouble(lng)), ADD_MODE);
                    drawLocation(newLocation);
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
                else
                {
                    Toast.makeText(mainActivity, "Thất bại", Toast.LENGTH_SHORT).show();
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateStaticLocation(final StaticLocationModel staticLocationModel, int mode){
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Circles").child(nameCircle).child("StaticLocation");
        final Query query = ref;

        ValueEventListener update_Location = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ref.child(staticLocationModel.getGuid()).setValue(staticLocationModel);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        ValueEventListener delete_Location = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ref.child(staticLocationModel.getGuid()).removeValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        switch (mode){
            case ADD_MODE:
            case UPDATE_MODE:
                ref.addListenerForSingleValueEvent(update_Location);
                break;
            case DELETE_MODE:
                ref.addListenerForSingleValueEvent(delete_Location);
                break;
        }
    }

    private void drawLocation(final LatLng location){
        Toast.makeText(mainActivity, "drawLocation", Toast.LENGTH_SHORT).show();
        FirebaseDatabase.getInstance().getReference().child("Circles").child(nameCircle).child("StaticLocation")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        mMap.clear();
                        LatLng latLng;
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            StaticLocationModel locationModel = data.getValue(StaticLocationModel.class);
                            latLng = new LatLng(locationModel.getCoor_x(), locationModel.getCoor_y());
                            mMap.addMarker(new MarkerOptions().position(latLng).title(locationModel.getName()));
                            if (location == null) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                mMap.moveCamera(CameraUpdateFactory.zoomTo(10.0f));
                            }
                        }

                        if (location != null) {
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
                            mMap.moveCamera(CameraUpdateFactory.zoomTo(10.0f));
                            newLocation = null;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }
}
