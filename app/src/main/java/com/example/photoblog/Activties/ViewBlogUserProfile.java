package com.example.photoblog.Activties;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.photoblog.Adapters.BlogRecycleAdapter;
import com.example.photoblog.Model_class.BlogPost;
import com.example.photoblog.Model_class.User;
import com.example.photoblog.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewBlogUserProfile extends AppCompatActivity {
    private TextView profile_user_name;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private String user_id;
    private Uri mainImageUri=null;
    private CircleImageView profile_user_image;

    private RecyclerView blog_list_view;
    private List<BlogPost> blog_list;
    private BlogRecycleAdapter blogRecycleAdapter;
    private DocumentSnapshot lastVisible;
    private Boolean isFirstPageisLoaded = true;
    public  List<User> user_list;
    private String blog_user_id;
    private Button blogAddFriend;
    private Button blogRemoveFriend;
    private Button deleteRequest;
    private Button messageFriend;
    private Toolbar toolbar;
    int t = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_blog_user_profile);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //
        blog_user_id = getIntent().getStringExtra("blogUserId");

        //
        blog_list =  new ArrayList<>();
        user_list =  new ArrayList<>();


        blog_list_view = findViewById(R.id.blog_view_profile_blog_posts);
        blogRecycleAdapter = new BlogRecycleAdapter(blog_list,user_list);
        blogAddFriend = findViewById(R.id.blog_view_addFriend);
        blogRemoveFriend =  findViewById(R.id.blog_view_delete_friend);
        deleteRequest =  findViewById(R.id.blog_view_delete_request);
        messageFriend = findViewById(R.id.message_friend);

        blog_list_view.setLayoutManager(new LinearLayoutManager(this));
        blog_list_view.setAdapter(blogRecycleAdapter);

        profile_user_name = findViewById(R.id.blog_view_profile_user_name);
        profile_user_image= findViewById(R.id.blog_view_profile_user_image);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore =  FirebaseFirestore.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();

        if (!user_id.equals(blog_user_id))
        {
            firebaseFirestore.collection("Notification/" + user_id + "/Friends").document(blog_user_id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                    if (documentSnapshot.exists()) {
                        blogRemoveFriend.setVisibility(View.VISIBLE);
                        messageFriend.setVisibility(View.VISIBLE);
                    } else {
                        firebaseFirestore.collection("Notification/" + blog_user_id + "/Requests").document(user_id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                                if (documentSnapshot.exists()) {
                                    deleteRequest.setVisibility(View.VISIBLE);
                                } else {
                                    blogAddFriend.setVisibility(View.VISIBLE);
                                }
                            }
                        });
                    }
                }
            });
        }

        blogAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> friendrequstMap = new HashMap<>();
                friendrequstMap.put("user_requst_id",user_id);
                friendrequstMap.put("timestamp", FieldValue.serverTimestamp());
                firebaseFirestore.collection("Notification/" + blog_user_id + "/Requests").document(user_id).set(friendrequstMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(ViewBlogUserProfile.this, "Request Sent", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                blogAddFriend.setVisibility(View.INVISIBLE);
                deleteRequest.setVisibility(View.VISIBLE);
            }
        });
        deleteRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseFirestore.collection("Notification/" + blog_user_id + "/Requests").document(user_id).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            blogAddFriend.setVisibility(View.VISIBLE);
                            deleteRequest.setVisibility(View.INVISIBLE);
                            Toast.makeText(ViewBlogUserProfile.this, "request Deleted", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        blogRemoveFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseFirestore.collection("Notification/" + blog_user_id + "/Friends").document(user_id).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            firebaseFirestore.collection("Notification/" +user_id + "/Friends").document(blog_user_id ).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        blogAddFriend.setVisibility(View.VISIBLE);
                                        blogRemoveFriend.setVisibility(View.INVISIBLE);
                                        Toast.makeText(ViewBlogUserProfile.this, "Friend removed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });


        firebaseFirestore.collection("User").document(blog_user_id).get()
                .addOnCompleteListener(ViewBlogUserProfile.this, new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    String user_name =  task.getResult().getString("name");
                    String image =  task.getResult().getString("image");
                    profile_user_name.setText(user_name);

                    RequestOptions placeholderRequest = new RequestOptions();
                    placeholderRequest.placeholder(R.drawable.ic_launcher);
                    Glide.with(ViewBlogUserProfile.this).setDefaultRequestOptions(placeholderRequest).load(image).into(profile_user_image);
                }
            }
        });



            firebaseFirestore = FirebaseFirestore.getInstance();

            blog_list_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    Boolean reachedBottom =  !recyclerView.canScrollVertically(+1);

                    if (reachedBottom){

                        loadMorePost();

                    }

                }
            });

            messageFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent chatIntent = new Intent(ViewBlogUserProfile.this,ChattingActivity.class);
                    chatIntent.putExtra("messageToWhom's id",blog_user_id);
                    startActivity(chatIntent);
                }
            });

        Query firstQuery;firstQuery = firebaseFirestore.collection("Posts").orderBy("timestamp",Query.Direction.DESCENDING);

            firstQuery.addSnapshotListener(ViewBlogUserProfile.this,new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                    if (!documentSnapshots.isEmpty()) {

                        //if (isFirstPageisLoaded)
                        {
                            lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);
                            blog_list.clear();
                            user_list.clear();
                        }


                        for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                            //while (t<3){

                            if (doc.getType() == DocumentChange.Type.ADDED) {

                                String blogPostId = doc.getDocument().getId();
                                final BlogPost blogPost = doc.getDocument().toObject(BlogPost.class).withId(blogPostId);

                                final String blogUserId =  doc.getDocument().getString("user_id");
                                firebaseFirestore.collection("User").document(Objects.requireNonNull(blogUserId)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (blog_user_id.equals(blogUserId))
                                        {
                                            if (task.isSuccessful()){

                                                User user =  task.getResult().toObject(User.class);

                                                if (isFirstPageisLoaded)
                                                {
                                                    user_list.add(0,user);
                                                    blog_list.add(0, blogPost);
                                                }
                                                else
                                                {
                                                    blog_list.add(blogPost);
                                                    user_list.add(user);
                                                }
                                                blogRecycleAdapter.notifyDataSetChanged();
                                                t++;
                                            }
                                        }
                                    }
                                });
                            }
                       // }
                        isFirstPageisLoaded = false;
                        }
                    }
                }
            });

    }

    public void loadMorePost(){
        Query nextQuery = firebaseFirestore.collection("Posts").orderBy("timestamp",Query.Direction.DESCENDING).startAfter(lastVisible).limit(3);

        nextQuery.addSnapshotListener(ViewBlogUserProfile.this,new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if (!documentSnapshots.isEmpty())
                {
                    lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() -1);
                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED)
                        {
                            final String blogPostId = doc.getDocument().getId();
                            final BlogPost blogPost = doc.getDocument().toObject(BlogPost.class).withId(blogPostId);
                            final String blogUserId =  doc.getDocument().getString("user_id");

                            firebaseFirestore.collection("User").document(Objects.requireNonNull(blogUserId)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()){
                                        if (blog_user_id.equals(blogUserId)){

                                            User user =  task.getResult().toObject(User.class);
                                            user_list.add(user);
                                            blog_list.add(blogPost);
                                            blogRecycleAdapter.notifyDataSetChanged();
                                        }
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });
    }
}
