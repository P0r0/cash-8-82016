package co.tslc.cashe.android;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import io.fabric.sdk.android.Fabric;

public class CASHe extends Application {
    private static CASHe instance;
    private Tracker mTracker;
    public boolean isInForeground = true;
    public boolean sessionExpired = false;
    public static String webApiUrl;
    public static int blocked = 1;
    public static boolean oneRequest = true;
    public static String validPromoCode = "";

    //private RequestQueue rQ;

    private String accessToken;
    private long expiryTime;
    private String tokenType;

    @Override
    public void onCreate()
    {
        super.onCreate();
       Fabric.with(this, new Crashlytics());
        instance = this;
        webApiUrl=getResources().getString(R.string.webApiUrl);
        //getkey();
        //rQ = Volley.newRequestQueue(getApplicationContext());
    }

    public void setAccessToken(String token) {this.accessToken = token;}
    public void setExpiryTime(int expiry) {
        this.expiryTime = (System.currentTimeMillis()/1000) + expiry;
    }
    public void setTokenType(String type) {this.tokenType = type;}

    public String getAccessToken() {return this.accessToken;}
    public long getExpiryTime() {return this.expiryTime;}
    public String getTokenType() {return this.tokenType;}

    public static synchronized CASHe getInstance(){
        if(instance==null){
            instance=new CASHe();
        }
        return instance;
    }
    void getkey(){
        try {
            PackageInfo info = getPackageManager().getPackageInfo("co.tslc.cashe.android", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String sign= Base64.encodeToString(md.digest(), Base64.DEFAULT);
                Log.d("MY KEY HASH:", sign);
                Toast.makeText(getApplicationContext(), sign, Toast.LENGTH_LONG).show();
            }
        } catch (PackageManager.NameNotFoundException e) {
        } catch (NoSuchAlgorithmException e) {
        }
    }
    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(R.xml.global_tracker);
        }
        return mTracker;
    }
}
