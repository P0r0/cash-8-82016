/*
 * Sample application to illustrate SSL pinning with DexGuard.
 *
 * Copyright (c) 2012-2016 GuardSquare NV
 */
package com.example;

import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

/**
 * This sample class creates a javax.net.ssl.HttpsURLConnection with public key
 * pinning.
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
public class PinnedPublicKeyHttpsURLConnectionFactory
{
    public HttpsURLConnection createHttpsURLConnection(String urlString)
    throws Throwable
    {
        // Code copied from running tools/server_public_key_pinning_code.sh.
        // In this example, we are choosing to only trust and pin the public
        // key of wikipedia.org.

        // Create a TrustManager that only accepts servers with the specified public key hashes.
        com.guardsquare.dexguard.runtime.net.PublicKeyTrustManager trustManager =
            new com.guardsquare.dexguard.runtime.net.PublicKeyTrustManager(new int[] {
                //0x608b0caf, 0xf0a5f6aa, 0x7ca39be7, 0xe6575f61,  // CN=GlobalSign Organization Validation CA - SHA256 - G2, O=GlobalSign nv-sa, C=BE
                0x85b6f688, 0x213565, 0xc1283ebf, 0x36590e13,      // CN=*.wikipedia.org, O="Wikimedia Foundation, Inc.", L=San Francisco, ST=California, C=US
        });

        TrustManager[] trustManagers = new TrustManager[] { trustManager };

        // Initialize the SSL context.
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustManagers, null);

        // Create the URL connection.
        URL url = new URL(urlString);
        HttpsURLConnection urlConnection = (HttpsURLConnection)url.openConnection();
        urlConnection.setSSLSocketFactory(sslContext.getSocketFactory());

        return urlConnection;
    }
}
