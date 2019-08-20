package com.example.malix.takeabreak.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;

import com.example.malix.takeabreak.R;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsActivity extends AppCompatActivity {

    private Toolbar mtoolbar;
    private LinearLayout mProfile,mMessage,mSignout;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mProfile= findViewById(R.id.profiletab);
        mMessage = findViewById(R.id.messagetab);
        mSignout = findViewById(R.id.signouttab);
        mtoolbar =findViewById(R.id.settingToolbar);
        mtoolbar.setTitle("Setting");
        mtoolbar.setNavigationIcon(R.drawable.ic_baseline_keyboard_backspace_24px);
        mtoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this , MainActivity.class);
                startActivity(intent);
                finish();
            }
        });


        mProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getApplicationContext() , ProfileMaking.class);
                startActivity(intent);
            }
        });

        mMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(SettingsActivity.this ,CustomMessage.class);
                    startActivity(intent);
                    finish();
            }
        });

        mSignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(SettingsActivity.this ,Signup.class);
                startActivity(intent);
                finish();
            }
        });



    }
}
