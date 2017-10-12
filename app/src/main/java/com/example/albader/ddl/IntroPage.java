package com.example.albader.ddl;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;



public class IntroPage extends Activity {

    //Timer 2 seconds
    public static int SPLASH_TIMER = 2000;

    @Override
    public void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_page);

        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer.
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                Intent i = new Intent(IntroPage.this, DeviceList.class);
                startActivity(i);

                // close this activity
                finish();
            }
        }, SPLASH_TIMER);
    }
}
