package com.example.photoblog.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    public ChatRecycleAdapter(List<MessageRetriving> messageRetrivingList){
        this.messageRetrivingList=messageRetrivingList;
    }

    /*
    public List<Comments> commentsList;
    public Context context;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    public CommentRecylceAdapter(List<Comments> commentsList){

        this.commentsList = commentsList;

    }
     */

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        firebaseFirestore =  FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat_list_view, viewGroup, false);
        context = viewGroup.getContext();


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return messageRetrivingList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
