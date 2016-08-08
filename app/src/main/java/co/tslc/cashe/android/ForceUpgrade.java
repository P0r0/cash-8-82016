package co.tslc.cashe.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by Pratap on 30-05-2016.
 */
public class ForceUpgrade extends Base {
    protected CASHe app;
    private Tracker mTracker;
   // private String currentVersion;
    private boolean isUpgrade;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_about);
        LayoutInflater inflater = (LayoutInflater)this.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.force_upgrade, frameLayout, true);
        typeFace(frameLayout);
        try {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            app = (CASHe)getApplication();
            mTracker = app.getDefaultTracker();
            mTracker.setScreenName("ForceUpgrade");
            mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        }catch(Exception e){}

        Button tvUpgrade = (Button)findViewById(R.id.tv_force_upgrade_ok);
        Button tvCancel = (Button)findViewById(R.id.tv_force_upgrade_cancel);
        Button tvUpgrade2 = (Button)findViewById(R.id.tv_force_upgrade_ok2);
        LinearLayout llUpgradeBtn = (LinearLayout)findViewById(R.id.ll_force_upgrade_btn);

        Intent i = getIntent();
        //currentVersion = i.getStringExtra("currentVersion");
        isUpgrade = i.getBooleanExtra("isUpgrade",false);

        if(!isUpgrade){
            llUpgradeBtn.setVisibility(View.VISIBLE);
            tvUpgrade2.setVisibility(View.GONE);
        }

        else{
            llUpgradeBtn.setVisibility(View.GONE);
            tvUpgrade2.setVisibility(View.VISIBLE);
        }

        tvUpgrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoPlayStore();
            }
        });
        tvUpgrade2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoPlayStore();
            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });
    }

    private void gotoPlayStore(){
        final String appPackageName = this.getPackageName(); // getPackageName() from Context or Activity object
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        menu.removeItem(R.id.navMenu);
        return true;
    }
}