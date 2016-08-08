package co.tslc.cashe.android;


import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class WhyCASHe extends AppCompatActivity {

    protected CASHe app;
    private Tracker mTracker;


    private static final String urlWhyCASHe = CASHe.webApiUrl + "api/cashe/data/whyCasheData";

    private static String accessToken = "";
    private static String refreshToken = "" ;
    private static String tokenType = "";

    //ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_why_cashe);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);
        try {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }catch(Exception e){}
        app = (CASHe)getApplication();
        app.isInForeground = true;
        mTracker = app.getDefaultTracker();
        mTracker.setScreenName("WhyCASHe");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        Intent i;
        i = getIntent();
        accessToken = i.getStringExtra("accessToken");
        refreshToken = i.getStringExtra("refreshToken");
        tokenType = i.getStringExtra("tokenType");

        /*ImageView ivSkip = (ImageView)findViewById(R.id.ivSkip);
        ivSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });*/

        ImageView ivClose = (ImageView) findViewById(R.id.ivClose);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { finish(); }
        });

        /*pd = ProgressDialog.show(WhyCASHe.this,
                "CASHe",
                "Loading...");*/

        StringRequest reqAuth = new StringRequest(Request.Method.GET, urlWhyCASHe,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("IPB", response.toString());
                        try {
                            JSONObject jsonStatus = new JSONObject(response.toString());
                            if(!jsonStatus.isNull("entity")) {
                                //pd.dismiss();
                                JSONObject jsonEntity = new JSONObject(jsonStatus.getString("entity"));
                                String averageCasheDisburseTime = jsonEntity.getString("averageCasheDisburseTime");
                                String totalLoanAmountApproved = jsonEntity.getString("totalLoanAmountApproved");
                                String moneyLoansPerSecond = jsonEntity.getString("moneyLoanPerSecond");
                                String totalCustomers = jsonEntity.getString("totalCustomer");
                                String[] sResources = {
                                    "",
                                    averageCasheDisburseTime,
                                    //totalLoanAmountApproved,
                                    //moneyLoansPerSecond,
                                    //totalCustomers
                                };
                                WhyCASHeAdaptr mCustomPagerAdapter = new WhyCASHeAdaptr(WhyCASHe.this, sResources);
                                ViewPager mViewPager = (ViewPager) findViewById(R.id.pager);
                                mViewPager.setAdapter(mCustomPagerAdapter);
                            }
                        } catch (JSONException e) {
                            //pd.dismiss();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        //pd.dismiss();
                        if(error instanceof NoConnectionError) {
                            Toast.makeText(WhyCASHe.this, "No Internet access. Please check your connection.", Toast.LENGTH_LONG).show();
                        }
                        else
                            finish();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", tokenType + " " + accessToken);
                return headers;
            }
        };

        reqAuth.setShouldCache(false);
        rQueue.getInstance(this).queueRequest("",reqAuth);
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
