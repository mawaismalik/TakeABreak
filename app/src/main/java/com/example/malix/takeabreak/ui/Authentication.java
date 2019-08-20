package com.example.malix.takeabreak.ui;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.malix.takeabreak.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class Authentication extends AppCompatActivity {

    //Firebase

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //Variables
    private ProgressBar phoneBar;
    private String phoneNumber;
    private String verificationId;
    private TextInputLayout enteredCode;
    private Button verifyBtn;
    private String mRecievedCode;
    //callbacks


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        mAuth = FirebaseAuth.getInstance();
        Intent intent = getIntent();
        phoneNumber = intent.getStringExtra("phoneNumber");
        sendVerificatioCode(phoneNumber);

        phoneBar = findViewById(R.id.phoneBar);
        phoneBar.setVisibility(View.VISIBLE);
        enteredCode = findViewById(R.id.code);
        verifyBtn = findViewById(R.id.verificationBtn);
        mRecievedCode = "";
        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String code = enteredCode.getEditText().getText().toString().trim();
                if (code.length() > 6 || code.length() < 6) {
                    enteredCode.setError("Please enter valid code");
                    return;
                } else {
                    verifyCode(code);
                }
            }
        });

    }

    private void verifyCode(String code) {

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);

    }

    private void signInWithCredential(PhoneAuthCredential credential) {

        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    Intent mainIntent = new Intent(Authentication.this, MainActivity.class);
                    startActivity(mainIntent);


                } else {

                    Toast.makeText(getApplicationContext(), "Login failed", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }


    private void sendVerificatioCode(String phoneNumber) {


        // phoneBar.setVisibility(View.VISIBLE);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                Authentication.this,               // Activity (for callback binding)
                mCallbacks
        );

    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            verificationId = s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            mRecievedCode = phoneAuthCredential.getSmsCode();
            if (mRecievedCode != null) {
                enteredCode.getEditText().setText(mRecievedCode);
                verifyCode(mRecievedCode);

            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    };
}