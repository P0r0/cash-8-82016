package co.tslc.cashe.android;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class ThankYou extends Base {

    protected CASHe app;
    private Tracker mTracker;

    private String name;
    private String email;
    private String custID;
    private String payAmt;

    private static String accessToken = "";
    private static String refreshToken = "" ;
    private static String tokenType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_thank_you);
        LayoutInflater inflater = (LayoutInflater)this.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.activity_thank_you, frameLayout, true);
        typeFace(frameLayout);
        try {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }catch(Exception e){}
        //getLayoutInflater().inflate(R.layout.activity_thank_you, frameLayout, true);
        //typeFace(frameLayout);

        app = (CASHe)getApplication();
        mTracker = app.getDefaultTracker();
        mTracker.setScreenName("ThankYou");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        Intent i = getIntent();
        name = i.getStringExtra("name");
        email = i.getStringExtra("email");
        custID = i.getStringExtra("custID");
        accessToken = i.getStringExtra("accessToken");
        refreshToken = i.getStringExtra("refreshToken");
        tokenType = i.getStringExtra("tokenType");
        payAmt = i.getStringExtra("payAmt");
        TextView tvCasheAmt = (TextView)findViewById(R.id.tvCasheAmt);
        tvCasheAmt.setText("\u20B9"+payAmt);

        ImageButton ibOK = (ImageButton)findViewById(R.id.ibOK);
        ibOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ThankYou.this, Dashboard.class);
                i.putExtra("name", name);
                i.putExtra("email", email);
                i.putExtra("custID", custID);
                i.putExtra("accessToken", accessToken);
                i.putExtra("refreshToken", refreshToken);
                i.putExtra("tokenType", tokenType);
                startActivity(i);
            }
        });
    }

    protected boolean isBlocked() {
        if(CASHe.blocked == 2)return true;
        else return false;}
}
