package com.myprojects.firebasepdfupload;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth fAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        fAuth = FirebaseAuth.getInstance();
        mAuthStateListener = firebaseAuth -> {
            FirebaseUser mFirebaseUser = firebaseAuth.getCurrentUser();
            if (mFirebaseUser != null) {
                Intent i = new Intent(SplashActivity.this, HomeActivity.class);
                startActivity(i);
                finish();
            } else {
                Intent i = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        };

        int SPLASH_TIME_OUT = 3000;
        new Handler().postDelayed(() -> logincheck(), SPLASH_TIME_OUT);
    }

    public void logincheck(){
        fAuth.addAuthStateListener(mAuthStateListener);
    }
}