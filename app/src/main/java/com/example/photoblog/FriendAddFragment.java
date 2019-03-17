package com.example.photoblog;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendAddFragment extends Fragment {

    private RecyclerView all_user_list_view;
    private List<User> user_list;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private FriendAddRecycleAdapter friendAddRecycleAdapter;

    public FriendAddFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend_add, container, false);

        user_list =  new ArrayList<>();
        all_user_list_view = view.findViewById(R.id.all_user_list);

        friendAddRecycleAdapter = new FriendAddRecycleAdapter(user_list);
        all_user_list_view.setLayoutManager(new LinearLayoutManager(getActivity()));
        all_user_list_view.setAdapter(friendAddRecycleAdapter);


        firebaseFirestore =  FirebaseFirestore.getInstance();
        firebaseAuth =  FirebaseAuth.getInstance();


        firebaseFirestore.collection("User").orderBy("name", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                for (DocumentChange doc : documentSnapshots.getDocumentChanges())
                {

                    if (doc.getType() == DocumentChange.Type.ADDED)
                    {
                        User user = doc.getDocument().toObject(User.class);
                        user_list.add(user);
                        friendAddRecycleAdapter.notifyDataSetChanged();
                    }
                }
            }
        });

        // Inflate the layout for this fragment
        return view;
    }
}
