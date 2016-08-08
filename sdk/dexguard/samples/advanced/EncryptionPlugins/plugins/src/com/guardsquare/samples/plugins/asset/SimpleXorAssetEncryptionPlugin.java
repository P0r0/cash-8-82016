/*
 * Sample to illustrate encryption plugins for DexGuard.
 *
 * Copyright (c) 2015-2016 GuardSquare NV
 */
package com.guardsquare.samples.plugins.asset;

import com.guardsquare.dexguard.encryption.asset.AssetEncryptionPlugin;
import com.guardsquare.samples.plugins.*;

import java.io.*;
import java.util.Set;

/**
 * This AssetEncryptor encrypts data by combining all bytes in the data
 * with a constant byte using the XOR operation.
 * The constant byte itself is the result of the XOR operation on a shared byte and
 * an asset-specific byte.
 */
public class SimpleXorAssetEncryptionPlugin
extends      AssetEncryptionPlugin<SimpleXorAssetEncryptionPlugin.SharedEncryptionKey,
                                   SimpleXorAssetEncryptionPlugin.EncryptionKey>
{
    // Implementations for AssetEncryptor.

    @Override
    public SharedEncryptionKey createAssetSharedEncryptionKey(long        seed,
                                                              Set<String> assetFileNames)
    {
        SharedEncryptionKey encryptionKey = new SharedEncryptionKey();
        encryptionKey.parameter = (byte) seed;
        return encryptionKey;
    }


    @Override
    public EncryptionKey createAssetEncryptionKey(SharedEncryptionKey sharedEncryptionKey,
                                                  long                seed,
                                                  String              assetFileName,
                                                  long                assetFileSize       )
    {
        EncryptionKey encryptionKey = new EncryptionKey();
        encryptionKey.parameter = (byte) assetFileSize;
        return encryptionKey;
    }


    @Override
    public OutputStream encryptAsset(OutputStream        outputStream,
                                     SharedEncryptionKey sharedEncryptionKey,
                                     EncryptionKey       encryptionKey       ) throws IOException
    {
        byte combinedXorValue =
           (byte)(sharedEncryptionKey.parameter ^ encryptionKey.parameter);

        return new XorOutputStream(outputStream, combinedXorValue);
    }


    @Override
    public InputStream decryptAsset(InputStream         inputStream,
                                    SharedEncryptionKey sharedEncryptionKey,
                                    EncryptionKey       encryptionKey       ) throws IOException
    {
        byte combinedXorValue =
            (byte) (sharedEncryptionKey.parameter ^ encryptionKey.parameter);

        return new XorInputStream(inputStream, combinedXorValue);
    }


    public static class SharedEncryptionKey
    {
        public byte parameter;
    }


    public static class EncryptionKey
    {
        public byte parameter;
    }
}
