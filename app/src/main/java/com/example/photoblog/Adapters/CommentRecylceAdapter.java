package com.example.photoblog.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.photoblog.Activties.CommentActivity;
import com.example.photoblog.Model_class.Comments;
import com.example.photoblog.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentRecylceAdapter extends RecyclerView.Adapter<CommentRecylceAdapter.ViewHolder>{
    public List<Comments> commentsList;
    public Context context;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    public CommentRecylceAdapter(List<Comments> commentsList){

        this.commentsList = commentsList;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        firebaseFirestore =  FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.comment_list_view, viewGroup, false);
        context = viewGroup.getContext();
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {

        final String commentID = commentsList.get(i).CommentId;

        viewHolder.setIsRecyclable(false);

        String commentMessage = commentsList.get(i).getMessage();
        viewHolder.setComment_message(commentMessage);

        String user_id = commentsList.get(i).getUser_id();

        firebaseFirestore.collection("User").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()){

                    String userName = task.getResult().getString("name");
                    String userImage = task.getResult().getString("image");

                    viewHolder.setUserData(userName,userImage);

                }
            }
        });

        try {
            long milliseconds =  commentsList.get(i).getTimestamp().getTime();
            String dateString = DateFormat.format("dd/MM/yyyy", new Date(milliseconds)).toString();
            viewHolder.setTime(dateString);
        }
        catch (Exception e){

        }
        String current_user_id =  firebaseAuth.getCurrentUser().getUid();
        if (current_user_id.equals(user_id) || current_user_id.equals(CommentActivity.blog_user_id)){
            viewHolder.deleteIcon.setVisibility(View.VISIBLE);
        }
        viewHolder.deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.deleteIcon.setVisibility(View.INVISIBLE);
                viewHolder.deleteconfirm.setVisibility(View.VISIBLE);
            }
        });
        viewHolder.deleteconfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseFirestore.collection("Posts/"+CommentActivity.blog_post_id+"/Comments").document(commentID).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(context, "Comment deleted", Toast.LENGTH_SHORT).show();
                            commentsList.remove(i);
                            notifyDataSetChanged();
                        }
                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return commentsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;

        private TextView comment_message;
        private TextView comment_user_name;
        private CircleImageView comment_user_image;
        private TextView commentDate;
        private Button deleteconfirm;
        private ImageView deleteIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
            deleteconfirm = mView.findViewById(R.id.commentDeletePostBtn);
            deleteIcon = mView.findViewById(R.id.commentDeleteIcon);
        }
        public void setComment_message(String message){

            comment_message = mView.findViewById(R.id.comment_message);
            comment_message.setText(message);

        }
        public void setUserData(String name,String image)
        {
            comment_user_name =  mView.findViewById(R.id.comment_user_name);
            comment_user_image = mView.findViewById(R.id.comment_user_image);

            comment_user_name.setText(name);
            RequestOptions placeHolder = new RequestOptions();
            placeHolder.placeholder(R.color.WhiteTransparentHalf);
            Glide.with(context).applyDefaultRequestOptions(placeHolder).load(image).into(comment_user_image);
        }
        public void setTime(String date)
        {
            commentDate =  mView.findViewById(R.id.commentDate);
            commentDate.setText(date);
        }
    }
}
