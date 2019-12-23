package com.android.project.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.project.Adapter.ConversationAdapter;
import com.android.project.Bussiness;
import com.android.project.ModelDatabase.ConversationModel;
import com.android.project.R;

import java.util.ArrayList;
import java.util.List;

public class Activity_List_Conversations extends AppCompatActivity {

    //private FirebaseListAdapter<MessageModel> adapter;

    private String userName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversations_list);

        // bắt gói Intent bundle từ Home Activity, lấy user name
        Intent callingIntent = getIntent();
        Bundle myBundle = callingIntent.getExtras();
        userName = myBundle.getString("userName");

        //Toast.makeText(getApplicationContext(), "qk ưab k;e;ls::::" + userName, Toast.LENGTH_LONG).show();/////////////////

        // hiển thị tin nhắn
        displayMessagesList();


    }

    // hiển thị tin nhắn
    private void displayMessagesList() {

        final List<String> listCircleName = Bussiness.getCircleUserJoinning(userName);  // List chứa ds circle mà userName tham gia////////////////////////

        final List<ConversationModel> ConversationList = new ArrayList<ConversationModel>(); // List cuộc trò chuyện (tên circle + last mess)

        for (int i=0;i<listCircleName.size();i++) {
            ConversationModel c1 = new ConversationModel(listCircleName.get(i),"Username" + i +": Let's start your conversation");
            ConversationList.add(c1);
        }


//        // Tạm thời đổ dữ liệu code cứng
//        ConversationModel c1 = new ConversationModel("General", "Username: abc... xyz");
//        ConversationModel c2 = new ConversationModel();
//        ConversationModel c3 = new ConversationModel();
//        ConversationModel c4 = new ConversationModel();
//        List<ConversationModel> ConversationTEMPlist = new ArrayList<ConversationModel>();
//        ConversationTEMPlist.add(c1);
//        ConversationTEMPlist.add(c2);
//        ConversationTEMPlist.add(c3);
//        ConversationTEMPlist.add(c4);



        ListView listOfConversations = (ListView) findViewById(R.id.list_of_conversations);
        ConversationAdapter conversationAddapter = new ConversationAdapter(this, ConversationList);

        listOfConversations.setAdapter(conversationAddapter);


        // onclick cho các message item

        listOfConversations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ConversationModel conversation_Selected = ConversationList.get(position);
                String user = userName;
                Intent intent = new Intent(Activity_List_Conversations.this, Activity_Chat.class);
                Bundle bundle = new Bundle();
                bundle.putString("nameCircle", conversation_Selected.getCircleNameConversation());
                bundle.putString("userName", userName);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
}
