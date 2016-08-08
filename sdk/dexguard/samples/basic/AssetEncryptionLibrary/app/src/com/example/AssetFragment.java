/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example;

import android.app.*;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.io.*;

public class AssetFragment extends Fragment {

    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        try {
            Activity activity = getActivity();

            // Open the message asset. DexGuard will encrypt the file for us.
            InputStream stream = activity.getAssets().open("message.txt");

            // Read the message from the stream.
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String message = reader.readLine();
            reader.close();

            // Display the message.
            TextView view = new TextView(activity);
            view.setText(message);
            view.setGravity(Gravity.CENTER);

            return view;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
