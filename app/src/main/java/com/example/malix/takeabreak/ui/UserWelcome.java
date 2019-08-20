package com.example.malix.takeabreak.ui;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.malix.takeabreak.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserWelcome extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase mFirebaseDatabase;

    private String TAG = "UserWelcome";
    private TextInputLayout mUsername;
    private Button mBtnGetstarted;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_welcome);


        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();

        mUsername = findViewById(R.id.etUsername);
        mBtnGetstarted = findViewById(R.id.btnGetStarted);

       mAuthListener = new FirebaseAuth.AuthStateListener() {
           @Override
           public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
               FirebaseUser user = firebaseAuth.getCurrentUser();

               if (user != null) {
                   // User is signed in
                   Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                   toastMessage("Successfully signed in with: " + user.getEmail());
               } else {
                   // User is signed out
                   Log.d(TAG, "onAuthStateChanged:signed_out");
                   toastMessage("Successfully signed out.");
               }

           }
       };

        // Read from the database
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Object value = dataSnapshot.getValue();
                Log.d("user name", "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("user namw", "Failed to read value.", error.toException());
            }
        });

        mBtnGetstarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    if(mUsername.getEditText().getText().toString().isEmpty()){
                        mUsername.setError("please enter name");
                        mUsername.setErrorEnabled(true);
                    }else{
                            FirebaseUser user = mAuth.getCurrentUser();
                            String userID = user.getUid();
                            mDatabaseReference.child(userID).child("UserName").setValue(mUsername.getEditText().getText().toString().trim());
                            toastMessage("Adding " + mUsername.getEditText().getText().toString().trim() + " to database...");
                        Intent mainIntent = new Intent( UserWelcome.this , MainActivity.class);
                        startActivity(mainIntent);
                    }


                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });



    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void toastMessage( String msg){
        Toast.makeText(getApplicationContext() ,msg , Toast.LENGTH_SHORT).show();
    }
}
