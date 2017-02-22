package com.sugarcube.todo.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import com.sugarcube.todo.R;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class SplashScreenActivity extends Activity {

    private static boolean splashLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences  = PreferenceManager.getDefaultSharedPreferences(this);
        boolean showSplashScreen = sharedPreferences.getBoolean("show_splash_screen", true);

        splashLoaded = !showSplashScreen;

        if (!splashLoaded) {
            setContentView(R.layout.activity_splash_screen);

            new Handler().postDelayed(new Runnable() {
                public void run() {
                    startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                    finish();
                }
            }, 1500);

            splashLoaded = true;
        }
        else {
            Intent goToMainActivity = new Intent(SplashScreenActivity.this, MainActivity.class);
            goToMainActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(goToMainActivity);
            finish();
        }

    }
}
