/*
 * Sample application to illustrate debug detection, emulator detection, and
 * root detectiom with DexGuard.
 *
 * Copyright (c) 2012-2016 GuardSquare NV
 */
package com.example;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.*;

import com.guardsquare.dexguard.runtime.detection.*;

/**
 * This Activity performs some environment checks.
 */
public class EnvironmentCheckActivity extends Activity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        TextView view = new TextView(this);
        new Delegate(view).execute();
        setContentView(view);
    }


    /**
     * This utility class performs debug detection, emulator detection, and
     * root detection, and sets up the view. If the environment is okay, the
     * application runs normally and displays "The environment is okay".
     * Otherwise, it displays information about the environment.
     *
     * We're putting this functionality in a separate class, so we can run it
     * in the background and so we can encrypt it, as an extra layer of
     * protection around the tamper detection and some essential code. We
     * can't encrypt the activity itself, for technical reasons, but an inner
     * class or another class are fine.
     *
     * The Delegate class is implemented as an asynchronous task.
     * This way we ensure that the little overhead, introduced by the
     * environment checks, does not affect the main application thread.
     */
    private class Delegate extends AsyncTask<Void,Void,String>
    {
        private TextView view;

        /**
         * We initialize a new Delegate instance with a TextView instance that
         * will be updated after the environment check.
         */
        public Delegate(TextView view)
        {
            this.view = view;
        }

        /**
         * This method will run in a separate thread.
         */
        @Override
        protected String doInBackground(Void... voids)
        {
            // We need a context for most methods.
            final Context context = EnvironmentCheckActivity.this;

            // You can pick your own value or values for OK,
            // to make the code less predictable.
            final int OK = 1;

            // Let the DexGuard runtime library detect whether the application
            // is debuggable. The return code equals OK if it is not.
            int isDebuggable = DebugDetector.isDebuggable(context, OK);

            // Let the DexGuard runtime library detect whether the a debugger
            // is attached to the application. The return code is OK if not so.
            int isDebuggerConnected = DebugDetector.isDebuggerConnected(OK);

            // Let the DexGuard runtime library detect whether the app is
            // signed with a debug key. The return code equals OK if not so.
            int isSignedWithDebugKey = DebugDetector.isSignedWithDebugKey(context, OK);

            // Let the DexGuard runtime library detect whether the app is
            // running in an emulator. The return code is OK if not so.
            int isRunningInEmulator = EmulatorDetector.isRunningInEmulator(context, OK);

            // Let the DexGuard runtime library detect whether the app is
            // running on a rooted device. The return code is OK if not so.
            int isDeviceRooted = RootDetector.isDeviceRooted(OK);

            // Return the result as a string.
            return (isDebuggable         != OK ? "The application is debuggable.\n"              : "") +
                   (isDebuggerConnected  != OK ? "A debugger is connected.\n"                    : "") +
                   (isSignedWithDebugKey != OK ? "The application is signed with a debug key.\n" : "") +
                   (isRunningInEmulator  != OK ? "The application is running on an emulator.\n"  : "") +
                   (isDeviceRooted       != OK ? "Device is rooted.\n"                           : "");
        }

        /**
         * This method is automatically called after the background thread has
         * finished executing. The result is passed as a parameter.
         */
        @Override
        protected void onPostExecute(String result)
        {
            view.setText(result);

            if (TextUtils.isEmpty(result))
            {
                view.setText("The environment is okay");
            }

            view.setPadding(10, 10, 10, 10);
            view.setGravity(Gravity.CENTER_VERTICAL);

            // Change the background color if there is an alert.
            if (!TextUtils.isEmpty(result))
            {
                view.setBackgroundColor(Color.RED);
            }
            else
            {
                view.setBackgroundColor(Color.GREEN);
            }
        }
    }
}
