/*
 * Sample application to illustrate SSL pinning with DexGuard.
 *
 * Copyright (c) 2012-2016 GuardSquare NV
 */
package com.example;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.guardsquare.dexguard.runtime.net.SSLPinningWebViewClient;
import com.guardsquare.dexguard.runtime.net.SimpleSSLPinningWebViewClient;
import com.guardsquare.dexguard.runtime.net.WrongSSLCertificateListener;

/**
 * Sample activity that shows a web view with SLL pinning.
 */
public class PinningWebViewActivity extends Activity
{
    public static final String EXTRA_URL    = "extraUrl";
    public static final String EXTRA_HASHES = "extraHashes";
    public static final String EXTRA_SIMPLE = "extraSimple";


    /**
     * Convenience method to start a PinningWebViewActivity with simple
     * SSLCertificate verification. This kind of pinning is faster but only
     * verifies after the page has already been loaded.
     *
     * @param context   the application context.
     * @param url       the URL to load.
     * @param keyHashes hashes of the public keys to accept.
     * @return intent to start the requested activity with.
     */
    public static Intent createSimplePinningWebViewIntent(Context  context,
                                                          String   url,
                                                          String[] keyHashes)
    {
        return createGenericPinningWebviewIntent(context, url, keyHashes, true);
    }


    /**
     * Convenience method to start a PinningWebViewActivity with regular
     * SSLCertificate verification. This method has some drawbacks; read the
     * comments in SSLPinningWebViewClient to learn more.
     *
     * @param context   the application context.
     * @param url       URL to load.
     * @param keyHashes hashes of the public keys to accept.
     * @return intent to start the requested activity with.
     */
    public static Intent createPinningWebViewIntent(Context  context,
                                                    String   url,
                                                    String[] keyHashes)
    {
        return createGenericPinningWebviewIntent(context, url, keyHashes, false);
    }


    private static Intent createGenericPinningWebviewIntent(Context  context,
                                                            String   url,
                                                            String[] keyHashes,
                                                            boolean  simple)
    {
        Intent intent = new Intent(context, PinningWebViewActivity.class);
        intent.putExtra(EXTRA_URL, url);
        intent.putExtra(EXTRA_HASHES, keyHashes);
        intent.putExtra(EXTRA_SIMPLE, simple);

        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Get arguments.
        String   url       = getIntent().getStringExtra(EXTRA_URL);
        String[] keyHashes = getIntent().getStringArrayExtra(EXTRA_HASHES);
        boolean  simple    = getIntent().getBooleanExtra(EXTRA_SIMPLE, false);

        // Create a web view with the main page.
        WebView webView = new WebView(this);

        // Get the correct WebViewClient
        WebViewClient client = simple ?
            createSimplePinningClient(webView, keyHashes) :
            createPinningClient(webView, keyHashes);

        webView.setWebViewClient(client);

        // Set layout to have the webview and load the content.
        webView.loadUrl(url);
        setContentView(webView);
    }


    /**
     * Creates and configures a SimpleSSLPinningWebViewClient.
     *
     * This kind of SSLPinning is simpler and does not interfere too much with
     * the structure of the WebView. The content will be fetched even if the
     * certificate is wrong; the certificate is only checked aftwerwards. If
     * the certificate is rejected, the content is cleared again.
     *
     * For some apps, this might not be secure enough. They require the
     * certificate to be checked before any content is fetched. In that case,
     * please use an instance of our SSLPinningWebViewClient.
     */
    private WebViewClient createSimplePinningClient(final    WebView view,
                                                    String[] publicKeyHashes)
    {
        // Set our desired certificates.
        final SimpleSSLPinningWebViewClient webClient = new SimpleSSLPinningWebViewClient(publicKeyHashes);

        // Callback for failed certificates.
        webClient.addWrongCertificateListener(new WrongSSLCertificateListener()
        {
            @Override
            public void onWrongCertificate()
            {
                Toast.makeText(PinningWebViewActivity.this,
                               "Wrong certificate detected! Clearing page.",
                               Toast.LENGTH_LONG).show();
            }
        });

        return webClient;
    }


    /**
     * Creates and configures an SSLPinningWebViewClient.
     *
     * This implementation of the WebViewClient reroutes https requests to
     * check the SSL certificates against a predefined list of accepted
     * certificates. This approach has some drawbacks, the most significant
     * ones are:
     * - Requests can sometimes not be fully replicated, because of missing data
     *   e.g. HTTP headers and method,...
     * - The WebView is still under the impression it loaded the initial URL.
     *   This could cause base_url issues and have strange effects on relative
     *   paths in the webpage.
     */
    private WebViewClient createPinningClient(WebView  view,
                                              String[] publicKeyHashes)
    {
        // Set our desired certificates.
        SSLPinningWebViewClient webClient = new SSLPinningWebViewClient(publicKeyHashes);

        // Callback for failed certificates.
        webClient.addWrongCertificateListener(new WrongSSLCertificateListener()
        {
            @Override
            public void onWrongCertificate()
            {
                // Note that this callback is not forced on the UI thread in our
                // example. To interact with it, we need to force our code on
                // that thread manually.
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Toast.makeText(getApplicationContext(),
                                       "Wrong certificate, page will not load.",
                                       Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        return webClient;
    }
}
