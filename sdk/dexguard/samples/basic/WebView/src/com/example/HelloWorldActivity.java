/*
 * Sample application to illustrate encryption of local WebView contents
 * with DexGuard.
 *
 * Copyright (c) 2012-2016 GuardSquare NV
 */
package com.example;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.guardsquare.dexguard.runtime.encryption.EncryptedWebViewClient;

/**
 * Sample activity that shows a web view.
 */
public class HelloWorldActivity extends Activity
{
    // An arbitrary http URL prefix to refer to local assets
    // (encrypted or not).
    private static final String ENCRYPTED_ASSET_PREFIX = "file://_/";


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Create a web view with the main page.
        WebView webView = new WebView(this);

        // Create a web view client for encrypted assets.
        webView.setWebViewClient(new EncryptedWebViewClient(new WebViewClient(),
                                                            getAssets(),
                                                            ENCRYPTED_ASSET_PREFIX,
                                                            new MyAssetInputStreamFactory()));

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.loadUrl(ENCRYPTED_ASSET_PREFIX + "index.html");

        setContentView(webView);
    }
}
