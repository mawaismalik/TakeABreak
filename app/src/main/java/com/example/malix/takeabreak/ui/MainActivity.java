package com.example.malix.takeabreak.ui;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.malix.takeabreak.R;
import com.example.malix.takeabreak.domain.datalayer.ConnectivityActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    // ToolBar
    private Toolbar mHotspotToolbar;
    private CardView mPesonalhold;
    private CardView mGrrouphold;
    private CardView mSchedulelhold;
    private  int PERMISSION_REQUEST_READ_PHONE_STATE,PERMISSION_REQUEST_CODE;
    private CardView mSetting;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Button mBtnUsername;
    private DatabaseReference myRef;
    private FirebaseDatabase mFirebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         mAuth = FirebaseAuth.getInstance();
         mBtnUsername = findViewById(R.id.btnusername);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = myRef = mFirebaseDatabase.getReference();

        mPesonalhold = findViewById(R.id.personalholdcard);
        mGrrouphold = findViewById(R.id.groupholdcard);
        mSchedulelhold = findViewById(R.id.scheduleholdcard);
        mSetting = findViewById(R.id.settingcard);

        requestSendSmsPermission();
        requestPermissionReadPhoneState();
        //setting listners
        mPesonalhold.setOnClickListener(this);
        mGrrouphold.setOnClickListener(this);
        mSchedulelhold.setOnClickListener(this);
        mSetting.setOnClickListener(this);


         mBtnUsername.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent mainIntent = new Intent( MainActivity.this , UserWelcome.class);
                 startActivity(mainIntent);
             }
         });

         mAuthListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                // User is signed in

            } else {
                // User is signed out
                Intent authIntent = new Intent(MainActivity.this, Signup.class);
                startActivity(authIntent);
                finish();
            }
            // ...
        }
    };


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


    @Override
    public void onClick(View v) {

        Intent intent;

        switch (v.getId()){

            case  (R.id.settingcard):
            intent = new Intent(MainActivity.this , SettingsActivity.class);
            startActivity(intent);
            break;

            case  (R.id.groupholdcard):
                intent = new Intent(MainActivity.this , ConnectivityActivity.class);
                startActivity(intent);
                break;
            case  (R.id.personalholdcard):
                intent = new Intent(MainActivity.this , Personal_hold_Activity.class);
                startActivity(intent);
                break;
            case  (R.id.scheduleholdcard):
                intent = new Intent(MainActivity.this , ScheduleHold.class);
                startActivity(intent);
                break;
            default:
                break;

        }

    }

    private void requestPermissionReadPhoneState(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_DENIED
                    || checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_DENIED)
            {
                String[] permissions = {Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE};
                requestPermissions(permissions, PERMISSION_REQUEST_READ_PHONE_STATE );
            }
        }
    }

    public void requestSendSmsPermission(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M){
            if(checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_DENIED){

                ActivityCompat.requestPermissions(MainActivity.this , new String[]{Manifest.permission.SEND_SMS} , PERMISSION_REQUEST_CODE);
            }
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        if(requestCode == PERMISSION_REQUEST_READ_PHONE_STATE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted: " + PERMISSION_REQUEST_READ_PHONE_STATE, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission NOT granted: " + PERMISSION_REQUEST_READ_PHONE_STATE, Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == PERMISSION_REQUEST_CODE)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }

    }

}
