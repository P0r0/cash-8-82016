package co.tslc.cashe.android;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.io.IOException;

import pl.droidsonroids.gif.GifImageView;

public class Congratulations extends Base {

    protected CASHe app;
    private Tracker mTracker;

    private String name;
    private String email;
    private String custID;
    private String casheAmt;
    private String inHand;
    private String inHandMsg;
    private String feeFlat;
    private String feeProc;
    private String feeProcLbl;

    private MediaPlayer mp = new MediaPlayer();

    private static String accessToken = "";
    private static String refreshToken = "" ;
    private static String tokenType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_congratulations);
        LayoutInflater inflater = (LayoutInflater)this.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.activity_congratulations, frameLayout, true);
        typeFace(frameLayout);
        try {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }catch(Exception e){}
       // getLayoutInflater().inflate(R.layout.activity_congratulations, frameLayout, true);
        //typeFace(frameLayout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        app = (CASHe)getApplication();
        mTracker = app.getDefaultTracker();
        mTracker.setScreenName("Congratulations");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        Intent i = getIntent();
        name = i.getStringExtra("name");
        email = i.getStringExtra("email");
        custID = i.getStringExtra("custID");
        accessToken = i.getStringExtra("accessToken");
        refreshToken = i.getStringExtra("refreshToken");
        tokenType = i.getStringExtra("tokenType");
        inHand = i.getStringExtra("inHand");
        inHandMsg = i.getStringExtra("inHandMsg");

        TextView tvCasheAmt = (TextView)findViewById(R.id.tvCasheAmt);
        TextView creditMsg = (TextView)findViewById(R.id.creditMsg);
        if(inHand!=null) tvCasheAmt.setText("\u20B9"+inHand.replace(".0",""));
        if(inHandMsg!=null) creditMsg.setText(inHandMsg);

        creditSound();

        ImageButton ibOK = (ImageButton)findViewById(R.id.ibOK);
        ibOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.stop();
                Intent i = new Intent(Congratulations.this, Dashboard.class);
                i.putExtra("name", name);
                i.putExtra("email", email);
                i.putExtra("custID", custID);
                i.putExtra("accessToken", accessToken);
                i.putExtra("refreshToken", refreshToken);
                i.putExtra("tokenType", tokenType);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
            }
        });

        TextView tvView = (TextView)findViewById(R.id.tvView);
        tvView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.stop();
                Intent i = new Intent(Congratulations.this, CasheHistory.class);
                i.putExtra("name", name);
                i.putExtra("email", email);
                i.putExtra("custID", custID);
                i.putExtra("accessToken", accessToken);
                i.putExtra("refreshToken", refreshToken);
                i.putExtra("tokenType", tokenType);
                i.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
               // i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
            }
        });

        try {
            GifImageView imageView = (GifImageView) findViewById(R.id.icon_credited);
            imageView.setBackgroundResource(R.drawable.congratulations);
          //  Glide.with(this).load("file:///android_asset/gifs/congratulations.gif").asGif().into(imageView);
        }catch (OutOfMemoryError outOfMemoryError ) {
            Log.e("IFB", "outOfMemoryError while reading file for sampleSize ");
        }catch(Throwable e){
            Log.e("IFB", "outOfMemoryError while reading file for sampleSize ");
        }
    }

    private void creditSound()
    {
        try {
            mp.reset();
            AssetFileDescriptor afd;
            afd = getAssets().openFd("sounds/credited.mp3");
            mp.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
            mp.prepare();
            mp.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {}
}
