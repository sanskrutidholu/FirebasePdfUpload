package com.myprojects.firebasepdfupload;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText et_phone, et_otp;
    private Button btn_verify, btn_generate;
    private String vId;
    private LinearLayout llnumber, llotp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpUi();

        btn_generate.setOnClickListener(v -> {
            String phone = "+91" + et_phone.getText().toString();
            if (et_phone.getText().toString().isEmpty()){
                Toast.makeText(MainActivity.this, "Please enter valid phone number", Toast.LENGTH_SHORT).show();
            }else{
                sendCode(phone);
            }
        });

        btn_verify.setOnClickListener(v -> {
            String code = et_otp.getText().toString();
            if (code.isEmpty()){
                Toast.makeText(MainActivity.this, "Please enter code", Toast.LENGTH_SHORT).show();
            }else{
                verifyCode(code);
            }
        });
    }

    private void sendCode(String phone) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phone)
                .setTimeout(60l, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(callbacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            vId = s;
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
           String code = phoneAuthCredential.getSmsCode();
            if (code != null){
                llnumber.setVisibility(View.GONE);
                llotp.setVisibility(View.VISIBLE);
                et_otp.setText(code);
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Log.e("","otperror: " + e.getMessage());
            llnumber.setVisibility(View.VISIBLE);
            llotp.setVisibility(View.GONE);
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(vId,code);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                Intent i = new Intent(this,HomeActivity.class);
                startActivity(i);
                finish();
            }else{
                Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setUpUi() {
        auth = FirebaseAuth.getInstance();
        llnumber = findViewById(R.id.llphone);
        llotp = findViewById(R.id.llotp);
        et_phone = findViewById(R.id.idEdtPhoneNumber);
        et_otp = findViewById(R.id.idEdtOtp);
        btn_verify = findViewById(R.id.idBtnVerify);
        btn_generate = findViewById(R.id.idBtnGetOtp);
    }
}