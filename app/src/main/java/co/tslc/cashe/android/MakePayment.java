package co.tslc.cashe.android;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import fr.ganfra.materialspinner.MaterialSpinner;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class MakePayment extends Base {

    protected CASHe app;
    private Tracker mTracker;

    private String name;
    private String email;
    private String custID;
    private String loanAmount;
    private String totalDueAmount;
    private String penaltyAmount;
    private String processingFee;
    private String chqDate;

    private static String accessToken = "";
    private static String refreshToken = "" ;
    private static String tokenType = "";

    private static final String urlBanks = CASHe.webApiUrl + "api/cashe/customer/getBanksName";
    private static final String urlDepSlip = CASHe.webApiUrl + "api/cashe/customer/upload/depositSlip";
    private static final String urlPay = CASHe.webApiUrl + "api/cashe/customer/payment";

    private TextView dateView;
    private DatePicker datePicker;
    private Calendar calendar;
    private int year, month, day;

    private String mCurrentSlip;
    private Bitmap mImageBitmapSlip;

    ProgressDialog pd;

    private int REQUEST_SLIP = 901;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_make_payment);
        LayoutInflater inflater = (LayoutInflater)this.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.activity_make_payment, frameLayout, true);
        typeFace(frameLayout);
        try {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }catch(Exception e){}
        //getLayoutInflater().inflate(R.layout.activity_make_payment, frameLayout, true);
        //typeFace(frameLayout);

        app = (CASHe)getApplication();
        mTracker = app.getDefaultTracker();
        mTracker.setScreenName("MakePayment");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        Intent i = getIntent();
        name = i.getStringExtra("name");
        email = i.getStringExtra("email");
        custID = i.getStringExtra("custID");
        accessToken = i.getStringExtra("accessToken");
        refreshToken = i.getStringExtra("refreshToken");
        tokenType = i.getStringExtra("tokenType");
        loanAmount = i.getStringExtra("loanAmount");
        totalDueAmount = i.getStringExtra("totalDueAmount");
        penaltyAmount = i.getStringExtra("penaltyAmount");
        processingFee = i.getStringExtra("processingFee");

        dateView = (TextView) findViewById(R.id.etChqDt);
        calendar = Calendar.getInstance();
        /*year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        showDate(year, month + 1, day);*/

        //TextView tvRepay = (TextView)findViewById(R.id.tvRepay);
        //tvRepay.setText(totalDueAmount);
        //TextView tvCasheAmt = (TextView)findViewById(R.id.tvCasheAmt);
        //tvCasheAmt.setText(loanAmount);
        //TextView tvProcFee = (TextView)findViewById(R.id.tvProcFee);
        //tvProcFee.setText(processingFee);

        final MaterialSpinner spnrType = (MaterialSpinner) findViewById(R.id.spnrType);
        spnrType.setEnabled(false);
        spnrType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                /*if (spnrType.getSelectedItem().toString().equalsIgnoreCase("Cheque")) {
                    EditText etChqDt = (EditText) findViewById(R.id.etChqDt);
                    EditText etAmt = (EditText) findViewById(R.id.etAmount);
                    EditText etChqNum = (EditText) findViewById(R.id.etChqNum);
                    TextInputLayout chqNumWrapper = (TextInputLayout) findViewById(R.id.chqNumWrapper);
                    TextInputLayout chqDtWrapper = (TextInputLayout) findViewById(R.id.etChqDtWrapper);
                    chqNumWrapper.setHint(null);
                    chqNumWrapper.setHint("Cheque Number");
                    chqDtWrapper.setHint(null);
                    chqDtWrapper.setHint("Cheque Date");
                    LinearLayout llSlip = (LinearLayout) findViewById(R.id.llSlip);
                    llSlip.setVisibility(View.VISIBLE);
                }*/
                if (spnrType.getSelectedItem().toString().equalsIgnoreCase("NEFT")) {
                    EditText etChqDt = (EditText) findViewById(R.id.etChqDt);
                    EditText etAmt = (EditText) findViewById(R.id.etAmount);
                    EditText etChqNum = (EditText) findViewById(R.id.etChqNum);
                    LinearLayout llSlip = (LinearLayout) findViewById(R.id.llSlip);
                    llSlip.setVisibility(View.GONE);
                    TextInputLayout chqNumWrapper = (TextInputLayout) findViewById(R.id.chqNumWrapper);
                    TextInputLayout chqDtWrapper = (TextInputLayout) findViewById(R.id.etChqDtWrapper);
                    chqNumWrapper.setHint(null);
                    chqNumWrapper.setHint("Transaction Number");
                    chqDtWrapper.setHint(null);
                    chqDtWrapper.setHint("Transfer Date");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ImageView ivAtt = (ImageView)findViewById(R.id.ivAtt);
        ivAtt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/jpeg");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Deposit Slip"), REQUEST_SLIP);
            }
        });

        if(!isFinishing()) pd = ProgressDialog.show(MakePayment.this, "Retrieving list of banks", "Loading...");
        StringRequest reqBanks = new StringRequest(com.android.volley.Request.Method.GET, urlBanks,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonBanks = new JSONObject(response);
                            JSONArray arrBanks = jsonBanks.getJSONArray("entity");
                            List<ListBanks> listBanks = new ArrayList<ListBanks>();
                            for (int i=0; i< arrBanks.length();i++) {
                                String bankName = arrBanks.getJSONObject(i).getString("bankName");
                                String bankID = arrBanks.getJSONObject(i).getString("bankId");
                                listBanks.add(new ListBanks(bankID,bankName));
                            }
                            AutoCompleteTextView actvBank = (AutoCompleteTextView)findViewById(R.id.actvBank);
                            actvBank.setAdapter(new bankSuggest(MakePayment.this, actvBank.getText().toString(), listBanks));
                            dismissDailog();
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
        rQueue.getInstance(this).queueRequest("",reqBanks);

        ImageButton ibDeposit = (ImageButton)findViewById(R.id.ibDeposit);
        ibDeposit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!validForm()) return;
                MaterialSpinner spnrType = (MaterialSpinner) findViewById(R.id.spnrType);
                if(spnrType.getSelectedItem().toString().equalsIgnoreCase("Cheque Date")) {
                    AsyncTask<String, Integer, Void> asyncProfile = new asyncReq().execute("depost_slip.jpg");
                }
                else
                {
                    StringRequest reqCust = new StringRequest(com.android.volley.Request.Method.POST, urlPay,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Log.d("IPB", response);
                                    try {
                                        JSONObject updated = new JSONObject(response);
                                        String statusType = updated.getString("statusType");
                                        if (statusType.equalsIgnoreCase("OK")) {
                                            EditText etAmt = (EditText) findViewById(R.id.etAmount);
                                            String payAmt = etAmt.getText().toString();
                                            Intent i = new Intent(MakePayment.this, ThankYou.class);
                                            i.putExtra("name", name);
                                            i.putExtra("email", email);
                                            i.putExtra("custID", custID);
                                            i.putExtra("accessToken", accessToken);
                                            i.putExtra("refreshToken", refreshToken);
                                            i.putExtra("tokenType", tokenType);
                                            i.putExtra("payAmt", payAmt);
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
                                }
                            }

                    ) {
                        @Override
                        public String getBodyContentType() {
                            return String.format("application/json; charset=utf-8");
                        }

                        @Override
                        public byte[] getBody() throws AuthFailureError {
                            String uInf = "";
                            try {
                                TextView etChqDt = (TextView) findViewById(R.id.etChqDt);
                                Date date = null;
                                try {
                                    date = new SimpleDateFormat("dd-mm-yyyy",  Locale.ENGLISH).parse(etChqDt.getText().toString());
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                String chqDate = new SimpleDateFormat("yyyy-mm-dd",  Locale.ENGLISH).format(date);
                                AutoCompleteTextView actvBank = (AutoCompleteTextView) findViewById(R.id.actvBank);
                                String bank = actvBank.getText().toString();
                                EditText etAmt = (EditText) findViewById(R.id.etAmount);
                                String amt = etAmt.getText().toString();
                                EditText etChqNum = (EditText) findViewById(R.id.etChqNum);
                                String chqNum = etChqNum.getText().toString();
                                uInf = "{\"customerLoanRepaymentId\": 0 ,\"paymentTypeId\": 1, \"bankName\": \"" + bank + "\", \"amount\": " + amt + ",\"paymentDate\": \"" + chqDate + "\",\"utrNumber\": \"" + chqNum + "\"}";
                                return uInf == null ? null : uInf.getBytes("utf-8");
                            } catch (UnsupportedEncodingException uee) {
                                VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                                        uInf, "utf-8");
                                return null;
                            }
                        }

                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            HashMap<String, String> headers = new HashMap<String, String>();
                            headers.put("Authorization", tokenType + " " + accessToken);
                            return headers;
                        }
                    };
                    reqCust.setShouldCache(false);
                    rQueue.getInstance(MakePayment.this).queueRequest("",reqCust);
                }

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
        EditText etChqDt = (EditText)findViewById(R.id.etChqDt);
        EditText etAmt = (EditText)findViewById(R.id.etAmount);
        EditText etChqNum = (EditText)findViewById(R.id.etChqNum);
        TextView tvSlip = (TextView)findViewById(R.id.tvSlip);
        LinearLayout llSlip = (LinearLayout)findViewById(R.id.llSlip);
        if(actvBank.getText().toString().isEmpty()||actvBank.getText().toString()==null||actvBank.getText().toString().length()<5)
        {
            valid = false;
            actvBank.setError("Please provide your bank name.");
        }
        if(etChqDt.getText().toString().isEmpty())
        {
            valid = false;
            etChqDt.setError("Please enter this date.");
        }
        try {
            Date dtChq = new SimpleDateFormat("dd-MM-yyyy",  Locale.ENGLISH).parse(etChqDt.getText().toString());
            Calendar c = Calendar.getInstance();
            if (!dtChq.before(c.getTime())) {
                etChqDt.setError("The date provided is invalid.");
                return false;
            }
            else
                etChqDt.setError(null);

            c = Calendar.getInstance();
            c.add(Calendar.DATE, -90);
            MaterialSpinner spnrType = (MaterialSpinner) findViewById(R.id.spnrType);
            if(spnrType.getSelectedItem().toString().equalsIgnoreCase("Cheque")) {
                if (dtChq.before(c.getTime())) {
                    etChqDt.setError("Cheque date is invalid.");
                    return false;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(etChqNum.getText().toString().isEmpty()||etChqNum.getText().toString().length()<6)
        {
            valid = false;
            etChqNum.setError("Please enter a valid number.");
        }
        if(etAmt.getText().toString().isEmpty())
        {
            valid = false;
            etAmt.setError("Please enter a valid amount to repay.");
        }
        if(llSlip.getVisibility() == View.VISIBLE) {
            if (mCurrentSlip == null) {
                valid = false;
                tvSlip.setError("Please upload deposit slip image");
            }
        }
        return valid;
    }


    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);
        //Toast.makeText(getApplicationContext(), "ca", Toast.LENGTH_SHORT)
        //        .show();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this, myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            showDate(arg1, arg2, arg3); //year/month/day
        }
    };

    private void showDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy",  Locale.ENGLISH);
        String strDate = format.format(calendar.getTime());
        dateView.setText(strDate.toString());
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SLIP && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();
            mCurrentSlip = uri.getPath();
            if (mCurrentSlip != null && !mCurrentSlip.toString().isEmpty() && !mCurrentSlip.equals("null")) {
                try {
                    mImageBitmapSlip = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                TextView tvSlip = (TextView) findViewById(R.id.tvSlip);
                tvSlip.setText(uri2Path(uri));
            }
        }
    }


    public void postRequest(String postURL, String fileName, Bitmap bmFile)
    {
        final Handler mHandler = new Handler(Looper.getMainLooper());
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmFile.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        final byte[] bitmapdata = stream.toByteArray();

        //
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .build();


        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("filename", fileName,
                        RequestBody.create(MediaType.parse("image/jpeg"), bitmapdata))
                .build();
        Request request = new Request.Builder()
                .url(postURL)
                .addHeader("Authorization", "bearer " + accessToken)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("MU", e.toString());
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                    }
                });
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                try {
                    JSONObject jsonSlip = new JSONObject(response.body().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                StringRequest reqCust = new StringRequest(com.android.volley.Request.Method.POST, urlPay,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                try {
                                    Log.d("IPB", response);
                                    JSONObject updated = new JSONObject(response);
                                    String statusType = updated.getString("statusType");
                                    if (statusType.equalsIgnoreCase("OK")) {
                                        EditText etAmt = (EditText) findViewById(R.id.etAmount);
                                        String payAmt = etAmt.getText().toString();
                                        Intent i = new Intent(MakePayment.this, ThankYou.class);
                                        i.putExtra("name", name);
                                        i.putExtra("email", email);
                                        i.putExtra("custID", custID);
                                        i.putExtra("accessToken", accessToken);
                                        i.putExtra("refreshToken", refreshToken);
                                        i.putExtra("tokenType", tokenType);
                                        i.putExtra("payAmt", payAmt);
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
                                    Toast.makeText(MakePayment.this, "No Internet access. Please check your connection.", Toast.LENGTH_LONG).show();
                                }
                            }
                        }

                ) {
                    @Override
                    public String getBodyContentType() {
                        return String.format("application/json; charset=utf-8");
                    }

                    @Override
                    public byte[] getBody() throws AuthFailureError {
                        String uInf = "";
                        try {
                            TextView etChqDt = (TextView) findViewById(R.id.etChqDt);
                            Date date = null;
                            try {
                                date = new SimpleDateFormat("dd-mm-yyyy", Locale.ENGLISH).parse(etChqDt.getText().toString());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            String chqDate = new SimpleDateFormat("yyyy-mm-dd",  Locale.ENGLISH).format(date);
                            AutoCompleteTextView actvBank = (AutoCompleteTextView) findViewById(R.id.actvBank);
                            String bank = actvBank.getText().toString();
                            EditText etAmt = (EditText) findViewById(R.id.etAmount);
                            String amt = etAmt.getText().toString();
                            EditText etChqNum = (EditText) findViewById(R.id.etChqNum);
                            String chqNum = etChqNum.getText().toString();
                            uInf = "{\"customerLoanRepaymentId\": 0 ,\"paymentTypeId\": 1, \"bankName\": \"" + bank + "\", \"amount\": " + amt + ",\"paymentDate\": \"" + chqDate + "\",\"utrNumber\": \"" + chqNum + "\"}";
                            return uInf == null ? null : uInf.getBytes("utf-8");
                        } catch (UnsupportedEncodingException uee) {
                            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                                    uInf, "utf-8");
                            return null;
                        }
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("Authorization", tokenType + " " + accessToken);
                        return headers;
                    }
                };
                reqCust.setShouldCache(false);
                rQueue.getInstance(MakePayment.this).queueRequest("",reqCust);
                /*mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        //Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                        //mTextView.setText(message);
                    }
                });*/
            }
        });
    }

    class asyncReq extends AsyncTask<String, Integer, Void> {

        boolean running;
        ProgressDialog progressDialog;

        @Override
        protected Void doInBackground(String... params) {
            if(mCurrentSlip != null && !mCurrentSlip.isEmpty()) postRequest(urlDepSlip, mCurrentSlip, mImageBitmapSlip);
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressDialog.setMessage(String.valueOf(values[0]));
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            running = true;

            if(!isFinishing()) progressDialog = ProgressDialog.show(MakePayment.this,"CASHe","Processing your payment details. Please wait!");

            /*progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    running = false;
                }
            });*/
        }

        @Override
        protected void onPostExecute(Void v)
        {
        }
    }

    public String uri2Path(Uri contentUri) {
        Cursor cursor = null;
        String fileName = "";
        try {
            cursor = getContentResolver().query(contentUri, new String[]{
                    MediaStore.Images.ImageColumns.DISPLAY_NAME
            }, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                fileName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME));
            }
        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }
        return fileName;
    }

    protected boolean isBlocked() {
        if(CASHe.blocked == 2)return true;
        else return false;}
}
