package com.loan8.loan8;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import com.preference.PowerPreference;
import com.preference.Preference;

public class SplashScreen extends AppCompatActivity {

    boolean isLogedIn;
    Preference preference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        PowerPreference.init(getApplicationContext());  //initilize PowerPreference here.
        preference = PowerPreference.getFileByName("LOANLY");
        preference.putBoolean("log", true);
        preference.putString("name", "Android Developer");

        isLogedIn = preference.getBoolean("log");
        String name = preference.getString("name");
        Log.d("Login ", String.valueOf(isLogedIn));
        Log.d("Name ", name);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashScreen.this, LogInActivity.class));
                finish();
            }
        }, 1500);

    }
}