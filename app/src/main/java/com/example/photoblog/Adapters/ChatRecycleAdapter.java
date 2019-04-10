package com.example.photoblog.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.photoblog.Model_class.MessageRetriving;
import com.example.photoblog.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ChatRecycleAdapter extends RecyclerView.Adapter<ChatRecycleAdapter.ViewHolder> {
    public Context context;
    public List<MessageRetriving> messageRetrivingList;
    public FirebaseFirestore firebaseFirestore;
    public FirebaseAuth firebaseAuth;
    public String current_user_id;

    public ChatRecycleAdapter(List<MessageRetriving> messageRetrivingList){
        this.messageRetrivingList=messageRetrivingList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        firebaseFirestore =  FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        current_user_id = firebaseAuth.getCurrentUser().getUid();

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat_list_view, viewGroup, false);
        context = viewGroup.getContext();


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        String message_text = messageRetrivingList.get(i).getMessage();
        String message_send_to_user = messageRetrivingList.get(i).getMessage_sent_to_user();



        if (current_user_id.equals(message_send_to_user)){
            viewHolder.recievedText(message_text);
            viewHolder.recieved_text.setVisibility(View.VISIBLE);
        }
        else {
            viewHolder.sentText(message_text);
            viewHolder.sent_text.setVisibility(View.VISIBLE);
        }



    }

    @Override
    public int getItemCount() {
        return messageRetrivingList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {

        private TextView recieved_text;
        private TextView sent_text;
        private View mView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
             mView = itemView;
             
        }

        public void recievedText(String message)
        {
            recieved_text =  mView.findViewById(R.id.recived_text);
            recieved_text.setText(message);
        }
        public void sentText (String message)
        {
            sent_text =  mView.findViewById(R.id.sent_text);
            sent_text.setText(message);
        }

    }
}
