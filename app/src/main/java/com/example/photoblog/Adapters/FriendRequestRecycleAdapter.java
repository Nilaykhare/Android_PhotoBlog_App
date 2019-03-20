package com.example.photoblog.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.photoblog.Model_class.FriendRequest;
import com.example.photoblog.R;
import com.example.photoblog.Activties.ViewBlogUserProfile;
import com.google.android.gms.tasks.OnCompleteListener;
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

public class FriendRequestRecycleAdapter extends RecyclerView.Adapter<FriendRequestRecycleAdapter.ViewHolder> {


    public List<FriendRequest> request_list;
    public Context context;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;


    public FriendRequestRecycleAdapter(List<FriendRequest>request_list) {
        this.request_list =request_list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        firebaseFirestore =  FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.friend_request_list_view, viewGroup, false);

        context = viewGroup.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {

        viewHolder.setIsRecyclable(false);

        final String user_id = request_list.get(i).getUser_requst_id();

        firebaseFirestore.collection("User").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()){

                    String userName = task.getResult().getString("name");
                    String userImage = task.getResult().getString("image");
                    //####### setting userdata ########
                    viewHolder.setUserData(userName,userImage);

                }
            }
        });

        final String current_user_id = firebaseAuth.getCurrentUser().getUid();
        viewHolder.acceptRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> friendMap = new HashMap<>();
                friendMap.put("user_requst_id",user_id);
                friendMap.put("timestamp", FieldValue.serverTimestamp());
                friendMap.put("friendship","yes");
                final Map<String, Object> friendMap2 = new HashMap<>();
                friendMap2.put("user_requst_id",current_user_id);
                friendMap2.put("timestamp", FieldValue.serverTimestamp());
                friendMap2.put("friendship","yes");
                firebaseFirestore.collection("Notification/" + current_user_id + "/Friends").document(user_id).set(friendMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){

                            firebaseFirestore.collection("Notification/" + user_id + "/Friends").document(current_user_id).set(friendMap2).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(context, "Friend Add", Toast.LENGTH_SHORT).show();
                                        firebaseFirestore.collection("Notification/"+current_user_id+"/Requests").document(user_id).delete();
                                        request_list.remove(i);
                                        notifyDataSetChanged();
                                        firebaseFirestore.collection("Notification/"+user_id+"/Requests").document(current_user_id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                            @Override
                                            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                                                if (documentSnapshot.exists()){
                                                    firebaseFirestore.collection("Notification/"+user_id+"/Requests").document(current_user_id).delete();
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });

       viewHolder.rejectRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseFirestore.collection("Notification/"+current_user_id+"/Requests").document(user_id).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(context, "Request deleted", Toast.LENGTH_SHORT).show();
                            request_list.remove(i);
                            notifyDataSetChanged();
                        }
                    }
                });
            }
        });
        viewHolder.requestUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent BlogUserIntent = new Intent(context, ViewBlogUserProfile.class);
                BlogUserIntent.putExtra("blogUserId",user_id);
                context.startActivity(BlogUserIntent);
            }
        });




    }

    @Override
    public int getItemCount() {
        return request_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private View mView;
        private CircleImageView requestUserImage;
        private TextView requestUsername;
        private ImageView acceptRequest;
        private ImageView rejectRequest;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            acceptRequest =  mView.findViewById(R.id.request_yes);
            requestUsername =  mView.findViewById(R.id.request_user_name);
            rejectRequest =  mView.findViewById(R.id.request_no);
        }

        public void setUserData(String name,String image)
        {
            requestUsername =  mView.findViewById(R.id.request_user_name);
            requestUserImage = mView.findViewById(R.id.request_user_image);

            requestUsername.setText(name);
            RequestOptions placeHolder = new RequestOptions();
            placeHolder.placeholder(R.color.WhiteTransparentHalf);
            Glide.with(context).applyDefaultRequestOptions(placeHolder).load(image).into(requestUserImage);
        }
    }
}