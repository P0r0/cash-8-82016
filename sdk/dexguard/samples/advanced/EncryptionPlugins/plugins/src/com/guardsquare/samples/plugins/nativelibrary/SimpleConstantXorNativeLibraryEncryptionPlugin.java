/*
 * Sample to illustrate encryption plugins for DexGuard.
 *
 * Copyright (c) 2015-2016 GuardSquare NV
 */
package com.guardsquare.samples.plugins.nativelibrary;

import com.guardsquare.dexguard.encryption.nativelibrary.NativeLibraryEncryptionPlugin;
import com.guardsquare.samples.plugins.*;

import java.io.*;

/**
 * This NativeLibraryEncryptor encrypts data by combining all bytes in the data
 * with a constant byte using the XOR operation.
 */
public class SimpleConstantXorNativeLibraryEncryptionPlugin
extends      NativeLibraryEncryptionPlugin<Void, Void>
{
    private static final byte OBFUSCATION_CONSTANT = 76;


    // Implementations for NativeLibraryEncryptor.

    @Override
    public OutputStream encryptNativeLibrary(OutputStream outputStream,
                                             Void         sharedEncryptionKey,
                                             Void         encryptionKey) throws IOException
    {
        return new XorOutputStream(outputStream, OBFUSCATION_CONSTANT);
    }


    @Override
    public InputStream decryptNativeLibrary(InputStream inputStream,
                                            Void        sharedEncryptionKey,
                                            Void        encryptionKey) throws IOException
    {
        return new XorInputStream(inputStream, OBFUSCATION_CONSTANT);
    }
}
