/*
 * Sample to illustrate encryption plugins for DexGuard.
 *
 * Copyright (c) 2015-2016 GuardSquare NV
 */
package com.guardsquare.samples.plugins.nativelibrary;

import com.guardsquare.dexguard.encryption.nativelibrary.NativeLibraryEncryptionPlugin;
import com.guardsquare.samples.plugins.*;

import java.io.*;
import java.util.Set;

/**
 * This NativeLibraryEncryptor encrypts data by combining all bytes in the data
 * with a constant byte using the XOR operation.
 * The constant byte itself is the result of the XOR operation on a shared byte and
 * a library-specific byte.
 */
public class SimpleXorNativeLibraryEncryptionPlugin
extends      NativeLibraryEncryptionPlugin<SimpleXorNativeLibraryEncryptionPlugin.SharedEncryptionKey,
                                           SimpleXorNativeLibraryEncryptionPlugin.EncryptionKey>
{
    // Implementations for NativeLibraryEncryptor.

    @Override
    public SharedEncryptionKey createNativeLibrarySharedEncryptionKey(long        seed,
                                                                      Set<String> libraryNames)
    {
        SharedEncryptionKey encryptionKey = new SharedEncryptionKey();
        encryptionKey.parameter = (byte) seed;
        return encryptionKey;
    }


    @Override
    public EncryptionKey createNativeLibraryEncryptionKey(SharedEncryptionKey sharedEncryptionKey,
                                                          long                seed,
                                                          String              libraryName,
                                                          long                approximateFileSize)
    {
        EncryptionKey encryptionKey = new EncryptionKey();
        encryptionKey.parameter = (byte) seed;
        return encryptionKey;
    }


    @Override
    public OutputStream encryptNativeLibrary(OutputStream        outputStream,
                                             SharedEncryptionKey sharedEncryptionKey,
                                             EncryptionKey       encryptionKey       ) throws IOException
    {
        byte combinedXorValue =
            (byte) (sharedEncryptionKey.parameter ^ encryptionKey.parameter);

        return new XorOutputStream(outputStream, combinedXorValue);
    }


    @Override
    public InputStream decryptNativeLibrary(InputStream         inputStream,
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
