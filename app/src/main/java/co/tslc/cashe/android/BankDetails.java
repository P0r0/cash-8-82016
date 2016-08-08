package co.tslc.cashe.android;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BankDetails extends Base {

    protected CASHe app;
    private Tracker mTracker;

    ProgressDialog pd;

    private static final String urlBanks = CASHe.webApiUrl + "api/cashe/customer/getBanksName";
    private static final String urlBankDetails = CASHe.webApiUrl + "api/cashe/customer/bankDetailsUpdate";
    private static final String urlIFSC = CASHe.webApiUrl + "api/cashe/customer/bankDetailByIFSC/";

    private String name;
    private String email;
    private String custID;
    private String casheAmt;
    private String inHand;
    private String feeFlat;
    private String feeProc;
    private String feeProcLbl;
    private String feeProcAmt;

    private List<ListBanks> listBanks = new ArrayList<ListBanks>();

    private static String accessToken = "";
    private static String refreshToken = "" ;
    private static String tokenType = "";

    private int bankPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_bank_details);
        LayoutInflater inflater = (LayoutInflater)this.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.activity_bank_details, frameLayout, true);
        typeFace(frameLayout);
        try {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }catch(Exception e){}
        //getLayoutInflater().inflate(R.layout.activity_bank_details, frameLayout, true);
       // typeFace(frameLayout);

        app = (CASHe)getApplication();
        mTracker = app.getDefaultTracker();
        mTracker.setScreenName("BankDetails");
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

        if(!isFinishing()) pd = ProgressDialog.show(BankDetails.this, "Retrieving list of banks", "Loading...");
        StringRequest reqBanks = new StringRequest(Request.Method.GET, urlBanks,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonBanks = new JSONObject(response);
                            JSONArray arrBanks = jsonBanks.getJSONArray("entity");
                            for (int i=0; i< arrBanks.length();i++) {
                                String bankName = arrBanks.getJSONObject(i).getString("bankName");
                                String bankID = arrBanks.getJSONObject(i).getString("bankId");
                                listBanks.add(new ListBanks(bankID,bankName));
                            }
                            AutoCompleteTextView actvBank = (AutoCompleteTextView)findViewById(R.id.actvBank);
                            actvBank.setAdapter(new bankSuggest(BankDetails.this, actvBank.getText().toString(), listBanks));
                            actvBank.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    bankPosition = position;
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                            dismissDailog();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dismissDailog();
                        error.printStackTrace();
                        if(error instanceof NoConnectionError) {
                            Toast.makeText(BankDetails.this, "No Internet access. Please check your connection.",Toast.LENGTH_LONG).show();
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
        reqBanks.setShouldCache(false);
        rQueue.getInstance(this).queueRequest("",reqBanks);
        Button btnFind = (Button)findViewById(R.id.btnFind);
        btnFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AutoCompleteTextView actvBank = (AutoCompleteTextView)findViewById(R.id.actvBank);
                EditText etIFSC = (EditText)findViewById(R.id.etIFSC);
                etIFSC.setError(null);
                Intent i = new Intent(BankDetails.this, IFSCSearch.class);
                bankPosition = getBankPos(actvBank.getText().toString());
                i.putExtra("accessToken", accessToken);
                i.putExtra("refreshToken", refreshToken);
                i.putExtra("tokenType", tokenType);
                i.putExtra("bankPosition", bankPosition);
                startActivityForResult(i, 654);
            }
        });
        ImageButton btnCasheIt = (ImageButton)findViewById(R.id.ibCASHeIt);
        btnCasheIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!validForm()) return;

                StringRequest reqBankDetails = new StringRequest(Request.Method.POST, urlBankDetails,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    Log.d("IPB", response);

                                        AutoCompleteTextView etBankName = (AutoCompleteTextView) findViewById(R.id.actvBank);
                                        EditText etAccNum = (EditText) findViewById(R.id.etAccNum);
                                        EditText etIFSC = (EditText) findViewById(R.id.etIFSC);

                                        Intent i = new Intent(BankDetails.this, CasheSummaryConfirm.class);
                                        i.putExtra("name", name);
                                        i.putExtra("email", email);
                                        i.putExtra("custID", custID);
                                        i.putExtra("accessToken", accessToken);
                                        i.putExtra("refreshToken", refreshToken);
                                        i.putExtra("tokenType", tokenType);
                                        i.putExtra("casheAmt", String.valueOf(casheAmt));
                                        i.putExtra("procFee", String.valueOf(feeProc));
                                        i.putExtra("flatFee", String.valueOf(feeFlat));
                                        i.putExtra("inHand", String.valueOf(inHand));
                                        i.putExtra("bankName", etBankName.getText().toString());
                                        i.putExtra("accNum", etAccNum.getText().toString());
                                        i.putExtra("ifsc", etIFSC.getText().toString());
                                        startActivity(i);
                                        finish();

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
                                    Toast.makeText(BankDetails.this, "No Internet access. Please check your connection.",Toast.LENGTH_LONG).show();
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
                        try {
                            AutoCompleteTextView etBankName = (AutoCompleteTextView)findViewById(R.id.actvBank);
                            EditText etAccNum = (EditText)findViewById(R.id.etAccNum);
                            EditText etIFSC = (EditText)findViewById(R.id.etIFSC);
                            String bankName = etBankName.getText().toString();
                            String ifsc = etIFSC.getText().toString();
                            String accNum = etAccNum.getText().toString();
                            uInf = "{\"bankName\" : \""+bankName+"\",\"ifsc\" : \""+ifsc+"\",\"accountNumber\" : \""+accNum+"\"}";
                            return uInf == null ? null : uInf.getBytes("utf-8");
                        } catch (UnsupportedEncodingException uee) {
                            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                                    uInf, "utf-8");
                            return null;
                        }

                    }

                };
                reqBankDetails.setShouldCache(false);
                rQueue.getInstance(BankDetails.this).queueRequest("",reqBankDetails);
            }
        });
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

    private boolean validForm()
    {
        boolean valid = true;
        AutoCompleteTextView actvBank = (AutoCompleteTextView)findViewById(R.id.actvBank);
        if(actvBank.getText().toString().isEmpty()||actvBank.getText().toString().length()<5)
        {
            actvBank.setError("Please provide your bank");
            return false;
        }
        /*EditText etAccNum = (EditText)findViewById(R.id.etAccNum);
        if(etAccNum.getText().toString().isEmpty()||etAccNum.getText().toString().length()<5)
        {
            etAccNum.setError("Please provide a valid account number");
            return false;
        }*/
        EditText etAccNum = (EditText)findViewById(R.id.etAccNum);
        if(!chkstr(etAccNum.getText().toString()) ||
                etAccNum.getText().toString().isEmpty()||etAccNum.getText().toString().length()<5)
        {
            etAccNum.setError("Please provide a valid account number");
            return false;
        }
        EditText etIFSC = (EditText)findViewById(R.id.etIFSC);
        if(etIFSC.getText().toString().isEmpty()|| etIFSC.getText().toString().length() < 11
                || !(etIFSC.getText().toString().matches("[A-Z|a-z]{4}[0][A-Z|a-z|\\d]{6}$")))
        {
            etIFSC.setError("Please provide a valid IFSC code");
            return false;
        }
        StringRequest reqIFSC = new StringRequest(Request.Method.GET, urlIFSC+"/"+ etIFSC.getText().toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject details = new JSONObject(response);
                            String statusType =  details.getString("statusType");
                            if(!statusType.equalsIgnoreCase("OK"))
                            {
                                EditText etIFSC = (EditText)findViewById(R.id.etIFSC);
                                etIFSC.setError("Please provide a valid IFSC code");
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
                            Toast.makeText(BankDetails.this, "No Internet access. Please check your connection.",Toast.LENGTH_LONG).show();
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
        reqIFSC.setShouldCache(false);
        rQueue.getInstance(this).queueRequest("",reqIFSC);
        return valid;
    }

    private int getBankPos(String bankName)
    {
        int pos = 0;
        for(int i=0;i<listBanks.size();i++)
        {
            if(listBanks.get(i).getName().equalsIgnoreCase(bankName))
            {
                pos = i+1;
                break;
            }
        }
        return pos;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 654 && resultCode == RESULT_OK && data != null) {
            String ifsc = data.getStringExtra("IFSC");
            String bank = data.getStringExtra("Bank");
            int bankPos = data.getIntExtra("bankPos", 0);
            EditText etIFSC = (EditText)findViewById(R.id.etIFSC);
            AutoCompleteTextView actvBank = (AutoCompleteTextView)findViewById(R.id.actvBank);
            actvBank.setListSelection(bankPos);
            actvBank.setText(bank);
            etIFSC.setText(ifsc);
            etIFSC.setError(null);
        }
    }

}
