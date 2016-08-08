package co.tslc.cashe.android;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.Spinner;
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
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class IFSCSearch extends AppCompatActivity {

    protected CASHe app;
    private Tracker mTracker;

    private static String accessToken = "";
    private static String refreshToken = "" ;
    private static String tokenType = "";
    private static String ifsc = "";
    private int bankPos;
    private String bankName;

    ArrayList<String> idStates = new ArrayList<String>();
    ArrayList<String> txtStates = new ArrayList<String>();
    ArrayList<String> idCities = new ArrayList<String>();
    ArrayList<String> txtCities = new ArrayList<String>();
    ArrayList<String> idBanks = new ArrayList<String>();
    ArrayList<String> txtBanks = new ArrayList<String>();
    ArrayList<String> idBranches = new ArrayList<String>();
    ArrayList<String> txtBranches = new ArrayList<String>();

    private static final String urlStates = CASHe.webApiUrl + "api/cashe/customer/getBankStates";
    private static final String urlCities = CASHe.webApiUrl + "api/cashe/customer/getBankStateCities";
    private static final String urlBanks = CASHe.webApiUrl + "api/cashe/customer/getBanksName";
    private static final String urlBranches = CASHe.webApiUrl + "api/cashe/customer/getBankBranch";

    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ifscsearch);
        try {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }catch(Exception e){}
        app = (CASHe)getApplication();
        app.isInForeground = true;

        mTracker = app.getDefaultTracker();
        mTracker.setScreenName("IFSCSearch");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        Intent i = getIntent();
        accessToken = i.getStringExtra("accessToken");
        refreshToken = i.getStringExtra("refreshToken");
        tokenType = i.getStringExtra("tokenType");
        bankPos = i.getIntExtra("bankPosition", 0);
        bankName = i.getStringExtra("bankName");

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_chevron_left_white_24dp);


        if(!isFinishing()) pd = ProgressDialog.show(this, "IFSC Codes", "Loading...");
        StringRequest reqBanks = new StringRequest(Request.Method.GET, urlBanks,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d("IFSC", response.toString());
                            JSONObject jsonBanks = new JSONObject(response);
                            JSONArray arrBanks = jsonBanks.getJSONArray("entity");
                            idBanks.add("0");
                            txtBanks.add("BANK");
                            for (int i=0; i< arrBanks.length();i++) {
                                String sBank = arrBanks.getJSONObject(i).getString("bankName");
                                String iBank = arrBanks.getJSONObject(i).getString("bankId");
                                idBanks.add(String.valueOf(iBank));
                                txtBanks.add(sBank);
                            }
                            AutoCompleteTextView actvBank = (AutoCompleteTextView)findViewById(R.id.actvBank);
                            actvBank.setAdapter(new ArrayAdapter<String>(IFSCSearch.this,
                                    android.R.layout.simple_spinner_dropdown_item,
                                    txtBanks));
                            actvBank.post(new Runnable() {
                                @Override
                                public void run() {
                                    AutoCompleteTextView actvBank = (AutoCompleteTextView) findViewById(R.id.actvBank);
                                    actvBank.setListSelection(bankPos);
                                    actvBank.setText(bankName);
                                }
                            });
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
                            Toast.makeText(IFSCSearch.this, "No Internet access. Please check your connection.",Toast.LENGTH_LONG).show();
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
        rQueue.getInstance(this).queueRequest("", reqBanks);

        AutoCompleteTextView actvBank = (AutoCompleteTextView)findViewById(R.id.actvBank);

        actvBank.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AutoCompleteTextView actvState = (AutoCompleteTextView) findViewById(R.id.actvState);
                AutoCompleteTextView actvCity = (AutoCompleteTextView) findViewById(R.id.actvCity);
                Spinner spnrBranch = (Spinner) findViewById(R.id.spnrBranch);
                TextView tvBranch = (TextView) findViewById(R.id.tvBranch);
                actvCity.setAdapter(null);
                spnrBranch.setAdapter(null);
                spnrBranch.setVisibility(View.GONE);
                tvBranch.setVisibility(View.GONE);
                TextView tvIFSC = (TextView) findViewById(R.id.tvIFSC);
                tvIFSC.setText("");
                tvIFSC.setVisibility(View.GONE);

                ///////
                //if (position == 0) return;
                AutoCompleteTextView actvBank = (AutoCompleteTextView) findViewById(R.id.actvBank);
                if (actvBank.getListSelection() == 0) return;
                String bankID = getBankID(actvBank.getText().toString());
                if (!actvState.getText().toString().isEmpty()) {
                    String cityID = getCityID(actvCity.getText().toString());
                    if(!isFinishing()) pd = ProgressDialog.show(IFSCSearch.this,"CASHe","Loading cities...");

                    StringRequest reqCities = new StringRequest(Request.Method.GET, urlCities + "/" + cityID,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject jsonCities = new JSONObject(response);
                                        JSONArray arrCities = jsonCities.getJSONArray("entity");
                                        idCities.clear();
                                        txtCities.clear();
                                        AutoCompleteTextView spnr = (AutoCompleteTextView) findViewById(R.id.actvCity);
                                        idCities.add("0");
                                        txtCities.add("CITY");
                                        for (int i = 0; i < arrCities.length(); i++) {
                                            String sCity = arrCities.getJSONObject(i).getString("bankCityName");
                                            String iCity = arrCities.getJSONObject(i).getString("bankCityId");
                                            idCities.add(String.valueOf(iCity));
                                            txtCities.add(sCity);
                                        }
                                        TextView tvCity = (TextView) findViewById(R.id.tvCity);
                                        AutoCompleteTextView actvCity = (AutoCompleteTextView) findViewById(R.id.actvCity);
                                        actvCity.setAdapter(new ArrayAdapter<String>(IFSCSearch.this,
                                                android.R.layout.simple_spinner_dropdown_item,
                                                txtCities));
                                        tvCity.setVisibility(View.VISIBLE);
                                        actvCity.setVisibility(View.VISIBLE);
                                        dismissDailog();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        dismissDailog();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    dismissDailog();
                                    error.printStackTrace();
                                    if (error instanceof NoConnectionError) {
                                        Toast.makeText(IFSCSearch.this, "No Internet access. Please check your connection.", Toast.LENGTH_LONG).show();
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
                    rQueue.getInstance(IFSCSearch.this).queueRequest("", reqCities);
                }
            }
        });

        StringRequest reqStates = new StringRequest(Request.Method.GET, urlStates,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonStates = new JSONObject(response);
                            JSONArray arrStates = jsonStates.getJSONArray("entity");
                            idStates.add("0");
                            txtStates.add("STATE");
                            for (int i=0; i< arrStates.length();i++) {
                                String sState = arrStates.getJSONObject(i).getString("bankStateName");
                                String iState = arrStates.getJSONObject(i).getString("bankStateId");
                                idStates.add(String.valueOf(iState));
                                txtStates.add(sState);
                            }
                            AutoCompleteTextView actvState = (AutoCompleteTextView)findViewById(R.id.actvState);
                            actvState.setAdapter(new ArrayAdapter<String>(IFSCSearch.this,
                                    android.R.layout.simple_spinner_dropdown_item,
                                    txtStates));
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
                            Toast.makeText(IFSCSearch.this, "No Internet access. Please check your connection.",Toast.LENGTH_LONG).show();
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
        rQueue.getInstance(this).queueRequest("", reqStates);
        dismissDailog();

        AutoCompleteTextView actvState = (AutoCompleteTextView)findViewById(R.id.actvState);

        actvState.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AutoCompleteTextView actvState = (AutoCompleteTextView) findViewById(R.id.actvState);
                AutoCompleteTextView actvCity = (AutoCompleteTextView) findViewById(R.id.actvCity);
                Spinner spnrBranch = (Spinner) findViewById(R.id.spnrBranch);
                actvCity.setAdapter(null);
                spnrBranch.setAdapter(null);
                TextView tvIFSC = (TextView) findViewById(R.id.tvIFSC);
                tvIFSC.setText("");
                tvIFSC.setVisibility(View.GONE);
                String stateID = getStateID(actvState.getText().toString());
                //if (position == 0) return;
                if(!isFinishing()) pd = ProgressDialog.show(IFSCSearch.this,"CASHe","Loading cities...");

                StringRequest reqCities = new StringRequest(Request.Method.GET, urlCities + "/" + stateID,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonCities = new JSONObject(response);
                                    JSONArray arrCities = jsonCities.getJSONArray("entity");
                                    idCities.clear();
                                    txtCities.clear();
                                    AutoCompleteTextView spnr = (AutoCompleteTextView) findViewById(R.id.actvCity);
                                    idCities.add("0");
                                    txtCities.add("CITY");
                                    for (int i = 0; i < arrCities.length(); i++) {
                                        String sCity = arrCities.getJSONObject(i).getString("bankCityName");
                                        String iCity = arrCities.getJSONObject(i).getString("bankCityId");
                                        idCities.add(String.valueOf(iCity));
                                        txtCities.add(sCity);
                                    }
                                    TextView tvCity = (TextView) findViewById(R.id.tvCity);
                                    AutoCompleteTextView actvCity = (AutoCompleteTextView) findViewById(R.id.actvCity);
                                    actvCity.setAdapter(new ArrayAdapter<String>(IFSCSearch.this,
                                            android.R.layout.simple_spinner_dropdown_item,
                                            txtCities));
                                    tvCity.setVisibility(View.VISIBLE);
                                    actvCity.setVisibility(View.VISIBLE);
                                    dismissDailog();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    dismissDailog();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                dismissDailog();
                                error.printStackTrace();
                                if(error instanceof NoConnectionError) {
                                    Toast.makeText(IFSCSearch.this, "No Internet access. Please check your connection.",Toast.LENGTH_LONG).show();
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
                rQueue.getInstance(IFSCSearch.this).queueRequest("",reqCities);
            }
        });

        AutoCompleteTextView actvCity = (AutoCompleteTextView)findViewById(R.id.actvCity);
        actvCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Spinner spnrBranch = (Spinner) findViewById(R.id.spnrBranch);
                spnrBranch.setAdapter(null);
                spnrBranch.setVisibility(View.GONE);
                TextView tvBranch = (TextView) findViewById(R.id.tvBranch);
                tvBranch.setVisibility(View.GONE);
                TextView tvIFSC = (TextView) findViewById(R.id.tvIFSC);
                tvIFSC.setText("");
                tvIFSC.setVisibility(View.GONE);
                //if (position == 0) return;
                AutoCompleteTextView actvBank = (AutoCompleteTextView) findViewById(R.id.actvBank);
                if (actvBank.getListSelection() == 0) return;
                AutoCompleteTextView actvCity = (AutoCompleteTextView) findViewById(R.id.actvCity);
                String bankID = getBankID(actvBank.getText().toString());
                String cityID = getCityID(actvCity.getText().toString());
                //if (position == 0) return;
                if(!isFinishing()) pd = ProgressDialog.show(IFSCSearch.this,"CASHe","Loading branches...");
                StringRequest reqBranches = new StringRequest(Request.Method.GET, urlBranches + "/" + bankID + "/" + cityID,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    dismissDailog();
                                    idBranches.clear();
                                    txtBranches.clear();
                                    idBranches.add("0");
                                    txtBranches.add("BRANCH");
                                    JSONObject jsonBranches = new JSONObject(response);
                                    if (jsonBranches.isNull("entity")) {
                                        Toast.makeText(IFSCSearch.this, "No branches found.", Toast.LENGTH_LONG).show();
                                        TextView tvBranch = (TextView) findViewById(R.id.tvBranch);
                                        Spinner spnrBranch = (Spinner) findViewById(R.id.spnrBranch);
                                        tvBranch.setVisibility(View.GONE);
                                        spnrBranch.setVisibility(View.GONE);
                                    } else {
                                        JSONArray arrBranches = jsonBranches.getJSONArray("entity");
                                        if (arrBranches.length() <= 0) {
                                            Toast.makeText(IFSCSearch.this, "No branches found.", Toast.LENGTH_LONG).show();
                                            TextView tvBranch = (TextView) findViewById(R.id.tvBranch);
                                            Spinner spnrBranch = (Spinner) findViewById(R.id.spnrBranch);
                                            tvBranch.setVisibility(View.GONE);
                                            spnrBranch.setVisibility(View.GONE);
                                        } else {
                                            for (int i = 0; i < arrBranches.length(); i++) {
                                                String sBranch = arrBranches.getJSONObject(i).getString("branchName");
                                                String iBranch = arrBranches.getJSONObject(i).getString("ifscCode");
                                                idBranches.add(iBranch);
                                                txtBranches.add(sBranch);
                                            }
                                            TextView tvBranch = (TextView) findViewById(R.id.tvBranch);
                                            Spinner spnrBranch = (Spinner) findViewById(R.id.spnrBranch);
                                            spnrBranch.setAdapter(new ArrayAdapter<String>(IFSCSearch.this,
                                                    android.R.layout.simple_spinner_dropdown_item,
                                                    txtBranches));
                                            tvBranch.setVisibility(View.VISIBLE);
                                            spnrBranch.setVisibility(View.VISIBLE);
                                        }
                                    }
                                } catch (Exception e) {
                                    dismissDailog();
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                dismissDailog();
                                error.printStackTrace();
                                if (error instanceof NoConnectionError) {
                                    Toast.makeText(IFSCSearch.this, "No Internet access. Please check your connection.", Toast.LENGTH_LONG).show();
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
                rQueue.getInstance(IFSCSearch.this).queueRequest("", reqBranches);
            }
        });

        Spinner spnrBranch = (Spinner)findViewById(R.id.spnrBranch);
        spnrBranch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) return;
                ifsc = idBranches.get(position);
                TextView tvIFSC = (TextView) findViewById(R.id.tvIFSC);
                tvIFSC.setText(ifsc);
                tvIFSC.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ImageButton ibDone = (ImageButton)findViewById(R.id.ibDone);
        ibDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent output = new Intent();
                AutoCompleteTextView actvBank = (AutoCompleteTextView) findViewById(R.id.actvBank);
                output.putExtra("IFSC", ifsc);
                output.putExtra("Bank", actvBank.getText().toString());
                output.putExtra("bankPos", actvBank.getListSelection());
                setResult(RESULT_OK, output);
                finish();
            }
        });

        actvBank.setListSelection(bankPos);
        actvBank.setText(bankName);
    }

    private String getBankID(String bankName)
    {
        int pos = 0;
        String bankID = "";
        for(int i=0;i<txtBanks.size();i++)
        {
            if(txtBanks.get(i).toString().equalsIgnoreCase(bankName))
            {
                //pos = i+1;
                pos = i;
                bankID = idBanks.get(pos).toString();
                break;
            }
        }
        return bankID;
    }

    public void dismissDailog(){
        try {
            if (pd != null)
                if (pd.isShowing()) {
                    pd.dismiss();
                    pd = null;
                }
        }catch(Exception epd){}
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        dismissDailog();
    }

    private String getStateID(String stateName)
    {
        int pos = 0;
        String stateID = "";
        for(int i=0;i<txtBanks.size();i++)
        {
            if(txtStates.get(i).toString().equalsIgnoreCase(stateName))
            {
                //pos = i+1;
                pos = i;
                stateID = idStates.get(pos).toString();
                break;
            }
        }
        return stateID;
    }

    private String getCityID(String cityName)
    {
        int pos = 0;
        String cityID = "";
        for(int i=0;i<txtCities.size();i++)
        {
            if(txtCities.get(i).toString().equalsIgnoreCase(cityName))
            {
                //pos = i+1;
                pos = i;
                cityID = idCities.get(pos).toString();
                break;
            }
        }
        return cityID;
    }

    @Override
    public void onBackPressed() {
        finish();
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
