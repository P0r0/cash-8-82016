package co.tslc.cashe.android;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by idris on 5/24/2016.
 */
public class RepayScreen  extends Base {
    protected CASHe app;
    private Tracker mTracker;
    private static final String urlCustInf = CASHe.webApiUrl + "api/cashe/customer/repayment/info";
    public ProgressDialog progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_about);
        LayoutInflater inflater = (LayoutInflater)this.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.repay_layout, frameLayout, true);
        typeFace(frameLayout);
        try {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            app = (CASHe)getApplication();
            mTracker = app.getDefaultTracker();
            mTracker.setScreenName("RepayScreen");
            mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        }catch(Exception e){}
        getLoanRepayInfo();
    }


    private void getLoanRepayInfo(){
        progress=new ProgressDialog(this);
        progress.setMessage("Getting bank details");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.show();
        StringRequest reqCust = new StringRequest(com.android.volley.Request.Method.GET, urlCustInf,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            try {
                                if (progress != null) {
                                    progress.dismiss();
                                    progress = null;
                                }
                            }catch (Exception e){}
                            Log.d("IPB", response);
                            JSONObject jsonAccount = new JSONObject(response.toString());
                            JSONObject usrEntity = new JSONObject(jsonAccount.getString("entity"));
                            TextView actName = (TextView) findViewById(R.id.account_name);
                            TextView acntNumber = (TextView) findViewById(R.id.account_number);
                            TextView bankName = (TextView) findViewById(R.id.bank_name);
                            TextView ifscCode = (TextView) findViewById(R.id.ifsc_code);
                            TextView paymentMode = (TextView) findViewById(R.id.payment_mode);
                            TextView accountType = (TextView) findViewById(R.id.account_type);


                            try {
                                actName.setText(usrEntity.getString("accountName"));
                                acntNumber.setText(usrEntity.getString("accountNumber"));
                                bankName.setText(usrEntity.getString("bankName"));
                                ifscCode.setText(usrEntity.getString("ifsc"));
                                paymentMode.setText(usrEntity.getString("modeOfPayment"));
                                accountType.setText(usrEntity.getString("accountType"));
                            }catch(Exception e){
                                try {
                                    Toast.makeText(RepayScreen.this, "Something went wrong. Please try again later.", Toast.LENGTH_LONG).show();
                                }catch(Exception ex){}
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        try {
                            if (progress != null) {
                                progress.dismiss();
                                progress = null;
                            }
                        }catch (Exception e){}
                        if(error instanceof NoConnectionError) {
                            Toast.makeText(RepayScreen.this, "No Internet access. Please check your connection.", Toast.LENGTH_LONG).show();
                        }
                    }
                }

        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", tokenType + " " + accessToken);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        reqCust.setShouldCache(false);
        rQueue.getInstance(this).queueRequest("", reqCust);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        menu.removeItem(R.id.navMenu);
        return true;
    }
}
