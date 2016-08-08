/*
 * Sample application to illustrate SSL pinning with DexGuard.
 *
 * Copyright (c) 2012-2016 GuardSquare NV
 */
package com.example;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * Sample activity that shows a web view with SSL pinning.
 *
 * If your application connects to an existing external server, you can
 * print out Java code with the MD5 hashes of the keys of the server, with
 * DexGuard's public key pinning tool. For example, for www.wikipedia.org:
 *
 *     bin/hash_certificate.sh "https://www.wikipedia.org"
 *
 * You can then copy/paste the Java code from the output, to create an instance
 * of com.guardsquare.dexguard.runtime.net.PublicKeyTrustManager for the given
 * server(s).
 *
 *
 * On the other hand, if you have your own X509 certificates, you can print
 * out the public keys, for instance with:
 *
 *     openssl x509 -inform pem -in wikipedia.pem -pubkey -noout
 * or
 *     openssl x509 -inform der -in wikipedia.der -pubkey -noout
 *
 * You can then print out the Java code with the MD5 hashes of these public
 * keys, for instance with:
 *
 *     bin/hash_certificate.sh "MII.....lHy"
 *
 * The tool ignores spaces and newlines in the public key strings, for easier
 * copy/pasting. You can then again copy/paste the Java code from the output,
 * to create an instance of
 * com.guardsquare.dexguard.runtime.net.PublicKeyTrustManager for the given
 * key(s).
 *
 * It is also possible to use the tool for multiple servers or public keys at once:
 *
 *     bin/hash_certificate.sh "https://wwww.wikipedia.org" "MII.....lHy" ...
 */
public class SampleStartActivity extends Activity
{
    private final String url =
        "https://upload.wikimedia.org/wikipedia/en/b/bc/Wiki.png";

    // Public key hashes for url.
    private final String[] publicKeyHashes = new String[]
    {
    //  "608B0CAFF0A5F6AA7CA39BE7E6575F61",      // CN=GlobalSign Organization Validation CA - SHA256 - G2, O=GlobalSign nv-sa, C=BE
        "85B6F68800213565C1283EBF36590E13",      // CN=*.wikipedia.org, O="Wikimedia Foundation, Inc.", L=San Francisco, ST=California, C=US
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Some layout work.
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        Button btnSimple = new Button(this);
        Button btnExtended = new Button(this);

        // Set listeners
        btnSimple.setText("SSL check after handshake.");
        btnSimple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(PinningWebViewActivity.createSimplePinningWebViewIntent(
                        SampleStartActivity.this, url, publicKeyHashes
                ));
            }
        });

        btnExtended.setText("SSL check before handshake.");
        btnExtended.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(PinningWebViewActivity.createPinningWebViewIntent(
                        SampleStartActivity.this, url, publicKeyHashes
                ));
            }
        });

        // Set layout to have the webview.
        layout.addView(btnSimple);
        layout.addView(btnExtended);
        setContentView(layout);
    }
}
