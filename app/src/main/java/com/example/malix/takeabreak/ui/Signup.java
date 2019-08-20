package com.example.malix.takeabreak.ui;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.malix.takeabreak.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;


public class Signup extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener  mAuthListener;

    private LinearLayout phoneLayout;
    private LinearLayout codeLayout;

    private TextInputLayout phoneText;
    private TextInputLayout codeText;
    private TextInputEditText phoneCode;

    private TextView errorText;

    private ProgressBar phoneBar;
    private ProgressBar codeBar;

    private Button sendBtn;
    private Button btnEmail;

    private String  mVerificationId;
    private CountryCodePicker mCodeSpinner;

    public int btnType = 0;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        mCodeSpinner = findViewById(R.id.codeSpinner);
        phoneText = findViewById(R.id.phoneNum);
        btnEmail = findViewById(R.id.btnEmail);

        // ccp
        mCodeSpinner.registerCarrierNumberEditText(phoneText.getEditText());
        InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        // check if no view has focus:
        View currentFocusedView = getCurrentFocus();
        if (currentFocusedView != null) {
            inputManager.hideSoftInputFromWindow(currentFocusedView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }

        sendBtn = findViewById(R.id.sendBtn);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String phone = phoneText.getEditText().getText().toString().trim();

               if(mCodeSpinner.isValidFullNumber()) {

                   String phoneNumer = mCodeSpinner.getFullNumberWithPlus();
                   Intent intent = new Intent(Signup.this, Authentication.class);
                   intent.putExtra("phoneNumber", phoneNumer);
                   startActivity(intent);
               }
               else{
                   phoneText.setError("Please enter Valid PhoneNumber");
                   return;
               }
            }
        });





    }
}


