package co.tslc.cashe.android;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ConfirmDetails extends Base {

    protected CASHe app;
    private Tracker mTracker;

    private String name;
    private String email;
    private String custID;
    private String custTitle;

    private String casheAmt;
    private String inHand;
    private String feeFlat;
    private String feeProc;
    private String feeProcLbl;
    private String feeProcAmt;
    private String dueDate;

    private static String accessToken = "";
    private static String refreshToken = "";
    private static String tokenType = "";

    private static String bankName = "";
    private static String custIFSC = "";
    private static String custAccNum = "";

    private static final String urlConfInf = CASHe.webApiUrl + "api/cashe/customer/details/verifyOnLoanRequest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_confirm_details);
        LayoutInflater inflater = (LayoutInflater)this.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.activity_confirm_details, frameLayout, true);
        typeFace(frameLayout);
        try {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }catch(Exception e){}
        //getLayoutInflater().inflate(R.layout.activity_confirm_details, frameLayout, true);
        //typeFace(frameLayout);
        app = (CASHe)getApplication();
        mTracker = app.getDefaultTracker();
        mTracker.setScreenName("ConfirmDetails");
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
        feeProc = i.getStringExtra("procFee");
        feeFlat = i.getStringExtra("flatFee");
        feeProcLbl = i.getStringExtra("procFeeLbl");
        feeProcAmt = i.getStringExtra("procFeeAmt");
        dueDate = i.getStringExtra("dueDate");

        ImageButton btnYes = (ImageButton) findViewById(R.id.btnYes);
        ImageButton btnNo = (ImageButton) findViewById(R.id.btnNo);

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ConfirmDetails.this, EditProfile.class);
                i.putExtra("name", name);
                i.putExtra("email", email);
                i.putExtra("custID", custID);
                i.putExtra("accessToken", accessToken);
                i.putExtra("refreshToken", refreshToken);
                i.putExtra("tokenType", tokenType);
                i.putExtra("casheAmt",casheAmt);
                i.putExtra("procFee",feeProc);
                i.putExtra("procFeeLbl",feeProcLbl);
                i.putExtra("procFeeAmt",feeProcAmt);
                i.putExtra("flatFee",String.valueOf(feeFlat));
                i.putExtra("inHand",inHand);
                i.putExtra("dueDate",dueDate);
                i.putExtra("eMode","Y");
                i.putExtra("lMode","Y");
                startActivity(i);
                finish();
            }
        });

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ConfirmDetails.this, CasheSummary.class);
                i.putExtra("name", name);
                i.putExtra("email", email);
                i.putExtra("custID", custID);
                i.putExtra("accessToken", accessToken);
                i.putExtra("refreshToken", refreshToken);
                i.putExtra("tokenType", tokenType);
                i.putExtra("casheAmt",casheAmt);
                i.putExtra("procFee",feeProc);
                i.putExtra("procFeeLbl",feeProcLbl);
                i.putExtra("procFeeAmt",feeProcAmt);
                i.putExtra("flatFee",String.valueOf(feeFlat));
                i.putExtra("inHand",inHand);
                i.putExtra("bankName", bankName);
                i.putExtra("ifsc", custIFSC);
                i.putExtra("accNum",custAccNum);
                i.putExtra("dueDate",dueDate);
                startActivity(i);
                //finish();
            }
        });

        StringRequest reqCust = new StringRequest(com.android.volley.Request.Method.GET, urlConfInf,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("IPB", response);
                        try {
                            if (null != response) {
                                JSONObject jsonCust = new JSONObject(response.toString());
                                JSONObject jsonEntity = new JSONObject(jsonCust.getString("entity"));
                                JSONObject jsonPD = jsonEntity.getJSONObject("personalDetailsDto");
                                JSONObject jsonBD = jsonEntity.getJSONObject("bankDetailDto");
                                String custName = jsonPD.getString("customerName");
                                String empName = jsonPD.getString("employerName");
                                String custPAN = jsonPD.getString("pan");
                                String custSal = jsonPD.getString("netSalaryMonthly");
                                bankName = jsonBD.getString("bankName");
                                custIFSC = jsonBD.getString("ifsc");
                                custAccNum = jsonBD.getString("accountNumber");
                                TextView tvName = (TextView) findViewById(R.id.tvEmpName);
                                TextView tvNetSal = (TextView) findViewById(R.id.tvNetSal);
                                TextView tvBankName = (TextView) findViewById(R.id.tvBankName);
                                TextView tvIFSC = (TextView) findViewById(R.id.tvIFSC);
                                TextView tvAccNum = (TextView) findViewById(R.id.tvAccNum);
                                tvName.setText(empName.toString());
                                tvNetSal.setText(custSal.toString());
                                tvBankName.setText(bankName.toString());
                                tvIFSC.setText(custIFSC.toString());
                                tvAccNum.setText(custAccNum.toString());
                            }
                            }catch(Exception e){
                                e.printStackTrace();
                            }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        if(error instanceof NoConnectionError) {
                            Toast.makeText(ConfirmDetails.this, "No Internet access. Please check your connection.", Toast.LENGTH_LONG).show();
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
        reqCust.setShouldCache(false);
        rQueue.getInstance(this).queueRequest("", reqCust);
    }
}
