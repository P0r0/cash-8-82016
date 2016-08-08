/*
 * Sample application to illustrate SSL pinning with DexGuard.
 *
 * Copyright (c) 2012-2016 GuardSquare NV
 */
package com.example;

import java.io.InputStream;
import java.security.KeyStore;

import android.content.Context;

/**
 * This factory creates a trust store from a key store bks-file in the assets.
 *
 * You can create your own trust store file as follows.
 *
 * If your application connects to an existing external server, you can
 * retrieve the certificates from the service with the openssl tool.
 * Depending on the availability of intermediate certificates on the mobile
 * device, it might be necessary to download the entire certificate chain into
 * the keystore. The Wikipedia sample below does require both certificates of
 * the chain to be in the store. You should adapt the provided steps for your
 * specific use case.
 *
 * On Unix/Linux, for www.wikipedia.com:
 *
 *     openssl s_client -showcerts -connect www.wikipedia.com:443 </dev/null 2> /dev/null |\
 *     awk '/BEGIN/{p=1;fn="cert"++i".pem"} /END/{p=0;print>fn} p{print>fn}'
 *
 * This step creates separate files for each of the certificates in the chain. The
 * files will be named cert1.pem, cert2.pem,...
 *
 * Download:
 *
 *     http://www.bouncycastle.org/download/bcprov-jdk15on-146.jar
 *
 * Don't download a later release, otherwise the Android runtime will throw an
 * exception ("java.io.IOException: Wrong version of key store.").
 *
 * Create a trust store with the previously generated certificate(s). Add each of
 * the certificates (if required) as follows:
 *
 *     keytool -importcert -v -trustcacerts -file cert1.pem -keystore myapptruststore.bks \
 *         -provider org.bouncycastle.jce.provider.BouncyCastleProvider \
 *         -providerpath bcprov-jdk15on-146.jar -noprompt \
 *         -storetype BKS -storepass mysecretpassword -alias cert1
 *
 *     keytool -importcert -v -trustcacerts -file cert2.pem -keystore myapptruststore.bks \
 *         -provider org.bouncycastle.jce.provider.BouncyCastleProvider \
 *         -providerpath bcprov-jdk15on-146.jar -noprompt \
 *         -storetype BKS -storepass mysecretpassword -alias cert2
 *
 * Optionally list the certificates in your trust store to verify:
 *
 *     keytool -list -v -keystore myapptruststore.bks -storepass mysecretpassword \
 *          -storetype BKS -provider org.bouncycastle.jce.provider.BouncyCastleProvider \
 *          -providerpath bcprov-jdk15on-146.jar
 *
 * Copy the trust store .bks file to the assets directory.
 */
public class TrustStoreFactory
{
    // The name and password of the trust store.
    //
    // You should let DexGuard encrypt this name and password in the code, and
    // the trust store file in the assets. In dexguard-project.txt:
    //
    //     -encryptstrings class com.example.TrustStoreFactory {
    //         java.lang.String TRUST_STORE_ASSET_NAME;
    //         java.lang.String TRUST_STORE_PASSWORD;
    //     }
    //
    //     -encryptassetfiles assets/myapptruststore.bks

    private static final String TRUST_STORE_ASSET_NAME = "myapptruststore.bks";
    private static final String TRUST_STORE_PASSWORD   = "mysecretpassword";


    private Context context;


    public TrustStoreFactory(Context context)
    {
        this.context = context;
    }


    public KeyStore createTrustStore() throws Throwable
    {
        // Retrieve the trust store file from the assets.
        InputStream inputStream = context.getAssets().open(TRUST_STORE_ASSET_NAME);
        try
        {
            // Create a key store with the retrieved input stream.
            KeyStore trustStore = KeyStore.getInstance("BKS");

            trustStore.load(inputStream, TRUST_STORE_PASSWORD.toCharArray());

            return trustStore;
        }
        finally
        {
                inputStream.close();
        }
    }
}
