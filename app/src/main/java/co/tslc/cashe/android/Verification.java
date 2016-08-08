package co.tslc.cashe.android;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import pl.droidsonroids.gif.GifImageView;

public class Verification extends Base {

    protected CASHe app;
    private Tracker mTracker;


    private String name;
    private String email;
    private String custID;
    private String endDate;

    private static String accessToken = "";
    private static String refreshToken = "" ;
    private static String tokenType = "";

    private Handler hndlr = new Handler();
    private Runnable runnable;

    GifImageView ivProc;

    private static final String urlStatus = CASHe.webApiUrl + "api/cashe/customer/status";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater)this.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.activity_verification, frameLayout, true);
        //getLayoutInflater().inflate(R.layout.activity_verification, frameLayout, true);
        typeFace(frameLayout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        try {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }catch(Exception e){}

        app = (CASHe)getApplication();
        mTracker = app.getDefaultTracker();
        mTracker.setScreenName("Verification");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        Button btnSupport = (Button) findViewById(R.id.support);
        btnSupport.setTransformationMethod(null);


        Intent i = getIntent();
        name = i.getStringExtra("name");
        email = i.getStringExtra("email");
        custID = i.getStringExtra("custID");
        endDate = i.getStringExtra("endDate");
        accessToken = i.getStringExtra("accessToken");
        refreshToken = i.getStringExtra("refreshToken");
        tokenType = i.getStringExtra("tokenType");

        //TextView tvDate = (TextView)findViewById(R.id.tvDate);
        //tvDate.setText(endDate);

        ImageButton ibSubmit = (ImageButton) findViewById(R.id.ibSubmit);
        ibSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Verification.this, EditProfile.class);
                i.putExtra("name", name);
                i.putExtra("email", email);
                i.putExtra("custID", custID);
                i.putExtra("accessToken", accessToken);
                i.putExtra("refreshToken", refreshToken);
                i.putExtra("tokenType", tokenType);
                i.putExtra("eMode","Y");
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            }
        });

        ImageButton ibTryAgain = (ImageButton) findViewById(R.id.ibTryAgain);
        ibTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Verification.this, EditProfile.class);
                i.putExtra("name", name);
                i.putExtra("email", email);
                i.putExtra("custID", custID);
                i.putExtra("accessToken", accessToken);
                i.putExtra("refreshToken", refreshToken);
                i.putExtra("tokenType", tokenType);
                i.putExtra("eMode", "Y");
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            }
        });

        btnSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("plain/text");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"support@cashe.co.in"});
                //intent.putExtra(Intent.EXTRA_SUBJECT, "subject");
                //intent.putExtra(Intent.EXTRA_TEXT, "mail body");
                startActivity(Intent.createChooser(intent, ""));
            }
        });

        StringRequest reqAuth = new StringRequest(Request.Method.GET, urlStatus,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        processStatus(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        if(error instanceof NoConnectionError) {
                            Toast.makeText(Verification.this, "No Internet access. Please check your connection.", Toast.LENGTH_LONG).show();
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
        rQueue.getInstance(this).queueRequest("", reqAuth);

        ImageButton ibGetYourCashe = (ImageButton)findViewById(R.id.ibGetYourCashe);
        ibGetYourCashe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                                                i = new Intent(Verification.this, Dashboard.class);
                                            }
                                            else {
                                                i = new Intent(Verification.this, Congratulations.class);
                                                String inHand = jsonEntity.getString("initialLoanAmount");
                                                String inHandMsg = jsonEntity.getString("initialLoanAmountMessage");
                                                i.putExtra("inHand",String.valueOf(inHand));
                                                i.putExtra("inHandMsg",String.valueOf(inHandMsg));
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
                                        else {
                                            i = new Intent(Verification.this, Congratulations.class);
                                            String inHand = jsonEntity.getString("initialLoanAmount");
                                            i.putExtra("inHand", String.valueOf(inHand));
                                        }
                                    }
                                    else
                                    {
                                        Intent i = new Intent(Verification.this, Dashboard.class);
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
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                error.printStackTrace();
                                if(error instanceof NoConnectionError) {
                                    Toast.makeText(Verification.this, "No Internet access. Please check your connection.", Toast.LENGTH_LONG).show();
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
                rQueue.getInstance(Verification.this).queueRequest("SignIn",reqAuth);
            }
        });


        runnable = new Runnable() {
            @Override
            public void run() {
                StringRequest reqAuth = new StringRequest(Request.Method.GET, urlStatus,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                processStatus(response);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                error.printStackTrace();
                                if(error instanceof NoConnectionError) {
                                    Toast.makeText(Verification.this, "No Internet access. Please check your connection.",Toast.LENGTH_LONG).show();
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
                rQueue.getInstance(Verification.this).queueRequest("",reqAuth);
                hndlr.postDelayed(this, 15000);
            }
        };

        hndlr.postDelayed(runnable, 15000);

        ivProc = (GifImageView) findViewById(R.id.ivProcessing);
        try {
            ivProc.setBackgroundResource(R.drawable.icon_processing_grey_anim);
            //Glide.with(this).load("file:///android_asset/gifs/icon_processing_grey_anim.gif").asGif().fitCenter().into(ivProc);
        }catch (OutOfMemoryError outOfMemoryError ) {
            Log.e("IFB", "outOfMemoryError while reading file for sampleSize ");
        }catch(Throwable e){
            Log.e("IFB", "outOfMemoryError while reading file for sampleSize ");
        }
    }

    @Override
    protected boolean isVerified() {return false;}

    private void processStatus(String response)
    {
        try {
            ImageView ivDocs = (ImageView) findViewById(R.id.ivDocuments);
            ImageView ivApproved = (ImageView) findViewById(R.id.ivApproved);
            Log.d("IPB", response);
            String customerStatusName = "";
            JSONArray msgList;
            JSONObject jsonStatus = null;
            JSONObject jsonEntity = null;
            try {
                jsonStatus = new JSONObject(response.toString());
                jsonEntity = new JSONObject(jsonStatus.getString("entity"));
                customerStatusName = jsonEntity.getString("customerStatusName");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            TextView tvDocs = (TextView) findViewById(R.id.tvDocuments);
            ivDocs.measure(0, 0);
            tvDocs.measure(0, 0);
            float icnL = ivDocs.getX();
            float icnC = (ivDocs.getMeasuredWidth() / 2) + icnL;
            float txtL = icnC - (tvDocs.getMeasuredWidth() / 2);
            tvDocs.setX(txtL);
            tvDocs.setVisibility(View.VISIBLE);
            if (customerStatusName.equalsIgnoreCase("Data Received")) {
                Bitmap bmProg = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                        R.drawable.icon_documents);
                ivDocs.setImageBitmap(bmProg);

                if (customerStatusName.equalsIgnoreCase("Data Received")) {
                    try {
                        mTracker.send(new HitBuilders.EventBuilder()
                                .setCategory("Action")
                                .setAction("Verification")
                                .setLabel("DataReceived")
                                .build());
                        if (!jsonEntity.isNull("messageList")) {
                            msgList = jsonEntity.getJSONArray("messageList");
                            if (msgList.length() > 0) {
                                String msgDt = msgList.get(0).toString();
                                Date date = null;
                                TextView tvDate = (TextView) findViewById(R.id.tvDate);
                                TextView tvTime = (TextView) findViewById(R.id.tvTime);
                                try {
                                    date = new SimpleDateFormat("MMM dd, yyyy hh:mm aa", Locale.ENGLISH).parse(msgDt);
                                    String prcDate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(date);
                                    String prcTime = new SimpleDateFormat("hh:mm aa", Locale.ENGLISH).format(date);
                                    tvDate.setText(prcDate);
                                    tvTime.setText(prcTime);
                                } catch (ParseException e) {
                                    if (msgDt != null) {
                                        String[] array = msgDt.split(",");
                                        String dateOnly = array[0].trim();
                                        String yearAndTime = array[1].trim();
                                        String timeOnly = yearAndTime.substring(4).trim();
                                        tvDate.setText(dateOnly);
                                        tvTime.setText(timeOnly);
                                    }
                                    e.printStackTrace();
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (customerStatusName.equalsIgnoreCase("Data Verification In Progress") ||
                    customerStatusName.equalsIgnoreCase("Partially Verified") || customerStatusName.equalsIgnoreCase("Credit Check Pending")) {
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("Verification")
                        .setLabel("DataVerificationInProgress")
                        .build());
            /*Bitmap bmProg = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                    R.drawable.icon_documents);
            ivDocs.setImageBitmap(bmProg);

            bmProg = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                    R.drawable.icon_processing);
            ivProc.setImageBitmap(bmProg);*/
                Bitmap bmProg = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                        R.drawable.icon_documents);
                ivDocs.setImageBitmap(bmProg);

                // ivProc.setImageResource(R.drawable.icon_processing_anim);
                try {
                    ivProc.setBackgroundResource(R.drawable.icon_processing_anim);
                    //Glide.with(this).load("file:///android_asset/gifs/icon_processing_anim.gif").asGif().into(ivProc);
                } catch (OutOfMemoryError outOfMemoryError) {
                    Log.e("IFB", "outOfMemoryError while reading file for sampleSize ");
                } catch (Throwable e) {
                    Log.e("IFB", "outOfMemoryError while reading file for sampleSize ");
                }
                TextView tvProcessing = (TextView) findViewById(R.id.tvProcessing);
                tvProcessing.setVisibility(View.VISIBLE);
                try {
                    if (!jsonEntity.isNull("messageList")) {
                        msgList = jsonEntity.getJSONArray("messageList");
                        if (msgList.length() > 0) {
                            String msgDt = msgList.get(0).toString();
                            Date date = null;
                            TextView tvDate = (TextView) findViewById(R.id.tvDate);
                            TextView tvTime = (TextView) findViewById(R.id.tvTime);
                            try {
                                date = new SimpleDateFormat("MMM dd, yyyy hh:mm aa", Locale.ENGLISH).parse(msgDt);
                                String prcDate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(date);
                                String prcTime = new SimpleDateFormat("hh:mm aa", Locale.ENGLISH).format(date);
                                tvDate.setText(prcDate);
                                tvTime.setText(prcTime);
                            } catch (ParseException e) {
                                if (msgDt != null) {
                                    String[] array = msgDt.split(",");
                                    String dateOnly = array[0].trim();
                                    String yearAndTime = array[1].trim();
                                    String timeOnly = yearAndTime.substring(4).trim();
                                    tvDate.setText(dateOnly);
                                    tvTime.setText(timeOnly);
                                }
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (customerStatusName.equalsIgnoreCase("Credit Approved")) {
                hndlr.removeCallbacks(runnable);

                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("Verification")
                        .setLabel("CreditApproved")
                        .build());
                LinearLayout llDate = (LinearLayout) findViewById(R.id.llDate);
                llDate.setVisibility(View.GONE);
                LinearLayout llProcDate = (LinearLayout) findViewById(R.id.llProcDate);
                llProcDate.setVisibility(View.GONE);
                TextView tvDoneBy = (TextView) findViewById(R.id.tvDoneBy);
                tvDoneBy.setVisibility(View.GONE);
                ImageView ivHdr = (ImageView) findViewById(R.id.ivHdr);
                ivHdr.setBackgroundResource(R.drawable.bg_arc);
                Bitmap bmProg = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                        R.drawable.icon_documents);
                ivDocs.setImageBitmap(bmProg);

                TextView tvStatus = (TextView) findViewById(R.id.tvStatus);
                tvStatus.setTextColor(Color.parseColor("#33ACA1"));
                tvStatus.setText("APPROVED");
                icnL = ivApproved.getX();
                ivApproved.measure(0, 0);
                icnC = (ivApproved.getMeasuredWidth() / 2) + icnL;
                tvStatus.measure(0, 0);
                txtL = icnC - (tvStatus.getMeasuredWidth() / 2);
                tvStatus.setX(txtL);
                tvStatus.setVisibility(View.VISIBLE);

                TextView tvProcessing = (TextView) findViewById(R.id.tvProcessing);
                tvProcessing.setVisibility(View.VISIBLE);

            /*bmProg = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                    R.drawable.icon_processing);
            ivProc.setImageBitmap(bmProg);*/
                //  ivProc.setImageResource(R.drawable.icon_processing_static);
                try {
                    ivProc.setBackgroundResource(R.drawable.icon_processing_static);
                    //Glide.with(this).load("file:///android_asset/gifs/icon_processing_anim.gif").asGif().into(ivProc);
                } catch (OutOfMemoryError outOfMemoryError) {
                    Log.e("IFB", "outOfMemoryError while reading file for sampleSize ");
                } catch (Throwable e) {
                    Log.e("IFB", "outOfMemoryError while reading file for sampleSize ");
                }

                bmProg = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                        R.drawable.icon_approved);
                ivApproved.setImageBitmap(bmProg);
                TextView tvDate = (TextView) findViewById(R.id.tvDate);
                tvDate.setVisibility(View.GONE);
                //Button btnRetry = (Button) findViewById(R.id.btnRetry);
                //btnRetry.setVisibility(View.GONE);
                ImageButton ibGetYourCashe = (ImageButton) findViewById(R.id.ibGetYourCashe);
                ibGetYourCashe.setVisibility(View.VISIBLE);
                //View div_one = (View)findViewById(R.id.divider_one);
                //div_one.setVisibility(View.VISIBLE);
                RelativeLayout rlApproved = (RelativeLayout) findViewById(R.id.rlApproved);
                rlApproved.setVisibility(View.VISIBLE);
                try {
                    GifImageView imageView = (GifImageView) findViewById(R.id.ivTick);
                    imageView.setBackgroundResource(R.drawable.icon_verified_anim);
                    //Glide.with(this).load("file:///android_asset/gifs/icon_verified_anim.gif").asGif().into(imageView);
                }catch (OutOfMemoryError outOfMemoryError ) {
                    Log.e("IFB", "outOfMemoryError while reading file for sampleSize ");
                }catch(Throwable e){
                    Log.e("IFB", "outOfMemoryError while reading file for sampleSize ");
                }
            }
            if (customerStatusName.equalsIgnoreCase("Data Verification Failed") || customerStatusName.equalsIgnoreCase("Credit Declined")
                    || customerStatusName.equalsIgnoreCase("Temporary Block") || customerStatusName.equalsIgnoreCase("Defaulted Block") || customerStatusName.equalsIgnoreCase("Permanent Block")) {
                Base.customerStatusName = customerStatusName;
                hndlr.removeCallbacks(runnable);
                LinearLayout llDate = (LinearLayout) findViewById(R.id.llDate);
                llDate.setVisibility(View.GONE);
                TextView tvDoneBy = (TextView) findViewById(R.id.tvDoneBy);
                tvDoneBy.setVisibility(View.GONE);
                ImageView ivHdr = (ImageView) findViewById(R.id.ivHdr);
                ivHdr.setBackgroundResource(R.drawable.bg_arc);
                TextView tvProcessing = (TextView) findViewById(R.id.tvProcessing);
                tvProcessing.setVisibility(View.VISIBLE);

                LinearLayout llProcDate = (LinearLayout) findViewById(R.id.llProcDate);
                llProcDate.setVisibility(View.GONE);

                Bitmap bmProg = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                        R.drawable.icon_documents);
                ivDocs.setImageBitmap(bmProg);

            /*bmProg = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                    R.drawable.icon_processing);
            ivProc.setImageBitmap(bmProg);*/
                //  ivProc.setImageResource(R.drawable.icon_processing_anim);

                try {
                    ivProc.setBackgroundResource(R.drawable.icon_processing_anim);
                    //Glide.with(this).load("file:///android_asset/gifs/icon_processing_anim.gif").asGif().into(ivProc);
                } catch (OutOfMemoryError outOfMemoryError) {
                    Log.e("IFB", "outOfMemoryError while reading file for sampleSize ");
                } catch (Throwable e) {
                    Log.e("IFB", "outOfMemoryError while reading file for sampleSize ");
                }

                bmProg = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                        R.drawable.icon_ineligible);
                ivApproved.setImageBitmap(bmProg);
                //View div_one = (View)findViewById(R.id.divider_one);
                //div_one.setVisibility(View.VISIBLE);
                TextView tvStatus = (TextView) findViewById(R.id.tvStatus);
                tvStatus.setText("NOT\nELIGIBLE");
                tvStatus.setTextColor(Color.parseColor("#FF0000"));
                icnL = ivApproved.getX();
                ivApproved.measure(0, 0);
                icnC = (ivApproved.getMeasuredWidth() / 2) + icnL;
                tvStatus.measure(0, 0);
                txtL = icnC - (tvStatus.getMeasuredWidth() / 2);
                tvStatus.setX(txtL);
                tvStatus.setVisibility(View.VISIBLE);
                if (customerStatusName.equalsIgnoreCase("Data Verification Failed")) {
                    mTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Action")
                            .setAction("Verification")
                            .setLabel("DataVerificationFailed")
                            .build());
                    RelativeLayout rlInvalid = (RelativeLayout) findViewById(R.id.rlInvalid);
                    rlInvalid.setVisibility(View.VISIBLE);
                    try {
                        jsonStatus = new JSONObject(response.toString());
                        jsonEntity = new JSONObject(jsonStatus.getString("entity"));
                        if (!jsonEntity.isNull("messageList")) {
                            msgList = jsonEntity.getJSONArray("messageList");
                            String docs = "";
                            for (int i = 0; i < msgList.length(); i++) {
                                String strDoc = msgList.getString(i);
                                if (docs == "") {
                                    if (!strDoc.isEmpty()) {
                                        docs = (i + 1) + ". " + strDoc + '\n';
                                    }
                                } else {
                                    if (!strDoc.isEmpty()) {
                                        docs = docs + (i + 1) + ". " + strDoc + '\n';
                                    }
                                }
                            }
                            TextView tvDocsToProvide = (TextView) findViewById(R.id.tvDocsToProvide);
                            tvDocsToProvide.setLineSpacing(2, 1);
                            tvDocsToProvide.setText(docs);
                        }
                        LinearLayout llKnowMore = (LinearLayout) findViewById(R.id.llKnowMore);
                        llKnowMore.setVisibility(View.VISIBLE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (customerStatusName.equalsIgnoreCase("Credit Declined")
                        || customerStatusName.equalsIgnoreCase("Temporary Block") ||customerStatusName.equalsIgnoreCase("Defaulted Block") || customerStatusName.equalsIgnoreCase("Permanent Block")) {

                    Intent i = new Intent(Verification.this, BlockedUser.class);
                    i.putExtra("name", name);
                    i.putExtra("email", email);
                    i.putExtra("custID", custID);
                    i.putExtra("accessToken", accessToken);
                    i.putExtra("refreshToken", refreshToken);
                    i.putExtra("tokenType", tokenType);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    finish();


                }
            }
            if (customerStatusName.equalsIgnoreCase("Defaulted")) {
                hndlr.removeCallbacks(runnable);

                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("Verification")
                        .setLabel("Defaulted")
                        .build());
                Intent i = new Intent(Verification.this, Dashboard.class);
                i.putExtra("name", name);
                i.putExtra("email", email);
                i.putExtra("custID", custID);
                i.putExtra("accessToken", accessToken);
                i.putExtra("refreshToken", refreshToken);
                i.putExtra("tokenType", tokenType);
                startActivity(i);
            }
        }
        catch(Exception e){}
    }

    @Override
    public void onBackPressed() {}
}
