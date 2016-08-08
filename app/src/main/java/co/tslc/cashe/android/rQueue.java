package co.tslc.cashe.android;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.android.volley.Request;

import java.util.List;

public class rQueue {

    protected CASHe app;

    private int CONNECTION_TIMEOUT = 30000;

    private static rQueue mInstance;
    private RequestQueue mRequestQueue;
    //private ImageLoader mImageLoader;
    private static Context mCtx;

    private boolean defaultAuthReqValue = false;

    private rQueue(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();

        /*mImageLoader = new ImageLoader(mRequestQueue,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap>
                            cache = new LruCache<String, Bitmap>(20);

                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                });*/
    }

    public static synchronized rQueue getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new rQueue(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void queueRequest(String currAct,Request<T> req) {
        app = (CASHe) mCtx.getApplicationContext();
        req.setRetryPolicy(new DefaultRetryPolicy(
                CONNECTION_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        if(!req.getUrl().contains("/authenticate")) {
            if (app.getExpiryTime() <= (System.currentTimeMillis() / 1000)) {
                if(!isAppIsInBackground(mCtx)) {
                    if(!currAct.equalsIgnoreCase("SignIn")) {
                        if (!app.sessionExpired) {
                            app.sessionExpired = true;
                            Toast.makeText(mCtx, "Your session expired. Please re-login", Toast.LENGTH_LONG).show();
                            Intent i = new Intent(mCtx, SignIn.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            mCtx.startActivity(i);
                            ((Activity) mCtx).finish();
                        }
                    }
                }
            } else
                getRequestQueue().add(req);
        }
        else
            getRequestQueue().add(req);
    }

    private boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

    /*public ImageLoader getImageLoader() {
        return mImageLoader;
    }*/
}