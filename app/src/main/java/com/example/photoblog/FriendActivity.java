package com.example.photoblog;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class FriendActivity extends AppCompatActivity {

    private BottomNavigationView mainBottomNav;
    private FriendAddFragment friendAddFragment;
    private FriendRequestFragment friendRequestFragment;
    private FriendsFragment friendsFragment;
    private FriendsPostFragment friendsPostFragment;
    private Toolbar friendToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
        mainBottomNav =  findViewById(R.id.FriendBottomNav);
        friendToolbar =  findViewById(R.id.friend_toolbar);

        setSupportActionBar(friendToolbar);
        getSupportActionBar().setTitle("Friends");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        friendAddFragment = new FriendAddFragment();
        friendRequestFragment =  new FriendRequestFragment();
        friendsFragment = new FriendsFragment();
        friendsPostFragment =  new FriendsPostFragment();

        replaceFragment(friendsFragment);


        mainBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()){
                    case R.id.bottom_friends:
                        replaceFragment(friendsFragment);
                        getSupportActionBar().setTitle("Friends");
                        return true;
                    case R.id.bottom_requests:
                        replaceFragment(friendRequestFragment);
                        getSupportActionBar().setTitle("Friend Requests");
                        return true;
                    case R.id.bottom_add_Friends:
                        replaceFragment(friendAddFragment);
                        getSupportActionBar().setTitle("Find Friends");
                        return true;
                    case R.id.friends_post:
                        replaceFragment(friendsPostFragment);
                        getSupportActionBar().setTitle("Friends Post");
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    private void replaceFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.friend_container,fragment);
        fragmentTransaction.commit();
    }
}
