package com.example.photoblog.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.photoblog.R;
import com.example.photoblog.Model_class.User;
import com.example.photoblog.Activties.ViewBlogUserProfile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendAddRecycleAdapter extends RecyclerView.Adapter<FriendAddRecycleAdapter.ViewHolder> {

    public Context context;
    public List<User> user_list;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    public FriendAddRecycleAdapter (List<User> user_list) {
        this.user_list = user_list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        firebaseFirestore =  FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.friend_all_user_list_view, viewGroup, false);

        context = viewGroup.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {

        final int x =i;

        viewHolder.setIsRecyclable(false);

        //############ name and image of request user######
         final String user_id = user_list.get(i).getId();
         String userName = user_list.get(i).getName();
         String userImage = user_list.get(i).getImage();
         viewHolder.setUserData(userName,userImage);

        final String current_user_id = firebaseAuth.getCurrentUser().getUid();

        // ######## visibility of corresponding button according to status of request#######
        firebaseFirestore.collection("Notification/" + current_user_id + "/Friends").document(user_id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent( DocumentSnapshot documentSnapshot,  FirebaseFirestoreException e) {
                if (documentSnapshot.exists())
                {
                    viewHolder.friendremovebtn.setVisibility(View.VISIBLE);
                }
                else{

                    firebaseFirestore.collection("Notification/" + user_id + "/Requests").document(current_user_id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent( DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                            if (documentSnapshot.exists())
                            {
                                viewHolder.requestCancel.setVisibility(View.VISIBLE);
                            }
                            else
                            {
                                viewHolder.friendAddbtn.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }
            }
        });

        //########### sending request##########
        viewHolder.friendAddbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> friendrequstMap = new HashMap<>();
                friendrequstMap.put("user_requst_id", current_user_id );
                friendrequstMap.put("timestamp", FieldValue.serverTimestamp());
                firebaseFirestore.collection("Notification/" + user_id + "/Requests").document(current_user_id).set(friendrequstMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(context, "Request Sent", Toast.LENGTH_SHORT).show();
                            viewHolder.friendAddbtn.setVisibility(View.INVISIBLE);
                            viewHolder.requestCancel.setVisibility(View.VISIBLE);
                        }
                    }
                });

            }
        });

        //############### Deleting request ###########
        viewHolder.requestCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseFirestore.collection("Notification/" + user_id + "/Requests").document(current_user_id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Request Cancelled", Toast.LENGTH_SHORT).show();
                        viewHolder.requestCancel.setVisibility(View.INVISIBLE);
                        viewHolder.friendAddbtn.setVisibility(View.VISIBLE);
                    }
                });
            }
        });

        //############# entering to user profile ##########
        viewHolder.requestUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent BlogUserIntent = new Intent(context, ViewBlogUserProfile.class);
                BlogUserIntent.putExtra("blogUserId",user_id);
                context.startActivity(BlogUserIntent);
            }
        });


        //########## delete or remove friend from friendlist #########
        viewHolder.friendremovebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.friendremovebtn.setVisibility(View.INVISIBLE);
                viewHolder.friendremovebtnFinal.setVisibility(View.VISIBLE);
            }
        });
        viewHolder.friendremovebtnFinal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> friendMap = new HashMap<>();
                friendMap.put("user_requst_id",user_id);
                friendMap.put("timestamp", FieldValue.serverTimestamp());
                friendMap.put("friendship","yes");
                firebaseFirestore.collection("Notification/" + current_user_id + "/Friends").document(user_id).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(context, "Friend removed", Toast.LENGTH_SHORT).show();
                            viewHolder.friendremovebtnFinal.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return user_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private View mView;
        private CircleImageView requestUserImage;
        private TextView requestUsername;
        private ImageView acceptRequest;
        private Button friendAddbtn;
        private Button friendremovebtn;
        private Button friendremovebtnFinal;
        private Button requestCancel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView= itemView;
            friendAddbtn =  mView.findViewById(R.id.all_add_friend_button);
            friendremovebtn =  mView.findViewById(R.id.all_remove_friend);
            requestCancel =  mView.findViewById(R.id.all_request_cancel);
            requestUsername =  mView.findViewById(R.id.all_friend_user_name);
            requestUsername =  mView.findViewById(R.id.all_friend_user_name);
            friendremovebtnFinal = mView.findViewById(R.id.all_remove_friend_final);
        }
        public void setUserData(String name,String image)
        {
            requestUsername =  mView.findViewById(R.id.all_friend_user_name);
            requestUserImage = mView.findViewById(R.id.all_friend_user_image);

            requestUsername.setText(name);
            RequestOptions placeHolder = new RequestOptions();
            placeHolder.placeholder(R.color.WhiteTransparentHalf);
            Glide.with(context).applyDefaultRequestOptions(placeHolder).load(image).into(requestUserImage);
        }
    }
}
