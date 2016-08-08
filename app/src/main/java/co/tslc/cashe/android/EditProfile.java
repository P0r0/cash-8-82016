package co.tslc.cashe.android;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost;
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
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
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

public class EditProfile extends Base {

    protected CASHe app;
    private Tracker mTracker;

    private List<ListBanks> listBanks = new ArrayList<ListBanks>();

    private JSONObject usrEntity = new JSONObject();
    private JSONObject usrBank = new JSONObject();

    private int bankPosition;
    private String currentStep = "profile_home";
    private boolean validIFSC = true;

    private int REQUEST_ADDR_PROOF = 901;
    private int REQUEST_SAL_SLIP = 902;
    private int REQUEST_PAN = 903;
    private int REQUEST_BANK_STMT_PROOF = 904;
    private int CLICK_SELFIE = 911;
    private int CLICK_ADDR_PROOF = 912;
    private int CLICK_SAL_SLIP = 913;
    private int CLICK_PAN = 914;
    private int CLICK_BANK_STMT_PROOF = 915;

    private Uri uriSelfie;
    private Uri uriAddrProof;
    private Uri uriSalSlip;
    private Uri uriPAN;
    private Uri uriBankStmt;

    private static final String BITMAP_STORAGE_KEY = "viewbitmap";
    private static final String IMAGEVIEW_VISIBILITY_STORAGE_KEY = "imageviewvisibility";
    private ImageView mImageView;
    private Bitmap mImageBitmap;
    private Bitmap mImageBitmapAddr;
    private Bitmap mImageBitmapSlip;
    private Bitmap mImageBitmapTN;
    private Bitmap mImageBitmapPAN;
    private String mCurrentBankStmt;
    private Bitmap bmSelfie;
    private static String TAG = "Cashe";
    ProgressDialog pd;

    private static final int ACTION_TAKE_PHOTO_B = 1;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 6;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_MEMORY = 7;

    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;

    CircTN ctSelfie;

    private int companyPosition;

    private String name;
    private String email;
    private String custID;
    private String custTitle;

    private static String accessToken = "";
    private static String refreshToken = "";
    private static String tokenType = "";

    private String casheAmt;
    private String inHand;
    private String feeFlat;
    private String feeProc;
    private String feeProcLbl;
    private String feeProcAmt;
    private static String eMode = "";
    private static String lMode = "";

    private static String customerStatusName;
    private static String custName = "";
    private static String custStatusId = "";
    private static String custAddr = "";
    private static String custAddrPerm = "";
    private static String custAddrProofID = "";
    private static String custLocality = "";
    private static String custCity = "";
    private static String custState = "";
    private static String custZip = "";
    private static String custZipPerm = "";
    private static String custAddrRank = "";
    private static String custCell = "";
    private static String custCellSinceM = "";
    private static String custCellSinceY = "";
    private static String custLandline = "";
    private static String custEmail = "";
    private static String custEmailPers = "";
    private static String custDOB = "";
    private static String custEduType = "";
    private static String custHouseType = "";
    private static String custHouseSinceM = "";
    private static String custHouseSinceY = "";
    private static String custSal = "";
    private static String custCompany = "";
    private static String custCompanyName = "";
    private static String custCompanyLandline = "";
    private static String custCompanyRank = "";
    private static String custCompanyDur = "";
    private static String custDesig = "";
    private static String custDOJ = "";
    private static String custPAN = "";
    private static String custSalSpouse = "";
    private static String custPurpose = "";
    private static String custStatus = "";
    private static String custReason = "";
    private static String custNewStatusID = "";
    private static String custValIDs = "";
    private static String custCellSince = "";
    private static String custHouseSince = "";
    private static String sameAsPerm = "";
    private static String bankName = "";
    private static String ifsc = "";
    private static String accNum = "";

    private static Boolean modEssential = false;
    private static Boolean modPersonal = false;
    private static Boolean modProfessional = false;
    private static Boolean modBank = false;
    private static Boolean modBankstmnt = false;

    private String mCurrentPhotoPath;
    private String mCurrentFileName;
    private String mCurrentAddrProof;
    private String mCurrentSalSlip;
    private String mCurrentPAN;

    private static final String JPEG_FILE_PREFIX = "cashe_selfie_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";

    private DatePicker datePicker;
    private Calendar calendar;
    private EditText dvDOB;
    private EditText dvDOJ;
    private int year, month, day;

    private ArrayList<String> company_names = new ArrayList<String>();
    private ArrayList<String> company_ids = new ArrayList<String>();
    private ArrayAdapter<String> adaptr;

    private static final String urlCust = CASHe.webApiUrl + "api/cashe/customer/update";
    private static final String urlCustInf = CASHe.webApiUrl + "api/cashe/customer/details";
    private static final String urlCustBank = CASHe.webApiUrl + "api/cashe/customer/getBankDetails";
    private static final String urlCustSlip = CASHe.webApiUrl + "api/cashe/customer/upload/payslip";
    private static final String urlCustAddr = CASHe.webApiUrl + "api/cashe/customer/upload/address";
    private static final String urlCustPic = CASHe.webApiUrl + "api/cashe/customer/upload/profile";
    private static final String urlCustPAN = CASHe.webApiUrl + "api/cashe/customer/upload/pancard";
    private static final String urlCustStmt = CASHe.webApiUrl + "api/cashe/customer/upload/bankStatement";
    private static final String urlStatus = CASHe.webApiUrl + "api/cashe/customer/status";
    private static final String urlStatic = CASHe.webApiUrl + "api/cashe/data/static";
    private static final String urlBanks = CASHe.webApiUrl + "api/cashe/customer/getBanksName";
    private static final String urlBankDetails = CASHe.webApiUrl + "api/cashe/customer/bankDetailsUpdate";
    private static final String urlBankDetailsGet = CASHe.webApiUrl + "api/cashe/customer/getBankDetails";
    private static final String urlCustPicURL = CASHe.webApiUrl + "api/cashe/customer/image";
    private static final String urlCompanies = CASHe.webApiUrl + "api/cashe/data/companies/";
    private static final String urlIFSC = CASHe.webApiUrl + "api/cashe/customer/bankDetailByIFSC/";
    private static final String urlPAN = CASHe.webApiUrl + "api/cashe/customer/validatePANOnProfileUpdate/";

    private List<ListCompanies> listCompanies = new ArrayList<ListCompanies>();

    TabHost tabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_edit_profile);
        getLayoutInflater().inflate(R.layout.activity_edit_profile, frameLayout, true);
        typeFace(frameLayout);

        app = (CASHe) getApplication();
        mTracker = app.getDefaultTracker();
        mTracker.setScreenName("EditProfile");
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
        eMode = i.getStringExtra("eMode");
        lMode = i.getStringExtra("lMode");

        //eMode = "Y";

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

        final AutoCompleteTextView actvCompany2 = (AutoCompleteTextView) findViewById(R.id.actvEmpName2);
        actvCompany2.setThreshold(3);
        actvCompany2.setEnabled(false);
        /*actvCompany.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                companyPosition = position;
            }
        });*/
        actvCompany2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                companyPosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                companyPosition = -1;
            }
        });

        //company_names = new ArrayList<String>();
        //company_ids =  new ArrayList<String>();

        actvCompany2.addTextChangedListener(new TextWatcher() {

            long lastPress = 0l;

            public void afterTextChanged(Editable s) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (s.toString().length() >= 3) {
                    if (System.currentTimeMillis() - lastPress > 500) {
                        lastPress = System.currentTimeMillis();
                        //company_names = new ArrayList<String>();
                        //company_ids = new ArrayList<String>();
                        companyPosition = -1;

                        fetchCompanies(s.toString(), actvCompany2);
                    }
                }
            }
        });

        final AutoCompleteTextView actvCompany = (AutoCompleteTextView) findViewById(R.id.actvEmpName);
        actvCompany.setThreshold(3);
        /*actvCompany.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                companyPosition = position;
            }
        });*/
        actvCompany.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                companyPosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                companyPosition = -1;
            }
        });

        //company_names = new ArrayList<String>();
        //company_ids =  new ArrayList<String>();

        actvCompany.addTextChangedListener(new TextWatcher() {

            long lastPress = 0l;

            public void afterTextChanged(Editable s) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (s.toString().length() >= 3) {
                    if (System.currentTimeMillis() - lastPress > 500) {
                        lastPress = System.currentTimeMillis();
                        //company_names = new ArrayList<String>();
                        //company_ids = new ArrayList<String>();
                        companyPosition = -1;

                        fetchCompanies(s.toString(), actvCompany);
                    }
                }
            }
        });


        CheckBox chkSame = (CheckBox) findViewById(R.id.chkAddrSame);
        chkSame.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                EditText etAddrPerm = (EditText) findViewById(R.id.etAddressPerm);
                EditText etZip = (EditText) findViewById(R.id.etPinCode);

                if (isChecked) {
                    etAddrPerm.setEnabled(false);
                    etZip.setEnabled(false);
                } else {
                    etAddrPerm.setEnabled(true);
                    etZip.setEnabled(true);
                }
            }
        });

        ImageButton btnEssential = (ImageButton) findViewById(R.id.btnDoneEssential);
        ImageButton btnPersonal = (ImageButton) findViewById(R.id.btnDonePersonal);
        ImageButton btnProfessional = (ImageButton) findViewById(R.id.btnDoneProfessional);
        ImageButton btnBank = (ImageButton) findViewById(R.id.btnDoneBank);

        btnEssential.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validFormEssential()) return;
                donePressed();
            }
        });

        btnPersonal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validFormPersonal()) return;
                donePressed();
            }
        });

        btnProfessional.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validFormProfessional()) return;
                donePressed();
            }
        });

        btnBank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validFormBank()) return;
                donePressed();
            }
        });

        ImageButton btnUpdate = (ImageButton) findViewById(R.id.btnUpdate);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (modBank) updateBank();

                if (modEssential || modPersonal || modProfessional || modBank || mCurrentFileName != null) {

                    mTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Action")
                            .setAction("EditProfile")
                            .setLabel("UpdateProfile")
                            .build());
                    StringRequest reqCust = new StringRequest(com.android.volley.Request.Method.POST, urlCust,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject updated = new JSONObject(response);
                                        if (updated.has("statusType")) {
                                            String statusType = updated.getString("statusType");
                                            if (statusType.equalsIgnoreCase("OK")) {
                                                AsyncTask<String, Integer, String> asyncProfile = new asyncReq().execute("selfie.jpg", "address.jpg", "payslip.jpg", "pan.jpg", "bankstatement.jpg");
                                            } else
                                                Toast.makeText(EditProfile.this, "An error occurred while updating your profile. Please try again later", Toast.LENGTH_LONG).show();
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
                                        Toast.makeText(EditProfile.this, "No Internet access. Please check your connection.", Toast.LENGTH_LONG).show();
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
                                EditText etName = (EditText) findViewById(R.id.etFullName);
                                EditText etPAN = (EditText) findViewById(R.id.etPAN);
                                EditText etEmail = (EditText) findViewById(R.id.etEmail);
                                EditText etMobile = (EditText) findViewById(R.id.etCellNumber);
                                String fullName = etName.getText().toString();
                                String emailAddr = etEmail.getText().toString();
                                String mobile = etMobile.getText().toString();
                                String pan = etPAN.getText().toString();
                                EditText tvAddr = (EditText) findViewById(R.id.etAddress);
                                String addr = escapedStr(tvAddr.getText().toString());
                                //EditText tvPinCode = (EditText) findViewById(R.id.etPinCode);
                                //String zip = tvPinCode.getText().toString();

                                EditText etNetSal = (EditText) findViewById(R.id.etNetSal);
                                String income = etNetSal.getText().toString();
                                String strPAN = etPAN.getText().toString();
                                if (strPAN.equalsIgnoreCase("")) strPAN = custPAN;

                                AutoCompleteTextView actvCompany = (AutoCompleteTextView) findViewById(R.id.actvEmpName);
                                String companyName = actvCompany.getText().toString();

                                String empId = null;
                                if (company_ids.size() > 0) {
                                    if (companyPosition >= 0)
                                        empId = company_ids.get(companyPosition);
                                }

                                //Personal
                                EditText etAddrPerm = (EditText) findViewById(R.id.etAddressPerm);
                                EditText etPinCode = (EditText) findViewById(R.id.etPinCode);
                                EditText etLandline = (EditText) findViewById(R.id.etLandline);
                                CheckBox chkSame = (CheckBox) findViewById(R.id.chkAddrSame);
                                MaterialSpinner spnrDOBDay = (MaterialSpinner) findViewById(R.id.spnrDOBDay);
                                MaterialSpinner spnrDOBMonth = (MaterialSpinner) findViewById(R.id.spnrDOBMonth);
                                MaterialSpinner spnrDOBYear = (MaterialSpinner) findViewById(R.id.spnrDOBYear);
                                String dob = "";
                                String strDOB = spnrDOBDay.getSelectedItem().toString() + " " + spnrDOBMonth.getSelectedItem().toString() + " " + spnrDOBYear.getSelectedItem().toString();
                                SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM yyyy", Locale.ENGLISH);
                                Date tmpDate = new Date();
                                try {
                                    tmpDate = dateFormat.parse(strDOB);
                                    dob = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(tmpDate);
                                } catch (ParseException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }

                                MaterialSpinner spnrQual = (MaterialSpinner) findViewById(R.id.spnrQual);
                                MaterialSpinner spnrHouse = (MaterialSpinner) findViewById(R.id.spnrHouse);
                                MaterialSpinner spnrPurpose = (MaterialSpinner) findViewById(R.id.spnrPurpose);
                                MaterialSpinner spnrAddrProof = (MaterialSpinner) findViewById(R.id.spnrAddrProof);

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
                                String strAddrProof = spnrAddrProof.getSelectedItem().toString();
                                String addrProofID = "1";
                                if (strAddrProof.equalsIgnoreCase("Aadhar Card")) addrProofID = "1";
                                if (strAddrProof.equalsIgnoreCase("Utility Bill"))
                                    addrProofID = "2";
                                if (strAddrProof.equalsIgnoreCase("Telephone Bill"))
                                    addrProofID = "3";
                                if (strAddrProof.equalsIgnoreCase("Passport")) addrProofID = "4";
                                if (strAddrProof.equalsIgnoreCase("Voter's ID")) addrProofID = "5";

                                String isPermSameAsCurr = "";
                                String addrSame = "false";
                                String addrPerm = escapedStr(etAddrPerm.getText().toString());
                                String zipPerm = etPinCode.getText().toString();
                                if (chkSame.isChecked()) {
                                    addrPerm = "";
                                    zipPerm = "null";
                                    addrSame = "true";
                                    isPermSameAsCurr = "true";
                                } else {
                                    isPermSameAsCurr = "false";
                                    addrPerm = escapedStr(etAddrPerm.getText().toString());
                                }
                                String landline = etLandline.getText().toString();

                                EditText etEmpLandline = (EditText) findViewById(R.id.etEmpLandline);
                                EditText etEmailOff = (EditText) findViewById(R.id.etEmailOffice);
                                MaterialSpinner spnrWorkSinceMonth = (MaterialSpinner) findViewById(R.id.spnrWorkingSinceMonth);
                                MaterialSpinner spnrWorkSinceYear = (MaterialSpinner) findViewById(R.id.spnrWorkingSinceYear);

                                String empLandline = etEmpLandline.getText().toString();
                                String emailAddrOff = etEmailOff.getText().toString();

                                String doj = "";
                                String strDOJ = "01 " + spnrWorkSinceMonth.getSelectedItem().toString() + " " + spnrWorkSinceYear.getSelectedItem().toString();
                                try {
                                    tmpDate = dateFormat.parse(strDOJ);
                                    doj = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(tmpDate);
                                } catch (ParseException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }

                                if (zipPerm.isEmpty()) zipPerm = "null";

                                try {
                                    String usrPostCode = "";
                                    String usrSpouseSalary = "";
                                    String usrPurpose = "";
                                    String usrAddrProofId = "";
                                    if (!usrEntity.isNull("postcode"))
                                        usrPostCode = usrEntity.getString("postcode");
                                    if (!usrEntity.isNull("spouseSalary"))
                                        usrSpouseSalary = usrEntity.getString("spouseSalary");
                                    if (!usrEntity.isNull("purposeOfCashe"))
                                        usrPurpose = usrEntity.getString("purposeOfCashe");
                                    if (!usrEntity.isNull("addressProofId"))
                                        usrAddrProofId = usrEntity.getString("addressProofId");

                                    if (!usrPurpose.equalsIgnoreCase(purposeID))
                                        usrPurpose = purposeID;
                                    if (!usrAddrProofId.equalsIgnoreCase(addrProofID))
                                        usrAddrProofId = addrProofID;

                                    uInf = "{\"customerId\":" + custID + ",\"customerName\":\"" + fullName + "\",\"title\":\"\",   " +
                                            "\"currentAddress\":\"" + addr + "\"," + "\"locality\":null,   \"city\":null,   " +
                                            "\"state\":null,\"postcode\":" + usrPostCode + ",\"mobNumber\":" + mobile + "," +
                                            "\"mobileUsageSince\":\"\",\"landlineNumber\":\"" + landline + "\"," +
                                            "\"email\":\"" + emailAddrOff + "\",\"dob\":\"" + dob + "\",\"educationTypeId\":" + qualID + "," +
                                            "\"personalEmailId\":\"" + emailAddr + "\"" + ",\"permanentAddress\":\"" + addrPerm + "\",\"permanentAddressPostCode\":" + zipPerm +
                                            ",\"isPermanetAddressSameAsCurrent\":" + isPermSameAsCurr +
                                            ",\"houseTypeId\":" + houseID + ",\"currentHouseSince\":\"\"," +
                                            "\"monthlyIncome\":" + income + ",\"companyId\":" + empId + ",\"companyName\":\"" + companyName + "\",\"companyLandlineNo\":\"" + empLandline + "\",\"designationId\":1,\"doj\":\"" + doj + "\"," +
                                            "\"pan\":\"" + pan + "\",\"spouseSalary\":" + usrSpouseSalary + ",\"purposeOfCashe\":\"" + usrPurpose + "\",\"addressProofId\":" + usrAddrProofId + " }";
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

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
                    rQueue.getInstance(EditProfile.this).queueRequest("", reqCust);
                }
            }
        });

        RelativeLayout sc01 = (RelativeLayout) findViewById(R.id.container);
        overrideFonts(this, sc01);

        TabHost host = (TabHost) findViewById(R.id.tabHost);
        host.setup();

        //Personal
        TabHost.TabSpec spec = host.newTabSpec("Personal Details");
        spec.setContent(R.id.Personal);
        spec.setIndicator("Personal Details");
        host.addTab(spec);

        //Bank Details
        spec = host.newTabSpec("Bank Details");
        spec.setContent(R.id.Bank);
        spec.setIndicator("Bank Details");
        host.addTab(spec);

        if (eMode != null) {
            if (eMode.equalsIgnoreCase("Y")) {
                ImageButton btnEdit = (ImageButton) findViewById(R.id.ivEdit);
                btnEdit.setVisibility(View.GONE);
                ImageButton btnProceed = (ImageButton) findViewById(R.id.ibProceed);
                btnProceed.setVisibility(View.VISIBLE);
                TableLayout tblPers = (TableLayout) findViewById(R.id.tblSignUpPersView);
                TableLayout tblPersEdt = (TableLayout) findViewById(R.id.tblSignUpPers);
                TableLayout tblBank = (TableLayout) findViewById(R.id.tblBankView);
                //LinearLayout llBank = (LinearLayout)findViewById(R.id.llBankDetails);
                tblPers.setVisibility(View.GONE);
                tblPersEdt.setVisibility(View.VISIBLE);
                tblBank.setVisibility(View.GONE);
                //llBank.setVisibility(View.VISIBLE);
            }
        }

        TextView btnFind = (TextView) findViewById(R.id.btnFind);
        btnFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(EditProfile.this, IFSCSearch.class);
                bankPosition = getBankPos(bankName);
                //AutoCompleteTextView actvBank = (AutoCompleteTextView) findViewById(R.id.actvBank);
                //bankPosition = actvBank.getListSelection();
                i.putExtra("accessToken", accessToken);
                i.putExtra("refreshToken", refreshToken);
                i.putExtra("tokenType", tokenType);
                i.putExtra("bankPosition", bankPosition);
                AutoCompleteTextView actvBank = (AutoCompleteTextView) findViewById(R.id.actvBank);
                i.putExtra("bankName", actvBank.getText().toString());
                startActivityForResult(i, 654);
            }
        });

        TableLayout tbl = (TableLayout) findViewById(R.id.tblSignUpPers);
        tbl.setStretchAllColumns(true);
        tbl = (TableLayout) findViewById(R.id.tblSignUpPersView);
        tbl.setStretchAllColumns(true);
        tbl = (TableLayout) findViewById(R.id.tblBankView);
        tbl.setStretchAllColumns(true);
        tbl = (TableLayout) findViewById(R.id.step4);
        tbl.setStretchAllColumns(true);

        ImageButton ibCam1 = (ImageButton) findViewById(R.id.ibCam1);
        ibCam1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                File photo = new File(Environment.getExternalStorageDirectory(), "cashe_address_proof.jpg");
                intent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photo));
                uriAddrProof = Uri.fromFile(photo);
                startActivityForResult(intent, CLICK_ADDR_PROOF);
            }
        });

        ImageButton ibCam2 = (ImageButton) findViewById(R.id.ibCam2);
        ibCam2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                File photo = new File(Environment.getExternalStorageDirectory(), "cashe_salary_slip.jpg");
                intent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photo));
                uriSalSlip = Uri.fromFile(photo);
                startActivityForResult(intent, CLICK_SAL_SLIP);
            }
        });


        ImageButton ibCam3 = (ImageButton) findViewById(R.id.ibCam3);
        ibCam3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                File photo = new File(Environment.getExternalStorageDirectory(), "cashe_pan_proof.jpg");
                intent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photo));
                uriPAN = Uri.fromFile(photo);
                startActivityForResult(intent, CLICK_PAN);
            }
        });


        ImageButton ibCam4 = (ImageButton) findViewById(R.id.ibCam4);
        ibCam4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                File photo = new File(Environment.getExternalStorageDirectory(), "cashe_bank_statement_proof.jpg");
                intent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photo));
                uriBankStmt = Uri.fromFile(photo);
                startActivityForResult(intent, CLICK_BANK_STMT_PROOF);
            }
        });


        StringRequest reqCust = new StringRequest(com.android.volley.Request.Method.GET, urlCustInf,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("IPB", response.toString());
                        try {
                            JSONObject jsonCust = new JSONObject(response.toString());
                            JSONObject jsonEntity = new JSONObject(jsonCust.getString("entity"));
                            usrEntity = new JSONObject(jsonCust.getString("entity"));
                            customerStatusName = jsonEntity.getString("customerStatusName");
                            custID = jsonEntity.getString("customerId");
                            custName = jsonEntity.getString("customerName");
                            if (!jsonEntity.isNull("pan"))
                                custPAN = jsonEntity.getString("pan");
                            else custPAN = "";
                            custTitle = jsonEntity.getString("title");
                            custStatusId = jsonEntity.getString("customerStatusId");
                            custAddr = jsonEntity.getString("currentAddress");
                            custAddrPerm = jsonEntity.getString("permanentAddress");
                            custAddrProofID = jsonEntity.getString("addressProofId");
                            custLocality = jsonEntity.getString("locality");
                            custCity = jsonEntity.getString("city");
                            custState = jsonEntity.getString("state");
                            custZip = jsonEntity.getString("postcode");
                            sameAsPerm = jsonEntity.getString("isPermanetAddressSameAsCurrent");
                            CheckBox chkSame = (CheckBox) findViewById(R.id.chkAddrSame);
                            if (sameAsPerm.toString().equalsIgnoreCase("true"))
                                chkSame.setChecked(true);
                            if (!jsonEntity.isNull("permanentAddressPostCode"))
                                custZipPerm = jsonEntity.getString("permanentAddressPostCode");

                            custAddrRank = jsonEntity.getString("addressRank");
                            custCell = jsonEntity.getString("mobNumber");
                            custLandline = jsonEntity.getString("landlineNumber");
                            custEmail = email = jsonEntity.getString("email");
                            custEmailPers = jsonEntity.getString("personalEmailId");

                            if (!jsonEntity.isNull("dob")) {
                                String tmpDate = jsonEntity.getString("dob");
                                Date date = null;
                                try {
                                    date = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(tmpDate);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    custDOB = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(date);
                                    MaterialSpinner spnrDOBDay = (MaterialSpinner) findViewById(R.id.spnrDOBDay);
                                    MaterialSpinner spnrDOBMonth = (MaterialSpinner) findViewById(R.id.spnrDOBMonth);
                                    MaterialSpinner spnrDOBYear = (MaterialSpinner) findViewById(R.id.spnrDOBYear);
                                    selectSpinnerItemByValue(spnrDOBDay, new SimpleDateFormat("dd", Locale.ENGLISH).format(date));
                                    selectSpinnerItemByValue(spnrDOBMonth, new SimpleDateFormat("MMM", Locale.ENGLISH).format(date));
                                    selectSpinnerItemByValue(spnrDOBYear, new SimpleDateFormat("yyyy", Locale.ENGLISH).format(date));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                custDOB = "";
                                usrEntity.put("dob", "");
                            }

                            if (!jsonEntity.isNull("doj")) {
                                String tmpDate = jsonEntity.getString("doj");
                                Date date = null;
                                try {
                                    date = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(tmpDate);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    custDOJ = new SimpleDateFormat("MMM yyyy", Locale.ENGLISH).format(date);
                                    MaterialSpinner spnrDOJMonth = (MaterialSpinner) findViewById(R.id.spnrWorkingSinceMonth);
                                    MaterialSpinner spnrDOJYear = (MaterialSpinner) findViewById(R.id.spnrWorkingSinceYear);
                                    selectSpinnerItemByValue(spnrDOJMonth, new SimpleDateFormat("MMM", Locale.ENGLISH).format(date));
                                    selectSpinnerItemByValue(spnrDOJYear, new SimpleDateFormat("yyyy", Locale.ENGLISH).format(date));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                custDOJ = "";
                                usrEntity.put("doj", "");
                            }

                            custEduType = jsonEntity.getString("educationTypeId");
                            custHouseType = jsonEntity.getString("houseTypeId");
                            custSal = jsonEntity.getString("monthlyIncome");
                            custCompany = jsonEntity.getString("companyId");
                            custCompanyName = jsonEntity.getString("companyName");
                            custCompanyRank = jsonEntity.getString("companyRank");
                            custCompanyDur = jsonEntity.getString("durationCurrentCompany");
                            custCompanyLandline = jsonEntity.getString("companyLandlineNo");
                            custDesig = jsonEntity.getString("designationId");
                            /*if(!jsonEntity.isNull("doj")) {
                                String tmpDate = jsonEntity.getString("doj");
                                Date date = null;
                                try {
                                    date = new SimpleDateFormat("yyyy-MM-dd").parse(tmpDate);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                custDOJ = new SimpleDateFormat("dd-MM-yyyy").format(date);
                            }*/
                            custPAN = jsonEntity.getString("pan");
                            custSalSpouse = jsonEntity.getString("spouseSalary");
                            custPurpose = jsonEntity.getString("purposeOfCashe");
                            custStatus = jsonEntity.getString("status");
                            custReason = jsonEntity.getString("reason");
                            custNewStatusID = jsonEntity.getString("newStatusId");
                            custValIDs = jsonEntity.getString("validationIds");
                            //custCellSince = jsonEntity.getString("mobileUsageSince");
                            //custHouseSince = jsonEntity.getString("currentHouseSince");

                            EditText etName = (EditText) findViewById(R.id.etFullName);
                            EditText etPAN = (EditText) findViewById(R.id.etPAN);
                            EditText etEmail = (EditText) findViewById(R.id.etEmail);
                            EditText etAddr = (EditText) findViewById(R.id.etAddress);
                            EditText etAddrPerm = (EditText) findViewById(R.id.etAddressPerm);
                            EditText etPinCode = (EditText) findViewById(R.id.etPinCode);
                            EditText etDOB = (EditText) findViewById(R.id.etDOB);
                            EditText etDOJ = (EditText) findViewById(R.id.etDOJ);
                            EditText etMobile = (EditText) findViewById(R.id.etCellNumber);
                            EditText etLandline = (EditText) findViewById(R.id.etLandline);
                            EditText etNetSal = (EditText) findViewById(R.id.etNetSal);
                            EditText etEmpLandline = (EditText) findViewById(R.id.etEmpLandline);
                            EditText etNetSalSpouse = (EditText) findViewById(R.id.etNetSalSpouse);
                            MaterialSpinner spnrHouse = (MaterialSpinner) findViewById(R.id.spnrHouse);
                            MaterialSpinner spnrTitle = (MaterialSpinner) findViewById(R.id.spnrTitle);
                            MaterialSpinner spnrQual = (MaterialSpinner) findViewById(R.id.spnrQual);

                            String tmp = "";

                            tmp = custEduType;
                            if (tmp.equalsIgnoreCase("1")) spnrQual.setSelection(1);
                            if (tmp.equalsIgnoreCase("2")) spnrQual.setSelection(2);
                            if (tmp.equalsIgnoreCase("3")) spnrQual.setSelection(3);

                            tmp = custHouseType;
                            if (tmp.equalsIgnoreCase("1")) spnrHouse.setSelection(1);
                            if (tmp.equalsIgnoreCase("2")) spnrHouse.setSelection(2);
                            if (tmp.equalsIgnoreCase("3")) spnrHouse.setSelection(3);

                            MaterialSpinner spnrPurpose = (MaterialSpinner) findViewById(R.id.spnrPurpose);
                            tmp = custPurpose;
                            if (tmp.equalsIgnoreCase("1")) spnrPurpose.setSelection(1);
                            if (tmp.equalsIgnoreCase("2")) spnrPurpose.setSelection(2);
                            if (tmp.equalsIgnoreCase("3")) spnrPurpose.setSelection(3);
                            if (tmp.equalsIgnoreCase("4")) spnrPurpose.setSelection(4);

                            MaterialSpinner spnrAddrProof = (MaterialSpinner) findViewById(R.id.spnrAddrProof);
                            tmp = custAddrProofID;
                            if (tmp.equalsIgnoreCase("1")) spnrAddrProof.setSelection(1);
                            if (tmp.equalsIgnoreCase("2")) spnrAddrProof.setSelection(2);
                            if (tmp.equalsIgnoreCase("3")) spnrAddrProof.setSelection(3);
                            if (tmp.equalsIgnoreCase("4")) spnrAddrProof.setSelection(4);
                            if (tmp.equalsIgnoreCase("5")) spnrAddrProof.setSelection(5);

                            etPAN.setText(custPAN);

                            etEmpLandline.setText(custCompanyLandline);

                            TextView tvName = (TextView) findViewById(R.id.tvProfileName);
                            TextView tvAddr = (TextView) findViewById(R.id.tvProfileCurrAddr);
                            TextView tvAddrPerm = (TextView) findViewById(R.id.tvProfilePermAddr);
                            TextView tvZip = (TextView) findViewById(R.id.tvPinCode);
                            TextView tvZipPerm = (TextView) findViewById(R.id.tvProfilePinPerm);
                            TextView tvEmail = (TextView) findViewById(R.id.tvProfileEmail);
                            TextView tvEmailOff = (TextView) findViewById(R.id.tvProfileOfficeEmail);
                            TextView tvOffLandline = (TextView) findViewById(R.id.tvProfileOfficeLandlineNumber);
                            TextView tvEdu = (TextView) findViewById(R.id.tvProfileEducation);
                            TextView tvAccomm = (TextView) findViewById(R.id.tvProfileEmpAccomodation);
                            TextView tvPurpose = (TextView) findViewById(R.id.tvProfileNetPurpose);
                            TextView tvProfileWorkingSince = (TextView) findViewById(R.id.tvProfileWorkingSince);
                            TextView tvBank = (TextView) findViewById(R.id.tvProfileBank);

                            tvProfileWorkingSince.setText(custDOJ);
                            if (custEduType.equalsIgnoreCase("1")) tvEdu.setText("Undergraduate");
                            if (custEduType.equalsIgnoreCase("2")) tvEdu.setText("Graduate");
                            if (custEduType.equalsIgnoreCase("3")) tvEdu.setText("Postgraduate");
                            if (custHouseType.equalsIgnoreCase("1")) tvAccomm.setText("PG");
                            if (custHouseType.equalsIgnoreCase("2")) tvAccomm.setText("Rent");
                            if (custHouseType.equalsIgnoreCase("3")) tvAccomm.setText("Own");

                            if (custPurpose.equalsIgnoreCase("1"))
                                tvPurpose.setText("Rent/Deposit");
                            if (custPurpose.equalsIgnoreCase("2")) tvPurpose.setText("Medical");
                            if (custPurpose.equalsIgnoreCase("3")) tvPurpose.setText("Travel");
                            if (custPurpose.equalsIgnoreCase("4")) tvPurpose.setText("Personal");

                            tvZip.setText(custZip);
                            if (sameAsPerm.toString().equalsIgnoreCase("true"))
                                tvZipPerm.setText(custZip);
                            else tvZipPerm.setText(custZipPerm);

                            TextView tvCellNum = (TextView) findViewById(R.id.tvProfileCellNumber);
                            TextView tvDOB = (TextView) findViewById(R.id.tvDOB);
                            TextView tvEmp = (TextView) findViewById(R.id.tvProfileEmpName);
                            TextView tvEmp2 = (TextView) findViewById(R.id.tvProfileEmpName2);
                            TextView tvLandline = (TextView) findViewById(R.id.tvLandline);
                            TextView tvProfileLandline = (TextView) findViewById(R.id.tvProfileLandlineNumber);
                            TextView tvJoin = (TextView) findViewById(R.id.tvDOJ);
                            TextView tvNetSal = (TextView) findViewById(R.id.tvProfileNetSal);
                            TextView tvPANView = (TextView) findViewById(R.id.tvProfilePAN);
                            TextView tvProfileDOB = (TextView) findViewById(R.id.tvProfileDOB);
                            EditText etEmailOff = (EditText) findViewById(R.id.etEmailOffice);
                            tvPANView.setText(custPAN);

                            tvName.setText(custName);
                            tvAddr.setText(custAddr);

                            if (sameAsPerm.toString().equalsIgnoreCase("true"))
                                tvAddrPerm.setText(custAddr);
                            else tvAddrPerm.setText(custAddrPerm);

                            tvCellNum.setText(custCell);
                            tvEmail.setText(custEmailPers);
                            tvEmailOff.setText(custEmail);
                            etEmailOff.setText(custEmail);
                            tvDOB.setText(custDOB);
                            tvProfileDOB.setText(custDOB);
                            tvEmp.setText(custCompanyName);
                            tvEmp2.setText(custCompanyName);
                            tvLandline.setText(custLandline);
                            tvOffLandline.setText(custCompanyLandline);
                            tvProfileLandline.setText(custLandline);
                            tvJoin.setText(custDOJ);
                            tvNetSal.setText(custSal);

                            AutoCompleteTextView actvCompany = (AutoCompleteTextView) findViewById(R.id.actvEmpName);
                            AutoCompleteTextView actvCompany2 = (AutoCompleteTextView) findViewById(R.id.actvEmpName2);
                            actvCompany.setText(custCompanyName);
                            actvCompany2.setText(custCompanyName);

                            etName.setText(custName);
                            etEmail.setText(custEmailPers);
                            etAddr.setText(custAddr);
                            if (sameAsPerm.toString().equalsIgnoreCase("true")) {
                                etAddrPerm.setText(custAddr);
                                etPinCode.setText(custZip);
                            } else {
                                etAddrPerm.setText(custAddrPerm);
                                etPinCode.setText(custZipPerm);

                            }
                            etDOB.setText(custDOB);
                            etDOJ.setText(custDOJ);
                            etMobile.setText(custCell);
                            etLandline.setText(custLandline);
                            etNetSal.setText(custSal);
                            etNetSalSpouse.setText(custSalSpouse);

                            ImageRequest request = new ImageRequest(urlCustPicURL + "/" + custID + "/profile",
                                    new Response.Listener<Bitmap>() {
                                        @Override
                                        public void onResponse(Bitmap bitmap) {
                                            try {
                                                ctSelfie = (CircTN) findViewById(R.id.selfie);
                                                mImageBitmapTN = ThumbnailUtils.extractThumbnail(bitmap, 128, 128);
                                                if (mImageBitmapTN != null)
                                                    ctSelfie.setImageBitmap(mImageBitmapTN);
                                            } catch (Exception e) {
                                            }
                                        }
                                    }, 0, 0, null,
                                    new Response.ErrorListener() {
                                        public void onErrorResponse(VolleyError error) {
                                            try {
                                                ctSelfie = (CircTN) findViewById(R.id.selfie);
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
                            rQueue.getInstance(EditProfile.this).queueRequest("", request);

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
                            Toast.makeText(EditProfile.this, "No Internet access. Please check your connection.", Toast.LENGTH_LONG).show();
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

        StringRequest reqBnk = new StringRequest(com.android.volley.Request.Method.GET, urlBankDetailsGet,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("IPB", response.toString());
                        try {
                            JSONObject jsonDet = new JSONObject(response.toString());
                            usrBank = new JSONObject(jsonDet.getString("entity"));
                            JSONObject jsonEntity = new JSONObject(jsonDet.getString("entity"));
                            String bankName = jsonEntity.getString("bankName");
                            String ifsc = jsonEntity.getString("ifsc");
                            String accountNumber = jsonEntity.getString("accountNumber");
                            TextView tvBank = (TextView) findViewById(R.id.tvBank);
                            TextView tvAccNum = (TextView) findViewById(R.id.tvAccNum);
                            TextView tvIFSC = (TextView) findViewById(R.id.tvIFSC);

                            tvBank.setText(bankName.toString());
                            tvAccNum.setText(accountNumber.toString());
                            tvIFSC.setText(ifsc.toString());
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
                            Toast.makeText(EditProfile.this, "No Internet access. Please check your connection.", Toast.LENGTH_LONG).show();
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
        reqBnk.setShouldCache(false);
        rQueue.getInstance(this).queueRequest("", reqBnk);


        StringRequest reqBanks = new StringRequest(com.android.volley.Request.Method.GET, urlBanks,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonBanks = new JSONObject(response);
                            JSONArray arrBanks = jsonBanks.getJSONArray("entity");
                            for (int i = 0; i < arrBanks.length(); i++) {
                                String bankName = arrBanks.getJSONObject(i).getString("bankName");
                                String bankID = arrBanks.getJSONObject(i).getString("bankId");
                                listBanks.add(new ListBanks(bankID, bankName));
                            }
                            AutoCompleteTextView actvBank = (AutoCompleteTextView) findViewById(R.id.actvBank);
                            actvBank.setAdapter(new bankSuggest(EditProfile.this, actvBank.getText().toString(), listBanks));
                            actvBank.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    bankPosition = position;
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
                        if (error instanceof NoConnectionError) {
                            Toast.makeText(EditProfile.this, "No Internet access. Please check your connection.", Toast.LENGTH_LONG).show();
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

        StringRequest reqCustBank = new StringRequest(com.android.volley.Request.Method.GET, urlCustBank,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("IPB", response.toString());
                        try {
                            JSONObject jsonCust = new JSONObject(response.toString());
                            JSONObject jsonEntity = new JSONObject(jsonCust.getString("entity"));
                            bankName = jsonEntity.getString("bankName");
                            ifsc = jsonEntity.getString("ifsc");
                            accNum = jsonEntity.getString("accountNumber");
                            AutoCompleteTextView actvBank = (AutoCompleteTextView) findViewById(R.id.actvBank);
                            EditText etIFSC = (EditText) findViewById(R.id.etIFSC);
                            final EditText etAccNum = (EditText) findViewById(R.id.etAccNum);
                            final View rowLblConfirmAccNum = findViewById(R.id.rowLblConfirmAccNum);
                            final View rowEtConfirmAccNum = findViewById(R.id.rowEtConfirmAccNum);
                            actvBank.setText(bankName);
                            etIFSC.setText(ifsc);
                            etAccNum.setText(accNum);
                            etAccNum.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                @Override
                                public void onFocusChange(View v, boolean hasFocus) {
                                    if (!hasFocus) {
                                        if (accNum.equals(etAccNum.getText().toString())) {
                                            etAccNum.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
                                            rowLblConfirmAccNum.setVisibility(View.GONE);
                                            rowEtConfirmAccNum.setVisibility(View.GONE);
                                        } else {
                                            etAccNum.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                            rowLblConfirmAccNum.setVisibility(View.VISIBLE);
                                            rowEtConfirmAccNum.setVisibility(View.VISIBLE);
                                        }
                                    }
                                }
                            });
                            etAccNum.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                }

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                }

                                @Override
                                public void afterTextChanged(Editable s) {
                                    if (accNum.equals(etAccNum.getText().toString())) {
                                        etAccNum.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
                                        etAccNum.setSelection(etAccNum.getText().length());
                                        rowLblConfirmAccNum.setVisibility(View.GONE);
                                        rowEtConfirmAccNum.setVisibility(View.GONE);
                                    } else {
                                        rowLblConfirmAccNum.setVisibility(View.VISIBLE);
                                        rowEtConfirmAccNum.setVisibility(View.VISIBLE);
                                    }
                                }
                            });
                            TextView tvBank = (TextView) findViewById(R.id.tvProfileBank);
                            TextView tvAccNum = (TextView) findViewById(R.id.tvProfileOfficeACNumber);
                            TextView tvIFSC = (TextView) findViewById(R.id.tvProfileIFSC);
                            tvBank.setText(bankName);
                            tvIFSC.setText(ifsc);
                            tvAccNum.setText(accNum);

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
                            Toast.makeText(EditProfile.this, "No Internet access. Please check your connection.", Toast.LENGTH_LONG).show();
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
        reqCustBank.setShouldCache(false);
        rQueue.getInstance(this).queueRequest("", reqCustBank);


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
                Intent intent = new Intent();
                intent.setType("image/jpeg");
                if (Build.VERSION.SDK_INT < 19) intent.setAction(Intent.ACTION_GET_CONTENT);
                else {
                    intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                }
                startActivityForResult(Intent.createChooser(intent, "Select Address Proof"), REQUEST_ADDR_PROOF);
            }
        });

        ImageButton ibAtt2 = (ImageButton) findViewById(R.id.ibAtt2);
        ibAtt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/jpeg");
                if (Build.VERSION.SDK_INT < 19) intent.setAction(Intent.ACTION_GET_CONTENT);
                else {
                    intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                }
                startActivityForResult(Intent.createChooser(intent, "Select Salary Slip"), REQUEST_SAL_SLIP);
            }
        });

        ImageButton ibAtt3 = (ImageButton) findViewById(R.id.ibAtt3);
        ibAtt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/jpeg");
                if (Build.VERSION.SDK_INT < 19) intent.setAction(Intent.ACTION_GET_CONTENT);
                else {
                    intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                }
                startActivityForResult(Intent.createChooser(intent, "Select PAN Card"), REQUEST_PAN);
            }
        });

        ImageButton ibAtt4 = (ImageButton) findViewById(R.id.ibAtt4);
        ibAtt4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/jpeg");
                if (Build.VERSION.SDK_INT < 19) intent.setAction(Intent.ACTION_GET_CONTENT);
                else {
                    intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                }
                startActivityForResult(Intent.createChooser(intent, "Select Bank Statement"), REQUEST_BANK_STMT_PROOF);
            }
        });

        ctSelfie = (CircTN) findViewById(R.id.selfie);
        setSelfieListenerOrDisable(
                ctSelfie,
                mTakePicOnClickListener,
                MediaStore.ACTION_IMAGE_CAPTURE
        );

        ImageButton btnEdit = (ImageButton) findViewById(R.id.ivEdit);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView caption = (TextView) findViewById(R.id.caption);
                caption.setText("EDIT PROFILE");
                ImageButton btnEdit = (ImageButton) findViewById(R.id.ivEdit);
                btnEdit.setVisibility(View.GONE);
                ImageButton btnProceed = (ImageButton) findViewById(R.id.ibProceed);
                btnProceed.setVisibility(View.VISIBLE);
                TableLayout tblPers = (TableLayout) findViewById(R.id.tblSignUpPersView);
                TableLayout tblPersEdt = (TableLayout) findViewById(R.id.tblSignUpPers);
                TableLayout tblBank = (TableLayout) findViewById(R.id.tblBankView);
                //LinearLayout llBank = (LinearLayout)findViewById(R.id.llBankDetails);
                tblPers.setVisibility(View.GONE);
                tblPersEdt.setVisibility(View.VISIBLE);
                tblBank.setVisibility(View.GONE);
                //llBank.setVisibility(View.VISIBLE);
            }
        });

        ImageButton btnSubmit = (ImageButton) findViewById(R.id.ibSubmit);

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
            setPic();
            galleryAddPic();
            //mCurrentPhotoPath = null;
        }

    }

    private void setPic() {
        try {
            int targetW = 400;
            int targetH = 400;

            Bitmap bitmap = decodeSampledBitmapFromPath(mCurrentPhotoPath, targetW, targetH);

            if (bitmap != null) {
        /* Associate the Bitmap to the ImageView */
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ctSelfie.setBackground(null);
                }
                mImageBitmapTN = ThumbnailUtils.extractThumbnail(bitmap, 128, 128);
                if (mImageBitmapTN != null) {
                    ctSelfie.setScaleType(ImageView.ScaleType.CENTER);
                    ctSelfie.setImageBitmap(mImageBitmapTN);
                }

                File f = new File(mCurrentPhotoPath);
                if (f != null) uriSelfie = Uri.fromFile(f);
                mImageBitmap = bitmap;
            }
        } catch (Exception e) {
        }
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
            ImageButton btnUpdate = (ImageButton) findViewById(R.id.btnUpdate);
            btnUpdate.setVisibility(View.VISIBLE);

        }

        if (requestCode == 1 && resultCode == RESULT_CANCELED) {
            mCurrentFileName = null;
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
    }

    // Some lifecycle callbacks so that the image can survive orientation change
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
            outState.putString("sameAsPerm", sameAsPerm);
            if (usrEntity != null)
                outState.putString("usrEntity", usrEntity.toString());
            if (usrBank != null)
                outState.putString("usrBank", usrBank.toString());
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
        } catch (Exception e) {
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        try {
            company_ids = savedInstanceState.getStringArrayList("company_ids");
            company_names = savedInstanceState.getStringArrayList("company_names");
            mCurrentPhotoPath = savedInstanceState.getString("mCurrentPhotoPath");
            mCurrentFileName = savedInstanceState.getString("mCurrentFileName");
            companyPosition = savedInstanceState.getInt("companyPosition");
            bankPosition = savedInstanceState.getInt("bankPosition");

            try {
                handleBigCameraPhoto();
            } catch (Exception e) {
            }
            sameAsPerm = savedInstanceState.getString("sameAsPerm");
            try {
                String jsonString = savedInstanceState.getString("usrEntity");
                usrEntity = new JSONObject(jsonString);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                String jsonString = savedInstanceState.getString("usrBank");
                usrBank = new JSONObject(jsonString);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (savedInstanceState.containsKey("uriSelfie")) {
                uriSelfie = Uri.parse(savedInstanceState.getString("uriSelfie"));
            }
            if (savedInstanceState.containsKey("uriAddrProof")) {
                uriAddrProof = Uri.parse(savedInstanceState.getString("uriAddrProof"));
            }
            if (savedInstanceState.containsKey("uriSalSlip")) {
                uriSalSlip = Uri.parse(savedInstanceState.getString("uriSalSlip"));
            }
            if (savedInstanceState.containsKey("uriPAN")) {
                uriPAN = Uri.parse(savedInstanceState.getString("uriPAN"));
            }
            if (savedInstanceState.containsKey("uriBankStmt")) {
                uriBankStmt = Uri.parse(savedInstanceState.getString("uriBankStmt"));
            }
        } catch (Exception e) {
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
            showDate(dvDOB, arg1, arg2 + 1, arg3); //year/month/day
        }
    };

    private DatePickerDialog.OnDateSetListener lsnrDOJ = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            showDate(dvDOJ, arg1, arg2 + 1, arg3); //year/month/day
        }
    };

    private void showDate(EditText dv, int year, int month, int day) {
        dv.setText(new StringBuilder().append(day).append("-")
                .append(month).append("-").append(year));
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

    public Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
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
        final Handler mHandler = new Handler(Looper.getMainLooper());
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        if (bmp == null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mCurrentFileName = null;
                    ctSelfie = (CircTN) findViewById(R.id.selfie);
                    ctSelfie.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.drawable.selfie_error, 128, 128));
                    setSelfieListenerOrDisable(
                            ctSelfie,
                            mTakePicOnClickListener,
                            MediaStore.ACTION_IMAGE_CAPTURE
                    );
                    ImageButton btn = (ImageButton) findViewById(R.id.ibSubmit);
                    btn.setEnabled(true);
                }
            });
            return null;
        }


        try {

            //    ByteArrayOutputStream stream = new ByteArrayOutputStream();
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
                    .addFormDataPart("filename", mCurrentFileName,
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

    class asyncReq extends AsyncTask<String, Integer, String> {

        boolean running;

        @Override
        protected String doInBackground(String... params) {
            String response = "";
            if (mCurrentFileName != null && !mCurrentFileName.isEmpty()) {
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("EditProfile")
                        .setLabel("UploadSelfie")
                        .build());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (pd != null && pd.isShowing()) pd.setMessage("Uploading selfie...");
                    }
                });
                response = postSelfie(urlCustPic, mImageBitmap, accessToken);
            }
            if (mCurrentAddrProof != null && !mCurrentAddrProof.isEmpty()) {
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("EditProfile")
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
                        .setAction("EditProfile")
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
                        .setAction("EditProfile")
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
            if (mCurrentBankStmt != null && !mCurrentBankStmt.isEmpty()) {
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("EditProfile")
                        .setLabel("UploadBankStatement")
                        .build());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (pd != null && pd.isShowing())
                            pd.setMessage("Uploading bank statement...");
                    }
                });
                response = postRequest(urlCustStmt, uriBankStmt, "bank_statement.jpg", accessToken);
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
                pd = ProgressDialog.show(EditProfile.this, "CASHe", "Uploading attachments. Please wait!");

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
            StringRequest reqAuth = new StringRequest(com.android.volley.Request.Method.GET, urlStatus,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonStatus = new JSONObject(response.toString());
                                String statusType = jsonStatus.getString("statusType");
                                if (statusType.equalsIgnoreCase("OK")) {
                                    if (pd != null && pd.isShowing()) pd.dismiss();
                                    Toast.makeText(EditProfile.this, "Profile updated successfully.", Toast.LENGTH_SHORT).show();
                                    modBank = false;
                                    modPersonal = false;
                                    modEssential = false;
                                    modProfessional = false;
                                    Base.customerStatusName = "";
                                    JSONObject jsonEntity = new JSONObject(jsonStatus.getString("entity"));
                                    String customerStatusName = jsonEntity.getString("customerStatusName");
                                    Intent i;
                                    if (customerStatusName.equalsIgnoreCase("Data Received P3")) {
                                        i = new Intent(EditProfile.this, Dashboard.class);
                                    } else {
                                        i = new Intent(EditProfile.this, Verification.class);
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
                                } else {
                                    Toast.makeText(EditProfile.this, "An error occurred while updating your profile. Please try again later.", Toast.LENGTH_LONG).show();
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
                                Toast.makeText(EditProfile.this, "No Internet access. Please check your connection.", Toast.LENGTH_LONG).show();
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
            rQueue.getInstance(EditProfile.this).queueRequest("EditProfile", reqAuth);
        }
    }

    private boolean validFormEssential() {
        boolean valid = true;
        EditText etName = (EditText) findViewById(R.id.etFullName);
        EditText etEmail = (EditText) findViewById(R.id.etEmail);
        EditText etCell = (EditText) findViewById(R.id.etCellNumber);
        EditText etAddress = (EditText) findViewById(R.id.etAddress);
        EditText etSal = (EditText) findViewById(R.id.etNetSal);


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

        if (etCell.getText().toString().isEmpty() || !Patterns.PHONE.matcher(etCell.getText().toString()).matches()) {
            etCell.setError("Cell number is invalid");
            if (valid) etCell.requestFocus();
            valid = false;
        }
        if (!etCell.getText().toString().trim().isEmpty()) {
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


        if (etEmail.getText().toString().trim().isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString().trim()).matches()) {
            etEmail.setError("Email is invalid or empty.");
            if (valid) etEmail.requestFocus();
            valid = false;
        }

        String strPAN = etPAN.getText().toString();
        Pattern pattern = Pattern.compile("[A-Z|a-z]{5}[0-9]{4}[A-Z|a-z]{1}");

        Matcher matcher = pattern.matcher(strPAN);
        if (!matcher.matches()) {
            etPAN.setError("Provide a valid PAN.");
            if (valid) etPAN.requestFocus();
            valid = false;
        }

        if (etAddress.getText().toString().isEmpty()) {
            etAddress.setError("Postal address is invalid.");
            if (valid) etAddress.requestFocus();
            valid = false;
        }

        AutoCompleteTextView empName = (AutoCompleteTextView) findViewById(R.id.actvEmpName);
        if (empName.getText().toString().isEmpty()) {
            empName.setError("Please provide your employer name.");
            if (valid) empName.requestFocus();
            valid = false;
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
                etSal.setError("Your monthly salary needs to be at least \u20B915000.");
                if (valid) etSal.requestFocus();
                valid = false;
            }
        }


        return valid;
    }

    private boolean validFormPersonal() {
        boolean valid = true;
        EditText etAddress = (EditText) findViewById(R.id.etAddressPerm);
        EditText etLandLine = (EditText) findViewById(R.id.etLandline);

        CheckBox chkSame = (CheckBox) findViewById(R.id.chkAddrSame);

        if (!chkSame.isChecked()) {
            if (etAddress.getText().toString().trim().isEmpty()) {
                etAddress.setError("Postal address is invalid.");
                if (valid) etAddress.requestFocus();
                valid = false;
            }
        }

        if (!etLandLine.getText().toString().trim().isEmpty()) {
            String landlineStart = etLandLine.getText().toString().substring(0, 1);
            if (landlineStart.equalsIgnoreCase("0")) {
                etLandLine.setError("Provide valid landline number.");
                if (valid) etLandLine.requestFocus();
                valid = false;
            }

        }

        if (etLandLine.getText().toString().trim().isEmpty()) {

            etLandLine.setError("Provide valid landline number.");
            if (valid) etLandLine.requestFocus();
            valid = false;
        }

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

    private boolean validFormProfessional() {
        boolean valid = true;

       /* AutoCompleteTextView empName = (AutoCompleteTextView) findViewById(R.id.actvEmpName2);
        if(empName.getText().toString().isEmpty())
        {
            empName.setError("Please provide your employer name.");
            if(valid) empName.requestFocus();
            valid = false;
        }*/

        EditText etEmpLandLine = (EditText) findViewById(R.id.etEmpLandline);

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

        EditText etEmail = (EditText) findViewById(R.id.etEmailOffice);
        if (etEmail.getText().toString().trim().isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString()).matches()) {
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
        } catch (ParseException e) {
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

    private boolean validFormBank() {
        boolean valid = true;
        AutoCompleteTextView actvBank = (AutoCompleteTextView) findViewById(R.id.actvBank);
        if (actvBank.getText().toString().isEmpty() || actvBank.getText().toString().length() < 5) {
            actvBank.setError("Please provide your bank");
            actvBank.requestFocus();
            valid = false;
        }

        EditText etAccNum = (EditText) findViewById(R.id.etAccNum);
        EditText etConfirmAccNum = (EditText)findViewById(R.id.etConfirmAccNum);
        View rowEtConfirmAccNum = findViewById(R.id.rowEtConfirmAccNum);
        if(!chkstr(etAccNum.getText().toString().trim()) ||
                etAccNum.getText().toString().trim().isEmpty() ||
                etAccNum.getText().toString().trim().length() < 5)
        {
            etAccNum.requestFocus();
            etAccNum.setError("Please provide a valid account number");
            valid = false;
        }
        else if(rowEtConfirmAccNum.getVisibility() == View.VISIBLE && !etConfirmAccNum.getText().toString().equals(etAccNum.getText().toString()))
        {
            etConfirmAccNum.requestFocus();
            etConfirmAccNum.setError("Account Number do not match");
            valid = false;
        }

        EditText etIFSC = (EditText) findViewById(R.id.etIFSC);
        if (etIFSC.getText().toString().isEmpty() || etIFSC.getText().toString().length() < 11 || !etIFSC.getText().toString().matches("[A-Z|a-z]{4}[0][A-Z|a-z|\\d]{6}$")) {
            etIFSC.setError("Please provide a valid IFSC code");
            etIFSC.requestFocus();
            valid = false;
        }
        /*StringRequest reqIFSC = new StringRequest(com.android.volley.Request.Method.GET, urlIFSC+"/"+ etIFSC.getText().toString(),
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
                            Toast.makeText(EditProfile.this, "No Internet access. Please check your connection.",Toast.LENGTH_LONG).show();
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
        rQueue.getInstance(this).queueRequest("",reqIFSC);*/
        return valid;
    }

    protected void setEditMode(ViewGroup root, boolean canEdit) {
        for (int i = 0; i < root.getChildCount(); i++) {
            View v = root.getChildAt(i);
            if (v instanceof EditText) {
                ((EditText) v).setEnabled(canEdit);
            }
        }

    }

    private class asyncImg extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public asyncImg(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
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

    private void fetchCompanies(String pfx, AutoCompleteTextView actvCompany2) {
        String input = "";
        final AutoCompleteTextView actvCompanyid = actvCompany2;

        StringRequest reqCompanies = new StringRequest(com.android.volley.Request.Method.GET, urlCompanies + pfx,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d("IPB", response.toString());
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
                                //AutoCompleteTextView actvCompany2 = (AutoCompleteTextView) findViewById(R.id.actvEmpName2);
                                actvCompanyid.setAdapter(adaptr);
                                adaptr.notifyDataSetChanged();
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
                            Toast.makeText(EditProfile.this, "No Internet access. Please check your connection.", Toast.LENGTH_LONG).show();
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

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }
        return inSampleSize;
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


    @Override
    public void onBackPressed() {
        backPressed();
    }

    private boolean donePressed() {
        if (currentStep == "edit_professional") {
            stateProfessional();
            //goBack(true, R.id.professionalDetailEdit, R.id.professionalDetail, "professional", "PROFILE");
            goBack(true, R.id.professionalDetailEdit, R.id.profileHome, "profile_home", "PROFILE");
        } else if (currentStep == "edit_personal") {
            statePersonal();
            //goBack(true, R.id.personalDetailEdit, R.id.personalDetail, "personal", "PROFILE");
            goBack(true, R.id.personalDetailEdit, R.id.profileHome, "profile_home", "PROFILE");
        } else if (currentStep == "edit_bank") {

            final EditText etIFSC = (EditText) findViewById(R.id.etIFSC);
            StringRequest reqIFSC = new StringRequest(com.android.volley.Request.Method.GET, urlIFSC + etIFSC.getText().toString(),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                String res = response;
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
                                            stateBank();
                                            //goBack(true, R.id.bankDetailEdit, R.id.bankDetail, "bank", "PROFILE");
                                            goBack(true, R.id.bankDetailEdit, R.id.profileHome, "profile_home", "PROFILE");
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
                                Toast.makeText(EditProfile.this, "No Internet access. Please check your connection.", Toast.LENGTH_LONG).show();
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

        } else if (currentStep == "edit_essential") {
            stateEssential();

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
                                    else
                                        goBack(true, R.id.essentialDetailEdit, R.id.profileHome, "profile_home", "PROFILE");
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
                                Toast.makeText(EditProfile.this, "No Internet access. Please check your connection.", Toast.LENGTH_LONG).show();
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


            //goBack(true, R.id.essentialDetailEdit, R.id.essentialDetail, "essential", "PROFILE");

        }
        return true;
    }


    private boolean backPressed() {
        if (currentStep == "professional") {
            goBack(true, R.id.professionalDetail, R.id.profileHome, "profile_home", "PROFILE");
        } else if (currentStep == "personal") {
            goBack(true, R.id.personalDetail, R.id.profileHome, "profile_home", "PROFILE");
        } else if (currentStep == "bank") {
            goBack(true, R.id.bankDetail, R.id.profileHome, "profile_home", "PROFILE");
        } else if (currentStep == "essential") {
            goBack(true, R.id.essentialDetail, R.id.profileHome, "profile_home", "PROFILE");
        } else if (currentStep == "profile_home") {
            if (modEssential || modProfessional || modPersonal || modBank || mCurrentFileName != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setCancelable(true);
                builder.setMessage("You have unsaved changes.");
                builder.setPositiveButton("Keep", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ImageButton btnUpdate = (ImageButton) findViewById(R.id.btnUpdate);
                        btnUpdate.callOnClick();
                    }
                });
                builder.setNegativeButton("Discard", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            } else
                finish();
        } else if (currentStep == "edit_professional") {
            stateProfessional();

            if (modProfessional) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setCancelable(true);
                builder.setMessage("You have unsaved changes.");
                builder.setPositiveButton("Keep", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!validFormProfessional()) return;
                        goBack(true, R.id.professionalDetailEdit, R.id.profileHome, "profile_home", "PROFILE");
                    }
                });
                builder.setNegativeButton("Discard", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AutoCompleteTextView actvCompany = (AutoCompleteTextView) findViewById(R.id.actvEmpName);
                        AutoCompleteTextView actvCompany2 = (AutoCompleteTextView) findViewById(R.id.actvEmpName2);
                        EditText etEmpLandline = (EditText) findViewById(R.id.etEmpLandline);
                        EditText etEmailOff = (EditText) findViewById(R.id.etEmailOffice);
                        MaterialSpinner spnrWorkSinceMonth = (MaterialSpinner) findViewById(R.id.spnrWorkingSinceMonth);
                        MaterialSpinner spnrWorkSinceYear = (MaterialSpinner) findViewById(R.id.spnrWorkingSinceYear);
                        actvCompany2.setText(custCompanyName);
                        etEmpLandline.setText(custCompanyLandline);
                        etEmailOff.setText(custEmail);
                        if (!usrEntity.isNull("doj")) {
                            String tmpDate = null;
                            try {
                                tmpDate = usrEntity.getString("doj");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Date date = null;
                            try {
                                date = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(tmpDate);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            try {
                                MaterialSpinner spnrDOJMonth = (MaterialSpinner) findViewById(R.id.spnrWorkingSinceMonth);
                                MaterialSpinner spnrDOJYear = (MaterialSpinner) findViewById(R.id.spnrWorkingSinceYear);
                                selectSpinnerItemByValue(spnrDOJMonth, new SimpleDateFormat("MMM", Locale.ENGLISH).format(date));
                                selectSpinnerItemByValue(spnrDOJYear, new SimpleDateFormat("yyyy", Locale.ENGLISH).format(date));
                            } catch (Exception e) {
                            }
                        }
                        modProfessional = false;
                        goBack(true, R.id.professionalDetailEdit, R.id.profileHome, "profile_home", "PROFILE");
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            } else
                goBack(true, R.id.professionalDetailEdit, R.id.profileHome, "profile_home", "PROFILE");
        } else if (currentStep == "edit_personal") {
            statePersonal();

            if (modPersonal) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setCancelable(true);
                builder.setMessage("You have unsaved changes.");
                builder.setPositiveButton("Keep", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!validFormPersonal()) return;
                        goBack(true, R.id.personalDetailEdit, R.id.profileHome, "profile_home", "PROFILE");
                    }
                });
                builder.setNegativeButton("Discard", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText etAddrPerm = (EditText) findViewById(R.id.etAddressPerm);
                        EditText etPinCode = (EditText) findViewById(R.id.etPinCode);
                        EditText etLandline = (EditText) findViewById(R.id.etLandline);
                        MaterialSpinner spnrDOBDay = (MaterialSpinner) findViewById(R.id.spnrDOBDay);
                        MaterialSpinner spnrDOBMonth = (MaterialSpinner) findViewById(R.id.spnrDOBMonth);
                        MaterialSpinner spnrDOBYear = (MaterialSpinner) findViewById(R.id.spnrDOBYear);
                        MaterialSpinner spnrQual = (MaterialSpinner) findViewById(R.id.spnrQual);
                        MaterialSpinner spnrHouse = (MaterialSpinner) findViewById(R.id.spnrHouse);
                        MaterialSpinner spnrPurpose = (MaterialSpinner) findViewById(R.id.spnrPurpose);
                        if (sameAsPerm.toString().equalsIgnoreCase("true")) {
                            etAddrPerm.setText(custAddr);
                            etPinCode.setText(custZip);
                        } else {
                            etAddrPerm.setText(custAddrPerm);
                            etPinCode.setText(custZipPerm);
                        }
                        etLandline.setText(custLandline);
                        if (!usrEntity.isNull("dob")) {
                            String tmpDate = null;
                            try {
                                tmpDate = usrEntity.getString("dob");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Date date = null;
                            try {
                                date = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(tmpDate);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            try {
                                selectSpinnerItemByValue(spnrDOBDay, new SimpleDateFormat("dd", Locale.ENGLISH).format(date));
                                selectSpinnerItemByValue(spnrDOBMonth, new SimpleDateFormat("MMM", Locale.ENGLISH).format(date));
                                selectSpinnerItemByValue(spnrDOBYear, new SimpleDateFormat("yyyy", Locale.ENGLISH).format(date));
                            } catch (Exception e) {
                            }
                        }

                        String tmp = "";

                        tmp = custEduType;
                        if (tmp.equalsIgnoreCase("1")) spnrQual.setSelection(1);
                        if (tmp.equalsIgnoreCase("2")) spnrQual.setSelection(2);
                        if (tmp.equalsIgnoreCase("3")) spnrQual.setSelection(3);

                        tmp = custHouseType;
                        if (tmp.equalsIgnoreCase("1")) spnrHouse.setSelection(1);
                        if (tmp.equalsIgnoreCase("2")) spnrHouse.setSelection(2);
                        if (tmp.equalsIgnoreCase("3")) spnrHouse.setSelection(3);

                        tmp = custPurpose;
                        if (tmp.equalsIgnoreCase("1")) spnrPurpose.setSelection(1);
                        if (tmp.equalsIgnoreCase("2")) spnrPurpose.setSelection(2);
                        if (tmp.equalsIgnoreCase("3")) spnrPurpose.setSelection(3);
                        if (tmp.equalsIgnoreCase("4")) spnrPurpose.setSelection(4);
                        modPersonal = false;
                        goBack(true, R.id.personalDetailEdit, R.id.profileHome, "profile_home", "PROFILE");
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            } else
                goBack(true, R.id.personalDetailEdit, R.id.profileHome, "profile_home", "PROFILE");
        } else if (currentStep == "edit_bank") {
            stateBank();

            if (modBank) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setCancelable(true);
                builder.setMessage("You have unsaved changes.");
                builder.setPositiveButton("Keep", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!validFormBank()) return;
                        final EditText etIFSC = (EditText) findViewById(R.id.etIFSC);
                        StringRequest reqIFSC = new StringRequest(com.android.volley.Request.Method.GET, urlIFSC + etIFSC.getText().toString(),
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            String res = response;
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
                                                        goBack(true, R.id.bankDetailEdit, R.id.profileHome, "profile_home", "PROFILE");
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
                                            Toast.makeText(EditProfile.this, "No Internet access. Please check your connection.", Toast.LENGTH_LONG).show();
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
                        rQueue.getInstance(EditProfile.this).queueRequest("", reqIFSC);


                    }
                });
                builder.setNegativeButton("Discard", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AutoCompleteTextView actvBank = (AutoCompleteTextView) findViewById(R.id.actvBank);
                        EditText etAccNum = (EditText) findViewById(R.id.etAccNum);
                        EditText etConfirmAccNum = (EditText) findViewById(R.id.etConfirmAccNum);
                        EditText etIFSC = (EditText) findViewById(R.id.etIFSC);
                        try {
                            actvBank.setText(usrBank.getString("bankName").toString());
                            etAccNum.setText(usrBank.get("accountNumber").toString());
                            etIFSC.setText(usrBank.getString("ifsc").toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        etAccNum.setError(null);
                        etAccNum.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
                        etConfirmAccNum.setError(null);
                        etConfirmAccNum.setText("");
                        View rowLblConfirmAccNum = findViewById(R.id.rowLblConfirmAccNum);
                        View rowEtConfirmAccNum = findViewById(R.id.rowEtConfirmAccNum);
                        rowLblConfirmAccNum.setVisibility(View.GONE);
                        rowEtConfirmAccNum.setVisibility(View.GONE);
                        modBank = false;
                        goBack(true, R.id.bankDetailEdit, R.id.profileHome, "profile_home", "PROFILE");
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            } else
                goBack(true, R.id.bankDetailEdit, R.id.profileHome, "profile_home", "PROFILE");
        } else if (currentStep == "edit_essential") {
            stateEssential();

            if (modEssential) {

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setCancelable(true);
                builder.setMessage("You have unsaved changes.");
                builder.setPositiveButton("Keep", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!validFormEssential()) return;
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
                                                if (statusPAN)
                                                    etPAN.setError("PAN card is already registered");
                                                else
                                                    goBack(true, R.id.essentialDetailEdit, R.id.profileHome, "profile_home", "PROFILE");
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
                                            Toast.makeText(EditProfile.this, "No Internet access. Please check your connection.", Toast.LENGTH_LONG).show();
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
                        rQueue.getInstance(EditProfile.this).queueRequest("", reqPAN);


                    }
                });
                builder.setNegativeButton("Discard", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText etName = (EditText) findViewById(R.id.etFullName);
                        EditText etEmail = (EditText) findViewById(R.id.etEmail);
                        EditText etMobile = (EditText) findViewById(R.id.etCellNumber);
                        EditText etPAN = (EditText) findViewById(R.id.etPAN);
                        EditText etAddress = (EditText) findViewById(R.id.etAddress);
                        AutoCompleteTextView actvCompany = (AutoCompleteTextView) findViewById(R.id.actvEmpName);
                        EditText etNetSal = (EditText) findViewById(R.id.etNetSal);
                        etName.setText(custName);
                        etEmail.setText(custEmailPers);
                        etMobile.setText(custCell);
                        etPAN.setText(custPAN);
                        etAddress.setText(custAddr);
                        etNetSal.setText(custSal);
                        modEssential = false;
                        goBack(true, R.id.essentialDetailEdit, R.id.profileHome, "profile_home", "PROFILE");
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            } else
                goBack(true, R.id.essentialDetailEdit, R.id.profileHome, "profile_home", "PROFILE");
        }
        ImageButton btnUpdate = (ImageButton) findViewById(R.id.btnUpdate);
        if (modBank || modPersonal || modProfessional || modEssential || mCurrentFileName != null)
            btnUpdate.setVisibility(View.VISIBLE);
        else
            btnUpdate.setVisibility(View.GONE);
        return true;
    }

    public void editPersonal(View v) {
        goAhead(true, R.id.personalDetail, R.id.personalDetailEdit, "edit_personal", "EDIT PROFILE");

    }


    private void goAhead(boolean isBack, int from, final int to, final String current_step, final String captionText) {

        getSupportActionBar().setDisplayHomeAsUpEnabled(isBack);
        final LinearLayout detailLayout = (LinearLayout) findViewById(from);
        detailLayout.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.up_to_down_full));

        detailLayout.postDelayed(new Runnable() {
            public void run() {
                detailLayout.setVisibility(View.GONE);
                LinearLayout home = (LinearLayout) findViewById(to);
                home.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.up_to_down_half));
                home.setVisibility(View.VISIBLE);
                currentStep = current_step;
                TextView caption = (TextView) findViewById(R.id.caption);
                caption.setText(captionText);

            }
        }, 500);
    }

    public void editProfessional(View v) {
        goAhead(true, R.id.professionalDetail, R.id.professionalDetailEdit, "edit_professional", "EDIT PROFILE");

    }

    public void editBank(View v) {
        goAhead(true, R.id.bankDetail, R.id.bankDetailEdit, "edit_bank", "EDIT PROFILE");

    }


    public void editEssential(View v) {
        goAhead(true, R.id.essentialDetail, R.id.essentialDetailEdit, "edit_essential", "EDIT PROFILE");

    }

    public void goToPersonal(View v) {

        goAhead(true, R.id.profileHome, R.id.personalDetail, "personal", "PROFILE");

    }


    public void goToProfessional(View v) {
        goAhead(true, R.id.profileHome, R.id.professionalDetail, "professional", "PROFILE");

    }


    public void goToBank(View v) {
        goAhead(true, R.id.profileHome, R.id.bankDetail, "bank", "PROFILE");

    }


    public void goToEssential(View v) {
        goAhead(true, R.id.profileHome, R.id.essentialDetail, "essential", "PROFILE");

    }

    public void backToEssential(View v) {


        stateEssential();

        if (modEssential) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(true);
            builder.setMessage("You have unsaved changes.");
            builder.setPositiveButton("Keep", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (!validFormEssential()) return;
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
                                            if (statusPAN)
                                                etPAN.setError("PAN card is already registered");
                                            else
                                                goBack(true, R.id.essentialDetailEdit, R.id.profileHome, "profile_home", "PROFILE");

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
                                        Toast.makeText(EditProfile.this, "No Internet access. Please check your connection.", Toast.LENGTH_LONG).show();
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
                    rQueue.getInstance(EditProfile.this).queueRequest("", reqPAN);
                }
            });
            builder.setNegativeButton("Discard", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    EditText etName = (EditText) findViewById(R.id.etFullName);
                    EditText etEmail = (EditText) findViewById(R.id.etEmail);
                    EditText etMobile = (EditText) findViewById(R.id.etCellNumber);
                    EditText etPAN = (EditText) findViewById(R.id.etPAN);
                    EditText etAddress = (EditText) findViewById(R.id.etAddress);
                    AutoCompleteTextView actvCompany = (AutoCompleteTextView) findViewById(R.id.actvEmpName);
                    EditText etNetSal = (EditText) findViewById(R.id.etNetSal);
                    etName.setText(custName);
                    etEmail.setText(custEmailPers);
                    etMobile.setText(custCell);
                    etPAN.setText(custPAN);
                    etAddress.setText(custAddr);
                    etNetSal.setText(custSal);
                    modEssential = false;
                    goBack(true, R.id.essentialDetailEdit, R.id.profileHome, "profile_home", "PROFILE");
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        } else
            goBack(true, R.id.essentialDetailEdit, R.id.profileHome, "profile_home", "PROFILE");

        //goBack(true, R.id.essentialDetailEdit, R.id.essentialDetail, "essential", "PROFILE");

    }

    public void goToEssentialEdit(View v) {
        goAhead(true, R.id.essentialDetail, R.id.essentialDetailEdit, "edit_essential", "EDIT PROFILE");

    }

    public void backToBank(View v) {


        stateBank();

        if (modBank) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(true);
            builder.setMessage("You have unsaved changes.");
            builder.setPositiveButton("Keep", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (!validFormBank()) return;
                    final EditText etIFSC = (EditText) findViewById(R.id.etIFSC);
                    StringRequest reqIFSC = new StringRequest(com.android.volley.Request.Method.GET, urlIFSC + etIFSC.getText().toString(),
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        String res = response;
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
                                                    goBack(true, R.id.bankDetailEdit, R.id.profileHome, "profile_home", "PROFILE");
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
                                        Toast.makeText(EditProfile.this, "No Internet access. Please check your connection.", Toast.LENGTH_LONG).show();
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
                    rQueue.getInstance(EditProfile.this).queueRequest("", reqIFSC);


                }
            });
            builder.setNegativeButton("Discard", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    AutoCompleteTextView actvBank = (AutoCompleteTextView) findViewById(R.id.actvBank);
                    EditText etAccNum = (EditText) findViewById(R.id.etAccNum);
                    EditText etConfirmAccNum = (EditText) findViewById(R.id.etConfirmAccNum);
                    EditText etIFSC = (EditText) findViewById(R.id.etIFSC);
                    try {
                        actvBank.setText(usrBank.getString("bankName").toString());
                        etAccNum.setText(usrBank.get("accountNumber").toString());
                        etIFSC.setText(usrBank.getString("ifsc").toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    etAccNum.setError(null);
                    etAccNum.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
                    etConfirmAccNum.setError(null);
                    etConfirmAccNum.setText("");
                    View rowLblConfirmAccNum = findViewById(R.id.rowLblConfirmAccNum);
                    View rowEtConfirmAccNum = findViewById(R.id.rowEtConfirmAccNum);
                    rowLblConfirmAccNum.setVisibility(View.GONE);
                    rowEtConfirmAccNum.setVisibility(View.GONE);
                    modBank = false;
                    goBack(true, R.id.bankDetailEdit, R.id.profileHome, "profile_home", "PROFILE");
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        } else
            goBack(true, R.id.bankDetailEdit, R.id.profileHome, "profile_home", "PROFILE");

        //goBack(true, R.id.bankDetailEdit, R.id.bankDetail, "bank", "PROFILE");

    }

    public void goToBankEdit(View v) {
        goAhead(true, R.id.bankDetail, R.id.bankDetailEdit, "edit_bank", "EDIT PROFILE");

    }


    public void backToPersonal(View v) {


        statePersonal();

        if (modPersonal) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(true);
            builder.setMessage("You have unsaved changes.");
            builder.setPositiveButton("Keep", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (!validFormPersonal()) return;
                    goBack(true, R.id.personalDetailEdit, R.id.profileHome, "profile_home", "PROFILE");
                }
            });
            builder.setNegativeButton("Discard", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    EditText etAddrPerm = (EditText) findViewById(R.id.etAddressPerm);
                    EditText etPinCode = (EditText) findViewById(R.id.etPinCode);
                    EditText etLandline = (EditText) findViewById(R.id.etLandline);
                    MaterialSpinner spnrDOBDay = (MaterialSpinner) findViewById(R.id.spnrDOBDay);
                    MaterialSpinner spnrDOBMonth = (MaterialSpinner) findViewById(R.id.spnrDOBMonth);
                    MaterialSpinner spnrDOBYear = (MaterialSpinner) findViewById(R.id.spnrDOBYear);
                    MaterialSpinner spnrQual = (MaterialSpinner) findViewById(R.id.spnrQual);
                    MaterialSpinner spnrHouse = (MaterialSpinner) findViewById(R.id.spnrHouse);
                    MaterialSpinner spnrPurpose = (MaterialSpinner) findViewById(R.id.spnrPurpose);
                    if (sameAsPerm.toString().equalsIgnoreCase("true")) {
                        etAddrPerm.setText(custAddr);
                        etPinCode.setText(custZip);
                    } else {
                        etAddrPerm.setText(custAddrPerm);
                        etPinCode.setText(custZipPerm);
                    }
                    etLandline.setText(custLandline);
                    if (!usrEntity.isNull("dob")) {
                        String tmpDate = null;
                        try {
                            tmpDate = usrEntity.getString("dob");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Date date = null;
                        try {
                            date = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(tmpDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        try {
                            selectSpinnerItemByValue(spnrDOBDay, new SimpleDateFormat("dd", Locale.ENGLISH).format(date));
                            selectSpinnerItemByValue(spnrDOBMonth, new SimpleDateFormat("MMM", Locale.ENGLISH).format(date));
                            selectSpinnerItemByValue(spnrDOBYear, new SimpleDateFormat("yyyy", Locale.ENGLISH).format(date));
                        } catch (Exception e) {
                        }
                    }

                    String tmp = "";

                    tmp = custEduType;
                    if (tmp.equalsIgnoreCase("1")) spnrQual.setSelection(1);
                    if (tmp.equalsIgnoreCase("2")) spnrQual.setSelection(2);
                    if (tmp.equalsIgnoreCase("3")) spnrQual.setSelection(3);

                    tmp = custHouseType;
                    if (tmp.equalsIgnoreCase("1")) spnrHouse.setSelection(1);
                    if (tmp.equalsIgnoreCase("2")) spnrHouse.setSelection(2);
                    if (tmp.equalsIgnoreCase("3")) spnrHouse.setSelection(3);

                    tmp = custPurpose;
                    if (tmp.equalsIgnoreCase("1")) spnrPurpose.setSelection(1);
                    if (tmp.equalsIgnoreCase("2")) spnrPurpose.setSelection(2);
                    if (tmp.equalsIgnoreCase("3")) spnrPurpose.setSelection(3);
                    if (tmp.equalsIgnoreCase("4")) spnrPurpose.setSelection(4);
                    modPersonal = false;
                    goBack(true, R.id.personalDetailEdit, R.id.profileHome, "profile_home", "PROFILE");
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        } else
            goBack(true, R.id.personalDetailEdit, R.id.profileHome, "profile_home", "PROFILE");

        //goBack(true, R.id.personalDetailEdit, R.id.personalDetail, "personal", "PROFILE");

    }

    public void goToPersonalEdit(View v) {
        goAhead(true, R.id.personalDetail, R.id.personalDetailEdit, "edit_personal", "EDIT PROFILE");

    }

    public void backToProfessional(View v) {

        stateProfessional();

        if (modProfessional) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(true);
            builder.setMessage("You have unsaved changes.");
            builder.setPositiveButton("Keep", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (!validFormProfessional()) return;
                    goBack(true, R.id.professionalDetailEdit, R.id.profileHome, "profile_home", "PROFILE");
                }
            });
            builder.setNegativeButton("Discard", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    AutoCompleteTextView actvCompany = (AutoCompleteTextView) findViewById(R.id.actvEmpName);
                    AutoCompleteTextView actvCompany2 = (AutoCompleteTextView) findViewById(R.id.actvEmpName2);
                    EditText etEmpLandline = (EditText) findViewById(R.id.etEmpLandline);
                    EditText etEmailOff = (EditText) findViewById(R.id.etEmailOffice);
                    MaterialSpinner spnrWorkSinceMonth = (MaterialSpinner) findViewById(R.id.spnrWorkingSinceMonth);
                    MaterialSpinner spnrWorkSinceYear = (MaterialSpinner) findViewById(R.id.spnrWorkingSinceYear);
                    actvCompany2.setText(custCompanyName);
                    etEmpLandline.setText(custCompanyLandline);
                    etEmailOff.setText(custEmail);
                    if (!usrEntity.isNull("doj")) {
                        String tmpDate = null;
                        try {
                            tmpDate = usrEntity.getString("doj");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Date date = null;
                        try {
                            date = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(tmpDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        try {
                            MaterialSpinner spnrDOJMonth = (MaterialSpinner) findViewById(R.id.spnrWorkingSinceMonth);
                            MaterialSpinner spnrDOJYear = (MaterialSpinner) findViewById(R.id.spnrWorkingSinceYear);
                            selectSpinnerItemByValue(spnrDOJMonth, new SimpleDateFormat("MMM", Locale.ENGLISH).format(date));
                            selectSpinnerItemByValue(spnrDOJYear, new SimpleDateFormat("yyyy", Locale.ENGLISH).format(date));
                        } catch (Exception e) {
                        }
                    }
                    modProfessional = false;
                    goBack(true, R.id.professionalDetailEdit, R.id.profileHome, "profile_home", "PROFILE");
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        } else
            goBack(true, R.id.professionalDetailEdit, R.id.profileHome, "profile_home", "PROFILE");

        //goBack(true, R.id.professionalDetailEdit, R.id.professionalDetail, "professional", "PROFILE");

    }

    public void goToProfessionalEdit(View v) {
        goAhead(true, R.id.professionalDetail, R.id.professionalDetailEdit, "edit_professional", "EDIT PROFILE");

    }

    private void goBack(boolean isBack, int from, final int to, final String current_step, final String title) {

        getSupportActionBar().setDisplayHomeAsUpEnabled(isBack);
        final LinearLayout detailLayout = (LinearLayout) findViewById(from);
        detailLayout.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.up_to_down_full));

        ImageButton btnUpdate = (ImageButton) findViewById(R.id.btnUpdate);
        if (modBank || modPersonal || modProfessional || modEssential)
            btnUpdate.setVisibility(View.VISIBLE);
        else
            btnUpdate.setVisibility(View.GONE);

        detailLayout.postDelayed(new Runnable() {
            public void run() {
                detailLayout.setVisibility(View.GONE);
                LinearLayout home = (LinearLayout) findViewById(to);
                home.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.up_to_down_half));
                home.setVisibility(View.VISIBLE);
                currentStep = current_step;
                TextView caption = (TextView) findViewById(R.id.caption);
                caption.setText(title);
//            ImageButton ibNext = (ImageButton) findViewById(R.id.ibNext);
//            ibNext.setImageDrawable(getResources().getDrawable(R.drawable.next_step_4));
            }
        }, 500);
    }

    public static void selectSpinnerItemByValue(MaterialSpinner spnr, String value) {
        for (int position = 1; position < spnr.getCount(); position++) {
            String str1 = spnr.getItemAtPosition(position).toString();
            if (spnr.getItemAtPosition(position).toString().equalsIgnoreCase(value)) {
                spnr.setSelection(position);
            } else
                continue;
        }
    }

    private void updateBank() {
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Action")
                .setAction("EditProfile")
                .setLabel("UpdateBank")
                .build());
        StringRequest reqBankDetails = new StringRequest(com.android.volley.Request.Method.POST, urlBankDetails,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d("IPB", response.toString());

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
                            Toast.makeText(EditProfile.this, "No Internet access. Please check your connection.", Toast.LENGTH_LONG).show();
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
        rQueue.getInstance(EditProfile.this).queueRequest("", reqBankDetails);
    }

    private void stateProfessional() {
        try {
            modProfessional = false;
            AutoCompleteTextView actvCompany = (AutoCompleteTextView) findViewById(R.id.actvEmpName);
            AutoCompleteTextView actvCompany2 = (AutoCompleteTextView) findViewById(R.id.actvEmpName2);
            EditText etEmpLandline = (EditText) findViewById(R.id.etEmpLandline);
            EditText etEmailOff = (EditText) findViewById(R.id.etEmailOffice);
            MaterialSpinner spnrWorkSinceMonth = (MaterialSpinner) findViewById(R.id.spnrWorkingSinceMonth);
            MaterialSpinner spnrWorkSinceYear = (MaterialSpinner) findViewById(R.id.spnrWorkingSinceYear);
            if (!actvCompany2.getText().toString().equals(custCompanyName)) modProfessional = true;
            boolean status1 = modProfessional;
            if (!etEmpLandline.getText().toString().equals(custCompanyLandline))
                modProfessional = true;
            status1 = modProfessional;
            if (!etEmailOff.getText().toString().equals(custEmail)) modProfessional = true;
            status1 = modProfessional;
            String doj = "";
            String strDOJ = "01 " + spnrWorkSinceMonth.getSelectedItem().toString() + " " + spnrWorkSinceYear.getSelectedItem().toString();
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM yyyy", Locale.ENGLISH);
                Date tmpDate = dateFormat.parse(strDOJ);
                doj = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(tmpDate);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                if (!doj.toString().trim().equalsIgnoreCase(usrEntity.getString("doj")))
                    modProfessional = true;
                status1 = modProfessional;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {

        }
    }

    private void statePersonal() {

        try {
            modPersonal = false;
            EditText etAddrPerm = (EditText) findViewById(R.id.etAddressPerm);
            EditText etPinCode = (EditText) findViewById(R.id.etPinCode);
            EditText etLandline = (EditText) findViewById(R.id.etLandline);
            MaterialSpinner spnrDOBDay = (MaterialSpinner) findViewById(R.id.spnrDOBDay);
            MaterialSpinner spnrDOBMonth = (MaterialSpinner) findViewById(R.id.spnrDOBMonth);
            MaterialSpinner spnrDOBYear = (MaterialSpinner) findViewById(R.id.spnrDOBYear);
            MaterialSpinner spnrQual = (MaterialSpinner) findViewById(R.id.spnrQual);
            MaterialSpinner spnrHouse = (MaterialSpinner) findViewById(R.id.spnrHouse);
            MaterialSpinner spnrPurpose = (MaterialSpinner) findViewById(R.id.spnrPurpose);

            if (sameAsPerm.toString().equalsIgnoreCase("true")) {
                if (!etAddrPerm.getText().toString().equalsIgnoreCase(custAddr)) modPersonal = true;
                if (!etPinCode.getText().toString().equalsIgnoreCase(custZip)) modPersonal = true;
            } else {
                if (!etAddrPerm.getText().toString().equalsIgnoreCase(custAddrPerm))
                    modPersonal = true;
                if (!etPinCode.getText().toString().equalsIgnoreCase(custZipPerm))
                    modPersonal = true;
            }

            boolean status = modPersonal;

            if (!etLandline.getText().toString().equalsIgnoreCase(custLandline)) modPersonal = true;
            String dob = "";
            if (spnrDOBDay.getSelectedItem() == null) {
                spnrDOBDay.setError("Please provide a valid date of birth.");
                return;
            }

            if (spnrDOBMonth.getSelectedItem() == null) {
                spnrDOBMonth.setError("Please provide a valid date of birth.");
                return;
            }
            if (spnrDOBYear.getSelectedItem() == null) {
                spnrDOBYear.setError("Please provide a valid date of birth.");
                return;
            }
            String strDOB = spnrDOBDay.getSelectedItem().toString() + " " + spnrDOBMonth.getSelectedItem().toString() + " " + spnrDOBYear.getSelectedItem().toString();
            status = modPersonal;
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM yyyy", Locale.ENGLISH);
                Date tmpDate = dateFormat.parse(strDOB);
                dob = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(tmpDate);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                if (!dob.toString().trim().equalsIgnoreCase(usrEntity.getString("dob")))
                    modPersonal = true;
                status = modPersonal;
            } catch (Exception e) {
                e.printStackTrace();
            }
            String tmp = "";
            if (spnrQual.getSelectedItem() == null) {
                spnrQual.setError("Please choose your qualification.");
                return;
            }
            tmp = spnrQual.getSelectedItem().toString();
            String qualID = "1";
            if (tmp.equalsIgnoreCase("Undergraduate")) qualID = "1";
            if (tmp.equalsIgnoreCase("Graduate")) qualID = "2";
            if (tmp.equalsIgnoreCase("Postgraduate")) qualID = "3";
            if (!qualID.trim().equals(custEduType.trim())) modPersonal = true;
            status = modPersonal;
            if (spnrHouse.getSelectedItem() == null) {
                spnrHouse.setError("Please choose your accommodation type.");
                return;
            }
            tmp = spnrHouse.getSelectedItem().toString();
            String houseID = "1";
            if (tmp.equalsIgnoreCase("PG")) houseID = "1";
            if (tmp.equalsIgnoreCase("Rent")) houseID = "2";
            if (tmp.equalsIgnoreCase("Own")) houseID = "3";
            if (!houseID.trim().equals(custHouseType.trim())) modPersonal = true;
            status = modPersonal;
            if (spnrPurpose.getSelectedItem() == null) {
                spnrPurpose.setError("Please choose your purpose for taking CASHe.");
                return;
            }
            tmp = spnrPurpose.getSelectedItem().toString();
            String purposeID = "1";
            if (tmp.equalsIgnoreCase("Rent/Deposit")) purposeID = "1";
            if (tmp.equalsIgnoreCase("Medical")) purposeID = "2";
            if (tmp.equalsIgnoreCase("Travel")) purposeID = "3";
            if (tmp.equalsIgnoreCase("Personal")) purposeID = "4";
            if (!purposeID.trim().equals(custPurpose.trim())) modPersonal = true;
            status = modPersonal;
        } catch (Exception e) {
        }
    }

    private void stateEssential() {
        try {
            modEssential = false;
            EditText etName = (EditText) findViewById(R.id.etFullName);
            EditText etEmail = (EditText) findViewById(R.id.etEmail);
            EditText etMobile = (EditText) findViewById(R.id.etCellNumber);
            EditText etPAN = (EditText) findViewById(R.id.etPAN);
            EditText etAddress = (EditText) findViewById(R.id.etAddress);
            MaterialSpinner spnrAddress = (MaterialSpinner) findViewById(R.id.spnrAddrProof);
            AutoCompleteTextView actvCompany = (AutoCompleteTextView) findViewById(R.id.actvEmpName);
            if (!actvCompany.getText().toString().equals(custCompanyName)) modEssential = true;
            boolean status1 = modEssential;
            EditText etNetSal = (EditText) findViewById(R.id.etNetSal);
            if (mCurrentAddrProof != null && !mCurrentAddrProof.isEmpty()) modEssential = true;
            boolean status2 = modEssential;
            if (mCurrentPAN != null && !mCurrentPAN.isEmpty()) modEssential = true;
            status2 = modEssential;
            if (mCurrentSalSlip != null && !mCurrentSalSlip.isEmpty()) modEssential = true;
            status2 = modEssential;
            if (!etName.getText().toString().equalsIgnoreCase(custName)) modEssential = true;
            status2 = modEssential;
            if (!etEmail.getText().toString().equalsIgnoreCase(custEmailPers)) modEssential = true;
            status2 = modEssential;
            if (!etMobile.getText().toString().equalsIgnoreCase(custCell)) modEssential = true;
            status2 = modEssential;
            if (!etPAN.getText().toString().equalsIgnoreCase(custPAN)) modEssential = true;
            status2 = modEssential;
            if (!etAddress.getText().toString().equalsIgnoreCase(custAddr)) modEssential = true;
            status2 = modEssential;
            if (!etNetSal.getText().toString().equalsIgnoreCase(custSal)) modEssential = true;
            status2 = modEssential;
            String tmp = "";
            tmp = spnrAddress.getSelectedItem().toString();
            String addrProofID = "1";
            if (tmp.equalsIgnoreCase("Aadhar Card")) addrProofID = "1";
            if (tmp.equalsIgnoreCase("Utility Bill")) addrProofID = "2";
            if (tmp.equalsIgnoreCase("Telephone Bill")) addrProofID = "3";
            if (tmp.equalsIgnoreCase("Passport")) addrProofID = "4";
            if (tmp.equalsIgnoreCase("Voter's ID")) addrProofID = "5";
            if (!addrProofID.trim().equals(custAddrProofID.trim())) modEssential = true;
            status2 = modEssential;
        } catch (Exception e) {
        }
    }

    private void stateBank() {
        modBank = false;
        AutoCompleteTextView actvBank = (AutoCompleteTextView) findViewById(R.id.actvBank);
        EditText etAccNum = (EditText) findViewById(R.id.etAccNum);
        EditText etIFSC = (EditText) findViewById(R.id.etIFSC);
        try {
            if (usrBank != null) {
                if (mCurrentBankStmt != null && !mCurrentBankStmt.isEmpty())
                    modBank = true;
                if (!actvBank.getText().toString().equalsIgnoreCase(usrBank.getString("bankName").toString()))
                    modBank = true;
                if (!etAccNum.getText().toString().equalsIgnoreCase(usrBank.get("accountNumber").toString()))
                    modBank = true;
                if (!etIFSC.getText().toString().equalsIgnoreCase(usrBank.getString("ifsc").toString()))
                    modBank = true;
            } else {
                if (mCurrentBankStmt != null && !mCurrentBankStmt.isEmpty())
                    modBank = true;
                if (actvBank.getText().toString() != null)
                    modBank = true;
                if (etAccNum.getText().toString() != null)
                    modBank = true;
                if (etIFSC.getText().toString() != null)
                    modBank = true;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void smartUpload() {
        Intent i = new Intent(EditProfile.this, SmartUpload.class);
        startActivity(i);
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

}