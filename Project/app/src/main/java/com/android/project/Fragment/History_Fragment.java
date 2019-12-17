package com.android.project.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.project.Activity.Activity_MyCircle_Home;
import com.android.project.Bussiness;
import com.android.project.ModelDatabase.HistoryModel;
import com.android.project.ModelDatabase.MessageModel;
import com.android.project.ModelDatabase.UserModel;
import com.android.project.R;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class History_Fragment extends Fragment {
    private String nameCircle;
    private String userName;
    Activity_MyCircle_Home mainActivity;
    Context context = null;

    public History_Fragment(String circleName, String userName){
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
        final View view = inflater.inflate(R.layout.history_fragment,container,false);

        ListView listView = view.findViewById(R.id.lvHistory);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Circles").child(nameCircle).child("History");

        FirebaseListAdapter<HistoryModel> adapter = new FirebaseListAdapter<HistoryModel>(mainActivity, HistoryModel.class,
                R.layout.history_in_line, databaseReference) {
            @Override
            protected void populateView(View v, HistoryModel model, int position) {

                TextView nameUser = v.findViewById(R.id.history_user);
                TextView text = v.findViewById(R.id.history_text);
                TextView time = v.findViewById(R.id.history_time);

                nameUser.setText(model.getUser());
                text.setText(model.getMessage());
                time.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", model.getTime()));
            }
        };

        listView.setAdapter(adapter);

        return view;
    }
}
