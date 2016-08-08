package co.tslc.cashe.android;


import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class Intro extends AppCompatActivity {

    protected CASHe app;
    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        try {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }catch(Exception e){}
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        app = (CASHe)getApplication();
        app.isInForeground = true;

        mTracker = app.getDefaultTracker();
        mTracker.setScreenName("Intro");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        IntroAdaptr mCustomPagerAdapter = new IntroAdaptr(Intro.this);
        ViewPager mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mCustomPagerAdapter);

        ImageView ivSkip = (ImageView)findViewById(R.id.ivSkip);
        ivSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPrefs = PreferenceManager
                        .getDefaultSharedPreferences(Intro.this);
                SharedPreferences.Editor ed;
                if(!sharedPrefs.contains("agreed")){
                    Intent mainIntent = new Intent(Intro.this, Agree.class);
                    startActivity(mainIntent);
                    finish();
                }
                else {
                    Intent mainIntent = new Intent(Intro.this, SignIn.class);
                    startActivity(mainIntent);
                    finish();
                }
            }
        });
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
