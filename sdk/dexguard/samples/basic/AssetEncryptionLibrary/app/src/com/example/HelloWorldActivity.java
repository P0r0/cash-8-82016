/*
 * Sample application to illustrate asset encryption with DexGuard.
 *
 * Copyright (c) 2012-2016 GuardSquare NV
 */
package com.example;

import android.app.*;
import android.os.Bundle;
import android.view.*;
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
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();

            transaction.add(R.id.top_frame, new LibraryAssetFragment());
            transaction.add(R.id.bottom_frame, new AssetFragment());

            transaction.commit();
        }

        // Briefly display a comment.
        Toast.makeText(this, "DexGuard has encrypted the asset files of this sample", Toast.LENGTH_LONG).show();
    }
}
