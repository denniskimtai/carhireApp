package app.carhire.com.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import app.carhire.com.BuildConfig;
import app.carhire.com.R;

public class SplashActivity extends AppCompatActivity {

    private String prefFile = BuildConfig.APPLICATION_ID + ".PREFERENCE_FILE_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //if user details are saved offline
        //proceed directly to main activity
        if (!getSharedPreferences(prefFile,Context.MODE_PRIVATE).getString("UserName","").equals(""))
        {
            startActivity(new Intent(SplashActivity.this,ViewCars.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
        }
        else
        {
            //new instance of handler class
            //post delayed method runs code in the run method after given milliseconds are over
            //i.e in this cae open second activity
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashActivity.this,LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    finish();
                }
            },3000);
        }
    }
}
