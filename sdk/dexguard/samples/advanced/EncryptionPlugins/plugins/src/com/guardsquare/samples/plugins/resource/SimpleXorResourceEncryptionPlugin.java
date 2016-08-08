/*
 * Sample to illustrate encryption plugins for DexGuard.
 *
 * Copyright (c) 2015-2016 GuardSquare NV
 */
package com.guardsquare.samples.plugins.resource;

import com.guardsquare.dexguard.encryption.resource.ResourceEncryptionPlugin;
import com.guardsquare.samples.plugins.*;

/**
 * This ResourceEncryptor encrypts data by combining all bytes in the data
 * with a constant byte using the XOR operation.
 * The constant byte itself is the result of the XOR operation on the
 * resource id, combined with the filename length.
 */
public class SimpleXorResourceEncryptionPlugin
extends      ResourceEncryptionPlugin
{
    // Implementations for ResourceEncryptor.

    @Override
    public byte[] encryptResource(byte[] bytes,
                                  int    resourceId,
                                  String fileName  )
    {
        byte combinedXorValue = (byte) (resourceId ^ fileName.length());

        return XorUtil.xor(bytes, combinedXorValue);
    }


    @Override
    public byte[] decryptResource(byte[] encryptedBytes,
                                  int    resourceId,
                                  String fileName       )
    {
        byte combinedXorValue = (byte) (resourceId ^ fileName.length());

        return XorUtil.xor(encryptedBytes, combinedXorValue);
    }
}
