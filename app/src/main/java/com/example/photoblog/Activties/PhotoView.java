package com.example.photoblog.Activties;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.photoblog.Fragments.NotificationFragment.PhotoFragment;
import com.example.photoblog.R;

public class PhotoView extends AppCompatActivity {
    public static String notification_blogPostId;
    private PhotoFragment photoFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);

        photoFragment = new PhotoFragment();
        replaceFragment(photoFragment);

        notification_blogPostId = getIntent().getStringExtra("blogPostId");

    }
    private void replaceFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.photo_cont,fragment);
        fragmentTransaction.commit();
    }
}
