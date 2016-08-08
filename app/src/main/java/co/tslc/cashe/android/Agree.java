package co.tslc.cashe.android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class Agree extends Base {
    protected CASHe app;
    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_agree);
        LayoutInflater inflater = (LayoutInflater)this.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.activity_agree, frameLayout, true);
        typeFace(frameLayout);
        try {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }catch(Exception e){}
        //getLayoutInflater().inflate(R.layout.activity_agree, frameLayout, true);
        //typeFace(frameLayout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        app = (CASHe)getApplication();
        mTracker = app.getDefaultTracker();
        mTracker.setScreenName("Agree");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        TextView tvTC = (TextView)findViewById(R.id.tvTC);
        TextView tvPP = (TextView)findViewById(R.id.tvPP);

        tvTC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Agree.this, Terms.class);
                startActivity(i);
            }
        });

        tvPP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Agree.this, Privacy.class);
                startActivity(i);
            }
        });

        ImageButton ibProceed = (ImageButton)findViewById(R.id.ibProceed);
        ibProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox chk = (CheckBox)findViewById(R.id.cbAgree);
                if(chk.isChecked())
                {
                    SharedPreferences sharedPrefs = PreferenceManager
                            .getDefaultSharedPreferences(Agree.this);
                    SharedPreferences.Editor ed;
                        ed = sharedPrefs.edit();
                        ed.putBoolean("agreed", true);
                        ed.commit();
                        Intent mainIntent = new Intent(Agree.this, SignIn.class);
                        startActivity(mainIntent);
                        finish();
                }
                else
                {
                    Toast.makeText(Agree.this,"You need to agree to our T&C and Privacy Policy to proceed.",Toast.LENGTH_LONG).show();
                }
            }
        });

        ImageView ivArr1 = (ImageView) findViewById(R.id.ivArr1);
        ImageView ivArr2 = (ImageView) findViewById(R.id.ivArr2);

        ivArr1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Agree.this, Terms.class);
                startActivity(i);
            }
        });

        ivArr2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Agree.this, Privacy.class);
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        menu.removeItem(R.id.navMenu);
        return true;
    }

    @Override
    protected boolean isVerified() {return false;}
}
