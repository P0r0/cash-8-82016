package co.tslc.cashe.android;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Consent extends Base {
    protected CASHe app;
    private Tracker mTracker;

    private String name;
    private String email;
    private String custID;
    private String casheAmt;
    private static String inHand;
    private String feeFlat;
    private String feeProc;
    private String feeProcLbl;

    private static String accessToken = "";
    private static String refreshToken = "" ;
    private static String tokenType = "";

    private static final String urlLoanRequest = CASHe.webApiUrl + "api/cashe/customer/loanRequest";
    private static final String urlStatus = CASHe.webApiUrl + "api/cashe/customer/status";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_consent);
        LayoutInflater inflater = (LayoutInflater)this.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.activity_consent, frameLayout, true);
        typeFace(frameLayout);
        try {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }catch(Exception e){}
       // getLayoutInflater().inflate(R.layout.activity_consent, frameLayout, true);
       // typeFace(frameLayout);

        app = (CASHe)getApplication();
        mTracker = app.getDefaultTracker();
        mTracker.setScreenName("Consent");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        Intent i = getIntent();
        name = i.getStringExtra("name");
        email = i.getStringExtra("email");
        custID = i.getStringExtra("custID");
        accessToken = i.getStringExtra("accessToken");
        refreshToken = i.getStringExtra("refreshToken");
        tokenType = i.getStringExtra("tokenType");
        casheAmt = i.getStringExtra("casheAmt");
        inHand = i.getStringExtra("inHand");
        TextView tvCA = (TextView)findViewById(R.id.tvCASHeAmt);
        tvCA.setText('\u20B9'+casheAmt);
        final TextView tvRnd = (TextView)findViewById(R.id.tvRandom);
        final EditText etCode = (EditText)findViewById(R.id.etCode);
        tvRnd.setText(genRand());

        TextView tvIOU = (TextView)findViewById(R.id.tvIOU);
        //tvIOU.setText("I confirm that I have read, understood and agreed to the CASHe Product Terms & Conditions and Related Policies. I grant my irrevocable consent to lender for making public, in case I default, my defaulter status on the website and my social networking sites as per Product Terms & Conditions. I hereby promise to pay CASHe/lender or order on demand Rs."+casheAmt+" together with interest @ 1.5% and delayed interest, if any, @ 1 % per week from due date until repayment thereof as per Product Terms & Conditions and related policies.");
        tvIOU.setText("I confirm that I have read, understood and agreed to the CASHe Product Terms & Conditions and Related Policies. I grant my irrevocable consent to lender and/or anyone authorised by lender for making public, in case I default, my defaulter status on the website of lender and/or CASHe and my social networking sites as per Product Terms & Conditions. I hereby promise to pay CASHe/lender or order on demand Rs."+casheAmt+" together with interest 36.50% p.a and delayed interest, if any, @ 52% p.a. from due date until repayment thereof as per Product Terms & Conditions and Related Policies.");

                tvIOU.setMovementMethod(LinkMovementMethod.getInstance());

        ImageButton ibRefresh = (ImageButton)findViewById(R.id.ibRefresh);
        ibRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TextView tvRnd = (TextView)findViewById(R.id.tvRandom);
                tvRnd.setText(genRand());
            }
        });

        ImageButton btnDone = (ImageButton)findViewById(R.id.ibDone);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tvRnd.getText().toString().equalsIgnoreCase(etCode.getText().toString())) {

                    v.setEnabled(false);
                    mTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Action")
                            .setAction("LoanRequest")
                            .build());
                    StringRequest reqLoan = new StringRequest(Request.Method.POST, urlLoanRequest,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        Log.d("IPB", response);
                                        final JSONObject jsonRequest = new JSONObject(response.toString());
                                        String statusType =  jsonRequest.getString("statusType");
                                        if(statusType.equalsIgnoreCase("OK"))
                                        {
                                            StringRequest reqAuth = new StringRequest(Request.Method.GET, urlStatus,
                                                new Response.Listener<String>() {
                                                    @Override
                                                    public void onResponse(String response) {
                                                        try {
                                                            JSONObject jsonStatus = new JSONObject(response.toString());
                                                            String statusType = jsonStatus.getString("statusType");
                                                            if(statusType.equalsIgnoreCase("OK"))
                                                            {
                                                                JSONObject jsonEntity = new JSONObject(jsonStatus.getString("entity"));
                                                                String customerStatusName = jsonEntity.getString("customerStatusName");
                                                                Intent i;
                                                                if(customerStatusName.equalsIgnoreCase("Cash Requested")||customerStatusName.equalsIgnoreCase("Credit Approved")) {
                                                                    if(jsonEntity.isNull("initialLoanAmount"))
                                                                    {
                                                                        if(inHand!=null) {
                                                                            i = new Intent(Consent.this, Congratulations.class);
                                                                            i.putExtra("inHand", inHand);
                                                                        }
                                                                        else
                                                                            i = new Intent(Consent.this, Dashboard.class);
                                                                    }
                                                                    else {
                                                                        i = new Intent(Consent.this, Congratulations.class);
                                                                        if(inHand==null) inHand = jsonEntity.getString("initialLoanAmount");
                                                                        String inHandMsg = jsonEntity.getString("initialLoanAmountMessage");
                                                                        i.putExtra("inHand",inHand);
                                                                        i.putExtra("inHandMsg",String.valueOf(inHandMsg));
                                                                    }
                                                                }
                                                                else {
                                                                    i = new Intent(Consent.this, Verification.class);
                                                                }
                                                                i.putExtra("name", name);
                                                                i.putExtra("email", email);
                                                                i.putExtra("custID", custID);
                                                                i.putExtra("accessToken", accessToken);
                                                                i.putExtra("refreshToken", refreshToken);
                                                                i.putExtra("tokenType", tokenType);
                                                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                startActivity(i);
                                                                finish();
                                                            }
                                                            else
                                                            {
                                                                Intent i = new Intent(Consent.this, Dashboard.class);
                                                                i.putExtra("name", name);
                                                                i.putExtra("email", email);
                                                                i.putExtra("custID", custID);
                                                                i.putExtra("accessToken", accessToken);
                                                                i.putExtra("refreshToken", refreshToken);
                                                                i.putExtra("tokenType", tokenType);
                                                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                startActivity(i);
                                                                finish();
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
                                                        if(error instanceof NoConnectionError) {
                                                            Toast.makeText(Consent.this, "No Internet access. Please check your connection.", Toast.LENGTH_LONG).show();
                                                        }
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
                                            rQueue.getInstance(Consent.this).queueRequest("SignIn",reqAuth);
                                        }
                                        else
                                        {
                                            ImageButton btnDone = (ImageButton)findViewById(R.id.ibDone);
                                            btnDone.setEnabled(true);
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
                                    ImageButton btnDone = (ImageButton)findViewById(R.id.ibDone);
                                    btnDone.setEnabled(true);
                                    if(error instanceof NoConnectionError) {
                                        Toast.makeText(Consent.this, "No Internet access. Please check your connection.", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                    ) {
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            HashMap<String, String> headers = new HashMap<String, String>();
                            headers.put("Authorization", tokenType + " " + accessToken);
                            return headers;
                        }

                        @Override
                        public String getBodyContentType() {
                            return String.format("application/json; charset=utf-8");
                        }

                        @Override
                        public byte[] getBody() throws AuthFailureError {
                            String uInf = "";
                            String finalPromoCode = "";
                            try {
                                if(casheAmt != null){
                                    try {
                                        casheAmt = casheAmt.replace("Rs. ", "");
                                    }catch(Exception e){
                                        casheAmt = "";
                                    }
                                }
                                if(CASHe.validPromoCode.equalsIgnoreCase(""))finalPromoCode= null;
                                else finalPromoCode = CASHe.validPromoCode;
                                uInf = "{\"customerId\":" + custID + ",\"cashRequestAmount\":" + casheAmt + ",\"promoCode\": \""+finalPromoCode+"\"}";
                                return uInf == null ? null : uInf.getBytes("utf-8");
                            } catch (UnsupportedEncodingException uee) {
                                VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                                        uInf, "utf-8");
                                return null;
                            }

                        }

                    };
                    reqLoan.setShouldCache(false);
                    rQueue.getInstance(Consent.this).queueRequest("",reqLoan);
                }
                else {
                    etCode.setError("Please enter the confirmation code to proceed.");
                }
            }
        });
    }

    public String genRand() {
        Random r = new Random( System.currentTimeMillis() );
        return String.valueOf(10000 + r.nextInt(20000));
    }
}
