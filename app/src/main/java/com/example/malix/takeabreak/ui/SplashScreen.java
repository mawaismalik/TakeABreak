package com.example.malix.takeabreak.ui;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.malix.takeabreak.R;

public class SplashScreen extends AppCompatActivity {

  //  LocalDatabase mydatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

      //  mydatabase = new LocalDatabase(this);

       // TextView slogan = (TextView)findViewById(R.id.slogan);
        Animation animation =
                AnimationUtils.loadAnimation(SplashScreen.this,
                        R.anim.bounce);
      //  slogan.startAnimation(animation);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreen.this , MainActivity.class);
                startActivity(intent);
                finish();
            }
        } , 3000);
    }
}
