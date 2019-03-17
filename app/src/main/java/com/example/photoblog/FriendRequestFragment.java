package com.example.photoblog;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendRequestFragment extends Fragment {
   private RecyclerView request_list_view;
   private List<FriendRequest> request_list;
   private FirebaseFirestore firebaseFirestore;
   private FirebaseAuth firebaseAuth;
   private FriendRequestRecycleAdapter friendRequestRecycleAdapter;

   private TextView friend;
    public FriendRequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend_request, container, false);

        request_list =  new ArrayList<>();
        request_list_view = view.findViewById(R.id.friend_request_list_view);

        friendRequestRecycleAdapter = new FriendRequestRecycleAdapter(request_list);
        request_list_view.setLayoutManager(new LinearLayoutManager(getActivity()));
        request_list_view.setAdapter(friendRequestRecycleAdapter);


        firebaseFirestore =  FirebaseFirestore.getInstance();
        firebaseAuth =  FirebaseAuth.getInstance();
        String current_user_id=firebaseAuth.getCurrentUser().getUid();

        firebaseFirestore.collection("Notification/"+current_user_id+"/Requests").orderBy("timestamp",Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                for (DocumentChange doc : documentSnapshots.getDocumentChanges())
                {

                    if (doc.getType() == DocumentChange.Type.ADDED)
                    {
                          FriendRequest friendRequest = doc.getDocument().toObject(FriendRequest.class);
                          request_list.add(friendRequest);
                          friendRequestRecycleAdapter.notifyDataSetChanged();
                    }
                }

            }
        });

        firebaseFirestore.collection("Notification/"+current_user_id+"/Requests").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                if (queryDocumentSnapshots.isEmpty()){

                    Toast.makeText(getActivity(), "NO REQUESTS", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

}
