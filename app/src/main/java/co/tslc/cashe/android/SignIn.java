package co.tslc.cashe.android;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.crashlytics.android.Crashlytics;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.linkedin.platform.APIHelper;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SignIn extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int REQUEST_CODE_PERMISSION = 2;
    private static final int RC_SIGN_IN = 9001;
    private static final int FORCE_UPGRADE_REQUEST = 9002;

    String[] rPerms = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    protected CASHe app;
    private Tracker mTracker;
    private GoogleApiClient mGoogleApiClient;

    ImageButton btnFB;
    ImageButton btnLI;
    //String rdrResponse;
    String email, name, first_name, last_name;
    private static String accessToken = "";
    private static String refreshToken = "";
    private static String tokenType = "";
    private static final String urlAppVersion = CASHe.webApiUrl + "api/cashe/customer/mobileversionupdate";
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        if(null != client) {
            client.connect();
            Action viewAction = Action.newAction(
                    Action.TYPE_VIEW, // TODO: choose an action type.
                    "SignIn Page", // TODO: Define a title for the content shown.
                    // TODO: If you have web page content that matches this app activity's content,
                    // make sure this auto-generated web page URL is correct.
                    // Otherwise, set the URL to null.
                    Uri.parse("http://host/path"),
                    // TODO: Make sure this auto-generated app deep link URI is correct.
                    Uri.parse("android-app://co.tslc.cashe.android/http/host/path")
            );
            AppIndex.AppIndexApi.start(client, viewAction);
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        if(null != client) {
            Action viewAction = Action.newAction(
                    Action.TYPE_VIEW, // TODO: choose an action type.
                    "SignIn Page", // TODO: Define a title for the content shown.
                    // TODO: If you have web page content that matches this app activity's content,
                    // make sure this auto-generated web page URL is correct.
                    // Otherwise, set the URL to null.
                    Uri.parse("http://host/path"),
                    // TODO: Make sure this auto-generated app deep link URI is correct.
                    Uri.parse("android-app://co.tslc.cashe.android/http/host/path")
            );
            AppIndex.AppIndexApi.end(client, viewAction);
            client.disconnect();
        }
    }

    public interface OnFinishListener {
        public void onFinish(JSONObject o);
    }

    private static final String urlAuth = CASHe.webApiUrl + "authenticate";
    private static final String urlStatus = CASHe.webApiUrl + "api/cashe/customer/status";

    CallbackManager callbackManager;

    static String uInf = "";

    ProgressDialog pd = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (CASHe) getApplication();
        app.isInForeground = true;

        mTracker = app.getDefaultTracker();
        mTracker.setScreenName("SignIn");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        try {
            FacebookSdk.sdkInitialize(SignIn.this);
            callbackManager = CallbackManager.Factory.create();

            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();

            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this,
                            this /* OnConnectionFailedListener */)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();

            LoginManager.getInstance().logOut();
            LISessionManager.getInstance(getApplicationContext()).clearSession();

            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                mGoogleApiClient.disconnect();
            }
        }catch(Exception e){}


        try {
            setContentView(R.layout.activity_sign_in);
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }catch(Exception e){}


/*        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // ...
                    }
                });*/

        btnFB = (ImageButton)findViewById(R.id.ibFacebook);

        btnFB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Action")
                            .setAction("SignIn")
                            .setLabel("Facebook")
                            .build());
                }catch (Exception e){}
                try {
                    if(FacebookSdk.isInitialized())
                        LoginManager.getInstance().logInWithReadPermissions(SignIn.this, Arrays.asList("public_profile,user_friends,email"));
                    else
                        FacebookSdk.sdkInitialize(SignIn.this);
                }catch (Exception e){}
                //",user_about_me,user_actions.books,user_actions.fitness,user_actions.music,user_actions.news,user_actions.video,user_birthday,user_education_history,user_events,user_games_activity,user_hometown,user_likes,user_location,user_managed_groups,user_photos,user_posts,user_relationships,user_relationship_details,user_religion_politics,user_tagged_places,user_videos,user_website,user_work_history,read_custom_friendlists,read_insights,read_audience_network_insights,read_page_mailboxes,manage_pages,publish_pages,publish_actions,rsvp_event,pages_show_list,ads_read"));
            }
        });
        ImageButton btnG = (ImageButton) findViewById(R.id.ibGoogle);
        btnG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnFB.setVisibility(View.GONE);
                ImageButton btnLI = (ImageButton) findViewById(R.id.ibLinkedIn);
                btnLI.setVisibility(View.GONE);
                ImageButton btnG = (ImageButton) findViewById(R.id.ibGoogle);
                btnG.setVisibility(View.GONE);
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        /*TextView tvTC = (TextView)findViewById(R.id.tvTC);
        TextView tvPP = (TextView)findViewById(R.id.tvPP);
        tvTC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SignIn.this,Terms.class);
                startActivity(i);
            }
        });
        tvPP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SignIn.this,Privacy.class);
                startActivity(i);
            }
        });*/

        ImageButton btnLI = (ImageButton) findViewById(R.id.ibLinkedIn);
        btnLI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("SignIn")
                        .setLabel("LinkedIn")
                        .build());
                LISessionManager.getInstance(getApplicationContext()).init(SignIn.this, buildScope(), new AuthListener() {
                    @Override
                    public void onAuthSuccess() {
                        String url = "https://api.linkedin.com/v1/people/~";
                        if (!isFinishing())
                            pd = ProgressDialog.show(SignIn.this, "CASHe", "Retrieving profile...");
                        APIHelper apiHelper = APIHelper.getInstance(getApplicationContext());
                        apiHelper.getRequest(SignIn.this, url, new ApiListener() {
                            @Override
                            public void onApiSuccess(ApiResponse apiResponse) {
                                JSONObject jsonAuth = null;
                                try {
                                    JSONObject jsonResponse = new JSONObject(apiResponse.toString());
                                    if (jsonResponse != null) {
                                        JSONObject jsonResponseData = new JSONObject(jsonResponse.getString("responseData"));
                                        if (jsonResponseData != null) {
                                            first_name = "";
                                            last_name = "";
                                            if (!jsonResponseData.isNull("firstName"))
                                                first_name = jsonResponseData.getString("firstName");
                                            if (!jsonResponseData.isNull("lastName"))
                                                last_name = jsonResponseData.getString("lastName");
                                            String liID = "";
                                            if (!jsonResponseData.isNull("id"))
                                                liID = jsonResponseData.getString("id");
                                            name = first_name + ' ' + last_name;
                                            email = "";
                                            uInf = "{accountType: \"" + "linkedin" + "\", socialAccountId: \"" + liID + "\", accessToken: \"" + "access_token" + "\" }";
                                            auth();
                                        } else {
                                            dismissDailog();
                                            btnFB.setVisibility(View.VISIBLE);
                                            ImageButton btnLI = (ImageButton) findViewById(R.id.ibLinkedIn);
                                            btnLI.setVisibility(View.VISIBLE);
                                            ImageButton btnG = (ImageButton) findViewById(R.id.ibGoogle);
                                            btnG.setVisibility(View.VISIBLE);
                                            Toast.makeText(SignIn.this, "An error occurred while retrieving your profile. Please try again later.",Toast.LENGTH_LONG).show();
                                            return;
                                        }
                                    } else {
                                        dismissDailog();
                                        btnFB.setVisibility(View.VISIBLE);
                                        ImageButton btnLI = (ImageButton) findViewById(R.id.ibLinkedIn);
                                        btnLI.setVisibility(View.VISIBLE);
                                        ImageButton btnG = (ImageButton) findViewById(R.id.ibGoogle);
                                        btnG.setVisibility(View.VISIBLE);
                                        Toast.makeText(SignIn.this, "An error occurred while retrieving your profile. Please try again later.",Toast.LENGTH_LONG).show();
                                        return;
                                    }
                                } catch (JSONException e) {
                                    dismissDailog();
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onApiError(LIApiError liApiError) {
                                // Error making GET request!
                                dismissDailog();
                            }
                        });

                    }

                    @Override
                    public void onAuthError(LIAuthError error) {
                        // Handle authentication errors
                        dismissDailog();
                    }
                }, true);

            }
        });

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                uInf = "{accountType: \"" + "facebook" + "\", socialAccountId: \"" + loginResult.getAccessToken().getUserId().toString() + "\", accessToken: \"" + loginResult.getAccessToken().getToken() + "\" }";
                btnFB.setVisibility(View.GONE);
                ImageButton btnLI = (ImageButton) findViewById(R.id.ibLinkedIn);
                btnLI.setVisibility(View.GONE);

                ImageButton btnG = (ImageButton) findViewById(R.id.ibGoogle);
                btnG.setVisibility(View.GONE);
                if (!isFinishing())
                    pd = ProgressDialog.show(SignIn.this, "CASHe", "Retrieving profile...");

                GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {

                            if (object != null) {
                                if (object.has("email")) email = object.getString("email");
                                if (object.has("name")) name = object.getString("name");
                                if (object.has("first_name"))
                                    first_name = object.optString("first_name");
                                if (object.has("last_name"))
                                    last_name = object.optString("last_name");
                                auth();
                            } else {
                                dismissDailog();
                                btnFB.setVisibility(View.VISIBLE);
                                ImageButton btnLI = (ImageButton) findViewById(R.id.ibLinkedIn);
                                btnLI.setVisibility(View.VISIBLE);
                                ImageButton btnG = (ImageButton) findViewById(R.id.ibGoogle);
                                btnG.setVisibility(View.VISIBLE);
                                Toast.makeText(SignIn.this, "An error occurred while retrieving your profile. Please try again later.", Toast.LENGTH_LONG).show();
                                return;
                            }
                        } catch (JSONException e) {
                            dismissDailog();
                            e.printStackTrace();
                        }
                    }
                });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,first_name,last_name,email");
                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();
            }

            @Override
            public void onCancel() {
                dismissDailog();
            }

            @Override
            public void onError(FacebookException exception) {
                dismissDailog();
                exception.printStackTrace();
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void handleGoogleSignInResult(GoogleSignInResult result) {
        Log.d("G+", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            name = acct.getDisplayName();
            email = acct.getEmail();
            String gID = acct.getId();
            uInf = "{accountType: \"" + "googleplus" + "\", socialAccountId: \"" + gID + "\", accessToken: \"" + "access_token" + "\" }";
            auth();
        } else {
            btnFB.setVisibility(View.VISIBLE);
            ImageButton btnLI = (ImageButton) findViewById(R.id.ibLinkedIn);
            btnLI.setVisibility(View.VISIBLE);
            ImageButton btnG = (ImageButton) findViewById(R.id.ibGoogle);
            btnG.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == FORCE_UPGRADE_REQUEST && resultCode == Activity.RESULT_CANCELED)
        {
            normalAppFlow();
        }
        else {
            try {
                callbackManager.onActivityResult(requestCode, resultCode, data);
                LISessionManager.getInstance(getApplicationContext()).onActivityResult(this, requestCode, resultCode, data);
                if (requestCode == RC_SIGN_IN) {
                    GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                    handleGoogleSignInResult(result);
                }
            } catch (Exception e) {

            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dismissDailog();
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

    /*@Override
    protected void onResume() {
        super.onResume();
        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }


    @Override
    protected void onPause() {
        super.onPause();
        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }
    */

    private static Scope buildScope() {
        return Scope.build(Scope.R_BASICPROFILE, Scope.R_EMAILADDRESS, Scope.W_SHARE);
    }

    private void auth() {
        if (pd != null && pd.isShowing()) pd.setMessage("Authenticating...");
        StringRequest authReq = new StringRequest(Request.Method.POST, urlAuth,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jsonAuth = null;
                        try {
                            if (null != response) {
                                Log.d("IPB", response.toString());
                                jsonAuth = new JSONObject(response.toString());
                                if (jsonAuth != null) {
                                    accessToken = jsonAuth.getString("access_token");
                                    refreshToken = jsonAuth.getString("refresh_token");
                                    tokenType = jsonAuth.getString("token_type");
                                    int expires = jsonAuth.getInt("expires_in");
                                    app.setAccessToken(accessToken);
                                    app.setTokenType(tokenType);
                                    app.setExpiryTime(expires);
                                    checkForAppUpgrade();
                                }
                            }
                        } catch (JSONException e) {
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
                            Toast.makeText(SignIn.this, "No Internet access. Please check your connection.", Toast.LENGTH_LONG).show();
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
                try {
                    return uInf == null ? null : uInf.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                            uInf, "utf-8");
                    return null;
                }
            }
        };
        authReq.setShouldCache(false);
        rQueue.getInstance(this).queueRequest("SignIn", authReq);

    }

    public void checkForAppUpgrade() {

        if (pd != null && pd.isShowing())
            pd.setMessage("Getting App Version");

        StringRequest reqCust = new StringRequest(com.android.volley.Request.Method.GET, urlAppVersion,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response1) {

                        try {
                            dismissDailog();
                            Log.d("IPB", response1);

                            PackageManager manager = SignIn.this.getPackageManager();
                            PackageInfo info = null;
                            String verCurApp = "";
                            try {
                                info = manager.getPackageInfo(
                                        SignIn.this.getPackageName(), 0);
                                verCurApp = info.versionName;
                            } catch (PackageManager.NameNotFoundException e) {
                                e.printStackTrace();
                            }
                            JSONObject jsonAccount = new JSONObject(response1.toString());
                            JSONObject usrEntity1 = new JSONObject(jsonAccount.getString("entity"));
                            String verCurAppService = usrEntity1.getString("mobileVersion");
                            Boolean isUpgrade = usrEntity1.getBoolean("forceUpdate");
                            if (!verCurApp.equalsIgnoreCase(verCurAppService)) {
                                Intent i = new Intent(SignIn.this, ForceUpgrade.class);
                                // i.putExtra("currentVersion", version);
                                i.putExtra("isUpgrade", isUpgrade);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivityForResult(i, FORCE_UPGRADE_REQUEST);
                                if (isUpgrade)
                                    finish();
                            }
                            else normalAppFlow();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        dismissDailog();
                        if (error instanceof NoConnectionError) {
                            Toast.makeText(SignIn.this, "No Internet access. Please check your connection.", Toast.LENGTH_LONG).show();
                        }
                    }
                }

        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", tokenType + " " + accessToken);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        reqCust.setShouldCache(false);
        rQueue.getInstance(SignIn.this).queueRequest("", reqCust);
    }

    public void normalAppFlow() {
        if (pd == null || !pd.isShowing()) {
            pd = new ProgressDialog(SignIn.this);
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.setCancelable(false);
            pd.show();
        }

        pd.setMessage("Setting up environment...");

        StringRequest reqAuth = new StringRequest(Request.Method.GET, urlStatus,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dismissDailog();
                        if (response != null) {
                            JSONObject obj = null;
                            try {
                                obj = new JSONObject(response);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            String statusType = "";
                            if (obj != null) {
                                if (!obj.isNull("statusType")) {
                                    try {
                                        statusType = obj.getString("statusType");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    if (statusType != null) {
                                        if (statusType.equalsIgnoreCase("OK")) {
                                            //reDir(response);
                                            SharedPreferences sharedpreferences = SignIn.this.getSharedPreferences("CASHe", Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedpreferences.edit();
                                            editor.putString("rdrResponse", response);
                                            editor.commit();
                                            //rdrResponse = response;
                                            checkPerms();
                                        } else {
                                            dismissDailog();
                                            btnFB.setVisibility(View.VISIBLE);
                                            ImageButton btnLI = (ImageButton) findViewById(R.id.ibLinkedIn);
                                            btnLI.setVisibility(View.VISIBLE);
                                            ImageButton btnG = (ImageButton) findViewById(R.id.ibGoogle);
                                            btnG.setVisibility(View.VISIBLE);
                                            Toast.makeText(SignIn.this, "An error occurred while retrieving your profile. Please try again later.", Toast.LENGTH_LONG).show();
                                            return;
                                        }
                                    } else {
                                        dismissDailog();
                                        btnFB.setVisibility(View.VISIBLE);
                                        ImageButton btnLI = (ImageButton) findViewById(R.id.ibLinkedIn);
                                        btnLI.setVisibility(View.VISIBLE);
                                        ImageButton btnG = (ImageButton) findViewById(R.id.ibGoogle);
                                        btnG.setVisibility(View.VISIBLE);
                                        Toast.makeText(SignIn.this, "An error occurred while retrieving your profile. Please try again later.", Toast.LENGTH_LONG).show();
                                        return;
                                    }
                                } else {
                                    dismissDailog();
                                    btnFB.setVisibility(View.VISIBLE);
                                    ImageButton btnLI = (ImageButton) findViewById(R.id.ibLinkedIn);
                                    btnLI.setVisibility(View.VISIBLE);
                                    ImageButton btnG = (ImageButton) findViewById(R.id.ibGoogle);
                                    btnG.setVisibility(View.VISIBLE);
                                    Toast.makeText(SignIn.this, "An error occurred while retrieving your profile. Please try again later.", Toast.LENGTH_LONG).show();
                                    return;
                                }
                            }
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dismissDailog();
                        error.printStackTrace();
                        if (error instanceof NoConnectionError) {
                            Toast.makeText(SignIn.this, "No Internet access. Please check your connection.", Toast.LENGTH_LONG).show();
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
        rQueue.getInstance(SignIn.this).queueRequest("SignIn", reqAuth);
    }

    private void checkPerms() {
        try {
            SharedPreferences sharedpreferences = SignIn.this.getSharedPreferences("CASHe", Context.MODE_PRIVATE);
            String rdrResponse = sharedpreferences.getString("rdrResponse", null);
            //
            if (ContextCompat.checkSelfPermission(this, rPerms[0])
                    != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, rPerms[1])
                            != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        rPerms, REQUEST_CODE_PERMISSION); //
            } else if (null != rdrResponse)
                reDir(rdrResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void reDir(String response) {
        try {
            JSONObject jsonStatus = new JSONObject(response.toString());
            JSONObject jsonEntity = new JSONObject(jsonStatus.getString("entity"));
            String custID = jsonEntity.getString("custId");
            String customerStatusName = jsonEntity.getString("customerStatusName");

            app.sessionExpired = false;
            Base.accessToken = accessToken;
            Base.refreshToken = refreshToken;
            Base.tokenType = tokenType;
            Base.name = name;
            Base.email = email;
            Base.custID = custID;

            Crashlytics.setUserIdentifier(custID);
            Crashlytics.setUserEmail(email);
            Crashlytics.setUserName(name);

            if (customerStatusName.equalsIgnoreCase("New")) {
                Intent i = new Intent(SignIn.this, SignUp.class);
                i.putExtra("name", name);
                i.putExtra("email", email);
                i.putExtra("custID", custID);
                i.putExtra("accessToken", accessToken);
                i.putExtra("refreshToken", refreshToken);
                i.putExtra("tokenType", tokenType);
                startActivity(i);
                finish();
            }
            if (customerStatusName.equalsIgnoreCase("Data Received P0")) {
                Intent i = new Intent(SignIn.this, SignUp.class);
                i.putExtra("name", name);
                i.putExtra("email", email);
                i.putExtra("custID", custID);
                i.putExtra("accessToken", accessToken);
                i.putExtra("refreshToken", refreshToken);
                i.putExtra("tokenType", tokenType);
                i.putExtra("phase", "P1");
                startActivity(i);
                finish();
            }
            if (customerStatusName.equalsIgnoreCase("Data Received P1")) {
                Intent i = new Intent(SignIn.this, SignUp.class);
                i.putExtra("name", name);
                i.putExtra("email", email);
                i.putExtra("custID", custID);
                i.putExtra("accessToken", accessToken);
                i.putExtra("refreshToken", refreshToken);
                i.putExtra("tokenType", tokenType);
                i.putExtra("phase", "P2");
                startActivity(i);
                finish();
            }
            if (customerStatusName.equalsIgnoreCase("Data Received P2")) {
                Intent i = new Intent(SignIn.this, SignUp.class);
                i.putExtra("name", name);
                i.putExtra("email", email);
                i.putExtra("custID", custID);
                i.putExtra("accessToken", accessToken);
                i.putExtra("refreshToken", refreshToken);
                i.putExtra("tokenType", tokenType);
                i.putExtra("phase", "P3");
                startActivity(i);
                finish();
            }
            if (customerStatusName.equalsIgnoreCase("Data Received") ||
                    customerStatusName.equalsIgnoreCase("Credit Check Pending") ||
                    customerStatusName.equalsIgnoreCase("Data Verification In Progress") ||
                    customerStatusName.equalsIgnoreCase("Partially Verified") ||
                    customerStatusName.equalsIgnoreCase("Data Verification Failed") ||
                    customerStatusName.equalsIgnoreCase("Credit Declined") ||
                    customerStatusName.equalsIgnoreCase("Permanent Block") ||
                    customerStatusName.equalsIgnoreCase("Defaulted Block") ||
                    customerStatusName.equalsIgnoreCase("Temporary Block")) {

                if (customerStatusName.equalsIgnoreCase("Credit Declined") ||
                        customerStatusName.equalsIgnoreCase("Permanent Block") ||
                        customerStatusName.equalsIgnoreCase("Defaulted Block") ||
                        customerStatusName.equalsIgnoreCase("Temporary Block")) {
                    Intent i = new Intent(SignIn.this, BlockedUser.class);
                    i.putExtra("name", name);
                    i.putExtra("email", email);
                    i.putExtra("custID", custID);
                    i.putExtra("accessToken", accessToken);
                    i.putExtra("refreshToken", refreshToken);
                    i.putExtra("tokenType", tokenType);
                    CASHe.blocked = 2;
                    startActivity(i);
                    finish();
                } else {
                    Intent i = new Intent(SignIn.this, Verification.class);
                    i.putExtra("name", name);
                    i.putExtra("email", email);
                    i.putExtra("custID", custID);
                    i.putExtra("accessToken", accessToken);
                    i.putExtra("refreshToken", refreshToken);
                    i.putExtra("tokenType", tokenType);
                    startActivity(i);
                    finish();
                }

            }
            if (customerStatusName.equalsIgnoreCase("Payment Overdue") || customerStatusName.equalsIgnoreCase("Cash Requested") || customerStatusName.equalsIgnoreCase("Data Received P3") || customerStatusName.equalsIgnoreCase("Defaulted") || customerStatusName.equalsIgnoreCase("Credit Approved")) {
                Intent i = new Intent(SignIn.this, Dashboard.class);
                i.putExtra("name", name);
                i.putExtra("email", email);
                i.putExtra("custID", custID);
                i.putExtra("accessToken", accessToken);
                i.putExtra("refreshToken", refreshToken);
                i.putExtra("tokenType", tokenType);
                startActivity(i);
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.length == 2 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                SharedPreferences sharedpreferences = SignIn.this.getSharedPreferences("CASHe", Context.MODE_PRIVATE);
                String rdrResponse = sharedpreferences.getString("rdrResponse", null);
                if (null != rdrResponse)
                    reDir(rdrResponse);
            } else
                checkPerms();
        }

    }

    @Override
    public void onBackPressed() {
        if (isTaskRoot()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(true);
            builder.setMessage("Do you want to exit CASHe?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        } else {
            finish();
        }
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

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d("G+", "onConnectionFailed:" + connectionResult);
    }

    @Override
    public void onConnected(Bundle arg0) {
        try {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi
                        .getCurrentPerson(mGoogleApiClient);
                name = currentPerson.getDisplayName();
                email = Plus.AccountApi.getAccountName(mGoogleApiClient);
                String gID = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient).getId();
                uInf = "{accountType: \"" + "googleplus" + "\", socialAccountId: \"" + gID + "\", accessToken: \"" + "access_token" + "\" }";
                auth();
            } else {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
        //updateUI(false);
    }

    /*Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
            new ResultCallback<Status>() {
                @Override
                public void onResult(Status status) {
                    // ...
                }
            });*/
}
