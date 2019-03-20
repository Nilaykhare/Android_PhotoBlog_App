package com.example.photoblog.Activties;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.photoblog.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPasswordActivity extends AppCompatActivity {

    private Button resetEmailbtn;
    private EditText resetEmailField;
    private TextView descProce;
    private FirebaseAuth firebaseAuth;
    private Toolbar forget_toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        resetEmailbtn =  findViewById(R.id.reset_email_button);
        resetEmailField = findViewById(R.id.reset_email_field);
        forget_toolbar = findViewById(R.id.forget_toolbar);

        setSupportActionBar(forget_toolbar);
        getSupportActionBar().setTitle("Forget password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        descProce =  findViewById(R.id.descPro);

        descProce.setText("Enter your Valid Email id in the text field. We will send an" +
                " email to your email account which consists of password reset Link. Click on that Link to reset to reset your password.");

        firebaseAuth =  FirebaseAuth.getInstance();

        resetEmailbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail =  resetEmailField.getText().toString().trim();
                if (TextUtils.isEmpty(userEmail)){
                    Toast.makeText(ForgetPasswordActivity.this, "Enter email Field", Toast.LENGTH_SHORT).show();
                }
                else {
                    firebaseAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                                Toast.makeText(ForgetPasswordActivity.this, "Email is sent", Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(ForgetPasswordActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}
