package co.tslc.cashe.android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import pl.droidsonroids.gif.GifImageView;

public class Splash extends AppCompatActivity {

    protected CASHe app;
    private Tracker mTracker;

    private final int SPLASH_DISPLAY_LENGTH = 6000;


    ImageView ivImage ;
    GifImageView gifImageSplash;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        app = (CASHe)getApplication();
        app.isInForeground = true;
        mTracker = app.getDefaultTracker();
        mTracker.setScreenName("Splash");

        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPrefs = PreferenceManager
                        .getDefaultSharedPreferences(Splash.this);
                SharedPreferences.Editor ed;
                if (!sharedPrefs.contains("introShown")) {
                    ed = sharedPrefs.edit();
                    ed.putBoolean("introShown", true);
                    ed.commit();
                    Intent mainIntent = new Intent(Splash.this, Intro.class);
                    startActivity(mainIntent);
                    finish();
                } else {
                    if (!sharedPrefs.contains("agreed")) {
                        Intent mainIntent = new Intent(Splash.this, Agree.class);
                        startActivity(mainIntent);
                        finish();
                    } else {
                        Intent mainIntent = new Intent(Splash.this, SignIn.class);
                        startActivity(mainIntent);
                        finish();
                    }
                }
            }
        }, SPLASH_DISPLAY_LENGTH);

        try{
            SharedPreferences sharedpreferences = Splash.this.getSharedPreferences("CASHe", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.clear();
            editor.commit();
        }catch (Exception e){}

        ivImage = (ImageView) findViewById(R.id.ivImage);
        gifImageSplash = (GifImageView) findViewById(R.id.iv);
        try {
            gifImageSplash.setBackgroundResource(R.drawable.splash);
            gifImageSplash.setVisibility(View.VISIBLE);
            ivImage.setVisibility(View.GONE);
        }catch (OutOfMemoryError outOfMemoryError ) {
            Log.e("IFB", "outOfMemoryError while reading file for sampleSize ");
            try {
                ivImage.setBackgroundResource(R.drawable.splash_xhdpi);
                ivImage.setVisibility(View.VISIBLE);
                gifImageSplash.setVisibility(View.GONE);
            }catch(Exception es){}
        }
        catch(Throwable e){
            try {
                ivImage.setBackgroundResource(R.drawable.splash_xhdpi);
                ivImage.setVisibility(View.VISIBLE);
                gifImageSplash.setVisibility(View.GONE);
            }catch(Exception es){}
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        app.isInForeground = true;
    }


    @Override
    protected void onPause() {
        super.onPause();
        app.isInForeground = false;
    }
}
