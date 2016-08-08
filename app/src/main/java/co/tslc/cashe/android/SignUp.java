package co.tslc.cashe.android;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.net.UnknownHostException;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.ganfra.materialspinner.MaterialSpinner;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class SignUp extends Base {
    protected CASHe app;
    private Tracker mTracker;

    private int REQUEST_ADDR_PROOF = 901;
    private int REQUEST_SAL_SLIP = 902;
    private int REQUEST_PAN = 903;
    private int REQUEST_BANK_STMT_PROOF = 904;
    private int CLICK_SELFIE = 911;
    private int CLICK_ADDR_PROOF = 912;
    private int CLICK_SAL_SLIP = 913;
    private int CLICK_PAN = 914;
    private int CLICK_BANK_STMT_PROOF = 915;

    private static final int ACTION_TAKE_PHOTO_B = 1;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 6;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_MEMORY = 7;

    private static final String BITMAP_STORAGE_KEY = "viewbitmap";
    private static final String IMAGEVIEW_VISIBILITY_STORAGE_KEY = "imageviewvisibility";
    private ImageView mImageView;


    private JSONObject usrEntity = new JSONObject();

    private static final String TAG_RESULT = "predictions";
    JSONObject json;

    private ArrayList<String> company_names = new ArrayList<String>();
    private ArrayList<String> company_ids = new ArrayList<String>();
    private ArrayAdapter<String> adaptr;

    ProgressDialog pd;

    private Uri uriSelfie;
    private Uri uriAddrProof;
    private Uri uriSalSlip;
    private Uri uriPAN;
    private Uri uriBankStmt;

    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
    private int stepNo = 1;

    private String phaseSignUp = "P0";
    private String payload = "";

    private boolean validIFSC = true;
    private boolean chkstep5 = false;
    private boolean stepnum8 = false;

    private List<ListBanks> listBanks = new ArrayList<ListBanks>();

    private List<String> listBankName = new ArrayList<String>();
    private int bankPosition = 0;

    CircTN ctSelfie;

    private String name;
    private String email;
    private String custID;
    private String custTitle;

    private String mCurrentPhotoPath;
    private String mCurrentFileName;
    private String mCurrentAddrProof;
    private String mCurrentSalSlip;
    private String mCurrentPAN;
    private String mCurrentBankStmt;

    private boolean proceedBtnClicked = false;

    private static final String JPEG_FILE_PREFIX = "cashe_selfie_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";

    private static String accessToken = "";
    private static String refreshToken = "";
    private static String tokenType = "";
    private static String TAG = "Cashe";

    private DatePicker datePicker;
    private Calendar calendar;
    private EditText dvDOB;
    private EditText dvDOJ;
    private int year, month, day;

    private int iCellYears;
    private int iCellMonths;
    private int iHouseYears;
    private int iHouseMonths;

    private static final String urlCust = CASHe.webApiUrl + "api/cashe/customer/update";
    private static final String urlCustInf = CASHe.webApiUrl + "api/cashe/customer/details";
    private static final String urlCustPicURL = CASHe.webApiUrl + "api/cashe/customer/image";
    private static final String urlCustSlip = CASHe.webApiUrl + "api/cashe/customer/upload/payslip";
    private static final String urlCustAddr = CASHe.webApiUrl + "api/cashe/customer/upload/address";
    private static final String urlCustPic = CASHe.webApiUrl + "api/cashe/customer/upload/profile";
    private static final String urlCustPAN = CASHe.webApiUrl + "api/cashe/customer/upload/pancard";
    private static final String urlCustStmt = CASHe.webApiUrl + "api/cashe/customer/upload/bankStatement";
    private static final String urlCompanies = CASHe.webApiUrl + "api/cashe/data/companies/";
    private static final String urlStatic = CASHe.webApiUrl + "api/cashe/data/static";
    private static final String urlBanks = CASHe.webApiUrl + "api/cashe/customer/getBanksName";
    private static final String urlBankDetails = CASHe.webApiUrl + "api/cashe/customer/bankDetailsUpdate";
    private static final String urlIFSC = CASHe.webApiUrl + "api/cashe/customer/bankDetailByIFSC/";
    private static final String urlPAN = CASHe.webApiUrl + "api/cashe/customer/searchPAN/";
    private static final String urlBankByPrefix = CASHe.webApiUrl + "api/cashe/customer/getBanksNameByPrefix/";

    private List<ListCompanies> listCompanies = new ArrayList<ListCompanies>();

    private int companyPosition = -1;

    public ImageButton btnProceed;

    //  ImageButton ibNext;
    private boolean nextButtonClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.activity_sign_up, frameLayout, true);
        // getLayoutInflater().inflate(R.layout.activity_sign_up, frameLayout, true);
        typeFace(frameLayout);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        try {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } catch (Exception e) {
        }
        app = (CASHe) getApplication();
        mTracker = app.getDefaultTracker();
        mTracker.setScreenName("SignUp");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_chevron_left_white_24dp);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setBackgroundDrawable(null);
        getSupportActionBar().setElevation(0);

        Intent i = getIntent();
        name = i.getStringExtra("name");
        email = i.getStringExtra("email");
        custID = i.getStringExtra("custID");
        accessToken = i.getStringExtra("accessToken");
        refreshToken = i.getStringExtra("refreshToken");
        tokenType = i.getStringExtra("tokenType");

        //VideoView vueVid = (VideoView)findViewById(R.id.vid);
        //vueVid.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.congrats));
        //vueVid.start();

        ImageButton ibUp1 = (ImageButton) findViewById(R.id.ibUp1);
        ImageButton ibUp2 = (ImageButton) findViewById(R.id.ibUp2);
        ImageButton ibUp3 = (ImageButton) findViewById(R.id.ibUp3);
        ImageButton ibUp4 = (ImageButton) findViewById(R.id.ibUp4);

        ibUp1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                smartUpload();
            }
        });
        ibUp1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                smartUpload();
            }
        });
        ibUp2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                smartUpload();
            }
        });
        ibUp3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                smartUpload();
            }
        });
        ibUp4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                smartUpload();
            }
        });


        TextView tvFindYE = (TextView) findViewById(R.id.tvFindYE);
        tvFindYE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageButton ibNext = (ImageButton) findViewById(R.id.ibNext);
                ibNext.performClick();
            }
        });


        Button btnFind = (Button) findViewById(R.id.btnFind);
        btnFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AutoCompleteTextView actvBank = (AutoCompleteTextView) findViewById(R.id.actvBank);
                EditText etIFSC = (EditText) findViewById(R.id.etIFSC);
                etIFSC.setError(null);
                Intent i = new Intent(SignUp.this, IFSCSearch.class);
                bankPosition = getBankPos(actvBank.getText().toString());
                i.putExtra("accessToken", accessToken);
                i.putExtra("refreshToken", refreshToken);
                i.putExtra("tokenType", tokenType);
                i.putExtra("bankName", actvBank.getText().toString());
                i.putExtra("bankPosition", bankPosition);
                startActivityForResult(i, 654);
            }
        });

        ImageButton btnProceed = (ImageButton) findViewById(R.id.proceed);
        btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proceedBtnClicked = true;
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                final TableLayout tblStep3new = (TableLayout) findViewById(R.id.tblStep3new);
                tblStep3new.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.down_to_up_full));
                if (ctSelfie != null) {
                    try {
                        ctSelfie.setClickable(false);
                        ImageView selfieInfo = (ImageView) findViewById(R.id.ivSelfieInfo);
                        selfieInfo.setVisibility(View.GONE);
                    } catch (Exception e) {
                    }
                }

                tblStep3new.postDelayed(new Runnable() {
                    public void run() {
                        TextView caption = (TextView) findViewById(R.id.caption);

                        tblStep3new.setVisibility(View.GONE);
                    /*TableLayout tblStep2 = (TableLayout) findViewById(R.id.step2);
                    tblStep2.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.down_to_up_half));
                    tblStep2.setVisibility(View.VISIBLE);
                    stepNo=2;
                    if (!chkstep5) getCustInfo();
                    ImageButton ibNext = (ImageButton) findViewById(R.id.ibNext);
                    ibNext.setImageDrawable(getResources().getDrawable(R.drawable.next_step_1));*/

                        if (phaseSignUp.equalsIgnoreCase("P1")) {

                            EditText etNetSal = (EditText) findViewById(R.id.etNetSal);
                            etNetSal.setEnabled(false);
                            CircTN selfie2 = (CircTN) findViewById(R.id.selfie);
                            selfie2.setVisibility(View.VISIBLE);
                            selfie2.getLayoutParams().height = 350;
                            ImageView arcImg2 = (ImageView) findViewById(R.id.bgArc);
                            arcImg2.setImageDrawable(getResources().getDrawable(R.drawable.bg_arc_subtract));
                            ImageView selfieInfo = (ImageView) findViewById(R.id.ivSelfieInfo);
                            selfieInfo.setVisibility(View.VISIBLE);
                            TableLayout tblStep2 = (TableLayout) findViewById(R.id.step2);
                            tblStep2.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.up_to_down_half));
                            tblStep2.setVisibility(View.VISIBLE);
                            stepNo = 2;
                            ImageButton ibNext = (ImageButton) findViewById(R.id.ibNext);
                            ibNext.setImageDrawable(getResources().getDrawable(R.drawable.next_step_1));
                            ibNext.setVisibility(View.VISIBLE);
                            try {
                                if (usrEntity != null && !usrEntity.isNull("dob")) {
                                    String tmpDate = null;
                                    try {
                                        tmpDate = usrEntity.getString("dob");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    Date date = null;
                                    try {
                                        date = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(tmpDate);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    try {
                                        MaterialSpinner spnrDOBDay = (MaterialSpinner) findViewById(R.id.spnrDOBDay);
                                        MaterialSpinner spnrDOBMonth = (MaterialSpinner) findViewById(R.id.spnrDOBMonth);
                                        MaterialSpinner spnrDOBYear = (MaterialSpinner) findViewById(R.id.spnrDOBYear);
                                        selectSpinnerItemByValue(spnrDOBDay, new SimpleDateFormat("dd", Locale.ENGLISH).format(date));
                                        selectSpinnerItemByValue(spnrDOBMonth, new SimpleDateFormat("MMM", Locale.ENGLISH).format(date));
                                        selectSpinnerItemByValue(spnrDOBYear, new SimpleDateFormat("yyyy", Locale.ENGLISH).format(date));
                                    }catch(Exception e){}
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                        if (phaseSignUp.equalsIgnoreCase("P2")) {
                            /*CircTN selfie2 = (CircTN) findViewById(R.id.selfie);
                            selfie2.setVisibility(View.INVISIBLE);
                            selfie2.getLayoutParams().height = 80;
                            ImageView selfieInfo = (ImageView) findViewById(R.id.ivSelfieInfo);
                            selfieInfo.setVisibility(View.GONE);
                            ImageView arcImg2 = (ImageView) findViewById(R.id.bgArc);

                            caption.setText("SIGN UP");

                            final TableLayout tblStep2 = (TableLayout) findViewById(R.id.step2);
                            tblStep2.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.down_to_up_full));
                            tblStep2.postDelayed(new Runnable() {
                                public void run() {
                                    tblStep2.setVisibility(View.GONE);
                                    TableLayout tblStep3 = (TableLayout) findViewById(R.id.step3);
                                    tblStep3.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.down_to_up_half));
                                    tblStep3.setVisibility(View.VISIBLE);
                                    stepNo = 3;
                                    ImageButton ibNext = (ImageButton) findViewById(R.id.ibNext);
                                    ibNext.setImageDrawable(getResources().getDrawable(R.drawable.next_step_3));
                                    ibNext.setVisibility(View.VISIBLE);
                                }
                            }, 500);*/
                            caption.setText("PERSONAL INFO");
                            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                            TableLayout tblStep5 = (TableLayout) findViewById(R.id.step5);
                            tblStep5.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.down_to_up_half));
                            tblStep5.setVisibility(View.VISIBLE);
                            stepNo = 5;
                            CircTN selfie2 = (CircTN) findViewById(R.id.selfie);
                            selfie2.setVisibility(View.INVISIBLE);
                            selfie2.getLayoutParams().height = 80;
                            ImageView selfieInfo = (ImageView) findViewById(R.id.ivSelfieInfo);
                            selfieInfo.setVisibility(View.GONE);
                            ImageView arcImg2 = (ImageView) findViewById(R.id.bgArc);
                            arcImg2.setImageDrawable(getResources().getDrawable(R.drawable.bg_arc));
                            ImageButton ibNext = (ImageButton) findViewById(R.id.ibNext);
                            ibNext.setImageDrawable(getResources().getDrawable(R.drawable.next_step_1));
                            ibNext.setVisibility(View.VISIBLE);
                            try {
                                if (!usrEntity.isNull("dob")) {
                                    String tmpDate = null;
                                    try {
                                        tmpDate = usrEntity.getString("dob");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    Date date = null;
                                    try {
                                    date = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(tmpDate);
                                    MaterialSpinner spnrDOBDay = (MaterialSpinner) findViewById(R.id.spnrDOBDay);
                                    MaterialSpinner spnrDOBMonth = (MaterialSpinner) findViewById(R.id.spnrDOBMonth);
                                    MaterialSpinner spnrDOBYear = (MaterialSpinner) findViewById(R.id.spnrDOBYear);
                                    selectSpinnerItemByValue(spnrDOBDay, new SimpleDateFormat("dd", Locale.ENGLISH).format(date));
                                    selectSpinnerItemByValue(spnrDOBMonth, new SimpleDateFormat("MMM", Locale.ENGLISH).format(date));
                                    selectSpinnerItemByValue(spnrDOBYear, new SimpleDateFormat("yyyy", Locale.ENGLISH).format(date));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        if (phaseSignUp.equalsIgnoreCase("P3")) {
                            stepnum8 = true;
                            caption.setText("BANK DETAILS");
                            TableLayout tblStep8 = (TableLayout) findViewById(R.id.step8);
                            tblStep8.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.up_to_down_half));
                            tblStep8.setVisibility(View.VISIBLE);
                            stepNo = 8;
                            ImageButton ibNext = (ImageButton) findViewById(R.id.ibNext);
                            ibNext.setImageDrawable(getResources().getDrawable(R.drawable.next_step_5));
                            ibNext.setVisibility(View.VISIBLE);
                        }
                    }
                }, 500);
            }
        });


        CheckBox chkSame = (CheckBox) findViewById(R.id.chkAddrSame);
        chkSame.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                EditText etAddrPerm = (EditText) findViewById(R.id.etAddressPerm);
                EditText etZip = (EditText) findViewById(R.id.etPinCodePerm);

                if (isChecked) {
                    etAddrPerm.setEnabled(false);
                    etZip.setEnabled(false);
                } else {
                    etAddrPerm.setEnabled(true);
                    etZip.setEnabled(true);
                }
            }
        });


        /*if(!isFinishing()&& CASHe.oneRequest) pd = ProgressDialog.show(SignUp.this, "Retrieving list of banks", "Loading...");
        StringRequest reqBanks = new StringRequest(com.android.volley.Request.Method.GET, urlBanks,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            if(null != response){
                                CASHe.oneRequest = false;
                                JSONObject jsonBanks = new JSONObject(response);
                                JSONArray arrBanks = jsonBanks.getJSONArray("entity");
                                for (int i=0; i< arrBanks.length();i++) {
                                    String bankName = arrBanks.getJSONObject(i).getString("bankName");
                                    String bankID = arrBanks.getJSONObject(i).getString("bankId");
                                    listBanks.add(new ListBanks(bankID,bankName));
                                }
                                AutoCompleteTextView actvBank = (AutoCompleteTextView)findViewById(R.id.actvBank);
                                actvBank.setAdapter(new bankSuggest(SignUp.this, actvBank.getText().toString(), listBanks));
                                actvBank.setOnItemSelectedListener(new OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        bankPosition = position;
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }
                                });
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        dismissDailog();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dismissDailog();

                        error.printStackTrace();
                        try {
                            if (error instanceof NoConnectionError) {
                                Toast.makeText(SignUp.this, "No Internet access. Please check your connection.", Toast.LENGTH_LONG).show();
                            }
                        }catch(Exception e){}
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
        rQueue.getInstance(this).queueRequest("", reqBanks);*/


        if (i.hasExtra("phase")) {
            phaseSignUp = i.getStringExtra("phase");
            if (phaseSignUp.equalsIgnoreCase("P1") || phaseSignUp.equalsIgnoreCase("P2") || phaseSignUp.equalsIgnoreCase("P3")) {
                try {

                    getCustInfo();

                    TableLayout tblStep1 = (TableLayout) findViewById(R.id.step1);
                    tblStep1.setVisibility(View.GONE);
                    TableLayout tblStep3new = (TableLayout) findViewById(R.id.tblStep3new);
                    tblStep3new.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.up_to_down_half));
                    TextView caption = (TextView) findViewById(R.id.caption);
                    caption.setText("DOCUMENTS REQUIRED");
                    CircTN selfie2 = (CircTN) findViewById(R.id.selfie);
                    selfie2.setVisibility(View.INVISIBLE);
                    selfie2.getLayoutParams().height = 80;
                    ImageView selfieInfo = (ImageView) findViewById(R.id.ivSelfieInfo);
                    selfieInfo.setVisibility(View.GONE);
                    ImageView arcImg2 = (ImageView) findViewById(R.id.bgArc);
                    arcImg2.setImageDrawable(getResources().getDrawable(R.drawable.bg_arc));
                    tblStep3new.setVisibility(View.VISIBLE);
                    ImageButton ibNext = (ImageButton) findViewById(R.id.ibNext);
                    ibNext.setVisibility(View.GONE);
                    stepNo = 31;

                    ImageRequest request = new ImageRequest(urlCustPicURL + "/" + custID + "/profile",
                            new Response.Listener<Bitmap>() {
                                @Override
                                public void onResponse(Bitmap bitmap) {
                                    try {
                                       Bitmap  mImageBitmapTN = ThumbnailUtils.extractThumbnail(bitmap, 128, 128);
                                        if (mImageBitmapTN != null)
                                            ctSelfie.setImageBitmap(mImageBitmapTN);
                                    } catch (Exception e) {
                                    }
                                }
                            }, 0, 0, null,
                            new Response.ErrorListener() {
                                public void onErrorResponse(VolleyError error) {
                                    try {
                                        ctSelfie.setImageResource(R.drawable.selfie_error);
                                    } catch (Exception e) {
                                    }
                                }
                            }) {
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            HashMap<String, String> headers = new HashMap<String, String>();
                            headers.put("Authorization", tokenType + " " + accessToken);
                            return headers;
                        }
                    };
                    request.setShouldCache(false);
                    rQueue.getInstance(SignUp.this).queueRequest("", request);
                } catch (Exception e) {
                }
            }
        }


        RelativeLayout cntnr = (RelativeLayout) findViewById(R.id.container);
        changeFonts(cntnr);

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        dvDOB = (EditText) findViewById(R.id.etDOB);
        dvDOJ = (EditText) findViewById(R.id.etDOJ);


        ImageButton ibAtt1 = (ImageButton) findViewById(R.id.ibAtt1);
        ibAtt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = LayoutInflater.from(SignUp.this);
                View promptView = layoutInflater.inflate(R.layout.got_it, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SignUp.this);
                alertDialogBuilder
                        .setView(promptView)
                        .setCancelable(false)
                        .setPositiveButton("Got it", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                Intent intent = new Intent();
                                intent.setType("image/jpeg");
                                if (Build.VERSION.SDK_INT < 19)
                                    intent.setAction(Intent.ACTION_GET_CONTENT);
                                else {
                                    intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                                }
                                startActivityForResult(Intent.createChooser(intent, "Select Address Proof"), REQUEST_ADDR_PROOF);
                            }
                        });
                AlertDialog dlgGotIt = alertDialogBuilder.create();
                dlgGotIt.show();
                Button btn = dlgGotIt.getButton(DialogInterface.BUTTON_POSITIVE);
                btn.setTextColor(Color.parseColor("#0097DF"));
            }
        });

        ImageButton ibAtt4 = (ImageButton) findViewById(R.id.ibAtt4);
        ibAtt4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = LayoutInflater.from(SignUp.this);
                View promptView = layoutInflater.inflate(R.layout.got_it, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SignUp.this);
                alertDialogBuilder
                        .setView(promptView)
                        .setCancelable(false)
                        .setPositiveButton("Got it", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                Intent intent = new Intent();
                                intent.setType("image/jpeg");
                                if (Build.VERSION.SDK_INT < 19)
                                    intent.setAction(Intent.ACTION_GET_CONTENT);
                                else {
                                    intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                                }
                                startActivityForResult(Intent.createChooser(intent, "Select Bank Statement"), REQUEST_BANK_STMT_PROOF);
                            }
                        });
                AlertDialog dlgGotIt = alertDialogBuilder.create();
                dlgGotIt.show();
                Button btn = dlgGotIt.getButton(DialogInterface.BUTTON_POSITIVE);
                btn.setTextColor(Color.parseColor("#0097DF"));
            }
        });


        final AutoCompleteTextView actvCompanyStep2 = (AutoCompleteTextView) findViewById(R.id.actvEmpNameStep2);
        actvCompanyStep2.setThreshold(3);
        actvCompanyStep2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                companyPosition = position;
                AutoCompleteTextView actvCompany = (AutoCompleteTextView) findViewById(R.id.actvEmpName);
                actvCompany.setText(actvCompanyStep2.getText().toString());
                actvCompany = (AutoCompleteTextView) findViewById(R.id.actvEmpName2);
                actvCompany.setText(actvCompanyStep2.getText().toString());
            }
        });

        final AutoCompleteTextView actvCompany = (AutoCompleteTextView) findViewById(R.id.actvEmpName);
        actvCompany.setThreshold(3);
        actvCompany.setEnabled(false);
/*        actvCompany.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                companyPosition = position;
                AutoCompleteTextView actvCompany2 = (AutoCompleteTextView) findViewById(R.id.actvEmpName2);
            }
        });*/

        final AutoCompleteTextView actvCompany2 = (AutoCompleteTextView) findViewById(R.id.actvEmpName2);
        actvCompany2.setThreshold(3);
        actvCompany2.setEnabled(false);
/*        actvCompany2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                companyPosition = position;
            }
        });*/

        final AutoCompleteTextView actvBankName = (AutoCompleteTextView) findViewById(R.id.actvBank);
        actvBankName.setThreshold(3);
        actvBankName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                bankPosition = position;

            }
        });

        actvBankName.addTextChangedListener(new TextWatcher() {

            long lastPress = 0l;

            public void afterTextChanged(Editable s) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() >= 3) {
                    if (System.currentTimeMillis() - lastPress > 500) {
                        lastPress = System.currentTimeMillis();
                        //companyPosition = -1;

                        fetchbank(s.toString());
                    }
                }
            }
        });

        actvCompanyStep2.addTextChangedListener(new TextWatcher() {

            long lastPress = 0l;

            public void afterTextChanged(Editable s) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() >= 3) {
                    if (System.currentTimeMillis() - lastPress > 500) {
                        lastPress = System.currentTimeMillis();
                        //companyPosition = -1;

                        fetchCompanies(s.toString(), actvCompanyStep2);
                    }
                }
            }
        });

      /*  actvCompany.addTextChangedListener(new TextWatcher() {

            long lastPress = 0l;

            public void afterTextChanged(Editable s) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() >= 3) {
                    if (System.currentTimeMillis() - lastPress > 500) {
                        lastPress = System.currentTimeMillis();
                        //  companyPosition = -1;
                        fetchCompanies(s.toString(), actvCompany);
                    }
                }
            }
        });*/
/*
        actvCompany2.addTextChangedListener(new TextWatcher() {

            long lastPress = 0l;

            public void afterTextChanged(Editable s) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() >= 3) {
                    if (System.currentTimeMillis() - lastPress > 500) {
                        lastPress = System.currentTimeMillis();
                        //company_names = new ArrayList<String>();
                        //company_ids = new ArrayList<String>();
                        //companyPosition = -1;

                        fetchCompanies(s.toString(), actvCompany2);
                    }
                }
            }
        });*/

        ImageButton ibAtt2 = (ImageButton) findViewById(R.id.ibAtt2);
        ibAtt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = LayoutInflater.from(SignUp.this);
                View promptView = layoutInflater.inflate(R.layout.got_it, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SignUp.this);
                alertDialogBuilder
                        .setView(promptView)
                        .setCancelable(false)
                        .setPositiveButton("Got it", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                Intent intent = new Intent();
                                intent.setType("image/jpeg");
                                if (Build.VERSION.SDK_INT < 19)
                                    intent.setAction(Intent.ACTION_GET_CONTENT);
                                else {
                                    intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                                }
                                startActivityForResult(Intent.createChooser(intent, "Select Salary Slip"), REQUEST_SAL_SLIP);
                            }
                        });
                AlertDialog dlgGotIt = alertDialogBuilder.create();
                dlgGotIt.show();
                Button btn = dlgGotIt.getButton(DialogInterface.BUTTON_POSITIVE);
                btn.setTextColor(Color.parseColor("#0097DF"));
            }
        });

        ImageButton ibAtt3 = (ImageButton) findViewById(R.id.ibAtt3);
        ibAtt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = LayoutInflater.from(SignUp.this);
                View promptView = layoutInflater.inflate(R.layout.got_it, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SignUp.this);
                alertDialogBuilder
                        .setView(promptView)
                        .setCancelable(false)
                        .setPositiveButton("Got it", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                Intent intent = new Intent();
                                intent.setType("image/jpeg");
                                if (Build.VERSION.SDK_INT < 19)
                                    intent.setAction(Intent.ACTION_GET_CONTENT);
                                else {
                                    intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                                }
                                startActivityForResult(Intent.createChooser(intent, "Select PAN Card"), REQUEST_PAN);
                            }
                        });
                AlertDialog dlgGotIt = alertDialogBuilder.create();
                dlgGotIt.show();
                Button btn = dlgGotIt.getButton(DialogInterface.BUTTON_POSITIVE);
                btn.setTextColor(Color.parseColor("#0097DF"));
            }
        });

        ImageView ivSelfie = (ImageView) findViewById(R.id.ivSelfieInfo);
        ivSelfie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SignUp.this, Selfie.class);
                startActivity(i);
            }
        });

        EditText etName = (EditText) findViewById(R.id.etFullName);
        final EditText etEmail = (EditText) findViewById(R.id.etEmail);
        etName.setText(name);
        etEmail.setText(email);

        etEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {

                } else {
                    ImageButton myBtn = (ImageButton) findViewById(R.id.ibNext);
                    myBtn.requestFocus();
                }
            }
        });
        EditText etPAN = (EditText) findViewById(R.id.etPAN);
        etPAN.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {

                } else {
                    ImageButton myBtn = (ImageButton) findViewById(R.id.ibAtt3);
                    myBtn.requestFocus();
                }
            }
        });

        /////TableLayout tbl = (TableLayout)findViewById(R.id.tblSignUpPers);
        TableLayout tbl = (TableLayout) findViewById(R.id.step1);
        tbl.setStretchAllColumns(true);
        tbl = (TableLayout) findViewById(R.id.step2);
        tbl.setStretchAllColumns(true);
        tbl = (TableLayout) findViewById(R.id.step3);
        tbl.setStretchAllColumns(true);
        tbl = (TableLayout) findViewById(R.id.step4);
        tbl.setStretchAllColumns(true);
        tbl = (TableLayout) findViewById(R.id.tblSignUpProf);
        tbl.setStretchAllColumns(true);

        ctSelfie = (CircTN) findViewById(R.id.selfie);
        setSelfieListenerOrDisable(
                ctSelfie,
                mTakePicOnClickListener,
                MediaStore.ACTION_IMAGE_CAPTURE
        );
        /*ctSelfie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                File photo = new File(Environment.getExternalStorageDirectory(), "cashe_selfie.jpg");
                intent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photo));
                uriSelfie = Uri.fromFile(photo);
                startActivityForResult(intent, CLICK_SELFIE);
            }
        });*/

        ImageButton ibCam1 = (ImageButton) findViewById(R.id.ibCam1);
        ibCam1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = LayoutInflater.from(SignUp.this);
                View promptView = layoutInflater.inflate(R.layout.got_it, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SignUp.this);
                alertDialogBuilder
                        .setView(promptView)
                        .setCancelable(false)
                        .setPositiveButton("Got it", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                                File photo = new File(Environment.getExternalStorageDirectory(), "cashe_address_proof.jpg");
                                intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                        Uri.fromFile(photo));
                                uriAddrProof = Uri.fromFile(photo);
                                startActivityForResult(intent, CLICK_ADDR_PROOF);
                            }
                        });
                AlertDialog dlgGotIt = alertDialogBuilder.create();
                dlgGotIt.show();
                Button btn = dlgGotIt.getButton(DialogInterface.BUTTON_POSITIVE);
                btn.setTextColor(Color.parseColor("#0097DF"));
            }
        });

        ImageButton ibCam2 = (ImageButton) findViewById(R.id.ibCam2);
        ibCam2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = LayoutInflater.from(SignUp.this);
                View promptView = layoutInflater.inflate(R.layout.got_it, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SignUp.this);
                alertDialogBuilder
                        .setView(promptView)
                        .setCancelable(false)
                        .setPositiveButton("Got it", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                                File photo = new File(Environment.getExternalStorageDirectory(), "cashe_salary_slip.jpg");
                                intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                        Uri.fromFile(photo));
                                uriSalSlip = Uri.fromFile(photo);
                                startActivityForResult(intent, CLICK_SAL_SLIP);
                            }
                        });
                AlertDialog dlgGotIt = alertDialogBuilder.create();
                dlgGotIt.show();
                Button btn = dlgGotIt.getButton(DialogInterface.BUTTON_POSITIVE);
                btn.setTextColor(Color.parseColor("#0097DF"));
            }
        });

        ImageButton ibCam3 = (ImageButton) findViewById(R.id.ibCam3);
        ibCam3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = LayoutInflater.from(SignUp.this);
                View promptView = layoutInflater.inflate(R.layout.got_it, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SignUp.this);
                alertDialogBuilder
                        .setView(promptView)
                        .setCancelable(false)
                        .setPositiveButton("Got it", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                                File photo = new File(Environment.getExternalStorageDirectory(), "cashe_pan_proof.jpg");
                                intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                        Uri.fromFile(photo));
                                uriPAN = Uri.fromFile(photo);
                                startActivityForResult(intent, CLICK_PAN);
                            }
                        });
                AlertDialog dlgGotIt = alertDialogBuilder.create();
                dlgGotIt.show();
                Button btn = dlgGotIt.getButton(DialogInterface.BUTTON_POSITIVE);
                btn.setTextColor(Color.parseColor("#0097DF"));
            }
        });

        ImageButton ibCam4 = (ImageButton) findViewById(R.id.ibCam4);
        ibCam4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = LayoutInflater.from(SignUp.this);
                View promptView = layoutInflater.inflate(R.layout.got_it, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SignUp.this);
                alertDialogBuilder
                        .setView(promptView)
                        .setCancelable(false)
                        .setPositiveButton("Got it", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                                File photo = new File(Environment.getExternalStorageDirectory(), "cashe_bank_statement_proof.jpg");
                                intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                        Uri.fromFile(photo));
                                uriBankStmt = Uri.fromFile(photo);
                                startActivityForResult(intent, CLICK_BANK_STMT_PROOF);
                            }
                        });
                AlertDialog dlgGotIt = alertDialogBuilder.create();
                dlgGotIt.show();
                Button btn = dlgGotIt.getButton(DialogInterface.BUTTON_POSITIVE);
                btn.setTextColor(Color.parseColor("#0097DF"));
            }
        });

        /*final MaterialSpinner spnrTitle = (MaterialSpinner)findViewById(R.id.spnrTitle);
        //TextView errorText = (TextView)spnrTitle.getSelectedView();
        //errorText.setTextColor(Color.parseColor("#0097DF"));

        spnrTitle.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                custTitle = (String) parent.getItemAtPosition(position);
                //TextView errorText = (TextView)spnrTitle.getSelectedView();
                //errorText.setTextColor(Color.parseColor("#726f5e"));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                custTitle = "";
            }
        });*/

        final EditText etCellSince = (EditText) findViewById(R.id.etCellSince);
        final EditText etHouseSince = (EditText) findViewById(R.id.etHouseSince);

        ImageView ivCell = (ImageView) findViewById(R.id.ivCell);

        ivCell.setOnClickListener(cellSince);
        etCellSince.setOnClickListener(cellSince);

        etHouseSince.setOnClickListener(houseSince);
        ImageView ivHouse = (ImageView) findViewById(R.id.ivHouse);
        ivHouse.setOnClickListener(houseSince);

        ImageButton ibNext = (ImageButton) findViewById(R.id.ibNext);
        ibNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextButtonClicked = true;
                nextButtonPressed();

                /*if(!validFormPersonal()) return;
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                TableLayout tblPersonal = (TableLayout)findViewById(R.id.tblSignUpPers);
                TableLayout tblProfessional = (TableLayout)findViewById(R.id.tblSignUpProf);
                ImageView ivPersonal = (ImageView)findViewById(R.id.ivPersonal);
                ImageView ivProfessional = (ImageView)findViewById(R.id.ivProfessional);
                ImageButton ibNext = (ImageButton) findViewById(R.id.ibNext);
                ibNext.setVisibility(View.GONE);
                ImageButton ibSubmit = (ImageButton)findViewById(R.id.ibSubmit);
                tblPersonal.setVisibility(View.GONE);
                ivPersonal.setVisibility(View.GONE);
                ivProfessional.setVisibility(View.VISIBLE);
                tblProfessional.setVisibility(View.VISIBLE);
                ibSubmit.setVisibility(View.VISIBLE);
                NestedScrollView scrollVue = (NestedScrollView) findViewById(R.id.scrollVue);
                scrollVue.scrollTo(0,0);*/
            }
        });

        if (ContextCompat.checkSelfPermission(SignUp.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(SignUp.this,
                    Manifest.permission.CAMERA)) {
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(SignUp.this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);
            }
        }

        if (ContextCompat.checkSelfPermission(SignUp.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(SignUp.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(SignUp.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_MEMORY);
            }
        }

        //spnrTitle.setFocusable(true);
        //spnrTitle.setFocusableInTouchMode(true);
        //spnrTitle.requestFocus();

        final MaterialSpinner spnrPurpose = (MaterialSpinner) findViewById(R.id.spnrPurpose);
        spnrPurpose.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spnrPurpose.requestFocus();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
        } else {
            mAlbumStorageDirFactory = new BaseAlbumDirFactory();
        }


    }

    Button.OnClickListener mTakePicOnClickListener =
            new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCurrentPhotoPath = null;
                    dispatchTakePictureIntent(ACTION_TAKE_PHOTO_B);
                }
            };


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (item != null && id == android.R.id.home) {

            return backPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean backPressed() {

        if (stepNo == 21) {
            TextView caption = (TextView) findViewById(R.id.caption);
            caption.setText("SIGN UP");
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            final TableLayout tblStep2 = (TableLayout) findViewById(R.id.step2new);
            tblStep2.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.up_to_down_full));
            tblStep2.postDelayed(new Runnable() {
                public void run() {
                    tblStep2.setVisibility(View.GONE);
                    TableLayout tblStep1 = (TableLayout) findViewById(R.id.step1);
                    tblStep1.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.up_to_down_half));
                    tblStep1.setVisibility(View.VISIBLE);
                    stepNo = 1;
                }
            }, 500);
        }

        if (stepNo == 2) {
//            TextView caption =(TextView)findViewById(R.id.caption);
//            caption.setText("SIGN UP");
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
//            final TableLayout tblStep2 = (TableLayout) findViewById(R.id.step2);
//            tblStep2.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.up_to_down_full));
//            tblStep2.postDelayed(new Runnable() {
//                public void run() {
//                    tblStep2.setVisibility(View.GONE);
//                    TableLayout tblStep1 = (TableLayout) findViewById(R.id.step1);
//                    tblStep1.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.up_to_down_half));
//                    tblStep1.setVisibility(View.VISIBLE);
//                    stepNo =1;
//                }
//            }, 500);
        }
        if (stepNo == 3) {
            TextView caption = (TextView) findViewById(R.id.caption);
            caption.setText("SIGN UP");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            final TableLayout tblStep3 = (TableLayout) findViewById(R.id.step3);
            tblStep3.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.up_to_down_full));
            tblStep3.postDelayed(new Runnable() {
                public void run() {
                    tblStep3.setVisibility(View.GONE);
                    TableLayout tblStep2 = (TableLayout) findViewById(R.id.step2);
                    tblStep2.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.up_to_down_half));
                    tblStep2.setVisibility(View.VISIBLE);
                    stepNo = 2;
                }
            }, 500);

        }
        if (stepNo == 4) {
            TextView caption = (TextView) findViewById(R.id.caption);
            caption.setText("SIGN UP");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            final TableLayout tblStep4 = (TableLayout) findViewById(R.id.step4);
            tblStep4.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.up_to_down_full));
            tblStep4.postDelayed(new Runnable() {
                public void run() {
                    tblStep4.setVisibility(View.GONE);
                    TableLayout tblStep3 = (TableLayout) findViewById(R.id.step3);
                    tblStep3.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.up_to_down_half));
                    tblStep3.setVisibility(View.VISIBLE);
                    stepNo = 3;
                }
            }, 500);

        }
        if (stepNo == 0) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }

        if (stepNo == 5) {
//        TextView caption =(TextView)findViewById(R.id.caption);
//        caption.setText("SIGN UP");
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
//        final TableLayout tblStep5 = (TableLayout) findViewById(R.id.step5);
//        tblStep5.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.up_to_down_full));
//        tblStep5.postDelayed(new Runnable() {
//            public void run() {
//                tblStep5.setVisibility(View.GONE);
//                TableLayout tblStep4 = (TableLayout) findViewById(R.id.step4);
//                tblStep4.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.up_to_down_half));
//                tblStep4.setVisibility(View.VISIBLE);
//                stepNo =4;
//            }
//        }, 500);

        }
        if (stepNo == 6) {
            TextView caption = (TextView) findViewById(R.id.caption);
            caption.setText("PERSONAL INFO");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            final TableLayout tblStep6 = (TableLayout) findViewById(R.id.step6);
            tblStep6.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.up_to_down_full));
            tblStep6.postDelayed(new Runnable() {
                public void run() {
                    tblStep6.setVisibility(View.GONE);
                    TableLayout tblStep5 = (TableLayout) findViewById(R.id.step5);
                    tblStep5.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.up_to_down_half));
                    tblStep5.setVisibility(View.VISIBLE);
                    stepNo = 5;
                    ImageButton ibNext = (ImageButton) findViewById(R.id.ibNext);
                    ibNext.setImageDrawable(getResources().getDrawable(R.drawable.next_step_1));
                    ibNext.setVisibility(View.VISIBLE);
                }
            }, 500);

        }
        if (stepNo == 7) {
            TextView caption = (TextView) findViewById(R.id.caption);
            caption.setText("PERSONAL DETAILS");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            final TableLayout tblStep7 = (TableLayout) findViewById(R.id.step7);
            tblStep7.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.up_to_down_full));
            tblStep7.postDelayed(new Runnable() {
                public void run() {
                    tblStep7.setVisibility(View.GONE);
                    TableLayout tblStep6 = (TableLayout) findViewById(R.id.step6);
                    tblStep6.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.up_to_down_half));
                    tblStep6.setVisibility(View.VISIBLE);
                    stepNo = 6;
                    ImageButton ibNext = (ImageButton) findViewById(R.id.ibNext);
                    ibNext.setImageDrawable(getResources().getDrawable(R.drawable.next_step_3));
                    ibNext.setVisibility(View.VISIBLE);
                }
            }, 500);

        }

        if (stepNo == 8) {

            if (!stepnum8) {
                TextView caption = (TextView) findViewById(R.id.caption);
                caption.setText("PERSONAL DETAILS");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                final TableLayout tblStep8 = (TableLayout) findViewById(R.id.step8);
                tblStep8.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.up_to_down_full));
                tblStep8.postDelayed(new Runnable() {
                    public void run() {
                        tblStep8.setVisibility(View.GONE);
                        TableLayout tblStep7 = (TableLayout) findViewById(R.id.step7);
                        tblStep7.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.up_to_down_half));
                        tblStep7.setVisibility(View.VISIBLE);
                        stepNo = 7;
                        ImageButton ibNext = (ImageButton) findViewById(R.id.ibNext);
                        ibNext.setImageDrawable(getResources().getDrawable(R.drawable.next_step_4));
                        ibNext.setVisibility(View.VISIBLE);
                    }
                }, 500);
            } else getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }

        if (stepNo == 9) {
            TextView caption = (TextView) findViewById(R.id.caption);
            caption.setText("BANK DETAILS");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            final TableLayout tblStep9 = (TableLayout) findViewById(R.id.step9);
            tblStep9.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.up_to_down_full));
            tblStep9.postDelayed(new Runnable() {
                public void run() {
                    tblStep9.setVisibility(View.GONE);
                    TableLayout tblStep8 = (TableLayout) findViewById(R.id.step8);
                    tblStep8.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.up_to_down_half));
                    tblStep8.setVisibility(View.VISIBLE);
                    stepNo = 8;
                    ImageButton ibNext = (ImageButton) findViewById(R.id.ibNext);
                    ibNext.setImageDrawable(getResources().getDrawable(R.drawable.next_step_5));
                    ibNext.setVisibility(View.VISIBLE);
                }
            }, 500);

        }
        return true;

    }


    /* Photo album for this application */
    private String getAlbumName() {
        return getString(R.string.app_name);
    }


    private File getAlbumDir() {
        File storageDir = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            Log.d("SP", getAlbumName());
            storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());

            if (storageDir != null) {
                if (!storageDir.mkdirs()) {
                    if (!storageDir.exists()) {
                        Log.d("CameraSample", "failed to create directory");
                        return null;
                    }
                }
            }

        } else {
            Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
        }

        return storageDir;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String imageFileName = JPEG_FILE_PREFIX + "_";
        mCurrentFileName = imageFileName + JPEG_FILE_SUFFIX;
        File albumF = getAlbumDir();
        File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
        return imageF;
    }

    private File setUpPhotoFile() throws IOException {

        File f = createImageFile();
        mCurrentPhotoPath = f.getAbsolutePath();

        return f;
    }

    private void setSelfieListenerOrDisable(
            CircTN btn,
            Button.OnClickListener onClickListener,
            String intentName
    ) {
        if (isIntentAvailable(this, intentName)) {
            btn.setOnClickListener(onClickListener);
        } else {
            /*btn.setText(
                    getText(R.string.cannot).toString() + " " + btn.getText());*/
            btn.setVisibility(View.GONE);
            btn.setClickable(false);
        }
    }

    private void setBtnListenerOrDisable(
            ImageButton btn,
            Button.OnClickListener onClickListener,
            String intentName
    ) {
        if (isIntentAvailable(this, intentName)) {
            btn.setOnClickListener(onClickListener);
        } else {
            /*btn.setText(
                    getText(R.string.cannot).toString() + " " + btn.getText());*/
            btn.setVisibility(View.GONE);
            //btn.setClickable(false);
        }
    }

    private void handleBigCameraPhoto() {

        if (mCurrentPhotoPath != null) {
            try {
                setPic();
                galleryAddPic();
                //mCurrentPhotoPath = null;
            } catch (Exception e) {
            }
        }

    }

    private void setPic() {

        int targetW = 400;
        int targetH = 400;

        Bitmap bitmap = decodeSampledBitmapFromPath(mCurrentPhotoPath, targetW, targetH);

        if (bitmap != null) {
        /* Associate the Bitmap to the ImageView */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                ctSelfie.setBackground(null);
            }
            Bitmap mImageBitmapTN = ThumbnailUtils.extractThumbnail(bitmap, 128, 128);
            if (mImageBitmapTN != null) {
                ctSelfie.setScaleType(ImageView.ScaleType.CENTER);
                ctSelfie.setImageBitmap(mImageBitmapTN);
            }

            File f = new File(mCurrentPhotoPath);
            if (f != null) uriSelfie = Uri.fromFile(f);

            // mImageBitmap = bitmap;
        }
    }

    private Bitmap getProfilePicBitmap() {

        int targetW = 400;
        int targetH = 400;
        Bitmap bitmap = null;
        if (mCurrentPhotoPath != null) {
            bitmap = decodeSampledBitmapFromPath(mCurrentPhotoPath, targetW, targetH);
        }
        return bitmap;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(mCurrentPhotoPath);
        if (f != null) {
            Uri contentUri = Uri.fromFile(f);
            mediaScanIntent.setData(contentUri);
            this.sendBroadcast(mediaScanIntent);
        }
    }

    private void dispatchTakePictureIntent(int actionCode) {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        switch (actionCode) {
            case ACTION_TAKE_PHOTO_B:
                File f = null;

                try {
                    f = setUpPhotoFile();
                    mCurrentPhotoPath = f.getAbsolutePath();
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    //takePictureIntent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    takePictureIntent.putExtra("android.intent.extras.CAMERA_FACING", 1);
                } catch (IOException e) {
                    e.printStackTrace();
                    f = null;
                    mCurrentPhotoPath = null;
                }
                break;

            default:
                break;
        } // switch

        startActivityForResult(takePictureIntent, actionCode);
    }

    public static boolean isIntentAvailable(Context context, String action) {
        final PackageManager packageManager = context.getPackageManager();
        final Intent intent = new Intent(action);
        List<ResolveInfo> list =
                packageManager.queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            handleBigCameraPhoto();
        }

        if (requestCode == 1 && resultCode == RESULT_CANCELED) {
            mCurrentFileName = null;
        }

        if (requestCode == 654 && resultCode == RESULT_OK && data != null) {
            String ifsc = data.getStringExtra("IFSC");
            String bank = data.getStringExtra("Bank");
            int bankPos = data.getIntExtra("bankPos", 0);
            EditText etIFSC = (EditText) findViewById(R.id.etIFSC);
            AutoCompleteTextView actvBank = (AutoCompleteTextView) findViewById(R.id.actvBank);
            actvBank.setListSelection(bankPos);
            actvBank.setText(bank);
            etIFSC.setText(ifsc);
            etIFSC.setError(null);
        }
        if (requestCode == CLICK_SELFIE && resultCode == RESULT_OK) {

            Uri imgSelfie = uriSelfie;
            getContentResolver().notifyChange(imgSelfie, null);
            ContentResolver cr = getContentResolver();
            mCurrentPhotoPath = imgSelfie.getPath();
            Bitmap mImageBitmapTN = decodeSampledBitmapFromPath(mCurrentPhotoPath, 128, 128);
            if (mImageBitmapTN != null) {
                ctSelfie.setScaleType(ImageView.ScaleType.CENTER);
                ctSelfie.setImageBitmap(mImageBitmapTN);
            } else {
                ctSelfie = (CircTN) findViewById(R.id.selfie);
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                        R.drawable.selfie_error);
                ctSelfie.setImageBitmap(bitmap);
                setSelfieListenerOrDisable(
                        ctSelfie,
                        mTakePicOnClickListener,
                        MediaStore.ACTION_IMAGE_CAPTURE
                );
            }
        }

        if (requestCode == CLICK_ADDR_PROOF && resultCode == RESULT_OK) {
            if (uriAddrProof != null && !uriAddrProof.toString().isEmpty() && !uriAddrProof.equals("null")) {
                Uri imgAddrProof = uriAddrProof;
                getContentResolver().notifyChange(imgAddrProof, null);
                ContentResolver cr = getContentResolver();
                mCurrentAddrProof = imgAddrProof.getPath();
                TextView tvAPF = (TextView) findViewById(R.id.tvAddrProofFileName);
                if (mCurrentAddrProof != null && !mCurrentAddrProof.toString().isEmpty() && !mCurrentAddrProof.equals("null")) {
                    tvAPF.setText(imgAddrProof.getLastPathSegment());
                } else {
                    tvAPF.setError("Please re-attach your address proof.");
                    tvAPF.requestFocus();
                }
            }
        }
        if (requestCode == CLICK_SAL_SLIP && resultCode == RESULT_OK) {
            if (uriSalSlip != null && !uriSalSlip.toString().isEmpty() && !uriSalSlip.equals("null")) {
                Uri imgSalSlip = uriSalSlip;
                getContentResolver().notifyChange(imgSalSlip, null);
                ContentResolver cr = getContentResolver();
                mCurrentSalSlip = imgSalSlip.getPath();
                TextView etSalSlip = (TextView) findViewById(R.id.etSalSlip);
                if (mCurrentSalSlip != null && !mCurrentSalSlip.toString().isEmpty() && !mCurrentSalSlip.equals("null")) {
                    etSalSlip.setText(imgSalSlip.getLastPathSegment());
                } else {
                    etSalSlip.setError("Please re-attach your salary slip.");
                    etSalSlip.requestFocus();
                }
            }
        }
        if (requestCode == CLICK_PAN && resultCode == RESULT_OK) {
            if (uriPAN != null && !uriPAN.toString().isEmpty() && !uriPAN.equals("null")) {
                Uri imgPAN = uriPAN;
                getContentResolver().notifyChange(imgPAN, null);
                ContentResolver cr = getContentResolver();

                mCurrentPAN = imgPAN.getPath();
                TextView tvPAN = (TextView) findViewById(R.id.tvPAN);
                if (mCurrentPAN != null && !mCurrentPAN.toString().isEmpty() && !mCurrentPAN.equals("null")) {
                    tvPAN.setText(imgPAN.getLastPathSegment());
                } else {
                    tvPAN.setError("Please re-attach your PAN card.");
                    tvPAN.requestFocus();
                }
            }
        }
        if (requestCode == CLICK_BANK_STMT_PROOF && resultCode == RESULT_OK) {
            if (uriBankStmt != null && !uriBankStmt.toString().isEmpty() && !uriBankStmt.equals("null")) {
                Uri imgBankStmt = uriBankStmt;
                getContentResolver().notifyChange(imgBankStmt, null);
                ContentResolver cr = getContentResolver();

                mCurrentBankStmt = imgBankStmt.getPath();
                TextView tvBankStmt = (TextView) findViewById(R.id.tvBankStmt);
                if (mCurrentBankStmt != null && !mCurrentBankStmt.toString().isEmpty() && !mCurrentBankStmt.equals("null")) {
                    tvBankStmt.setText(imgBankStmt.getLastPathSegment());
                } else {
                    tvBankStmt.setError("Please re-attach your bank statement.");
                    tvBankStmt.requestFocus();
                }
            }
        }
        if (requestCode == REQUEST_ADDR_PROOF && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            uriAddrProof = uri;
            mCurrentAddrProof = uri.getPath();
            TextView tvAPF = (TextView) findViewById(R.id.tvAddrProofFileName);
            if (mCurrentAddrProof != null && !mCurrentAddrProof.toString().isEmpty() && !mCurrentAddrProof.equals("null")) {
                tvAPF.setText(uri2Path(uri));
            } else {
                tvAPF.setError("Please re-attach your address proof.");
                tvAPF.requestFocus();
            }
        }
        if (requestCode == REQUEST_SAL_SLIP && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();
            uriSalSlip = uri;
            mCurrentSalSlip = uri.getPath();
            TextView etSS = (TextView) findViewById(R.id.etSalSlip);
            if (mCurrentSalSlip != null && !mCurrentSalSlip.toString().isEmpty() && !mCurrentSalSlip.equals("null")) {
                etSS.setText(uri2Path(uri));
            } else {
                etSS.setError("Please re-attach your salary slip.");
                etSS.requestFocus();
            }
        }
        if (requestCode == REQUEST_PAN && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();
            uriPAN = uri;
            mCurrentPAN = uri.getPath();
            TextView tvPAN = (TextView) findViewById(R.id.tvPAN);
            if (mCurrentPAN != null && !mCurrentPAN.toString().isEmpty() && !mCurrentPAN.equals("null")) {
                tvPAN.setText(uri2Path(uri));
            } else {
                tvPAN.setError("Please re-attach your PAN card.");
                tvPAN.requestFocus();
            }
        }
        if (requestCode == REQUEST_BANK_STMT_PROOF && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();
            uriBankStmt = uri;
            mCurrentBankStmt = uri.getPath();
            TextView tvBankStmt = (TextView) findViewById(R.id.tvBankStmt);
            if (mCurrentBankStmt != null && !mCurrentBankStmt.toString().isEmpty() && !mCurrentBankStmt.equals("null")) {
                tvBankStmt.setText(uri2Path(uri));
            } else {
                tvBankStmt.setError("Please re-attach your bank statement.");
                tvBankStmt.requestFocus();
            }
        }
    }

    // Some lifecycle callbacks so that the image can survive orientation
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        try {
            if (mCurrentPhotoPath != null) {
                outState.putString("mCurrentPhotoPath", mCurrentPhotoPath.toString());
            }
            if (mCurrentFileName != null) {
                outState.putString("mCurrentFileName", mCurrentFileName.toString());
            }
            outState.putStringArrayList("company_ids", company_ids);
            outState.putStringArrayList("company_names", company_names);
            outState.putInt("companyPosition", companyPosition);
            outState.putInt("bankPosition", bankPosition);
            outState.putInt("stepNo", stepNo);
            outState.putString("phaseSignUp", phaseSignUp);


            if (mCurrentAddrProof != null)
                outState.putString("mCurrentAddrProof", mCurrentAddrProof);

            if (mCurrentPAN != null)
                outState.putString("mCurrentPAN", mCurrentPAN);

            if (mCurrentSalSlip != null)
                outState.putString("mCurrentSalSlip", mCurrentSalSlip);

            if (mCurrentBankStmt != null)
                outState.putString("mCurrentBankStmt", mCurrentBankStmt);


            if (usrEntity != null)
                outState.putString("usrEntity", usrEntity.toString());
            if (uriSelfie != null) {
                outState.putString("uriSelfie", uriSelfie.toString());
            }
            if (uriAddrProof != null) {
                outState.putString("uriAddrProof", uriAddrProof.toString());
            }
            if (uriSalSlip != null) {
                outState.putString("uriSalSlip", uriSalSlip.toString());
            }
            if (uriPAN != null) {
                outState.putString("uriPAN", uriPAN.toString());
            }
            if (uriBankStmt != null) {
                outState.putString("uriBankStmt", uriBankStmt.toString());
            }
            outState.putBoolean("proceedBtnClicked", proceedBtnClicked);
            outState.putBoolean("nextButtonClicked", nextButtonClicked);

            MaterialSpinner spnrAddrProof = (MaterialSpinner) findViewById(R.id.spnrAddrProof);
            int strAddrProof = spnrAddrProof.getSelectedItemPosition();
            outState.putInt("strAddrProof", strAddrProof);
        } catch (Exception e) {
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (null != savedInstanceState) {
            try {
                mCurrentPhotoPath = savedInstanceState.getString("mCurrentPhotoPath");
                mCurrentFileName = savedInstanceState.getString("mCurrentFileName");
                company_ids = savedInstanceState.getStringArrayList("company_ids");
                company_names = savedInstanceState.getStringArrayList("company_names");
                stepNo = savedInstanceState.getInt("stepNo");
                phaseSignUp = savedInstanceState.getString("phaseSignUp");
                handleBigCameraPhoto();
            } catch (Exception e) {
            }
            companyPosition = savedInstanceState.getInt("companyPosition");
            bankPosition = savedInstanceState.getInt("bankPosition");
            try {
                String jsonString = savedInstanceState.getString("usrEntity");
                usrEntity = new JSONObject(jsonString);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                try {
                    mCurrentAddrProof = savedInstanceState.getString("mCurrentAddrProof");

                    mCurrentPAN = savedInstanceState.getString("mCurrentPAN");

                    mCurrentSalSlip = savedInstanceState.getString("mCurrentSalSlip");

                    mCurrentBankStmt = savedInstanceState.getString("mCurrentBankStmt");
                } catch (Exception e) {
                }

                if (savedInstanceState.containsKey("uriSelfie")) {
                    uriSelfie = Uri.parse(savedInstanceState.getString("uriSelfie"));
                }
                if (savedInstanceState.containsKey("uriAddrProof")) {
                    uriAddrProof = Uri.parse(savedInstanceState.getString("uriAddrProof"));
                    TextView tvAPF = (TextView) findViewById(R.id.tvAddrProofFileName);
                    tvAPF.setText(uriAddrProof.getLastPathSegment());
                }
                if (savedInstanceState.containsKey("uriSalSlip")) {
                    uriSalSlip = Uri.parse(savedInstanceState.getString("uriSalSlip"));
                    TextView etSalSlip = (TextView) findViewById(R.id.etSalSlip);
                    etSalSlip.setText(uriSalSlip.getLastPathSegment());
                }
                if (savedInstanceState.containsKey("uriPAN")) {
                    uriPAN = Uri.parse(savedInstanceState.getString("uriPAN"));
                    TextView tvPAN = (TextView) findViewById(R.id.tvPAN);
                    tvPAN.setText(uriPAN.getLastPathSegment());
                }
                if (savedInstanceState.containsKey("uriBankStmt")) {
                    uriBankStmt = Uri.parse(savedInstanceState.getString("uriBankStmt"));
                    TextView tvBankStmt = (TextView) findViewById(R.id.tvBankStmt);
                    tvBankStmt.setText(uriBankStmt.getLastPathSegment());
                }


                try {
                    MaterialSpinner spnrAddrProof = (MaterialSpinner) findViewById(R.id.spnrAddrProof);
                    int posit = savedInstanceState.getInt("strAddrProof");
                    spnrAddrProof.setSelection(posit, false);
                } catch (Exception e) {
                }

                if (btnProceed == null)
                    btnProceed = (ImageButton) findViewById(R.id.proceed);
                proceedBtnClicked = savedInstanceState.getBoolean("proceedBtnClicked");
                if (proceedBtnClicked)
                    btnProceed.performClick();


            } catch (Exception e) {
            }
        }
    }


    @SuppressWarnings("deprecation")
    public void setDOB(View view) {
        showDialog(998);
    }

    @SuppressWarnings("deprecation")
    public void setDOJ(View view) {
        showDialog(999);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 998) {
            return new DatePickerDialog(this, lsnrDOB, year, month, day);
        }
        if (id == 999) {
            return new DatePickerDialog(this, lsnrDOJ, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener lsnrDOB = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            showDate(dvDOB, arg1, arg2, arg3); //year/month/day
        }
    };

    private DatePickerDialog.OnDateSetListener lsnrDOJ = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            showDate(dvDOJ, arg1, arg2, arg3); //year/month/day
        }
    };

    private void showDate(EditText dv, int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        try {
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
            String strDate = format.format(calendar.getTime());
            dv.setText(strDate.toString());
        } catch (Exception e) {
        }
    }

    public String postRequest(String postURL, Uri imgURI, String fileName, String accessToken) {
        final Handler mHandler = new Handler(Looper.getMainLooper());
        final String fName = fileName;
        final String docType = "";
        //try {
        //Bitmap bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), imgURI);
        Bitmap bmp = decodeSampledBitmapFromUri(imgURI, 1024, 1024);
        if (bmp == null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (fName.equalsIgnoreCase("address_proof.jpg")) {
                        mCurrentAddrProof = null;
                        TextView tvAddrProof = (TextView) findViewById(R.id.tvAddrProofFileName);
                        tvAddrProof.setError("Please re-attach your address proof.");
                        onBackPressed();
                    } else if (fName.equalsIgnoreCase("pan.jpg")) {
                        mCurrentPAN = null;
                        TextView tvPAN = (TextView) findViewById(R.id.tvPAN);
                        tvPAN.setError("Please re-attach your PAN card.");
                        onBackPressed();
                    } else if (fName.equalsIgnoreCase("salary_slip.jpg")) {
                        mCurrentSalSlip = null;
                        TextView etSalSlip = (TextView) findViewById(R.id.etSalSlip);
                        etSalSlip.setError("Please re-attach your salary proof.");
                    } else if (fName.equalsIgnoreCase("bank_statement.jpg")) {
                        mCurrentBankStmt = null;
                        TextView tvBankStmt = (TextView) findViewById(R.id.tvBankStmt);
                        tvBankStmt.setError("Please re-attach your bank statement.");
                    }
                    ImageButton btn = (ImageButton) findViewById(R.id.ibSubmit);
                    btn.setEnabled(true);

                }
            });
            return null;
        }

     /*   ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        final byte[] bitmapdata = stream.toByteArray();

        OkHttpClient client = new OkHttpClient();
        client.interceptors().add(new Interceptor() {
            @Override
            public com.squareup.okhttp.Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                com.squareup.okhttp.Response response = chain.proceed(request); //try

                int tryCount = 0;
                while (!response.isSuccessful() && tryCount < 1) {
                    Log.d("DU", "Attempt failed - " + tryCount);
                    mTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Action")
                            .setAction("SignUp")
                            .setLabel("RetryUpload")
                            .setValue(tryCount)
                            .build());
                    tryCount++;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() { if(pd!=null && pd.isShowing()) pd.setMessage("Retrying upload...");}});
                    response = chain.proceed(request); //retry
                }
                return response;
            }
        });
        RequestBody requestBody = new MultipartBuilder()
                .type(MultipartBuilder.FORM)
                .addPart(
                        Headers.of("Content-Disposition", "form-data; name=\"file\"; filename=\"" + fileName + "\""),
                        RequestBody.create(MediaType.parse("image/jpeg"), bitmapdata))
                .build();

        final Request request = new Request.Builder()
                .url(postURL)
                .addHeader("Authorization", "bearer " + accessToken)
                .post(requestBody)
                .build();*/
        //
        try {

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 80, stream);
            final byte[] bitmapdata = stream.toByteArray();

            //OkHttpClient client = new OkHttpClient();
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

            //OkHttpClient client = new OkHttpClient();
            okhttp3.Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (UnknownHostException | UnsupportedEncodingException e) {
            Log.e(TAG, "Error: " + e.getLocalizedMessage());
        } catch (Exception e) {
            Log.e(TAG, "Other Error: " + e.getLocalizedMessage());
        }
        return null;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public String postSelfie(String postURL, Bitmap bmp, String accessToken) {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        if (bmp == null) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        mCurrentFileName = null;

                        ctSelfie = (CircTN) findViewById(R.id.selfie);
                        /*Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                                R.drawable.selfie_error);*/
                        ctSelfie.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.drawable.selfie_error, 128, 128));
                        setSelfieListenerOrDisable(
                                ctSelfie,
                                mTakePicOnClickListener,
                                MediaStore.ACTION_IMAGE_CAPTURE
                        );
                        ImageButton btn = (ImageButton) findViewById(R.id.ibSubmit);
                        btn.setEnabled(true);
                    } catch (Exception e) {
                    }
                }
            });
            return null;
        }

        try {
            bmp.compress(Bitmap.CompressFormat.JPEG, 70, stream);
            final byte[] bitmapdata = stream.toByteArray();

            //OkHttpClient client = new OkHttpClient();
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(120, TimeUnit.SECONDS)
                    .writeTimeout(120, TimeUnit.SECONDS)
                    .readTimeout(120, TimeUnit.SECONDS)
                    .build();


            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("filename", mCurrentFileName,
                            RequestBody.create(MediaType.parse("image/jpeg"), bitmapdata))
                    .build();
            Request request = new Request.Builder()
                    .url(postURL)
                    .addHeader("Authorization", "bearer " + accessToken)
                    .post(requestBody)
                    .build();
            try {
                if (bmp != null && !bmp.isRecycled())
                    bmp.recycle();
            } catch (Exception e) {
            }
            //OkHttpClient client = new OkHttpClient();
            okhttp3.Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (UnknownHostException | UnsupportedEncodingException e) {
            Log.e(TAG, "Error: " + e.getLocalizedMessage());
        } catch (Exception e) {
            Log.e(TAG, "Other Error: " + e.getLocalizedMessage());
        }
        return null;
    }


    class asyncReq extends AsyncTask<String, Integer, String> {

        boolean running;

        @Override
        protected String doInBackground(String... params) {
            String response = "";
            /*if(mCurrentFileName != null && !mCurrentFileName.isEmpty()) {
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("SignUp")
                        .setLabel("UploadSelfie")
                        .build());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() { if(pd!=null && pd.isShowing()) pd.setMessage("Uploading selfie...");}});
                postSelfie(urlCustPic, mImageBitmap, accessToken);
            }*/
            if (mCurrentAddrProof != null && !mCurrentAddrProof.isEmpty()) {
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("SignUp")
                        .setLabel("UploadAddressProof")
                        .build());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (pd != null && pd.isShowing())
                            pd.setMessage("Uploading address proof...");
                    }
                });
                response = postRequest(urlCustAddr, uriAddrProof, "address_proof.jpg", accessToken);
            }
            if (mCurrentSalSlip != null && !mCurrentSalSlip.isEmpty()) {
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("SignUp")
                        .setLabel("UploadSalarySlip")
                        .build());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (pd != null && pd.isShowing()) pd.setMessage("Uploading salary slip...");
                    }
                });
                response = postRequest(urlCustSlip, uriSalSlip, "salary_slip.jpg", accessToken);
            }
            if (mCurrentPAN != null && !mCurrentPAN.isEmpty()) {
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("SignUp")
                        .setLabel("UploadPANCard")
                        .build());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (pd != null && pd.isShowing()) pd.setMessage("Uploading PAN card...");
                    }
                });
                response = postRequest(urlCustPAN, uriPAN, "pan.jpg", accessToken);
            }
            return response;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            //if(pd!=null) pd.setMessage(String.valueOf(values[0]));
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            running = true;

            if (!isFinishing())
                pd = ProgressDialog.show(SignUp.this, "CASHe", "Uploading attachments. Please wait!");

            /*progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    running = false;
                }
            });*/
        }

        @Override
        protected void onPostExecute(String v) {

            dismissDailog();
            TextView caption = (TextView) findViewById(R.id.caption);
            caption.setText("PERSONAL INFO");
            final TableLayout tblStep4 = (TableLayout) findViewById(R.id.step4);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            tblStep4.setVisibility(View.GONE);
            TableLayout tblStep5 = (TableLayout) findViewById(R.id.step5);
            tblStep5.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.down_to_up_half));
            tblStep5.setVisibility(View.VISIBLE);
            stepNo = 5;
            CircTN selfie2 = (CircTN) findViewById(R.id.selfie);
            selfie2.setVisibility(View.INVISIBLE);
            selfie2.getLayoutParams().height = 80;
            ImageView selfieInfo = (ImageView) findViewById(R.id.ivSelfieInfo);
            selfieInfo.setVisibility(View.GONE);
            ImageView arcImg2 = (ImageView) findViewById(R.id.bgArc);
            arcImg2.setImageDrawable(getResources().getDrawable(R.drawable.bg_arc));
            ImageButton ibNext = (ImageButton) findViewById(R.id.ibNext);
            ibNext.setImageDrawable(getResources().getDrawable(R.drawable.next_step_1));

            /*if(pd!=null) pd.dismiss();
            Intent i = new Intent(SignUp.this, Verification.class);
            i.putExtra("name", name);
            i.putExtra("email", email);
            i.putExtra("custID", custID);
            i.putExtra("accessToken", accessToken);
            i.putExtra("refreshToken", refreshToken);
            i.putExtra("tokenType", tokenType);
            startActivity(i);
            finish();*/
        }
    }

    public void dismissDailog() {
        try {
            if (pd != null)
                if (pd.isShowing()) {
                    pd.dismiss();
                    pd = null;
                }
        } catch (Exception epd) {
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dismissDailog();
    }


    class asyncReqSelfie extends AsyncTask<Bitmap, Integer, Void> {

        boolean running;

        @Override
        protected Void doInBackground(Bitmap... params) {
            if (mCurrentFileName != null && !mCurrentFileName.isEmpty()) {
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("SignUp")
                        .setLabel("UploadSelfie")
                        .build());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (pd != null && pd.isShowing()) pd.setMessage("Uploading selfie...");
                    }
                });
                postSelfie(urlCustPic, params[0], accessToken);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            //if(pd!=null) pd.setMessage(String.valueOf(values[0]));
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            running = true;

            if (!isFinishing())
                pd = ProgressDialog.show(SignUp.this, "CASHe", "Uploading attachments. Please wait!");

            /*progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    running = false;
                }
            });*/
        }

        @Override
        protected void onPostExecute(Void v) {
            getCustInfo();
            dismissDailog();
            TextView caption = (TextView) findViewById(R.id.caption);
            caption.setText("APPROVAL");
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            final TableLayout tblStep2new = (TableLayout) findViewById(R.id.step2new);
            tblStep2new.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.down_to_up_full));
            tblStep2new.postDelayed(new Runnable() {
                public void run() {

                    ///
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    ///


                    tblStep2new.setVisibility(View.GONE);
                    TableLayout tblStep0 = (TableLayout) findViewById(R.id.step0);

                    tblStep0.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.down_to_up_half));
                    tblStep0.setVisibility(View.VISIBLE);
                    stepNo = 0;
                    ImageButton ibNext = (ImageButton) findViewById(R.id.ibNext);
                    ibNext.setImageDrawable(getResources().getDrawable(R.drawable.next_step_3));
                    ibNext.setVisibility(View.VISIBLE);
                    CircTN selfie2 = (CircTN) findViewById(R.id.selfie);
                    selfie2.setVisibility(View.INVISIBLE);
                    selfie2.getLayoutParams().height = 80;
                    ImageView arcImg2 = (ImageView) findViewById(R.id.bgArc);
                    arcImg2.setImageDrawable(getResources().getDrawable(R.drawable.bg_arc));
                    ImageView selfieInfo = (ImageView) findViewById(R.id.ivSelfieInfo);
                    selfieInfo.setVisibility(View.GONE);
                    TextView tvFindYe = (TextView) findViewById(R.id.tvFindYE);
                    tvFindYe.setVisibility(View.GONE);
                    TextView caption = (TextView) findViewById(R.id.caption);
                    caption.setText("PERSONAL INFO");

                    TextView custName = (TextView) findViewById(R.id.custName);
                    EditText etName = (EditText) findViewById(R.id.etFullName);
                    String fullName = etName.getText().toString().toUpperCase();
                    custName.setText(fullName);

                    mTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Action")
                            .setAction("SignUp")
                            .setLabel("Eligibility")
                            .build());
                    congratsSound();
                }
            }, 500);
            /*if(pd!=null) pd.dismiss();
            Intent i = new Intent(SignUp.this, Verification.class);
            i.putExtra("name", name);
            i.putExtra("email", email);
            i.putExtra("custID", custID);
            i.putExtra("accessToken", accessToken);
            i.putExtra("refreshToken", refreshToken);
            i.putExtra("tokenType", tokenType);
            startActivity(i);
            finish();*/
        }
    }

    private void congratsSound() {
        try {
            MediaPlayer mp = new MediaPlayer();
            mp.reset();
            AssetFileDescriptor afd;
            afd = getAssets().openFd("sounds/congrats.mp3");
            mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            mp.prepare();
            mp.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class asyncReq2 extends AsyncTask<String, Integer, Void> {

        boolean running;

        @Override
        protected Void doInBackground(String... params) {
            if (mCurrentBankStmt != null && !mCurrentBankStmt.isEmpty()) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (pd != null && pd.isShowing())
                            pd.setMessage("Uploading bank statement...");
                    }
                });
                postRequest(urlCustStmt, uriBankStmt, "bank_statement.jpg", accessToken);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            //if(pd!=null) pd.setMessage(String.valueOf(values[0]));
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            running = true;

            if (!isFinishing())
                pd = ProgressDialog.show(SignUp.this, "CASHe", "Uploading bank statement...");

            /*progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    running = false;
                }
            });*/
        }

        @Override
        protected void onPostExecute(Void v) {
            if (pd != null && pd.isShowing()) pd.dismiss();
            Intent i = new Intent(SignUp.this, Dashboard.class);
            i.putExtra("name", name);
            i.putExtra("email", email);
            i.putExtra("custID", custID);
            i.putExtra("accessToken", accessToken);
            i.putExtra("refreshToken", refreshToken);
            i.putExtra("tokenType", tokenType);
            startActivity(i);
            finish();


            /*if(pd!=null) pd.dismiss();
            Intent i = new Intent(SignUp.this, Verification.class);
            i.putExtra("name", name);
            i.putExtra("email", email);
            i.putExtra("custID", custID);
            i.putExtra("accessToken", accessToken);
            i.putExtra("refreshToken", refreshToken);
            i.putExtra("tokenType", tokenType);
            startActivity(i);
            finish();*/
        }
    }


    public boolean validFormPersonal() {
        boolean valid = true;

        EditText etName = (EditText) findViewById(R.id.etFullName);
        EditText etEmail = (EditText) findViewById(R.id.etEmail);
        EditText etCell = (EditText) findViewById(R.id.etCellNumber);
        EditText etAddress = (EditText) findViewById(R.id.etAddress);
        EditText etZip = (EditText) findViewById(R.id.etPinCode);
        EditText etDOB = (EditText) findViewById(R.id.etDOB);
        EditText etLandLine = (EditText) findViewById(R.id.etLandline);
        EditText etCellSince = (EditText) findViewById(R.id.etCellSince);
        EditText etHouseSince = (EditText) findViewById(R.id.etHouseSince);


        if (mCurrentFileName == null || mCurrentFileName.isEmpty()) {
            /*Bitmap bitmap= BitmapFactory.decodeResource(getResources(),
                    R.drawable.selfie_error);*/
            ctSelfie.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.drawable.selfie_error, 128, 128));
            valid = false;
        }

        //MaterialSpinner spnrTitle = (MaterialSpinner)findViewById(R.id.spnrTitle);
        //String strTitle = spnrTitle.getSelectedItem().toString();

        /*if(strTitle.equalsIgnoreCase("Title"))
        {
            //TextView errorText = (TextView)spnrTitle.getSelectedView();
            //errorText.setTextColor(Color.RED);
            spnrTitle.setError("Please choose a title.");
            spnrTitle.setHintColor(Color.RED);
            if(valid) spnrTitle.requestFocus();
            valid = false;
        }*/

        if (etName.getText().toString().isEmpty() || etName.getText().toString().length() < 3) {
            etName.setError("Please provide your name.");
            if (valid) etName.requestFocus();
            valid = false;
        } else if (!etName.getText().toString().matches("^[a-zA-Z,. ]*$")) {
            etName.setError("Provide a valid name.");
            if (valid) etName.requestFocus();
            valid = false;
        } else {
            etName.setError(null);
        }

        EditText etPAN = (EditText) findViewById(R.id.etPAN);
        if (etPAN.getText().toString().isEmpty() || etPAN.getText().toString().length() != 10) {
            etPAN.setError("Provide a valid PAN.");
            if (valid) etPAN.requestFocus();
            valid = false;
        }
        String strPAN = etPAN.getText().toString();
        Pattern pattern = Pattern.compile("[A-Z|a-z]{5}[0-9]{4}[A-Z|a-z]{1}$");

        Matcher matcher = pattern.matcher(strPAN);
        if (!matcher.matches()) {
            etPAN.setError("Provide a valid PAN.");
            if (valid) etPAN.requestFocus();
            valid = false;
        }
        TextView tvPAN = (TextView) findViewById(R.id.tvPAN);
        if (mCurrentPAN == null || mCurrentPAN.isEmpty()) {
            tvPAN.setError("Please attach your PAN card.");
            valid = false;
        } else
            tvPAN.setError(null);

        if (etAddress.getText().toString().isEmpty()) {
            etAddress.setError("Postal address is invalid.");
            if (valid) etAddress.requestFocus();
            valid = false;
        }
        if (etZip.getText().toString().isEmpty()) {
            etZip.setError("Pincode is invalid.");
            if (valid) etZip.requestFocus();
            valid = false;
        }

        if (!etZip.getText().toString().isEmpty()) {
            String strZipStart = etZip.getText().toString().substring(0, 1);
            if (strZipStart.equalsIgnoreCase("0")) {
                etZip.setError("Pincode is invalid.");
                if (valid) etZip.requestFocus();
                valid = false;
            }
        }

        MaterialSpinner spnrAddrProof = (MaterialSpinner) findViewById(R.id.spnrAddrProof);
        String strAddrProof = spnrAddrProof.getSelectedItem().toString();
        if (strAddrProof.equalsIgnoreCase("Address Proof")) {
            spnrAddrProof.setError("Please choose an address proof.");
            if (valid) spnrAddrProof.requestFocus();
            valid = false;
        } else {
            spnrAddrProof.setError(null);
        }

        TextView tvAddrProof = (TextView) findViewById(R.id.tvAddrProofFileName);
        if (mCurrentAddrProof == null || mCurrentAddrProof.isEmpty()) {
            tvAddrProof.setError("Please attach an address proof.");
            valid = false;
        } else
            tvAddrProof.setError(null);

        if (etCell.getText().toString().isEmpty() || !Patterns.PHONE.matcher(etCell.getText().toString()).matches()) {
            etCell.setError("Cell number is invalid");
            if (valid) etCell.requestFocus();
            valid = false;
        }
        if (!etCell.getText().toString().isEmpty()) {
            String cellStart = etCell.getText().toString().substring(0, 1);
            if (cellStart.equalsIgnoreCase("0")) {
                etCell.setError("Provide a valid mobile number.");
                if (valid) etCell.requestFocus();
                valid = false;
            }
            if (!cellStart.equalsIgnoreCase("9") && !cellStart.equalsIgnoreCase("8") && !cellStart.equalsIgnoreCase("7")) {
                etCell.setError("Provide valid mobile number.");
                if (valid) etCell.requestFocus();
                valid = false;
            }
        }
        if (etCellSince.getText().toString().equalsIgnoreCase("")) {
            etCellSince.setError("Please specify ");
            valid = false;
        }

        if (!etLandLine.getText().toString().isEmpty()) {
            String landlineStart = etLandLine.getText().toString().substring(0, 1);
            if (landlineStart.equalsIgnoreCase("0")) {
                etLandLine.setError("Provide valid landline number.");
                if (valid) etLandLine.requestFocus();
                valid = false;
            }
        }

        if (etEmail.getText().toString().isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString()).matches()) {
            etEmail.setError("Email is invalid or empty.");
            if (valid) etEmail.requestFocus();
            valid = false;
        }

        if (etDOB.getText().toString().isEmpty()) {
            etDOB.setError("Birth date is required.");
            if (valid) etDOB.requestFocus();
            valid = false;
        }
        /*else
        {
            etDOB.setError(null);
        }*/
        /*String strDOB = etDOB.getText().toString();
        try {
            TextInputLayout dobWrapper = (TextInputLayout) findViewById(R.id.dobWrapper);
            Date dtDOB = new SimpleDateFormat("dd-MM-yyyy").parse(strDOB);
            Calendar c = Calendar.getInstance();
            c.add(Calendar.YEAR, -18);
            if (!dtDOB.before(c.getTime())) {
                etDOB.setError("Birth date is invalid.");
                if(valid) etDOB.requestFocus();
                valid = false;
            }
            c = Calendar.getInstance();
            c.add(Calendar.YEAR, -55);
            if(dtDOB.before(c.getTime()))
            {
               etDOB.setError("Birth date is invalid.");
                if(valid) etDOB.requestFocus();
                valid = false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            valid = false;
        }*/

        MaterialSpinner spnrQual = (MaterialSpinner) findViewById(R.id.spnrQual);
        String strQual = spnrQual.getSelectedItem().toString();
        if (strQual.equalsIgnoreCase("Educational Qualification")) {
            spnrQual.setError("Please choose your qualification.");
            if (valid) spnrQual.requestFocus();
            valid = false;
        }

        MaterialSpinner spnrHouse = (MaterialSpinner) findViewById(R.id.spnrHouse);
        String strHouse = spnrHouse.getSelectedItem().toString();
        if (strHouse.equalsIgnoreCase("Accommodation")) {
            spnrHouse.setError("Please choose accommodation type.");
            if (valid) spnrHouse.requestFocus();
            valid = false;
        }

        if (etHouseSince.getText().toString().equalsIgnoreCase("")) {
            etHouseSince.setError("Please specify duration in current house.");
            valid = false;
        }
        if (valid) {
            stepNo++;
        }
        return valid;
    }

    public boolean validStep1() {
        boolean valid = true;

        EditText etName = (EditText) findViewById(R.id.etFullName);
        EditText etEmail = (EditText) findViewById(R.id.etEmail);
        EditText etCell = (EditText) findViewById(R.id.etCellNumber);

        if (mCurrentFileName == null || mCurrentFileName.isEmpty()) {
          /*  Bitmap bitmap= BitmapFactory.decodeResource(getResources(),
                    R.drawable.selfie_error);*/
            ctSelfie.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.drawable.selfie_error, 128, 128));
            valid = false;
        }


        if (etName.getText().toString().trim().isEmpty() || etName.getText().toString().trim().length() < 3) {
            etName.setError("Please provide your name.");
            valid = false;
        } else if (!etName.getText().toString().trim().matches("^[a-zA-Z,. ]*$")) {
            etName.setError("Provide a valid name.");
            valid = false;
        } else {
            etName.setError(null);
        }


        if (etCell.getText().toString().isEmpty() || !Patterns.PHONE.matcher(etCell.getText().toString()).matches()) {
            etCell.setError("Cell number is invalid");
            valid = false;
        }
        if (!etCell.getText().toString().trim().isEmpty()) {
            String cellStart = etCell.getText().toString().substring(0, 1);
            if (cellStart.equalsIgnoreCase("0")) {
                etCell.setError("Provide a valid mobile number.");
                valid = false;
            }
            if (!cellStart.equalsIgnoreCase("9") && !cellStart.equalsIgnoreCase("8") && !cellStart.equalsIgnoreCase("7")) {
                etCell.setError("Provide valid mobile number.");
                valid = false;
            }
        }


        if (etEmail.getText().toString().trim().isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString().trim()).matches()) {
            etEmail.setError("Email is invalid or empty.");
            valid = false;
        }


        return valid;
    }

    public boolean validStep21() {
        boolean valid = true;

        MaterialSpinner spnrDOBDayStep2 = (MaterialSpinner) findViewById(R.id.spnrDOBDayStep2);
        MaterialSpinner spnrDOBMonthStep2 = (MaterialSpinner) findViewById(R.id.spnrDOBMonthStep2);
        MaterialSpinner spnrDOBYearStep2 = (MaterialSpinner) findViewById(R.id.spnrDOBYearStep2);

        AutoCompleteTextView actvCompanyStep2 = (AutoCompleteTextView) findViewById(R.id.actvEmpNameStep2);
        if (actvCompanyStep2.getText().toString().trim().isEmpty()) {
            actvCompanyStep2.setError("Please enter your employer name");
            valid = false;
        }

        if (spnrDOBDayStep2.getSelectedItem().toString().equalsIgnoreCase("Day")) {
            spnrDOBDayStep2.setError("Select a day");
            valid = false;
        }
        if (spnrDOBMonthStep2.getSelectedItem().toString().equalsIgnoreCase("Month")) {
            spnrDOBMonthStep2.setError("Select a month");
            valid = false;
        }
        if (spnrDOBYearStep2.getSelectedItem().toString().equalsIgnoreCase("Year")) {
            spnrDOBMonthStep2.setError("Select a year");
            valid = false;
        }

        String dob = "";
        String strDOB = spnrDOBDayStep2.getSelectedItem().toString() + " " + spnrDOBMonthStep2.getSelectedItem().toString() + " " + spnrDOBYearStep2.getSelectedItem().toString();
        SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM yyyy", Locale.ENGLISH);
        Date tmpDate = new Date();
        try {
            tmpDate = dateFormat.parse(strDOB);
            dob = dateFormat.format(tmpDate);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (dob.isEmpty()) {
            spnrDOBDayStep2.setError("Provide a valid birth date.");
            spnrDOBMonthStep2.setError("Provide a valid birth date.");
            spnrDOBYearStep2.setError("Provide a valid birth date.");
            valid = false;
        }

        TextView etSalStep2 = (TextView) findViewById(R.id.etNetSalStep2);

        if (etSalStep2.getText().toString().trim().isEmpty()) {
            etSalStep2.setError("Please enter your monthly salary");
            valid = false;
        }

        if (!etSalStep2.getText().toString().trim().isEmpty()) {
            if (etSalStep2.getText().toString().substring(0, 1).equalsIgnoreCase("0")) {
                etSalStep2.setError("Please enter your monthly salary.");
                valid = false;
            }

            double netSalStep2 = Double.parseDouble(etSalStep2.getText().toString());
            if (netSalStep2 < 15000) {
                etSalStep2.setError("Your monthly salary needs to be at least \u20B915,000");
                valid = false;
            }
        }


        return valid;
    }

    //***MF New step 3 validation here- image screen
    public boolean validStep31() {
        boolean valid = true;

        return valid;
    }

    public boolean validStep2() {
        boolean valid = true;




        /*if(mCurrentFileName == null || mCurrentFileName.isEmpty())
        {
            Bitmap bitmap= BitmapFactory.decodeResource(getResources(),
                    R.drawable.selfie_error);
            ctSelfie.setImageBitmap(bitmap);
            valid = false;
        }*/


        EditText etPAN = (EditText) findViewById(R.id.etPAN);
        if (etPAN.getText().toString().trim().isEmpty() || etPAN.getText().toString().trim().length() != 10) {
            etPAN.setError("Provide a valid PAN.");
            valid = false;
        }
        String strPAN = etPAN.getText().toString().trim();
        Pattern pattern = Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]{1}");

        Matcher matcher = pattern.matcher(strPAN);
        if (!matcher.matches()) {
            etPAN.setError("Provide a valid PAN.");
            valid = false;
        }
        TextView tvPAN = (TextView) findViewById(R.id.tvPAN);
        if (mCurrentPAN == null || mCurrentPAN.isEmpty()) {
            tvPAN.setError("Please attach your PAN card.");
            valid = false;
        } else
            tvPAN.setError(null);


        return valid;
    }

    public boolean validStep3() {
        boolean valid = true;


        EditText etAddress = (EditText) findViewById(R.id.etAddress);
        EditText etZip = (EditText) findViewById(R.id.etPinCode);

        if (etZip.getText().toString().trim().isEmpty()) {
            etZip.setError("Pincode is invalid.");
            if (valid) etZip.requestFocus();
            valid = false;
        }

        if (!etZip.getText().toString().trim().isEmpty()) {
            String strZipStart = etZip.getText().toString().trim().substring(0, 1);
            if (strZipStart.equalsIgnoreCase("0")) {
                etZip.setError("Pincode is invalid.");
                if (valid) etZip.requestFocus();
                valid = false;
            }
        }

        /*if(mCurrentFileName == null || mCurrentFileName.isEmpty())
        {
            Bitmap bitmap= BitmapFactory.decodeResource(getResources(),
                    R.drawable.selfie_error);
            ctSelfie.setImageBitmap(bitmap);
            valid = false;
        }*/

        if (etAddress.getText().toString().trim().isEmpty()) {
            etAddress.setError("Postal address is invalid.");
            valid = false;
        }


        MaterialSpinner spnrAddrProof = (MaterialSpinner) findViewById(R.id.spnrAddrProof);
        String strAddrProof = spnrAddrProof.getSelectedItem().toString();
        if (strAddrProof.equalsIgnoreCase("Select address proof")) {
            spnrAddrProof.setError("Please choose an address proof.");
            valid = false;
            spnrAddrProof.setFocusable(true);
            spnrAddrProof.setFocusableInTouchMode(true);
            spnrAddrProof.requestFocus();
        } else {
            spnrAddrProof.setError(null);
        }

        TextView tvAddrProof = (TextView) findViewById(R.id.tvAddrProofFileName);
        if (mCurrentAddrProof == null || mCurrentAddrProof.isEmpty()) {
            tvAddrProof.setError("Please attach an address proof.");
            valid = false;
            tvAddrProof.setFocusable(true);
            tvAddrProof.setFocusableInTouchMode(true);
            tvAddrProof.requestFocus();
        } else
            tvAddrProof.setError(null);


        return valid;
    }

    public boolean validStep4() {
        boolean valid = true;

        AutoCompleteTextView empName = (AutoCompleteTextView) findViewById(R.id.actvEmpName);
        if (empName.getText().toString().trim().isEmpty()) {
            empName.setError("Please provide your employer name.");
            valid = false;
        }
        TextView etSal = (TextView) findViewById(R.id.etNetSal);

        if (etSal.getText().toString().trim().isEmpty()) {
            etSal.setError("Please enter your monthly salary");
            valid = false;
        }

        if (!etSal.getText().toString().trim().isEmpty()) {
            if (etSal.getText().toString().trim().substring(0, 1).equalsIgnoreCase("0")) {
                etSal.setError("Please enter your monthly salary.");
                valid = false;
            }

            double netSal = Double.parseDouble(etSal.getText().toString().trim());
            if (netSal < 15000) {
                etSal.setError("Your monthly salary needs to be at least \u20B915,000");
                valid = false;
            }
        }

        TextView etSalSlip = (TextView) findViewById(R.id.etSalSlip);
        if (mCurrentSalSlip == null || mCurrentSalSlip.isEmpty()) {
            etSalSlip.setError("Please attach a salary proof.");
            valid = false;
        } else
            etSalSlip.setError(null);


        return valid;
    }

    public boolean validStep5() {
        boolean valid = true;

        CheckBox chkSame = (CheckBox) findViewById(R.id.chkAddrSame);

        if (!chkSame.isChecked()) {
            EditText etAddrPerm = (EditText) findViewById(R.id.etAddressPerm);
            EditText etZip = (EditText) findViewById(R.id.etPinCodePerm);

            if (etAddrPerm.getText().toString().trim().isEmpty()) {
                etAddrPerm.setError("Postal address is invalid.");
                valid = false;
            }

            if (etZip.getText().toString().trim().isEmpty()) {
                etZip.setError("Pincode is invalid.");
                valid = false;
            }

            if (!etZip.getText().toString().trim().isEmpty()) {
                String strZipStart = etZip.getText().toString().trim().substring(0, 1);
                if (strZipStart.equalsIgnoreCase("0")) {
                    etZip.setError("Pincode is invalid.");
                    valid = false;
                }
            }
        }
        EditText etLandLine = (EditText) findViewById(R.id.etLandline);

        if (etLandLine.getText().toString().trim().isEmpty()) {
            etLandLine.setError("Provide valid landline number.");
            valid = false;
        }

        if (!etLandLine.getText().toString().trim().isEmpty()) {
            String landlineStart = etLandLine.getText().toString().trim().substring(0, 1);
            if (landlineStart.equalsIgnoreCase("0")) {
                etLandLine.setError("Provide valid landline number.");
                valid = false;
            }
        }

        return valid;
    }

    public boolean validStep6() {
        boolean valid = true;

        MaterialSpinner spnrDOBDay = (MaterialSpinner) findViewById(R.id.spnrDOBDay);
        MaterialSpinner spnrDOBMonth = (MaterialSpinner) findViewById(R.id.spnrDOBMonth);
        MaterialSpinner spnrDOBYear = (MaterialSpinner) findViewById(R.id.spnrDOBYear);

        if (spnrDOBDay.getSelectedItem().toString().equalsIgnoreCase("Day")) {
            spnrDOBDay.setError("Select a day");
            valid = false;
        }
        if (spnrDOBMonth.getSelectedItem().toString().equalsIgnoreCase("Month")) {
            spnrDOBMonth.setError("Select a month");
            valid = false;
        }
        if (spnrDOBYear.getSelectedItem().toString().equalsIgnoreCase("Year")) {
            spnrDOBMonth.setError("Select a year");
            valid = false;
        }

        String dob = "";
        String strDOB = spnrDOBDay.getSelectedItem().toString() + " " + spnrDOBMonth.getSelectedItem().toString() + " " + spnrDOBYear.getSelectedItem().toString();
        SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM yyyy", Locale.ENGLISH);
        Date tmpDate = new Date();
        try {
            tmpDate = dateFormat.parse(strDOB);
            dob = dateFormat.format(tmpDate);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (dob.isEmpty()) {
            spnrDOBDay.setError("Provide a valid birth date.");
            spnrDOBMonth.setError("Provide a valid birth date.");
            spnrDOBYear.setError("Provide a valid birth date.");
            valid = false;
        }

        MaterialSpinner spnrQual = (MaterialSpinner) findViewById(R.id.spnrQual);
        String strQual = spnrQual.getSelectedItem().toString();
        if (strQual.equalsIgnoreCase("Select education")) {
            spnrQual.setError("Please choose your qualification.");
            valid = false;
        }

        MaterialSpinner spnrHouse = (MaterialSpinner) findViewById(R.id.spnrHouse);
        String strHouse = spnrHouse.getSelectedItem().toString();
        if (strHouse.equalsIgnoreCase("Select accommodation")) {
            spnrHouse.setError("Select accommodation type.");
            valid = false;
        }

        MaterialSpinner spnrPurpose = (MaterialSpinner) findViewById(R.id.spnrPurpose);
        String strPurpose = spnrPurpose.getSelectedItem().toString();
        if (strPurpose.equalsIgnoreCase("Select purpose")) {
            spnrPurpose.setError("Select purpose of taking CASHe.");
            valid = false;
        }

        return valid;
    }

    public boolean validStep7() {
        boolean valid = true;

       /* AutoCompleteTextView actvCompany = (AutoCompleteTextView) findViewById(R.id.actvEmpName2);
        if(actvCompany.getText().toString().isEmpty())
        {
            actvCompany.setError("Please provide employer name.");
            valid = false;
        }*/

        EditText etEmpLandLine = (EditText) findViewById(R.id.etEmpLandline);

        if (etEmpLandLine.getText().toString().trim().isEmpty()) {
            etEmpLandLine.setError("Provide valid landline number.");
            valid = false;
        }

        if (!etEmpLandLine.getText().toString().trim().isEmpty()) {
            String landlineStart = etEmpLandLine.getText().toString().trim().substring(0, 1);
            if (landlineStart.equalsIgnoreCase("0")) {
                etEmpLandLine.setError("Provide valid landline number.");
                if (valid) etEmpLandLine.requestFocus();
                valid = false;
            }
        }

        EditText etEmail = (EditText) findViewById(R.id.etEmailOffice);
        if (etEmail.getText().toString().trim().isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString().trim()).matches()) {
            etEmail.setError("Email is invalid or empty.");
            if (valid) etEmail.requestFocus();
            valid = false;
        }

        MaterialSpinner spnrWorkSinceMonth = (MaterialSpinner) findViewById(R.id.spnrWorkingSinceMonth);
        MaterialSpinner spnrWorkSinceYear = (MaterialSpinner) findViewById(R.id.spnrWorkingSinceYear);

        String doj = "";
        String strDOJ = "01 " + spnrWorkSinceMonth.getSelectedItem().toString() + " " + spnrWorkSinceYear.getSelectedItem().toString();
        SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM yyyy", Locale.ENGLISH);
        Date tmpDate = new Date();
        try {
            tmpDate = dateFormat.parse(strDOJ);
            dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            doj = dateFormat.format(tmpDate);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (doj.isEmpty()) {
            spnrWorkSinceMonth.setError("Please provide working since.");
            spnrWorkSinceYear.setError("Please provide working since.");
            valid = false;
        }

        return valid;
    }

    public boolean validStep8() {
        boolean valid = true;
        AutoCompleteTextView actvBank = (AutoCompleteTextView) findViewById(R.id.actvBank);
        if (actvBank.getText().toString().trim().isEmpty() || actvBank.getText().toString().trim().length() < 5) {
            actvBank.setError("Please provide your bank");
            valid = false;
        }
        EditText etAccNum = (EditText)findViewById(R.id.etAccNum);
        EditText etConfirmAccNum = (EditText)findViewById(R.id.etConfirmAccNum);
        if(!chkstr(etAccNum.getText().toString().trim()) ||
                etAccNum.getText().toString().trim().isEmpty() ||
                etAccNum.getText().toString().trim().length() < 5)
        {
            etAccNum.setError("Please provide a valid account number");
            valid = false;
        }
        else if(!etConfirmAccNum.getText().toString().equals(etAccNum.getText().toString()))
        {
            etConfirmAccNum.setError("Account Number do not match");
            valid = false;
        }

        EditText etIFSC = (EditText)findViewById(R.id.etIFSC);
        if(etIFSC.getText().toString().trim().isEmpty()|| etIFSC.getText().toString().trim().length() < 11
                || !etIFSC.getText().toString().trim().matches("[A-Z|a-z]{4}[0][A-Z|a-z|\\d]{6}$"))
        {
            etIFSC.setError("Please provide a valid IFSC code");
            valid = false;
        }
        TextView tvBankStmt = (TextView) findViewById(R.id.tvBankStmt);
        if (mCurrentBankStmt == null || mCurrentBankStmt.isEmpty()) {
            tvBankStmt.setError("Please attach your bank statement.");
            valid = false;
        }


        return valid;
    }

    public boolean validStep9() {
        boolean valid = true;
//        EditText etDOB = (EditText)findViewById(R.id.etDOB);
//        if(etDOB.getText().toString().isEmpty())
//        {
//            etDOB.setError("Birth date is required.");
//            valid = false;
//        }
//        /*else
//        {
//            etDOB.setError(null);
//        }*/
//        String strDOB = etDOB.getText().toString();
//        try {
//            TextInputLayout dobWrapper = (TextInputLayout) findViewById(R.id.dobWrapper);
//            Date dtDOB = new SimpleDateFormat("dd-MM-yyyy").parse(strDOB);
//            Calendar c = Calendar.getInstance();
//            c.add(Calendar.YEAR, -18);
//            if (!dtDOB.before(c.getTime())) {
//                etDOB.setError("Birth date is invalid.");
//                valid = false;
//            }
//            c = Calendar.getInstance();
//            c.add(Calendar.YEAR, -55);
//            if(dtDOB.before(c.getTime()))
//            {
//                etDOB.setError("Birth date is invalid.");
//                valid = false;
//            }
//        } catch (ParseException e) {
//            e.printStackTrace();
//            valid = false;
//        }

        MaterialSpinner spnrQual = (MaterialSpinner) findViewById(R.id.spnrQual);
        String strQual = spnrQual.getSelectedItem().toString();
        if (strQual.equalsIgnoreCase("Educational Qualification")) {
            spnrQual.setError("Please choose your qualification.");
            valid = false;
        }

        MaterialSpinner spnrHouse = (MaterialSpinner) findViewById(R.id.spnrHouse);
        String strHouse = spnrHouse.getSelectedItem().toString();
        if (strHouse.equalsIgnoreCase("Accommodation")) {
            spnrHouse.setError("Please choose accommodation type.");
            valid = false;
        }
        if (valid) {
            stepNo++;
        }
        return valid;
    }

    public boolean validFormProfessional() {
        boolean valid = true;

        //Professional
        TextView etDOJ = (TextView) findViewById(R.id.etDOJ);
        TextView etSal = (TextView) findViewById(R.id.etNetSal);
        EditText etEmpLandLine = (EditText) findViewById(R.id.etEmpLandline);

        AutoCompleteTextView empName = (AutoCompleteTextView) findViewById(R.id.actvEmpName);
        if (empName.getText().toString().isEmpty()) {
            empName.setError("Please provide your employer name.");
            if (valid) empName.requestFocus();
            valid = false;
        }

        if (etEmpLandLine.getText().toString().isEmpty()) {
            etEmpLandLine.setError("Please provide your employer's landline number.");
            if (valid) etEmpLandLine.requestFocus();
            valid = false;
        }

        if (!etEmpLandLine.getText().toString().isEmpty()) {
            String landlineStart = etEmpLandLine.getText().toString().substring(0, 1);
            if (landlineStart.equalsIgnoreCase("0")) {
                etEmpLandLine.setError("Please provide your employer's landline number.");
                if (valid) etEmpLandLine.requestFocus();
                valid = false;
            }
        }

        TextInputLayout dojWrapper = (TextInputLayout) findViewById(R.id.dojWrapper);
        if (etDOJ.getText().toString().isEmpty()) {
            etDOJ.setError("Date of joining is required.");
            if (valid) etDOJ.requestFocus();
            valid = false;
        } else
            etDOJ.setError(null);
        try {
            Date dtDOJ = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).parse(etDOJ.getText().toString());
            Calendar c = Calendar.getInstance();
            if (!dtDOJ.before(c.getTime())) {
                etDOJ.setError("Date of joining is invalid.");
                if (valid) etDOJ.requestFocus();
                valid = false;
            } else
                etDOJ.setError(null);

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (etSal.getText().toString().isEmpty()) {
            etSal.setError("Please enter your monthly salary");
            if (valid) etSal.requestFocus();
            valid = false;
        }

        if (!etSal.getText().toString().isEmpty()) {
            if (etSal.getText().toString().substring(0, 1).equalsIgnoreCase("0")) {
                etSal.setError("Please enter your monthly salary.");
                if (valid) etSal.requestFocus();
                valid = false;
            }

            double netSal = Double.parseDouble(etSal.getText().toString());
            if (netSal < 15000) {
                etSal.setError("Your monthly salary needs to be at least \u20B915,000.");
                if (valid) etSal.requestFocus();
                valid = false;
            }
        }

        TextView etSalSlip = (TextView) findViewById(R.id.etSalSlip);
        if (mCurrentSalSlip == null || mCurrentSalSlip.isEmpty()) {
            etSalSlip.setError("Please attach a salary proof.");
            if (valid) etSalSlip.requestFocus();
            valid = false;
        } else
            etSalSlip.setError(null);

        EditText etNetSalSpouse = (EditText) findViewById(R.id.etNetSalSpouse);
        TextInputLayout netSalSpouseWrapper = (TextInputLayout) findViewById(R.id.netSalSpouseWrapper);
        String incomeSpouse = etNetSalSpouse.getText().toString();
        if (!incomeSpouse.isEmpty()) {
            if (incomeSpouse.substring(0, 1).equalsIgnoreCase("0")) {
                etNetSalSpouse.setError("Please provide a valid amount.");
                if (valid) etNetSalSpouse.requestFocus();
                valid = false;
            }
        }
        return valid;
    }

    protected void changeFonts(ViewGroup root) {
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/HUM521N_1.TTF");

        for (int i = 0; i < root.getChildCount(); i++) {
            View v = root.getChildAt(i);
            if (v instanceof TextView) {
                ((TextView) v).setTypeface(tf);
            } else if (v instanceof EditText) {
                ((EditText) v).setTypeface(tf);
            } else if (v instanceof ViewGroup) {
                changeFonts((ViewGroup) v);
            }
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

    public String uri2physPath(Uri uri) {

        String path = null;
        String[] projection = {MediaStore.Files.FileColumns.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);

        if (cursor == null) {
            path = uri.getPath();
        } else {
            cursor.moveToFirst();
            int column_index = cursor.getColumnIndexOrThrow(projection[0]);
            path = cursor.getString(column_index);
            cursor.close();
        }

        return ((path == null || path.isEmpty()) ? (uri.getPath()) : path);
    }

    @Override
    public void onBackPressed() {

        backPressed();

        //super.onBackPressed();
        /*getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        TableLayout tblPersonal = (TableLayout)findViewById(R.id.tblSignUpPers);
        TableLayout tblProfessional = (TableLayout)findViewById(R.id.tblSignUpProf);
        if(tblProfessional.getVisibility()==View.VISIBLE) {
            ImageView ivPersonal = (ImageView) findViewById(R.id.ivPersonal);
            ImageView ivProfessional = (ImageView) findViewById(R.id.ivProfessional);
            ImageButton ibNext = (ImageButton) findViewById(R.id.ibNext);
            ibNext.setVisibility(View.VISIBLE);
            ImageButton ibSubmit = (ImageButton) findViewById(R.id.ibSubmit);
            tblPersonal.setVisibility(View.VISIBLE);
            ivPersonal.setVisibility(View.VISIBLE);
            ivProfessional.setVisibility(View.GONE);
            tblProfessional.setVisibility(View.GONE);
            ibSubmit.setVisibility(View.GONE);
            //ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
            //scrollView.scrollTo(0,0);
        }*/
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_WRITE_MEMORY: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    protected boolean isVerified() {
        return false;
    }

    View.OnClickListener cellSince = new View.OnClickListener() {
        public void onClick(View v) {
            LayoutInflater layoutInflater = LayoutInflater.from(SignUp.this);

            View promptView = layoutInflater.inflate(R.layout.cell_since, null);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SignUp.this);

            // set prompts.xml to be the layout file of the alertdialog builder
            alertDialogBuilder.setView(promptView);

            final MaterialSpinner spnrYearsCell = (MaterialSpinner) promptView.findViewById(R.id.spnrYearsCell);
            final MaterialSpinner spnrMonthsCell = (MaterialSpinner) promptView.findViewById(R.id.spnrMonthsCell);

            // setup a dialog window
            alertDialogBuilder
                    .setCancelable(true)
                            //.setTitle("Cell number in use since")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });

            final AlertDialog alertCellSince = alertDialogBuilder.create();
            alertCellSince.show();
            alertCellSince.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String strYearsCell = spnrYearsCell.getSelectedItem().toString();
                    String strMonthsCell = spnrMonthsCell.getSelectedItem().toString();
                    if (strYearsCell.equalsIgnoreCase("0") && strMonthsCell.equalsIgnoreCase("0")) {
                        TextView errorText = (TextView) spnrYearsCell.getSelectedView();
                        errorText.setError("Please choose mobile usage since.");
                        errorText = (TextView) spnrMonthsCell.getSelectedView();
                        errorText.setError("Please choose mobile usage since.");
                    } else if (strYearsCell.equalsIgnoreCase("Years")) {
                        TextView errorText = (TextView) spnrYearsCell.getSelectedView();
                        errorText.setError("Please choose mobile usage since.");
                    } else if (strMonthsCell.equalsIgnoreCase("Months")) {
                        TextView errorText = (TextView) spnrMonthsCell.getSelectedView();
                        errorText.setError("Please choose mobile usage since.");
                    } else {
                        String cellYears = spnrYearsCell.getSelectedItem().toString();
                        String cellMonths = spnrMonthsCell.getSelectedItem().toString();
                        EditText etCellSince = (EditText) findViewById(R.id.etCellSince);
                        etCellSince.setText(cellYears + " year(s) and " + cellMonths + " month(s)");
                        if (cellYears.equalsIgnoreCase(">50")) cellYears = "51";
                        iCellYears = Integer.parseInt(cellYears);
                        iCellMonths = Integer.parseInt(cellMonths);
                        alertCellSince.dismiss();
                    }
                }
            });
        }
    };

    View.OnClickListener houseSince = new View.OnClickListener() {
        public void onClick(View v) {
            LayoutInflater layoutInflater = LayoutInflater.from(SignUp.this);

            View promptView = layoutInflater.inflate(R.layout.house_since, null);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SignUp.this);

            // set prompts.xml to be the layout file of the alertdialog builder
            alertDialogBuilder.setView(promptView);

            final MaterialSpinner spnrYearsHouse = (MaterialSpinner) promptView.findViewById(R.id.spnrYearsHouse);
            final MaterialSpinner spnrMonthsHouse = (MaterialSpinner) promptView.findViewById(R.id.spnrMonthsHouse);

            // setup a dialog window
            alertDialogBuilder
                    .setCancelable(true)
                            //.setTitle("Living in current house since")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });

            final AlertDialog alertHouseSince = alertDialogBuilder.create();
            alertHouseSince.show();
            alertHouseSince.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String strYearsHouse = spnrYearsHouse.getSelectedItem().toString();
                    String strMonthsHouse = spnrMonthsHouse.getSelectedItem().toString();
                    if (strYearsHouse.equalsIgnoreCase("0") && strMonthsHouse.equalsIgnoreCase("0")) {
                        TextView errorText = (TextView) spnrYearsHouse.getSelectedView();
                        errorText.setError("Please choose house since.");
                        errorText = (TextView) spnrMonthsHouse.getSelectedView();
                        errorText.setError("Please choose house since.");
                    } else if (strYearsHouse.equalsIgnoreCase("Years")) {
                        TextView errorText = (TextView) spnrYearsHouse.getSelectedView();
                        errorText.setError("Please choose house since.");
                    } else if (strMonthsHouse.equalsIgnoreCase("Months")) {
                        TextView errorText = (TextView) spnrMonthsHouse.getSelectedView();
                        errorText.setError("Please choose house since.");
                    } else {
                        String houseYears = spnrYearsHouse.getSelectedItem().toString();
                        String houseMonths = spnrMonthsHouse.getSelectedItem().toString();
                        EditText etHouseSince = (EditText) findViewById(R.id.etHouseSince);
                        etHouseSince.setText(houseYears + " year(s) and " + houseMonths + " month(s)");
                        if (houseYears.equalsIgnoreCase(">50")) houseYears = "51";
                        iHouseYears = Integer.parseInt(houseYears);
                        iHouseMonths = Integer.parseInt(houseMonths);
                        alertHouseSince.dismiss();
                    }

                }
            });
        }
    };


    private void nextButtonPressed() {

        if (stepNo == 1 && !validStep1()) {
            return;
        } else if (stepNo == 1 && validStep1()) {
            TextView caption = (TextView) findViewById(R.id.caption);
            caption.setText("SIGN UP");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            final TableLayout tblStep1 = (TableLayout) findViewById(R.id.step1);
            tblStep1.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.down_to_up_full));
            tblStep1.postDelayed(new Runnable() {
                public void run() {
                    tblStep1.setVisibility(View.GONE);
                    TableLayout tblStep2new = (TableLayout) findViewById(R.id.step2new);
                    tblStep2new.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.down_to_up_half));
                    tblStep2new.setVisibility(View.VISIBLE);
                    stepNo = 21;
                    TextView tvFindYe = (TextView) findViewById(R.id.tvFindYE);
                    tvFindYe.setVisibility(View.VISIBLE);
                    ImageButton ibNext = (ImageButton) findViewById(R.id.ibNext);
                    ibNext.setImageDrawable(getResources().getDrawable(R.drawable.next_step_2));
                    ibNext.setVisibility(View.VISIBLE);
                }
            }, 500);
        } else if (stepNo == 21 && validStep21()) {


            AutoCompleteTextView actvCompanyStep2 = (AutoCompleteTextView) findViewById(R.id.actvEmpNameStep2);
            EditText etNetSalStep2 = (EditText) findViewById(R.id.etNetSalStep2);
            String companyName = actvCompanyStep2.getText().toString();
            verifyCompanies(companyName);
            //step123();

        } else if (stepNo == 31 && !validStep31()) {
            return;
        } else if (stepNo == 31 && validStep31()) {
            TextView caption = (TextView) findViewById(R.id.caption);
            caption.setText("PERSONAL INFO");
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            final TableLayout tblStep3new = (TableLayout) findViewById(R.id.tblStep3new);
            tblStep3new.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.down_to_up_full));
        } else if (stepNo == 2 && !validStep2()) {
            return;
        } else if (stepNo == 2 && validStep2()) {
            TextView caption = (TextView) findViewById(R.id.caption);
            caption.setText("SIGN UP");

            final TableLayout tblStep2 = (TableLayout) findViewById(R.id.step2);

            final EditText etPAN = (EditText) findViewById(R.id.etPAN);
            StringRequest reqPAN = new StringRequest(com.android.volley.Request.Method.GET, urlPAN + etPAN.getText().toString(),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject details = new JSONObject(response);
                                String statusType = details.getString("statusType");

                                if (statusType.equalsIgnoreCase("OK")) {

                                    boolean statusPAN = details.getBoolean("entity");
                                    if (statusPAN) etPAN.setError("PAN card is already registered");
                                    else {
                                        tblStep2.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.down_to_up_full));
                                        tblStep2.postDelayed(new Runnable() {
                                            public void run() {
                                                tblStep2.setVisibility(View.GONE);
                                                TableLayout tblStep3 = (TableLayout) findViewById(R.id.step3);
                                                tblStep3.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.down_to_up_half));
                                                tblStep3.setVisibility(View.VISIBLE);
                                                stepNo = 3;
                                                ImageButton ibNext = (ImageButton) findViewById(R.id.ibNext);
                                                ibNext.setImageDrawable(getResources().getDrawable(R.drawable.next_step_3));
                                            }
                                        }, 500);
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
                            if (error instanceof NoConnectionError) {
                                Toast.makeText(SignUp.this, "No Internet access. Please check your connection.", Toast.LENGTH_LONG).show();
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
            reqPAN.setShouldCache(false);
            rQueue.getInstance(this).queueRequest("", reqPAN);

        } else if (stepNo == 3 && !validStep3()) {
            return;
        } else if (stepNo == 3 && validStep3()) {
            TextView caption = (TextView) findViewById(R.id.caption);
            caption.setText("SIGN UP");
            final TableLayout tblStep3 = (TableLayout) findViewById(R.id.step3);
            tblStep3.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.down_to_up_full));
            tblStep3.postDelayed(new Runnable() {
                public void run() {
                    tblStep3.setVisibility(View.GONE);
                    TableLayout tblStep4 = (TableLayout) findViewById(R.id.step4);
                    tblStep4.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.down_to_up_half));
                    tblStep4.setVisibility(View.VISIBLE);
                    stepNo = 4;
                    ImageButton ibNext = (ImageButton) findViewById(R.id.ibNext);
                    ibNext.setImageDrawable(getResources().getDrawable(R.drawable.next_step_4));
                }
            }, 500);
        } else if (stepNo == 4 && !validStep4()) {
            return;
        } else if (stepNo == 4 && validStep4()) {

            TextView caption = (TextView) findViewById(R.id.caption);
            caption.setText("APPROVAL");
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            final TableLayout tblStep4 = (TableLayout) findViewById(R.id.step4);
            tblStep4.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.down_to_up_full));
            CircTN selfie2 = (CircTN) findViewById(R.id.selfie);
            selfie2.setVisibility(View.INVISIBLE);
            selfie2.getLayoutParams().height = 80;
            ImageView arcImg2 = (ImageView) findViewById(R.id.bgArc);
            arcImg2.setImageDrawable(getResources().getDrawable(R.drawable.bg_arc));
            ImageView selfieInfo = (ImageView) findViewById(R.id.ivSelfieInfo);
            selfieInfo.setVisibility(View.GONE);
            tblStep4.postDelayed(new Runnable() {
                public void run() {
                    StringRequest reqCust = new StringRequest(com.android.volley.Request.Method.POST, urlCust,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                    try {
                                        Log.d("IPB", response);
                                        JSONObject updated = new JSONObject(response);
                                        String statusType = updated.getString("statusType");
                                        if (statusType.equalsIgnoreCase("OK")) {

                                            phaseSignUp = "P2";
                                            AsyncTask<String, Integer, String> asyncReq = new asyncReq().execute("address.jpg", "payslip.jpg", "pan.jpg");
                                            getCustInfo();

                                        } else {
                                            tblStep4.setVisibility(View.GONE);
                                            TableLayout tblStep0 = (TableLayout) findViewById(R.id.step0);
                                            tblStep0.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.down_to_up_half));
                                            tblStep0.setVisibility(View.VISIBLE);
                                            stepNo = 0;
                                            ImageButton ibNext = (ImageButton) findViewById(R.id.ibNext);
                                            ibNext.setImageDrawable(getResources().getDrawable(R.drawable.next_step_1));
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
                                    if (error instanceof NoConnectionError) {
                                        Toast.makeText(SignUp.this, "No Internet access. Please check your connection.", Toast.LENGTH_LONG).show();
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

                            //P1 Construct
                            String uInf = "";
                            try {
                                //step 1
                                EditText etName = (EditText) findViewById(R.id.etFullName);
                                EditText etEmail = (EditText) findViewById(R.id.etEmail);
                                EditText etMobile = (EditText) findViewById(R.id.etCellNumber);

                                //step 2
                                EditText etPAN = (EditText) findViewById(R.id.etPAN);

                                //step 3
                                EditText etAddr = (EditText) findViewById(R.id.etAddress);
                                EditText etPinCode = (EditText) findViewById(R.id.etPinCode);
                                MaterialSpinner spnrAddrProof = (MaterialSpinner) findViewById(R.id.spnrAddrProof);
                                AutoCompleteTextView actvCompany = (AutoCompleteTextView) findViewById(R.id.actvEmpName);
                                EditText etNetSal = (EditText) findViewById(R.id.etNetSal);

                                String fullName = etName.getText().toString();
                                String mobile = etMobile.getText().toString();
                                String emailAddr = etEmail.getText().toString();
                                String pan = etPAN.getText().toString();
                                String addr = escapedStr(etAddr.getText().toString());
                                String zip = etPinCode.getText().toString();
                                String strAddrProof = spnrAddrProof.getSelectedItem().toString();
                                String addrProofID = "1";
                                if (strAddrProof.equalsIgnoreCase("Aadhar Card")) addrProofID = "1";
                                if (strAddrProof.equalsIgnoreCase("Utility Bill"))
                                    addrProofID = "2";
                                if (strAddrProof.equalsIgnoreCase("Telephone Bill"))
                                    addrProofID = "3";
                                if (strAddrProof.equalsIgnoreCase("Passport")) addrProofID = "4";
                                if (strAddrProof.equalsIgnoreCase("Voter's ID")) addrProofID = "5";
                                String companyName = actvCompany.getText().toString();
                                String income = etNetSal.getText().toString();

                                if (zip.isEmpty() || zip.equalsIgnoreCase("")) zip = "000000";

                                String custName = fullName;
                                String custCell = "";
                                String persEmail = "";
                                String custDOB = "";
                                //String income = "";
                                String empId = null;

                                try {
                                    usrEntity.put("pan", pan);
                                    usrEntity.put("currentAddress", addr);
                                    usrEntity.put("postcode", zip);
                                } catch (Exception e) {

                                }
                                try {
                                    if (!usrEntity.isNull("customerName"))
                                        custName = usrEntity.getString("customerName");
                                    if (!usrEntity.isNull("mobNumber"))
                                        custCell = usrEntity.getString("mobNumber");
                                    if (!usrEntity.isNull("personalEmailId"))
                                        persEmail = usrEntity.getString("personalEmailId");
                                    if (!usrEntity.isNull("monthlyIncome"))
                                        income = usrEntity.getString("monthlyIncome");
                                    if (!usrEntity.isNull("dob"))
                                        custDOB = usrEntity.getString("dob");
                                    if (!usrEntity.isNull("companyId"))
                                        empId = usrEntity.getString("companyId");
                                    if (!usrEntity.isNull("companyName"))
                                        companyName = usrEntity.getString("companyName");
                                    //if(!income.equalsIgnoreCase(income1)) income = income1;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                //P1
                                uInf = "{\"customerId\":" + custID + ",\"customerName\":\"" + custName + "\", \"title\": \"\",  " +
                                        "\"currentAddress\":\"" + addr + "\", \"permanentAddress\":\"\",  \"locality\":null,   \"city\":\"\",   " +
                                        "\"state\":\"\",\"postcode\":\"" + zip + "\",\"mobNumber\":\"" + custCell + "\"," +
                                        "\"mobileUsageSince\":\"\",\"landlineNumber\":\"\"," +
                                        "\"email\":\"\",\"personalEmailId\":\"" + persEmail + "\",\"dob\":\"" + custDOB + "\",\"educationTypeId\":null," +
                                        "\"houseTypeId\":null,\"currentHouseSince\":\"\"," +
                                        "\"monthlyIncome\":" + income + ",\"companyId\":" + empId + ",\"companyName\":\"" + companyName + "\",\"companyLandlineNo\": \"\",\"designationId\":1,\"doj\":\"\"," +
                                        "\"pan\":\"" + pan + "\",\"spouseSalary\":0,\"purposeOfCashe\":\"\",\"addressProofId\":" + addrProofID + ", \"wsCallType\": \"P1\", \"permanentAddressPostCode\":null,\"isPermanetAddressSameAsCurrent\":false }";
                                Log.d("P1", uInf);
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
                    rQueue.getInstance(SignUp.this).queueRequest("", reqCust);
                    //code here end
                }
            }, 500);
        } else if (stepNo == 0) {
            handleBigCameraPhoto();
            TextView caption = (TextView) findViewById(R.id.caption);
            caption.setText("DOCUMENTS REQUIRED");
            final TableLayout tblStep0 = (TableLayout) findViewById(R.id.step0);
            tblStep0.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.down_to_up_full));
            tblStep0.postDelayed(new Runnable() {
                public void run() {
                    tblStep0.setVisibility(View.GONE);
                    TableLayout tblStep3new = (TableLayout) findViewById(R.id.tblStep3new);
                    tblStep3new.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.down_to_up_half));
                    tblStep3new.setVisibility(View.VISIBLE);
                    stepNo = 31;
                    ImageButton ibNext = (ImageButton) findViewById(R.id.ibNext);
                    ibNext.setImageDrawable(getResources().getDrawable(R.drawable.btn_proceed));
                    ibNext.setVisibility(View.GONE);
                    TextView btnLearnUpload = (TextView) findViewById(R.id.btnLearnUpload);
                    btnLearnUpload.setPaintFlags(btnLearnUpload.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                }
            }, 500);
        }

        if (stepNo == 5 && !validStep5()) {
            return;
        } else if (stepNo == 5 && validStep5()) {


            TextView caption = (TextView) findViewById(R.id.caption);
            caption.setText("PERSONAL INFO");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            final TableLayout tblStep5 = (TableLayout) findViewById(R.id.step5);
            tblStep5.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.down_to_up_full));
            tblStep5.postDelayed(new Runnable() {
                public void run() {
                    tblStep5.setVisibility(View.GONE);
                    TableLayout tblStep6 = (TableLayout) findViewById(R.id.step6);
                    tblStep6.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.down_to_up_half));
                    tblStep6.setVisibility(View.VISIBLE);
                    stepNo = 6;
                    ImageButton ibNext = (ImageButton) findViewById(R.id.ibNext);
                    ibNext.setImageDrawable(getResources().getDrawable(R.drawable.next_step_3));
                    ibNext.setVisibility(View.VISIBLE);
                }
            }, 500);
        } else if (stepNo == 6 && !validStep6()) {
            return;
        } else if (stepNo == 6 && validStep6()) {
            TextView caption = (TextView) findViewById(R.id.caption);
            caption.setText("PERSONAL INFO");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            final TableLayout tblStep6 = (TableLayout) findViewById(R.id.step6);
            tblStep6.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.down_to_up_full));
            tblStep6.postDelayed(new Runnable() {
                public void run() {
                    tblStep6.setVisibility(View.GONE);
                    TableLayout tblStep7 = (TableLayout) findViewById(R.id.step7);
                    tblStep7.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.down_to_up_half));
                    tblStep7.setVisibility(View.VISIBLE);
                    stepNo = 7;
                    ImageButton ibNext = (ImageButton) findViewById(R.id.ibNext);
                    ibNext.setImageDrawable(getResources().getDrawable(R.drawable.next_step_4));
                    ibNext.setVisibility(View.VISIBLE);
                }
            }, 500);
        } else if (stepNo == 7 && !validStep7()) {
            return;
        } else if (stepNo == 7 && validStep7()) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            final TableLayout tblStep7 = (TableLayout) findViewById(R.id.step7);
            tblStep7.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.down_to_up_full));
            tblStep7.postDelayed(new Runnable() {
                public void run() {

                    mTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Action")
                            .setAction("SignUp")
                            .setLabel("BankDetails")
                            .build());

                    StringRequest reqCust = new StringRequest(com.android.volley.Request.Method.POST, urlCust,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    String statusType = "";
                                    if (pd != null && pd.isShowing()) pd.dismiss();
                                    JSONObject updated = null;
                                    try {
                                        updated = new JSONObject(response);
                                        statusType = updated.getString("statusType");
                                        if (statusType.equalsIgnoreCase("OK")) {
                                            phaseSignUp = "P3";
                                            TableLayout tblStep7 = (TableLayout) findViewById(R.id.step7);
                                            tblStep7.setVisibility(View.GONE);
                                            TableLayout tblStep8 = (TableLayout) findViewById(R.id.step8);
                                            tblStep8.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.down_to_up_half));
                                            tblStep8.setVisibility(View.VISIBLE);
                                            stepNo = 8;
                                            TextView caption = (TextView) findViewById(R.id.caption);
                                            caption.setText("BANK DETAILS");

                                            ImageButton ibNext = (ImageButton) findViewById(R.id.ibNext);
                                            ibNext.setImageDrawable(getResources().getDrawable(R.drawable.next_step_5));
                                            ibNext.setVisibility(View.VISIBLE);
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
                                    if (error instanceof NoConnectionError) {
                                        Toast.makeText(SignUp.this, "No Internet access. Please check your connection.", Toast.LENGTH_LONG).show();
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
                            //P2
                            String uInf = "";
                            try {
                                //step 5
                                EditText etAddrPerm = (EditText) findViewById(R.id.etAddressPerm);
                                EditText etPinCodePerm = (EditText) findViewById(R.id.etPinCodePerm);
                                CheckBox chkSame = (CheckBox) findViewById(R.id.chkAddrSame);
                                EditText etLandline = (EditText) findViewById(R.id.etLandline);

                                //step 6
                                MaterialSpinner spnrDOBDay = (MaterialSpinner) findViewById(R.id.spnrDOBDay);
                                MaterialSpinner spnrDOBMonth = (MaterialSpinner) findViewById(R.id.spnrDOBMonth);
                                MaterialSpinner spnrDOBYear = (MaterialSpinner) findViewById(R.id.spnrDOBYear);
                                MaterialSpinner spnrQual = (MaterialSpinner) findViewById(R.id.spnrQual);
                                MaterialSpinner spnrHouse = (MaterialSpinner) findViewById(R.id.spnrHouse);
                                MaterialSpinner spnrPurpose = (MaterialSpinner) findViewById(R.id.spnrPurpose);

                                //step 7
                                AutoCompleteTextView actvCompany2 = (AutoCompleteTextView) findViewById(R.id.actvEmpName2);
                                EditText etEmpLandline = (EditText) findViewById(R.id.etEmpLandline);
                                EditText etEmailOff = (EditText) findViewById(R.id.etEmailOffice);
                                MaterialSpinner spnrWorkSinceMonth = (MaterialSpinner) findViewById(R.id.spnrWorkingSinceMonth);
                                MaterialSpinner spnrWorkSinceYear = (MaterialSpinner) findViewById(R.id.spnrWorkingSinceYear);

                                String addrSame = "false";
                                String addrPerm = escapedStr(etAddrPerm.getText().toString());
                                String zipPerm = etPinCodePerm.getText().toString();
                                if (chkSame.isChecked()) {
                                    addrPerm = "";
                                    zipPerm = "";
                                    addrSame = "true";
                                }
                                String landline = etLandline.getText().toString();
                                String dob = "";
                                String strDOB = spnrDOBDay.getSelectedItem().toString() + " " + spnrDOBMonth.getSelectedItem().toString() + " " + spnrDOBYear.getSelectedItem().toString();
                                SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM yyyy", Locale.ENGLISH);
                                Date tmpDate = new Date();
                                try {
                                    tmpDate = dateFormat.parse(strDOB);
                                    dob = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(tmpDate);
                                } catch (Exception e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }

                                String tmp = "";
                                tmp = spnrQual.getSelectedItem().toString();
                                String qualID = "1";
                                if (tmp.equalsIgnoreCase("Undergraduate")) qualID = "1";
                                if (tmp.equalsIgnoreCase("Graduate")) qualID = "2";
                                if (tmp.equalsIgnoreCase("Postgraduate")) qualID = "3";
                                tmp = spnrHouse.getSelectedItem().toString();
                                String houseID = "1";
                                if (tmp.equalsIgnoreCase("PG")) houseID = "1";
                                if (tmp.equalsIgnoreCase("Rent")) houseID = "2";
                                if (tmp.equalsIgnoreCase("Own")) houseID = "3";
                                tmp = spnrPurpose.getSelectedItem().toString();
                                String purposeID = "1";
                                if (tmp.equalsIgnoreCase("Rent/Deposit")) purposeID = "1";
                                if (tmp.equalsIgnoreCase("Medical")) purposeID = "2";
                                if (tmp.equalsIgnoreCase("Travel")) purposeID = "3";
                                if (tmp.equalsIgnoreCase("Personal")) purposeID = "4";
                                String companyName = actvCompany2.getText().toString();
                                String empLandline = etEmpLandline.getText().toString();
                                String emailAddrOff = etEmailOff.getText().toString();
                                String doj = "";
                                String strDOJ = "01 " + spnrWorkSinceMonth.getSelectedItem().toString() + " " + spnrWorkSinceYear.getSelectedItem().toString();
                                dateFormat = new SimpleDateFormat("d MMM yyyy", Locale.ENGLISH);
                                tmpDate = new Date();
                                try {
                                    tmpDate = dateFormat.parse(strDOJ);
                                    doj = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(tmpDate);
                                } catch (Exception e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }

                                String empId = null;
                               /* if(company_ids.size()>0) {
                                    if (companyPosition >= 0)
                                        empId = company_ids.get(companyPosition);
                                }*/
                                //if(company_ids.size()==0){
                                empId = String.valueOf(companyPosition);
                                // }
                                String empName = actvCompany2.getText().toString();

                                if (zipPerm.isEmpty()) zipPerm = "null";

                                try {
                                    //P2
                                    String custName = "";
                                    String currAddr = "";
                                    String currZip = "";
                                    String custCell = "";
                                    String persEmail = "";
                                    String income = "";
                                    String custPAN = "";
                                    String addrProofID = "1";
                                    try {
                                        if (!usrEntity.isNull("customerName"))
                                            custName = usrEntity.getString("customerName");
                                        if (!usrEntity.isNull("currentAddress"))
                                            currAddr = escapedStr(usrEntity.getString("currentAddress").toString());
                                        if (!usrEntity.isNull("postcode"))
                                            currZip = usrEntity.getString("postcode");
                                        else currZip = "null";
                                        if (!usrEntity.isNull("mobNumber"))
                                            custCell = usrEntity.getString("mobNumber");
                                        if (!usrEntity.isNull("personalEmailId"))
                                            persEmail = usrEntity.getString("personalEmailId");
                                        if (!usrEntity.isNull("monthlyIncome"))
                                            income = usrEntity.getString("monthlyIncome");
                                        if (!usrEntity.isNull("pan"))
                                            custPAN = usrEntity.getString("pan");
                                        if (!usrEntity.isNull("addressProofId"))
                                            addrProofID = usrEntity.getString("addressProofId");
                                    } catch (Exception e) {
                                    }
                                    if (empId.equalsIgnoreCase("-1")) empId = null;
                                    //P2
                                    uInf = "{\"customerId\":" + custID + ",\"customerName\":\"" + custName + "\", \"title\": \"\",  " +
                                            "\"currentAddress\":\"" + currAddr + "\", \"permanentAddress\":\"" + addrPerm + "\",  \"locality\":null,   \"city\":\"\",   " +
                                            "\"state\":\"\",\"postcode\":" + currZip + ",\"mobNumber\":\"" + custCell + "\"," +
                                            "\"mobileUsageSince\":\"\",\"landlineNumber\":\"" + landline + "\"," +
                                            "\"email\":\"" + emailAddrOff + "\",\"personalEmailId\":\"" + persEmail + "\",\"dob\":\"" + dob + "\",\"educationTypeId\":" + qualID + "," +
                                            "\"houseTypeId\":" + houseID + ",\"currentHouseSince\":\"\"," +
                                            "\"monthlyIncome\":" + income + ",\"companyId\":" + empId + ",\"companyName\":\"" + empName + "\",\"companyLandlineNo\": \"" + empLandline + "\",\"designationId\":1,\"doj\":\"" + doj + "\"," +
                                            "\"pan\":\"" + custPAN + "\",\"spouseSalary\":0,\"purposeOfCashe\":\"" + purposeID + "\",\"addressProofId\":" + addrProofID + ", \"wsCallType\": \"P2\", \"permanentAddressPostCode\":" + zipPerm + ",\"isPermanetAddressSameAsCurrent\":" + addrSame + " }";
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                Log.d("P2", uInf);
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
                    rQueue.getInstance(SignUp.this).queueRequest("", reqCust);
                    //AsyncTask<String, Integer, String> asyncProfile = new asyncReq().execute("selfie.jpg", "address.jpg", "payslip.jpg", "pan.jpg");
                }
            }, 500);
        } else if (stepNo == 8 && !validStep8()) {
            // Boolean v = validStep8();
            return;
        } else if (stepNo == 8 && validStep8()) {
            //Boolean v = validStep8();

            final EditText etIFSC = (EditText) findViewById(R.id.etIFSC);
            StringRequest reqIFSC = new StringRequest(com.android.volley.Request.Method.GET, urlIFSC + etIFSC.getText().toString(),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject details = new JSONObject(response);
                                String statusType = details.getString("statusType");
                                JSONObject jsonEntity = new JSONObject(details.getString("entity"));
                                String bankName = jsonEntity.getString("bankName");
                                AutoCompleteTextView etBankName = (AutoCompleteTextView) findViewById(R.id.actvBank);
                                String strBankName = etBankName.getText().toString();

                                if (!statusType.equalsIgnoreCase("OK")) {
                                    etIFSC.requestFocus();
                                    etIFSC.setError("Please provide a valid IFSC code");
                                } else {
                                    if (details.isNull("entity")) {
                                        etIFSC.requestFocus();
                                        etIFSC.setError("Please provide a valid IFSC code");
                                    } else {
                                        if (!bankName.equalsIgnoreCase(strBankName)) {
                                            etIFSC.requestFocus();
                                            etIFSC.setError("IFSC does not belong to this bank");
                                        } else {
                                            final TableLayout tblStep8 = (TableLayout) findViewById(R.id.step8);
                                            tblStep8.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.down_to_up_full));
                                            tblStep8.postDelayed(new Runnable() {
                                                public void run() {

                                                    StringRequest reqBankDetails = new StringRequest(com.android.volley.Request.Method.POST, urlBankDetails,
                                                            new Response.Listener<String>() {
                                                                @Override
                                                                public void onResponse(String response) {
                                                                    try {
                                                                        final JSONObject updated = new JSONObject(response);
                                                                        String statusType = updated.getString("statusType");
                                                                        if (statusType.equalsIgnoreCase("OK")) {
                                                                            AsyncTask<String, Integer, Void> asyncProfile = new asyncReq2().execute("bank_statement.jpg");
                                                                        } else {
                                                                            SignUp.this.runOnUiThread(new Runnable() {
                                                                                public void run() {
                                                                                    try {
                                                                                        Toast.makeText(SignUp.this, updated.optString("entity", "Something went wrong, Please try again later"), Toast.LENGTH_LONG).show();
                                                                                    } catch (Exception e) {
                                                                                        e.printStackTrace();
                                                                                    }
                                                                                }
                                                                            });
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
                                                                    if (error instanceof NoConnectionError) {
                                                                        Toast.makeText(SignUp.this, "No Internet access. Please check your connection.", Toast.LENGTH_LONG).show();
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
                                                                AutoCompleteTextView etBankName = (AutoCompleteTextView) findViewById(R.id.actvBank);
                                                                EditText etAccNum = (EditText) findViewById(R.id.etAccNum);
                                                                EditText etIFSC = (EditText) findViewById(R.id.etIFSC);
                                                                String bankName = etBankName.getText().toString();
                                                                String ifsc = etIFSC.getText().toString();
                                                                String accNum = etAccNum.getText().toString();
                                                                uInf = "{\"bankName\" : \"" + bankName + "\",\"ifsc\" : \"" + ifsc + "\",\"accountNumber\" : \"" + accNum + "\"}";
                                                                return uInf == null ? null : uInf.getBytes("utf-8");
                                                            } catch (UnsupportedEncodingException uee) {
                                                                VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                                                                        uInf, "utf-8");
                                                                return null;
                                                            }

                                                        }

                                                    };
                                                    reqBankDetails.setShouldCache(false);
                                                    rQueue.getInstance(SignUp.this).queueRequest("", reqBankDetails);

                                                }
                                            }, 500);
                                        }
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
                            if (error instanceof NoConnectionError) {
                                Toast.makeText(SignUp.this, "No Internet access. Please check your connection.", Toast.LENGTH_LONG).show();
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
            rQueue.getInstance(this).queueRequest("", reqIFSC);


        } ;

    }

    public void step123() {
        int copmpos = companyPosition;
        TextView caption = (TextView) findViewById(R.id.caption);
        caption.setText("APPROVAL");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        //final TableLayout tblStep21 = (TableLayout) findViewById(R.id.step2new);
        //tblStep21.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.down_to_up_full));
        //tblStep21.postDelayed(new Runnable() {
        //    public void run() {
        StringRequest reqCust = new StringRequest(com.android.volley.Request.Method.POST, urlCust,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            Log.d("IPB", response);
                            JSONObject updated = new JSONObject(response);
                            String statusType = updated.getString("statusType");
                            if (statusType.equalsIgnoreCase("OK")) {

                                phaseSignUp = "P1";
                                JSONObject jsonEntity = new JSONObject(updated.getString("entity"));
                                String maxCred = jsonEntity.getString("maxCreditEligible");
                                TextView etCredEligible = (TextView) findViewById(R.id.tvCredEligible);
                                etCredEligible.setText('\u20B9' + maxCred.replace(".0", ""));
                                getCustInfo();
                                AsyncTask<Bitmap, Integer, Void> asyncSelfie = new asyncReqSelfie().execute(getProfilePicBitmap());
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
                        if (error instanceof NoConnectionError) {
                            Toast.makeText(SignUp.this, "No Internet access. Please check your connection.", Toast.LENGTH_LONG).show();
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
                //P0 Construct
                String uInf = "";
                try {
                    EditText etName = (EditText) findViewById(R.id.etFullName);
                    EditText etEmail = (EditText) findViewById(R.id.etEmail);
                    EditText etMobile = (EditText) findViewById(R.id.etCellNumber);

                    AutoCompleteTextView actvCompanyStep2 = (AutoCompleteTextView) findViewById(R.id.actvEmpNameStep2);
                    EditText etNetSalStep2 = (EditText) findViewById(R.id.etNetSalStep2);

                    String fullName = etName.getText().toString();
                    String mobile = etMobile.getText().toString();
                    String emailAddr = etEmail.getText().toString();
                    String companyName = actvCompanyStep2.getText().toString();
                    String income = etNetSalStep2.getText().toString();

                    MaterialSpinner spnrDOBDay = (MaterialSpinner) findViewById(R.id.spnrDOBDayStep2);
                    MaterialSpinner spnrDOBMonth = (MaterialSpinner) findViewById(R.id.spnrDOBMonthStep2);
                    MaterialSpinner spnrDOBYear = (MaterialSpinner) findViewById(R.id.spnrDOBYearStep2);

                    String dob = "";
                    String strDOB = spnrDOBDay.getSelectedItem().toString() + " " + spnrDOBMonth.getSelectedItem().toString() + " " + spnrDOBYear.getSelectedItem().toString();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM yyyy", Locale.ENGLISH);
                    Date tmpDate = new Date();
                    try {
                        tmpDate = dateFormat.parse(strDOB);
                        dob = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(tmpDate);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    String empId = null; //"null";
                    if (company_ids.size() > 0) {
                        if (companyPosition >= 0)
                            empId = company_ids.get(companyPosition);
                    }

                    //P0
                    uInf = "{\"customerId\":" + custID + ",\"customerName\":\"" + fullName + "\", \"title\": \"\",  " +
                            "\"currentAddress\":\"\", \"permanentAddress\":\"\",  \"locality\":null,   \"city\":\"\",   " +
                            "\"state\":\"\",\"postcode\":\"\",\"mobNumber\":\"" + mobile + "\"," +
                            "\"mobileUsageSince\":\"\",\"landlineNumber\":\"\"," +
                            "\"email\":\"\",\"personalEmailId\":\"" + emailAddr + "\",\"dob\":\"" + dob + "\",\"educationTypeId\":null," +
                            "\"houseTypeId\":null,\"currentHouseSince\":\"\"," +
                            "\"monthlyIncome\":" + income + ",\"companyId\":" + empId + ",\"companyName\":\"" + companyName + "\",\"companyLandlineNo\": \"\",\"designationId\":1,\"doj\":\"\"," +
                            "\"pan\":\"\",\"spouseSalary\":0,\"purposeOfCashe\":\"\",\"addressProofId\":null, \"wsCallType\": \"P0\", \"permanentAddressPostCode\":null,\"isPermanetAddressSameAsCurrent\":true }";
                    Log.d("P0", uInf);
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
        rQueue.getInstance(SignUp.this).queueRequest("", reqCust);
    }


    private void verifyCompanies(final String pfx) {
        String input = "";
        String pfxArray[] = null;
        if (pfx != null)
            pfxArray = pfx.split(" ");
        StringRequest reqCompanies = new StringRequest(com.android.volley.Request.Method.GET, urlCompanies + (pfxArray != null && pfxArray.length > 0 ? pfxArray[0] : pfx),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d("IPB", response);
                            JSONObject jsonObject = new JSONObject(response.toString());
                            if (!jsonObject.isNull("entity")) {
                                companyPosition = -1;
                                company_ids.clear();
                                company_names.clear();
                                JSONArray arrCompanies = jsonObject.getJSONArray("entity");
                                for (int i = 0; i < arrCompanies.length(); i++) {
                                    String companyName = arrCompanies.getJSONObject(i).getString("name");
                                    String companyID = arrCompanies.getJSONObject(i).getString("id");
                                    listCompanies.add(new ListCompanies(companyID, companyName));
                                    company_names.add(companyName);
                                    company_ids.add(companyID);
                                    if (companyName.equalsIgnoreCase(pfx)) {
                                        companyPosition = i;
                                    }
                                }

                            } else companyPosition = -1;


                            step123();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        if (error instanceof NoConnectionError) {
                            Toast.makeText(SignUp.this, "No Internet access. Please check your connection.", Toast.LENGTH_LONG).show();
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
        rQueue.getInstance(this).getRequestQueue().cancelAll("companies");
        reqCompanies.setTag("companies");
        reqCompanies.setShouldCache(false);
        rQueue.getInstance(this).queueRequest("", reqCompanies);

    }


    private void fetchCompanies(String pfx, final AutoCompleteTextView actv) {
        String input = "";
        try {
            pfx = URLEncoder.encode(pfx, "UTF-8");
            Log.d("IPB pfx pfx pfx", pfx);
        } catch (UnsupportedEncodingException ignored) {
            // Can be safely ignored because UTF-8 is always supported
            String pfxArray[] = null;
            if (pfx != null)
                pfxArray = pfx.split(" ");
            pfx = pfxArray != null && pfxArray.length > 0 ? pfxArray[0] : pfx;
        }
        StringRequest reqCompanies = new StringRequest(com.android.volley.Request.Method.GET, urlCompanies + pfx,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d("IPB", response);
                            JSONObject jsonObject = new JSONObject(response.toString());
                            if (!jsonObject.isNull("entity")) {
                                company_ids.clear();
                                company_names.clear();
                                JSONArray arrCompanies = jsonObject.getJSONArray("entity");
                                for (int i = 0; i < arrCompanies.length(); i++) {
                                    String companyName = arrCompanies.getJSONObject(i).getString("name");
                                    String companyID = arrCompanies.getJSONObject(i).getString("id");
                                    listCompanies.add(new ListCompanies(companyID, companyName));
                                    company_names.add(companyName);
                                    company_ids.add(companyID);
                                }
                                adaptr = new ArrayAdapter<String>(
                                        getApplicationContext(),
                                        android.R.layout.simple_list_item_1, company_names) {
                                    @Override
                                    public View getView(int position,
                                                        View convertView, ViewGroup parent) {
                                        View view = super.getView(position,
                                                convertView, parent);
                                        TextView text = (TextView) view
                                                .findViewById(android.R.id.text1);
                                        text.setTextColor(Color.BLACK);
                                        return view;
                                    }
                                };
                                actv.setAdapter(adaptr);
                                adaptr.notifyDataSetChanged();

                            } else companyPosition = -1;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        if (error instanceof NoConnectionError) {
                            Toast.makeText(SignUp.this, "No Internet access. Please check your connection.", Toast.LENGTH_LONG).show();
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
        rQueue.getInstance(this).getRequestQueue().cancelAll("companies");
        reqCompanies.setTag("companies");
        reqCompanies.setShouldCache(false);
        rQueue.getInstance(this).queueRequest("", reqCompanies);
    }


    private int getBankPos(String bankName) {
        int pos = 0;
        for (int i = 0; i < listBanks.size(); i++) {
            if (listBanks.get(i).getName().equalsIgnoreCase(bankName)) {
                pos = i + 1;
                break;
            }
        }
        return pos;
    }


    private Bitmap decodeSampledBitmapFromUri(Uri uri, int reqWidth, int reqHeight) {
        Bitmap bmp = null;
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        try {
            BitmapFactory.decodeStream(getContentResolver().openInputStream(uri), null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        try {
            bmp = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri), null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bmp;
    }

    private Bitmap decodeSampledBitmapFromPath(String fPath, int reqWidth, int reqHeight) {
        Bitmap bmp = null;
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(fPath, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        bmp = BitmapFactory.decodeFile(fPath, options);
        return bmp;
    }

    boolean validateIFSC() {
        EditText etIFSC = (EditText) findViewById(R.id.etIFSC);
        StringRequest reqIFSC = new StringRequest(com.android.volley.Request.Method.GET, urlIFSC + etIFSC.getText().toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject details = new JSONObject(response);
                            String statusType = details.getString("statusType");

                            if (!statusType.equalsIgnoreCase("OK")) validIFSC = false;
                            else {
                                if (details.isNull("entity")) validIFSC = false;
                                else validIFSC = true;

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
                        if (error instanceof NoConnectionError) {
                            Toast.makeText(SignUp.this, "No Internet access. Please check your connection.", Toast.LENGTH_LONG).show();
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
        rQueue.getInstance(this).queueRequest("", reqIFSC);
        return validIFSC;

    }

    private void getCustInfo() {


        StringRequest reqCust = new StringRequest(com.android.volley.Request.Method.GET, urlCustInf,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            Log.d("IPB", response);
                            JSONObject jsonCust = new JSONObject(response.toString());
                            usrEntity = new JSONObject(jsonCust.getString("entity"));
                            AutoCompleteTextView actvCompany = (AutoCompleteTextView) findViewById(R.id.actvEmpName);
                            AutoCompleteTextView actvCompany2 = (AutoCompleteTextView) findViewById(R.id.actvEmpName2);
                            try {
                                actvCompany.setText(usrEntity.getString("companyName"));
                                actvCompany2.setText(usrEntity.getString("companyName"));
                                String str2 = usrEntity.getString("companyName");
                                companyPosition = Integer.parseInt(usrEntity.getString("companyId"));
                                String str1 = usrEntity.getString("companyId");
                            } catch (Exception e) {
                            }

                            if (usrEntity != null) {
                                if (!usrEntity.isNull("companyName")) {
                                    actvCompany = (AutoCompleteTextView) findViewById(R.id.actvEmpName);
                                    actvCompany.setText(usrEntity.getString("companyName"));
                                }

                                if (!usrEntity.isNull("monthlyIncome")) {
                                    EditText etNetSal = (EditText) findViewById(R.id.etNetSal);
                                    etNetSal.setText(usrEntity.getString("monthlyIncome"));
                                }
                                if (!usrEntity.isNull("dob")) {
                                    String tmpDate = usrEntity.getString("dob");
                                    Date date = null;
                                    try {
                                        date = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(tmpDate);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    try {
                                        MaterialSpinner spnrDOBDay = (MaterialSpinner) findViewById(R.id.spnrDOBDay);
                                        MaterialSpinner spnrDOBMonth = (MaterialSpinner) findViewById(R.id.spnrDOBMonth);
                                        MaterialSpinner spnrDOBYear = (MaterialSpinner) findViewById(R.id.spnrDOBYear);
                                        selectSpinnerItemByValue(spnrDOBDay, new SimpleDateFormat("dd", Locale.ENGLISH).format(date));
                                        selectSpinnerItemByValue(spnrDOBMonth, new SimpleDateFormat("MMM", Locale.ENGLISH).format(date));
                                        selectSpinnerItemByValue(spnrDOBYear, new SimpleDateFormat("yyyy", Locale.ENGLISH).format(date));
                                    }catch(Exception e){}
                                }
                            }


                            ImageRequest request = new ImageRequest(urlCustPicURL + "/" + custID + "/profile",
                                    new Response.Listener<Bitmap>() {
                                        @Override
                                        public void onResponse(Bitmap bitmap) {
                                            ctSelfie.setImageBitmap(bitmap);
                                        }
                                    }, 0, 0, null,
                                    new Response.ErrorListener() {
                                        public void onErrorResponse(VolleyError error) {
                                            ctSelfie.setImageResource(R.drawable.selfie_error);
                                        }
                                    }) {
                                @Override
                                public Map<String, String> getHeaders() throws AuthFailureError {
                                    HashMap<String, String> headers = new HashMap<String, String>();
                                    headers.put("Authorization", tokenType + " " + accessToken);
                                    return headers;
                                }
                            };
                            request.setShouldCache(false);
                            rQueue.getInstance(SignUp.this).queueRequest("", request);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        if (error instanceof NoConnectionError) {
                            Toast.makeText(SignUp.this, "No Internet access. Please check your connection.", Toast.LENGTH_LONG).show();
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


    private String escapedStr(String jsonSrc) {
        String jsonOut = "";
        jsonOut = jsonSrc.replace("\r\n", " ");
        jsonOut = jsonOut.replace("\r", " ");
        jsonOut = jsonOut.replace("\n", " ");
        jsonOut = jsonOut.replace("\t", " ");
        jsonOut = jsonOut.replace("\b", "");
        return jsonOut;
    }

    public static void selectSpinnerItemByValue(MaterialSpinner spnr, String value) {
        try {
            for (int position = 1; position < spnr.getCount(); position++) {
                if (spnr.getItemAtPosition(position).toString().equalsIgnoreCase(value)) {
                    spnr.setSelection(position);
                } else
                    continue;
            }
        }catch(Exception e){}
    }

    private void smartUpload() {
        Intent i = new Intent(SignUp.this, SmartUpload.class);
        startActivity(i);
    }

    public void smartUpload(View v) {
        Intent i = new Intent(SignUp.this, SmartUpload.class);
        startActivity(i);
    }

    private void fetchbank(final String pfx) {
        StringRequest reqBankDetails = new StringRequest(com.android.volley.Request.Method.POST, urlBankByPrefix,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
/*                        try {
                            if(null != response){
                                listBanks.clear();
                                JSONObject jsonBanks = new JSONObject(response);
                                JSONArray arrBanks = jsonBanks.getJSONArray("entity");
                                for (int i=0; i< arrBanks.length();i++) {
                                    String bankName = arrBanks.getJSONObject(i).getString("bankName");
                                    String bankID = arrBanks.getJSONObject(i).getString("bankId");
                                    listBanks.add(new ListBanks(bankID,bankName));
                                }
                                AutoCompleteTextView actvBank = (AutoCompleteTextView)findViewById(R.id.actvBank);
                                actvBank.setAdapter(new bankSuggest(SignUp.this, actvBank.getText().toString(), listBanks));
                                actvBank.setOnItemSelectedListener(new OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        bankPosition = position;
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }
                                });
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }*/

                        //
                        try {
                            Log.d("IPB", response);
                            JSONObject jsonBanks = new JSONObject(response);
                            JSONArray arrBanks = jsonBanks.getJSONArray("entity");
                            if (arrBanks != null) {
                                listBanks.clear();
                                listBankName.clear();
                                for (int i = 0; i < arrBanks.length(); i++) {
                                    String bankName = arrBanks.getJSONObject(i).getString("bankName");
                                    String bankID = arrBanks.getJSONObject(i).getString("bankId");
                                    listBanks.add(new ListBanks(bankID, bankName));
                                    listBankName.add(bankName);
                                }

                                AutoCompleteTextView actvBank = (AutoCompleteTextView) findViewById(R.id.actvBank);
                                adaptr = new ArrayAdapter<String>(
                                        getApplicationContext(),
                                        android.R.layout.simple_list_item_1, listBankName) {
                                    @Override
                                    public View getView(int position,
                                                        View convertView, ViewGroup parent) {
                                        View view = super.getView(position,
                                                convertView, parent);
                                        TextView text = (TextView) view
                                                .findViewById(android.R.id.text1);
                                        text.setTextColor(Color.BLACK);
                                        return view;
                                    }
                                };
                                actvBank.setAdapter(adaptr);
                                adaptr.notifyDataSetChanged();
                                actvBank.setOnItemSelectedListener(new OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        bankPosition = position;
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }
                                });

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        //
                        dismissDailog();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        if (error instanceof NoConnectionError) {
                            Toast.makeText(SignUp.this, "No Internet access. Please check your connection.", Toast.LENGTH_LONG).show();
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

                    uInf = "{\"value\" : \"" + pfx + "\"}";
                    return uInf == null ? null : uInf.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                            uInf, "utf-8");
                    return null;
                }

            }

        };
        rQueue.getInstance(this).getRequestQueue().cancelAll("fetchbankname");
        reqBankDetails.setTag("fetchbankname");
        reqBankDetails.setShouldCache(false);
        rQueue.getInstance(this).queueRequest("", reqBankDetails);

    }

}