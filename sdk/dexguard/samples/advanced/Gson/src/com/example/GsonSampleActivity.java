/*
 * Sample application to illustrate processing with DexGuard.
 *
 * Copyright (c) 2012-2016 GuardSquare NV
 */
package com.example;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.*;

import com.google.gson.*;

/**
 * Sample activity that displays a JSON string.
 */
public class GsonSampleActivity extends Activity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Create an object instance.
        //Person person =
        //    new Person(
        //        new Name("John", "Smith"),
        //        Sex.MALE,
        //        new Address(1, "Main Street", "New York City", "NY", "USA"));

        Gson gson = new GsonBuilder().create();

        // Convert JSON to an object instance.
        Person person = gson.fromJson(
            "{" +
            "  name:    {last:\"Smith\",first:\"John\"}," +
            "  sex:     male," +
            "  address: {street:\"Main Street\",city:\"New York City\",zip:\"NY\",country:\"USA\",number:1}" +
            "}",
            Person.class);

        // Convert the object instance.
        String jsonString = gson.toJson(person);

        // Display the message.
        TextView view = new TextView(this);
        view.setText("JSON = " + jsonString);
        view.setGravity(Gravity.CENTER);
        setContentView(view);

        // Briefly display a comment.
        Toast.makeText(this, "DexGuard has processed the GSON code in this sample", Toast.LENGTH_LONG).show();
    }
}
