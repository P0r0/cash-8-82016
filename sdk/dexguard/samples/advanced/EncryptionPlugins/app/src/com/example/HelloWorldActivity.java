/*
 * Sample application to illustrate class encryption with DexGuard.
 *
 * Copyright (c) 2012-2016 GuardSquare NV
 */
package com.example;

import android.app.Activity;
import android.os.Bundle;
import android.widget.*;

import java.io.*;

import com.example.jni.NativeSecret;

/**
 * Sample activity that displays "Hello world!".
 */
public class HelloWorldActivity extends Activity
{
    private static final String MESSAGE = "This is an encrypted string in code.";


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Initialize the activity's layout from an encrypted resource.
        setContentView(R.layout.main);

        // Read and display a message from an encrypted asset.
        TextView view1 = (TextView)findViewById(R.id.asset_message);
        view1.setText(readAsset());

        // Read and display a message from an encrypted native library.
        TextView view2 = (TextView)findViewById(R.id.native_library_message);
        view2.setText(new NativeSecret().getMessage());

        // Read and display a message from an encrypted class.
        TextView view3 = (TextView)findViewById(R.id.class_message);
        view3.setText(new SecretClass().getMessage());

        // Display a message from an encrypted string.
        TextView view4 = (TextView)findViewById(R.id.string_message);
        view4.setText(MESSAGE);

        // Briefly display a comment.
        Toast.makeText(this, "DexGuard has encrypted resources, assets, native libraries, classes and strings using custom encryption plugins.", Toast.LENGTH_LONG).show();
    }


    private String readAsset()
    {
        try
        {
            // Open the message asset. DexGuard will encrypt the file for us.
            InputStream stream = getAssets().open("message.txt");

            // Read the message from the stream.
            BufferedReader reader  = new BufferedReader(new InputStreamReader(stream));
            String         message = reader.readLine();
            reader.close();

            return message;
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}
