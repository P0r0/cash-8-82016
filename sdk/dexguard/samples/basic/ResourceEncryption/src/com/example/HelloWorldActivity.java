/*
 * Sample application to illustrate asset encryption with DexGuard.
 *
 * Copyright (c) 2012-2016 GuardSquare NV
 */
package com.example;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.*;

import java.io.*;

/**
 * Sample activity that displays "Hello world!".
 */
public class HelloWorldActivity extends Activity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Initialize the activity's layout from an encrypted resource.
        setContentView(R.layout.main);

        // Briefly display a comment.
        Toast.makeText(this, "DexGuard has encrypted the layout resource of this sample.", Toast.LENGTH_LONG).show();
    }

}
