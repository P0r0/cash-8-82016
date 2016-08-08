package co.tslc.cashe.android;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Dashboard extends Base {

    private DrawerLayout mDrawerLayout;

    protected CASHe app;
    private Tracker mTracker;

    private String name;
    private String email;
    private String custID;
    public int casheAmt_ = 0;
    public int inHand_ = 0;
    private int processingFee;
    private double feePerc;
    private String procFeeLbl;

    private boolean usrDefaulted;

    private static String accessToken = "";
    private static String refreshToken = "" ;
    private static String tokenType = "";

    private static final String urlStatus = CASHe.webApiUrl + "api/cashe/customer/status";
    private static final String urlCredit = CASHe.webApiUrl + "api/cashe/customer/creditDetails";
    private static final String urlConfInf = CASHe.webApiUrl + "api/cashe/customer/details/verifyOnLoanRequest";
    private static final String urlPromoCode = CASHe.webApiUrl + "/api/cashe/customer/verifyPromoCode/";

    int incrDeg = 0;
    int incrCurr = 0;
    int incrMax = 0;

    private List<Loan> loans = new ArrayList<Loan>();
    private ListView lvHistory;
    private HistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_dashboard);

        LayoutInflater inflater = (LayoutInflater)this.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.activity_dashboard, frameLayout, true);
        typeFace(frameLayout);
        try {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }catch(Exception e){}
        //getLayoutInflater().inflate(R.layout.activity_dashboard, frameLayout, true);
        //typeFace(frameLayout);

        app = (CASHe)getApplication();
        mTracker = app.getDefaultTracker();
        mTracker.setScreenName("Dashboard");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        CASHe.validPromoCode="";
        Intent i = getIntent();
        name = i.getStringExtra("name");
        email = i.getStringExtra("email");
        custID = i.getStringExtra("custID");
        accessToken = i.getStringExtra("accessToken");
        refreshToken = i.getStringExtra("refreshToken");
        tokenType = i.getStringExtra("tokenType");

        ImageButton btnOK = (ImageButton)findViewById(R.id.ibGetYourCashe);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                StringRequest reqCust = new StringRequest(com.android.volley.Request.Method.GET, urlConfInf,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d("IPB", response);
                                try {
                                    JSONObject bankDetails = new JSONObject(response);
                                    JSONObject jsonEntity = new JSONObject(bankDetails.getString("entity"));
                                    String dueDate = jsonEntity.getString("dueDate");
                                    Date date = null;
                                    try {
                                        date = new SimpleDateFormat("yyyy-mm-dd").parse(dueDate);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    dueDate = new SimpleDateFormat("dd-mm-yyyy").format(date);

                                    if(!jsonEntity.isNull("bankDetailDto"))
                                    {
                                        TextView tvCasheAmt = (TextView)findViewById(R.id.casheAmt);
                                        TextView tvProcFeeLbl = (TextView)findViewById(R.id.tvProcFeeLbl);
                                        TextView tvProcFee = (TextView)findViewById(R.id.tvProcFee);
                                        TextView inHand = (TextView) findViewById(R.id.inHand);
                                        TextView tvOneTime = (TextView)findViewById(R.id.oneTimeFee);
                                        Intent i = new Intent(Dashboard.this, ConfirmDetails.class);
                                        i.putExtra("name", name);
                                        i.putExtra("email", email);
                                        i.putExtra("custID", custID);
                                        i.putExtra("accessToken", accessToken);
                                        i.putExtra("refreshToken", refreshToken);
                                        i.putExtra("tokenType", tokenType);
                                        i.putExtra("casheAmt",tvCasheAmt.getText());
                                        String str1 = tvCasheAmt.getText().toString();
                                        i.putExtra("procFee",String.valueOf(feePerc));
                                        i.putExtra("procFeeLbl",tvProcFeeLbl.getText());
                                        i.putExtra("procFeeAmt",tvProcFee.getText());
                                        //i.putExtra("flatFee",String.valueOf(processingFee));
                                        i.putExtra("flatFee",tvOneTime.getText());
                                        i.putExtra("inHand",inHand.getText());
                                        i.putExtra("dueDate",dueDate);
                                        startActivity(i);
                                        //finish();
                                    }
                                    else
                                    {
                                        TextView tvCasheAmt = (TextView)findViewById(R.id.casheAmt);
                                        TextView tvProcFeeLbl = (TextView)findViewById(R.id.tvProcFeeLbl);
                                        TextView tvProcFee = (TextView)findViewById(R.id.tvProcFee);
                                        TextView inHand = (TextView) findViewById(R.id.inHand);
                                        TextView tvOneTime = (TextView)findViewById(R.id.oneTimeFee);
                                        Intent i = new Intent(Dashboard.this, CasheSummary.class);
                                        i.putExtra("name", name);
                                        i.putExtra("email", email);
                                        i.putExtra("custID", custID);
                                        i.putExtra("accessToken", accessToken);
                                        i.putExtra("refreshToken", refreshToken);
                                        i.putExtra("tokenType", tokenType);
                                        i.putExtra("casheAmt",tvCasheAmt.getText());
                                        i.putExtra("procFee",String.valueOf(feePerc));
                                        i.putExtra("procFeeLbl",tvProcFeeLbl.getText());
                                        i.putExtra("procFeeAmt",tvProcFee.getText());
                                        //i.putExtra("flatFee",String.valueOf(processingFee));
                                        i.putExtra("flatFee",tvOneTime.getText());
                                        i.putExtra("inHand",inHand.getText());
                                        i.putExtra("dueDate",dueDate);
                                        startActivity(i);
                                        //finish();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                error.printStackTrace();
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
                reqCust.setShouldCache(false);
                rQueue.getInstance(Dashboard.this).queueRequest("", reqCust);
            }
        });

        Button btnWhyCASHe = (Button)findViewById(R.id.btnWhyCASHe);
        btnWhyCASHe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Dashboard.this, WhyCASHe.class);
                i.putExtra("name", name);
                i.putExtra("email", email);
                i.putExtra("custID", custID);
                i.putExtra("accessToken", accessToken);
                i.putExtra("refreshToken", refreshToken);
                i.putExtra("tokenType", tokenType);
                startActivity(i);
            }
        });

        ImageButton btnRepay = (ImageButton)findViewById(R.id.ibRepay);
        btnRepay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TextView tvCasheAmt = (TextView) findViewById(R.id.casheAmt);
                TextView tvProcFeeLbl = (TextView) findViewById(R.id.tvProcFeeLbl);
                TextView tvProcFee = (TextView) findViewById(R.id.tvProcFee);
                TextView inHand = (TextView) findViewById(R.id.inHand);
                Intent i = new Intent(Dashboard.this, CasheHistory.class);
                i.putExtra("name", name);
                i.putExtra("email", email);
                i.putExtra("custID", custID);
                i.putExtra("accessToken", accessToken);
                i.putExtra("refreshToken", refreshToken);
                i.putExtra("tokenType", tokenType);
                i.putExtra("casheAmt", tvCasheAmt.getText());
                i.putExtra("procFee", String.valueOf(feePerc));
                i.putExtra("procFeeLbl", tvProcFeeLbl.getText());
                i.putExtra("procFeeAmt", tvProcFee.getText());
                i.putExtra("flatFee", String.valueOf(processingFee));
                i.putExtra("inHand", inHand.getText());
                startActivity(i);
                finish();
            }
        });

        ImageButton ibDefaulted = (ImageButton) findViewById(R.id.ibDefaulted);
        ibDefaulted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //
                Intent i = new Intent(Dashboard.this, RepayScreen.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });

        TextView tvPromoCode = (TextView)findViewById(R.id.tvPromoCode);
        tvPromoCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayAlertDialog();
            }
        });

        StringRequest reqAuth = new StringRequest(Request.Method.GET, urlStatus,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonStatus = new JSONObject(response.toString());
                            JSONObject jsonEntity = new JSONObject(jsonStatus.getString("entity"));
                            String custID = jsonEntity.getString("custId");
                            final String customerStatusName = jsonEntity.getString("customerStatusName");
                            if(customerStatusName.equalsIgnoreCase("Defaulted") || customerStatusName.equalsIgnoreCase("Payment Overdue")) setupDefaulted();
                            else if (customerStatusName.equalsIgnoreCase("Defaulted Block")){
                                Intent i = new Intent(Dashboard.this, BlockedUser.class);
                                i.putExtra("name", name);
                                i.putExtra("email", email);
                                i.putExtra("custID", custID);
                                i.putExtra("accessToken", accessToken);
                                i.putExtra("refreshToken", refreshToken);
                                i.putExtra("tokenType", tokenType);
                                CASHe.blocked = 2;
                                startActivity(i);
                                finish();
                            }
                            else setupDash();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
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
        rQueue.getInstance(this).queueRequest("", reqAuth);
    }

    private void setupDash()
    {
        final ImageView ivDialPlus = (ImageView)findViewById(R.id.ivDialPlus);
        final ImageView ivDialMinus = (ImageView)findViewById(R.id.ivDialMinus);


        StringRequest reqStatus = new StringRequest(Request.Method.GET, urlCredit,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d("IPB", response);
                            JSONObject jsonStatus = new JSONObject(response.toString());
                            JSONObject jsonEntity = new JSONObject(jsonStatus.getString("entity"));


                            final int creditEligibleMin = jsonEntity.getInt("minCreditEligible");
                            final int creditEligibleMax = jsonEntity.getInt("maxCreditEligible");
                            final int loanIncrement =  jsonEntity.getInt("loanRequestIncrement");
                            processingFee = 0;
                            if(jsonEntity.has("processingFeeFlat")) {
                                try {
                                    if(jsonEntity.getInt("processingFeeFlat")>0) {
                                        processingFee = Integer.parseInt(jsonEntity.getString("processingFeeFlat"));
                                        //if(!promoFlatFee.equalsIgnoreCase(""))processingFee = processingFee-Integer.parseInt(promoFlatFee);
                                    }
                                }
                                catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            feePerc = jsonEntity.getDouble("processingFeePercentage");
                            TextView txtMin = (TextView)findViewById(R.id.minAmt);
                            TextView txtMax = (TextView)findViewById(R.id.maxAmt);
                            TextView tvProcFeeLbl = (TextView)findViewById(R.id.tvProcFeeLbl);
                            TextView tvProcFee = (TextView)findViewById(R.id.tvProcFee);
                            TextView tvOneTime = (TextView)findViewById(R.id.oneTimeFee);
                            if(processingFee <=0)
                            {
                                RelativeLayout rlOneTime = (RelativeLayout)findViewById(R.id.rlOneTime);
                                rlOneTime.setVisibility(View.GONE);
                                TextView tvPromoCode = (TextView)findViewById(R.id.tvPromoCode);
                                tvPromoCode.setVisibility(View.GONE);
                                TextView tvPromoMsgApplied = (TextView)findViewById(R.id.tvPromoCodeApplied);
                                tvPromoMsgApplied.setVisibility(View.GONE);
                            }
                            else
                                tvOneTime.setText(String.valueOf(processingFee));

                            int pendingAmt = 0;
                            int loanAvail = creditEligibleMax;
                            if(jsonEntity.has("pendingLoanAmount")) {
                                try {
                                    if(!jsonEntity.getString("pendingLoanAmount").equals("null")) {
                                        pendingAmt = Integer.parseInt(jsonEntity.getString("pendingLoanAmount"));
                                        loanAvail = loanAvail - pendingAmt;
                                        TextView tvCan = (TextView)findViewById(R.id.tvCasheYouCanGet);
                                        if(loanAvail >= loanIncrement) {
                                            tvCan.setText("CASHe LEFT");
                                            Button btnWhyCASHe = (Button)findViewById(R.id.btnWhyCASHe);
                                            btnWhyCASHe.setVisibility(View.GONE);
                                            TextView tvUsed = (TextView) findViewById(R.id.tvUsed);
                                            tvUsed.setText("CASHe YOU HAVE USED: \u20B9" + pendingAmt);
                                            tvUsed.setVisibility(View.VISIBLE);
                                            //TextView tvReqBnk = (TextView) findViewById(R.id.tvReqBnk);
                                            //tvReqBnk.setText("Update your documents regularly.");
                                        }
                                        if(loanAvail == 0 || loanAvail < loanIncrement)
                                        {
                                            TextView tvPromoCode = (TextView)findViewById(R.id.tvPromoCode);
                                            tvPromoCode.setVisibility(View.GONE);
                                            TextView tvPromoMsgApplied = (TextView)findViewById(R.id.tvPromoCodeApplied);
                                            tvPromoMsgApplied.setVisibility(View.GONE);
                                            ImageButton ibGetYourCASHe = (ImageButton)findViewById(R.id.ibGetYourCashe);
                                            ibGetYourCASHe.setVisibility(View.GONE);
                                            tvCan.setText("LIMIT UTILIZED");
                                            TextView tvCasheVal = (TextView)findViewById(R.id.tvCasheVal);
                                            tvCasheVal.setText("\u20B9"+String.valueOf(creditEligibleMax-loanAvail).replace(".0",""));
                                            txtMin.setText("\u20B9"+"0");
                                            txtMax.setText("\u20B9"+"0");
                                            //View div = (View)findViewById(R.id.action_divider);
                                            //div.setVisibility(View.GONE);
                                            View div = (View)findViewById(R.id.action_divider2);
                                            div.setVisibility(View.GONE);
                                            div = (View)findViewById(R.id.action_divider_light);
                                            div.setVisibility(View.GONE);
                                            RelativeLayout rl = (RelativeLayout)findViewById(R.id.rlCasheAmt);
                                            rl.setVisibility(View.GONE);
                                            rl = (RelativeLayout)findViewById(R.id.rlOneTime);
                                            rl.setVisibility(View.GONE);
                                            rl = (RelativeLayout)findViewById(R.id.rlProcFee);
                                            rl.setVisibility(View.GONE);
                                            rl = (RelativeLayout)findViewById(R.id.rlInHand);
                                            rl.setVisibility(View.GONE);
                                            //TextView tvReqBank = (TextView)findViewById(R.id.tvReqBnk);
                                            //tvReqBank.setVisibility(View.GONE);
                                            Button btn = (Button)findViewById(R.id.btnWhyCASHe);
                                            btn.setVisibility(View.GONE);
                                            /*ImageView ivOut = (ImageView)findViewById(R.id.ivOutstanding);
                                            ivOut.setVisibility(View.VISIBLE);
                                            TextView tvOut = (TextView)findViewById(R.id.tvOutstanding);
                                            tvOut.setText("Kindly pay the outstanding amount of \u20B9"+ pendingAmt +".");
                                            tvOut.setVisibility(View.VISIBLE);
                                            ImageButton ibRepay = (ImageButton)findViewById(R.id.ibRepay);
                                            ibRepay.setVisibility(View.VISIBLE);*/
                                            RelativeLayout rlUtilized = (RelativeLayout)findViewById(R.id.rlUtilized);
                                            rlUtilized.setVisibility(View.VISIBLE);
                                        }
                                    }
                                }
                                catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            if(loanAvail >= loanIncrement)
                            {
                                //final int incrMax = (loanAvail - creditEligibleMin)/loanIncrement;
                                final int incrMax = (loanAvail - creditEligibleMin)/loanIncrement;
                                if(incrMax>0) {
                                    incrDeg = 180 / incrMax;
                                }
                                else
                                {
                                    incrDeg = 0;
                                }
                                TextView tvCasheVal = (TextView)findViewById(R.id.tvCasheVal);
                                tvCasheVal.setText("\u20B9" + String.valueOf(loanAvail));
                                txtMax.setText("\u20B9" + String.valueOf(loanAvail));
                                txtMin.setText("\u20B9" + String.valueOf(creditEligibleMin));
                                tvProcFeeLbl.setText("INTEREST ("+String.valueOf(feePerc).replace(".0","")+"%)");
                                tvProcFee.setText("\u20B9"+feePerc);
                                procFeeLbl = "INTEREST ("+String.valueOf(feePerc)+"%)";

                                TextView dialAmt = (TextView) findViewById(R.id.tvDialAmt);
                                //String dialAmtStr = dialAmt.getText().toString().replace("\u20B9", "");
                                int amtVal = loanIncrement;
                                dialAmt.setText("\u20B9"+loanIncrement);
                                TextView casheAmt = (TextView) findViewById(R.id.casheAmt);
                                casheAmt.setText(String.valueOf(amtVal));
                                TextView procFee = (TextView) findViewById(R.id.tvProcFee);
                                procFee.setText(String.valueOf(Math.round(amtVal * feePerc / 100)));
                                TextView inHand = (TextView) findViewById(R.id.inHand);
                                inHand.setText(String.valueOf(amtVal - Math.round((amtVal * feePerc) / 100) - processingFee).replace(".0",""));

                                ivDialPlus.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        ImageView dial = (ImageView) findViewById(R.id.ivDial);
                                        TextView dialAmt = (TextView) findViewById(R.id.tvDialAmt);
                                        float dRot = dial.getRotation();
                                        Log.d("dRot", String.valueOf(dRot));
                                        if (incrCurr < incrMax) {
                                            incrCurr = incrCurr + 1;

                                            dial.setRotation(dRot + incrDeg);
                        /*RotateAnimation rotateAnim = new RotateAnimation(0.0f, incrDeg,
                                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                                RotateAnimation.RELATIVE_TO_SELF, 0.5f);

                        rotateAnim.setDuration(500);
                        rotateAnim.setFillAfter(true);
                        dial.startAnimation(rotateAnim);*/
                                            String dialAmtStr = dialAmt.getText().toString().replace("\u20B9", "");
                                            int amtVal = Integer.valueOf(dialAmtStr) + loanIncrement;
                                            dialAmt.setText("\u20B9" + amtVal);

                                            TextView casheAmt = (TextView) findViewById(R.id.casheAmt);
                                            casheAmt.setText(String.valueOf(amtVal));
                                            TextView procFee = (TextView) findViewById(R.id.tvProcFee);
                                            procFee.setText(String.valueOf(amtVal * feePerc / 100));
                                            TextView inHand = (TextView) findViewById(R.id.inHand);
                                            inHand.setText(String.valueOf(amtVal - (amtVal * feePerc / 100) - processingFee).replace(".0",""));
                                        }
                                    }
                                });

                                ivDialMinus.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        ImageView dial = (ImageView)findViewById(R.id.ivDial);
                                        TextView dialAmt = (TextView)findViewById(R.id.tvDialAmt);
                                        float dRot = dial.getRotation();
                                        Log.d("ROT",String.valueOf(dRot));
                                        if(incrCurr > 0) {
                                            incrCurr = incrCurr - 1;

                                            dial.setRotation(dRot-incrDeg);
                        /*RotateAnimation rotateAnim = new RotateAnimation(0.0f, -incrDeg,
                                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                                RotateAnimation.RELATIVE_TO_SELF, 0.5f);

                        rotateAnim.setDuration(500);
                        rotateAnim.setFillAfter(true);
                        dial.startAnimation(rotateAnim);*/
                                            String dialAmtStr = dialAmt.getText().toString().replace("\u20B9","");
                                            int amtVal = Integer.valueOf(dialAmtStr.toString()) - loanIncrement;
                                            dialAmt.setText("\u20B9" + amtVal);

                                            TextView casheAmt = (TextView) findViewById(R.id.casheAmt);
                                            casheAmt.setText(String.valueOf(amtVal));
                                            TextView procFee = (TextView) findViewById(R.id.tvProcFee);
                                            procFee.setText(String.valueOf(amtVal * feePerc / 100).replace(".0",""));
                                            TextView inHand = (TextView) findViewById(R.id.inHand);
                                            inHand.setText(String.valueOf(amtVal - (amtVal * feePerc / 100) - processingFee).replace("0.",""));
                                        }
                                    }
                                });
                            }

                                /*public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                    progress = progress / loanIncrement;
                                    progress = progress * loanIncrement;
                                    TextView casheVal = (TextView) findViewById(R.id.casheVal);
                                    casheVal.setText("Rs. " + String.valueOf(progress));
                                    TextView casheAmt = (TextView) findViewById(R.id.casheAmt);
                                    casheAmt.setText("Rs. " + String.valueOf(progress));
                                    TextView procFee = (TextView) findViewById(R.id.procFee);
                                    procFee.setText("- Rs. " + String.valueOf(progress * feeProc / 100));
                                    TextView tvFlatFee = (TextView) findViewById(R.id.flatFee);
                                    tvFlatFee.setText("- Rs. "+String.valueOf(processingFee));
                                    TextView inHand = (TextView) findViewById(R.id.inHand);
                                    inHand.setText("Rs. " + String.valueOf((progress - (progress * feeProc / 100)) - processingFee));
                                }*/

                            //pd.hide();

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
                            Toast.makeText(Dashboard.this, "No Internet access. Please check your connection.",Toast.LENGTH_LONG).show();
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

        reqStatus.setShouldCache(false);
        rQueue.getInstance(this).queueRequest("",reqStatus);

    }

    private void setupDefaulted()
    {
        RelativeLayout rLayout = (RelativeLayout) findViewById(R.id.rLayout);
        RelativeLayout dLayout = (RelativeLayout) findViewById(R.id.dLayout);
        ImageView bgArc = (ImageView) findViewById(R.id.bgArc);
        TextView tvDefaulted = (TextView) findViewById(R.id.tvDefaulted);
        tvDefaulted.setVisibility(View.VISIBLE);
        bgArc.setVisibility(View.GONE);
        ImageView bgArc2 = (ImageView) findViewById(R.id.bgArc2);
        bgArc2.setVisibility(View.VISIBLE);
        rLayout.setVisibility(View.GONE);
        dLayout.setVisibility(View.VISIBLE);
        StringRequest reqLoan = new StringRequest(Request.Method.GET, urlCredit,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d("IPB", response);
                            JSONObject jsonObject = new JSONObject(response.toString());
                            if(!jsonObject.isNull("entity"))
                            {
                                JSONObject jsonEntity = jsonObject.getJSONObject("entity");
                                if(!jsonEntity.isNull("loadRequestDetails")) {
                                    JSONArray jsonLoans = jsonEntity.getJSONArray("loadRequestDetails");
                                    for (int i = 0; i < jsonLoans.length(); i++) {
                                        Loan loan = new Loan();
                                        try {
                                            JSONObject obj = jsonLoans.getJSONObject(i);

                                            String tmp;
                                            String tmpDate;

                                            tmp = obj.get("penaltyAmount").toString();
                                            if(!tmp.equalsIgnoreCase("0"))
                                            {
                                                tmp = obj.get("approvedDate").toString();
                                                if(!tmp.equalsIgnoreCase("null")) {
                                                    Date date = null;
                                                    try {
                                                        date = new SimpleDateFormat("yyyy-mm-dd").parse(tmp);
                                                    } catch (ParseException e) {
                                                        e.printStackTrace();
                                                    }
                                                    tmpDate = new SimpleDateFormat("dd-mm-yyyy").format(date);
                                                    loan.setApprovedDate(tmpDate);
                                                }
                                                else
                                                    loan.setApprovedDate("");
                                                //loan.setDaysLeftMsg(obj.get("daysLeftMessage").toString());

                                                tmp = obj.get("dueDate").toString();
                                                if(!tmp.equalsIgnoreCase("null")) {
                                                    Date date = null;
                                                    try {
                                                        date = new SimpleDateFormat("yyyy-mm-dd").parse(tmp);
                                                    } catch (ParseException e) {
                                                        e.printStackTrace();
                                                    }
                                                    tmpDate = new SimpleDateFormat("dd-mm-yyyy").format(date);
                                                    loan.setDueDate(tmpDate.toString());
                                                }
                                                else
                                                    loan.setDueDate("");
                                                loan.setLoanAmount(obj.get("principalAmount").toString());
                                                loan.setPenaltyAmount(obj.get("penaltyAmount").toString());
                                                loan.setProcessingFee(obj.get("processingFee").toString());
                                                loan.setLoanStatus(obj.get("loanStatus").toString());
                                                //loan.setTotalDueAmount("\u20B9" + obj.get("totalAmountDue").toString());
                                                loan.setTotalDueAmount(obj.get("totalAmountDue").toString());
                                                loans.add(loan);
                                            }
                                            //loan.setLoanStatus();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    lvHistory = (ListView) findViewById(R.id.lvHistory);
                                    adapter = new HistoryAdapter(Dashboard.this, loans);
                                    lvHistory.setAdapter(adapter);
                                }
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
                            Toast.makeText(Dashboard.this, "No Internet access. Please check your connection.",Toast.LENGTH_LONG).show();
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
        reqLoan.setShouldCache(false);
        rQueue.getInstance(this).queueRequest("",reqLoan);

        lvHistory = (ListView)findViewById(R.id.lvHistory);
        typeFace(lvHistory);
        lvHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RelativeLayout rl = (RelativeLayout) view.findViewById(R.id.rlDetails);
                if (rl.getVisibility() == View.GONE) {
                    TextView tvShowHide = (TextView) view.findViewById(R.id.tvToggle);
                    tvShowHide.setText("HIDE DETAILS");
                    rl.setAlpha(0);
                    rl.setVisibility(View.VISIBLE);
                    rl.animate().alpha(1).setDuration(500);
                } else {
                    TextView tvShowHide = (TextView) view.findViewById(R.id.tvToggle);
                    tvShowHide.setText("SHOW DETAILS");
                    rl.animate().alpha(0).setDuration(3000);
                    rl.setVisibility(View.GONE);
                }
                lvHistory.setSelection(position);
            }
        });
        ImageButton ibRepay = (ImageButton) findViewById(R.id.ibRepay);
        ibRepay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent i = new Intent(Dashboard.this, MakePayment.class);
                i.putExtra("name", name);
                i.putExtra("email", email);
                i.putExtra("custID", custID);
                i.putExtra("accessToken", accessToken);
                i.putExtra("refreshToken", refreshToken);
                i.putExtra("tokenType", tokenType);
                startActivity(i);*/
                Intent i = new Intent(Dashboard.this, RepayScreen.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP );
                startActivity(i);
            }
        });
    }

    public int pxToDp(int px) {
        DisplayMetrics displayMetrics = Dashboard.this.getResources().getDisplayMetrics();
        int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }

    @Override
    protected boolean isDefaulted() {return usrDefaulted;}

    public void displayAlertDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.promo_popup, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);


        TextView tvPromoOK = (TextView)dialogView.findViewById(R.id.tv_promo_ok);
        TextView tvPromoCANCEL = (TextView)dialogView.findViewById(R.id.tv_promo_cancel);
        final LinearLayout llPromoBtn = (LinearLayout)dialogView.findViewById(R.id.ll_promo_btn);
        final TextView tvPromoMsg = (TextView)dialogView.findViewById(R.id.tv_promo_msg);
        final TextView tvPromoProceed = (TextView)dialogView.findViewById(R.id.tv_promo_proceed);
        final TextInputLayout etPromo = (TextInputLayout)dialogView.findViewById(R.id.til_promo_code);



        tvPromoOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                EditText etPromoCode = (EditText) dialogView.findViewById(R.id.et_promo_code);
                TextInputLayout til = (TextInputLayout) dialogView.findViewById(R.id.til_promo_code);
                if (etPromoCode.getText().toString().trim().isEmpty()){
                    //etPromoCode.setError("Field can not be empty");
                    til.setErrorEnabled(true);
                    til.setError("Field can not be empty");
                }

                else{
                    TextView tvPromoMsgApplied = (TextView)findViewById(R.id.tvPromoCodeApplied);
                    //tvPromoMsgApplied.setVisibility(View.GONE);
                    StringRequest reqAuth = new StringRequest(Request.Method.GET, urlPromoCode+etPromoCode.getText().toString(),
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        Log.d("Promo Responce",response);
                                        String waiveOffAmount = "";
                                        String promoCode = "";
                                        JSONObject jsonStatus = new JSONObject(response.toString());
                                        JSONObject jsonEntity = new JSONObject(jsonStatus.getString("entity"));
                                        if(!jsonEntity.isNull("promoCode")){
                                            promoCode = jsonEntity.getString("promoCode");
                                            CASHe.validPromoCode = promoCode;
                                        }
                                        //else CASHe.validPromoCode = "";
                                        EditText etPromoCode1 = (EditText) dialogView.findViewById(R.id.et_promo_code);
                                        if(jsonEntity.getString("promoCodeStatus").equalsIgnoreCase("VALID")){
                                            TextView tvCasheAmt = (TextView) findViewById(R.id.casheAmt);
                                            TextView tvInterest = (TextView) findViewById(R.id.tvProcFee);
                                            TextView tvOneProcFee = (TextView)findViewById(R.id.oneTimeFee);
                                            TextView inHand = (TextView) findViewById(R.id.inHand);
                                            TextView tvPromoMsgApplied = (TextView)findViewById(R.id.tvPromoCodeApplied);
                                            tvPromoMsgApplied.setVisibility(View.VISIBLE);
                                            Resources res = getResources();
                                            String text = String.format(res.getString(R.string.promo_applied), etPromoCode1.getText().toString());
                                            waiveOffAmount = jsonEntity.getString("waiveOffAmount");
                                            tvPromoMsgApplied.setText(text);
                                            if(processingFee >=
                                                    Integer.parseInt(waiveOffAmount)) {
                                                inHand.setText(String.valueOf(Integer.parseInt(tvCasheAmt.getText().toString()) -
                                                        Integer.parseInt(tvInterest.getText().toString().replace(".0", "")) -
                                                        processingFee + Integer.parseInt(waiveOffAmount)).replace(".0", ""));
                                                tvOneProcFee.setText(String.valueOf(processingFee -
                                                        Integer.parseInt(waiveOffAmount)).replace(".0", ""));
                                            }
                                            else{
                                                inHand.setText(String.valueOf(Integer.parseInt(tvCasheAmt.getText().toString()) -
                                                        Integer.parseInt(tvInterest.getText().toString().replace(".0", ""))));
                                                tvOneProcFee.setText("0");
                                            }
                                            tvPromoMsg.setTextColor(Color.parseColor("#33ACA1"));
                                        }

                                        String message = jsonEntity.getString("message");
                                        llPromoBtn.setVisibility(View.GONE);
                                        etPromo.setVisibility(View.GONE);
                                        tvPromoMsg.setVisibility(View.VISIBLE);
                                        tvPromoProceed.setVisibility(View.VISIBLE);
                                        tvPromoMsg.setText(message);
                                        //setupDash(processingFeeFlat);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    error.printStackTrace();
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
                rQueue.getInstance(Dashboard.this).queueRequest("", reqAuth);
            }

            }
        });

        final AlertDialog dialog = dialogBuilder.create();
        dialog.show();
        tvPromoProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        tvPromoCANCEL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
}
