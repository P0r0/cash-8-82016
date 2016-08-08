package co.tslc.cashe.android;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.RelativeLayout;

public class SmartUpload extends Base {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_upload);
        try {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }catch(Exception e){}
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_chevron_left_white_24dp);
        RelativeLayout sc01=(RelativeLayout) findViewById(R.id.container_smart_upload);
        overrideFonts(this,sc01);




    }


}
