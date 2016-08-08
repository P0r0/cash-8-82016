package co.tslc.cashe.android;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
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
import java.util.Locale;
import java.util.Map;

public class CasheHistory extends Base {

    protected CASHe app;
    private Tracker mTracker;

    private static final String urlHistory = CASHe.webApiUrl + "api/cashe/customer/loanHistory";

    private String name;
    private String email;
    private String custID;

    private static String accessToken = "";
    private static String refreshToken = "" ;
    private static String tokenType = "";

    private List<Loan> loans = new ArrayList<Loan>();
    private ListView lvHistory;
    private HistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_cashe_history);
        LayoutInflater inflater = (LayoutInflater)this.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.activity_cashe_history, frameLayout, true);
        typeFace(frameLayout);
        try {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }catch(Exception e){}

        app = (CASHe)getApplication();
        mTracker = app.getDefaultTracker();
        mTracker.setScreenName("CASHeHistory");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        Intent i = getIntent();
        name = i.getStringExtra("name");
        email = i.getStringExtra("email");
        custID = i.getStringExtra("custID");
        accessToken = i.getStringExtra("accessToken");
        refreshToken = i.getStringExtra("refreshToken");
        tokenType = i.getStringExtra("tokenType");

        StringRequest reqLoan = new StringRequest(Request.Method.GET, urlHistory,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d("IPB", response);
                            JSONObject jsonObject = new JSONObject(response.toString());
                            if(!jsonObject.isNull("entity"))
                            {
                                JSONObject jsonEntity = jsonObject.getJSONObject("entity");
                                if(!jsonEntity.isNull("totalLoanAmountDue"))
                                {
                                    TextView totalLoanAmount = (TextView) findViewById(R.id.totalLoanAmt);
                                    totalLoanAmount.setText(jsonEntity.get("totalLoanAmountDue").toString().replace(".0",""));
                                }
                                if(!jsonEntity.isNull("casheHistoryDtos")) {
                                    JSONArray jsonLoans = jsonEntity.getJSONArray("casheHistoryDtos");
                                    if(jsonLoans.length()<=0 || jsonEntity.get("totalLoanAmountDue").toString().replace(".0","").equalsIgnoreCase("0"))
                                    {
                                        ImageButton ibRepay = (ImageButton) findViewById(R.id.ibRepay);
                                        ibRepay.setVisibility(View.GONE);
                                    }
                                    for (int i = 0; i < jsonLoans.length(); i++) {
                                        Loan loan = new Loan();
                                        try {
                                            JSONObject obj = jsonLoans.getJSONObject(i);
                                            String tmp;
                                            String tmpDate;

                                            tmp = obj.get("approvedDate").toString();
                                            if(!tmp.equalsIgnoreCase("null")) {
                                                Date date = null;
                                                try {
                                                    date = new SimpleDateFormat("yyyy-mm-dd", Locale.ENGLISH).parse(tmp);
                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                }
                                                tmpDate = new SimpleDateFormat("dd-mm-yyyy", Locale.ENGLISH).format(date);
                                                loan.setApprovedDate(tmpDate);
                                            }
                                            else
                                                loan.setApprovedDate("");
                                            loan.setDaysLeftMsg(obj.get("daysLeftMessage").toString());

                                            tmp = obj.get("dueDate").toString();
                                            if(!tmp.equalsIgnoreCase("null")) {
                                                Date date = null;
                                                try {
                                                    date = new SimpleDateFormat("yyyy-mm-dd", Locale.ENGLISH).parse(tmp);
                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                }
                                                tmpDate = new SimpleDateFormat("dd-mm-yyyy", Locale.ENGLISH).format(date);
                                                loan.setDueDate(tmpDate.toString());
                                            }
                                            else
                                                loan.setDueDate("");
                                            //loan.setLoanAmount(obj.get("loanAmount").toString().replace(".0",""));
                                            loan.setLoanAmount(obj.get("totalLoanRequestedAmount").toString().replace(".0",""));
                                            loan.setPenaltyAmount(obj.get("penaltyAmount").toString().replace(".0", ""));
                                            loan.setProcessingFee(obj.get("processingFee").toString().replace(".0", ""));
                                            loan.setTotalDueAmount(obj.get("totalDueAmount").toString().replace(".0",""));
                                            loan.setLoanStatus(obj.get("loanStatus").toString());
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        loans.add(loan);

                                    }
                                    lvHistory = (ListView) findViewById(R.id.lvHistory);
                                    adapter = new HistoryAdapter(CasheHistory.this, loans);
                                    lvHistory.setAdapter(adapter);
                                }
                            }
                            else {
                                ImageButton ibRepay = (ImageButton) findViewById(R.id.ibRepay);
                                ibRepay.setVisibility(View.GONE);
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
                            Toast.makeText(CasheHistory.this, "No Internet access. Please check your connection.", Toast.LENGTH_LONG).show();
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

        final ListView lvHistory = (ListView)findViewById(R.id.lvHistory);
        typeFace(lvHistory);

        lvHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RelativeLayout rl = (RelativeLayout) view.findViewById(R.id.rlDetails);
                if(rl.getVisibility()==View.GONE)
                {
                    TextView tvShowHide = (TextView)view.findViewById(R.id.tvToggle);
                    tvShowHide.setText("HIDE DETAILS");
                    rl.setAlpha(0);
                    rl.setVisibility(View.VISIBLE);
                    rl.animate().alpha(1).setDuration(500);
                }
                else {
                    TextView tvShowHide = (TextView)view.findViewById(R.id.tvToggle);
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
                Intent i = new Intent(CasheHistory.this, RepayScreen.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP );
                startActivity(i);
                /*Intent i = new Intent(CasheHistory.this, MakePayment.class);
                i.putExtra("name", name);
                i.putExtra("email", email);
                i.putExtra("custID", custID);
                i.putExtra("accessToken", accessToken);
                i.putExtra("refreshToken", refreshToken);
                i.putExtra("tokenType", tokenType);*/
                /*i.putExtra("loanAmount", loans.get(position).getLoanAmount());
                i.putExtra("totalDueAmount", loans.get(position).getTotalDueAmount());
                i.putExtra("penaltyAmount", loans.get(position).getPenaltyAmount());
                i.putExtra("processingFee", loans.get(position).getProcessingFee());*/
                //startActivity(i);
            }
        });
    }

    /*@Override
    public void onBackPressed() {
        Intent i = new Intent(CasheHistory.this, Dashboard.class);
        i.putExtra("name", name);
        i.putExtra("email", email);
        i.putExtra("custID", custID);
        i.putExtra("accessToken", accessToken);
        i.putExtra("refreshToken", refreshToken);
        i.putExtra("tokenType", tokenType);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }*/

    protected boolean isBlocked() {
        if(CASHe.blocked == 2)return true;
        else return false;}
}
