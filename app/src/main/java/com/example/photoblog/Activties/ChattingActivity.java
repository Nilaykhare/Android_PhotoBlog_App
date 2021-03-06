package com.example.photoblog.Activties;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.photoblog.Adapters.ChatRecycleAdapter;
import com.example.photoblog.Model_class.MessageRetriving;
import com.example.photoblog.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.photoblog.Activties.CommentActivity.blog_post_id;

public class ChattingActivity extends AppCompatActivity {

    private Toolbar chat_toolbar;
    private Button messageSendBtn;
    private EditText messageField;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private String current_user_id;
    private String reciever_id;
    private String reciever_name;

    private RecyclerView chatRecyclerView;
    private ChatRecycleAdapter chatRecycleAdapter;
    private List<MessageRetriving> messageRetrivingList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);

        reciever_id = getIntent().getStringExtra("messageToWhom's id");
        reciever_name = getIntent().getStringExtra("user_name");

        chat_toolbar = findViewById(R.id.chat_toolbar);
        setSupportActionBar(chat_toolbar);
        getSupportActionBar().setTitle(reciever_name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        messageField = findViewById(R.id.chat_message_field);
        messageSendBtn = findViewById(R.id.chat_send_btn);
        chatRecyclerView = findViewById(R.id.chat_recycleview);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore =  FirebaseFirestore.getInstance();

        messageRetrivingList = new ArrayList<>();
        chatRecycleAdapter = new ChatRecycleAdapter(messageRetrivingList);

        chatRecyclerView.setLayoutManager(new LinearLayoutManager(ChattingActivity.this));
        chatRecyclerView.setAdapter(chatRecycleAdapter);

        current_user_id = firebaseAuth.getCurrentUser().getUid();

        messageSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Date currentTime = Calendar.getInstance().getTime();
                String text_message =  messageField.getText().toString();
                if (!TextUtils.isEmpty(text_message)){

                    //add details
                    final Map<String,Object> send_text =  new HashMap<>();
                    send_text.put("timestamp",FieldValue.serverTimestamp());
                    send_text.put("message_sent_to_user",reciever_id);
                    send_text.put("message",text_message);

                    firebaseFirestore.collection("Chats/"+current_user_id+"/"+reciever_id).document().set(send_text).addOnCompleteListener(ChattingActivity.this,new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                messageField.setText("");
                                firebaseFirestore.collection("Chats/"+reciever_id+"/"+current_user_id).document().set(send_text).addOnCompleteListener(ChattingActivity.this, new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                    }
                                });
                            }
                        }
                    });

                    //add details
                    Map<String, Object> reciever_text = new HashMap<>();
                }
                else
                {
                    Toast.makeText(ChattingActivity.this,"Enter message",Toast.LENGTH_SHORT).show();

                }

            }
        });

        firebaseFirestore.collection("Chats/"+current_user_id+"/"+reciever_id).orderBy("timestamp", Query.Direction.ASCENDING).addSnapshotListener(ChattingActivity.this,new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {

                if (!queryDocumentSnapshots.isEmpty()){
                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()){

                        if (doc.getType()==DocumentChange.Type.ADDED){
                            MessageRetriving message =doc.getDocument().toObject(MessageRetriving.class);
                            messageRetrivingList.add(message);
                            chatRecycleAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });


    }
}
