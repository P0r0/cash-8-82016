/*
 * Sample to illustrate encryption plugins for DexGuard.
 *
 * Copyright (c) 2015-2016 GuardSquare NV
 */
package com.guardsquare.samples.plugins.asset;

import com.guardsquare.dexguard.encryption.asset.AssetEncryptionPlugin;
import com.guardsquare.samples.plugins.*;

import java.io.*;

/**
 * This AssetEncryptor encrypts data by combining all bytes in the data
 * with a constant byte using the XOR operation.
 */
public class SimpleConstantXorAssetEncryptionPlugin
extends      AssetEncryptionPlugin<Void, Void>
{
    private static final byte OBFUSCATION_CONSTANT = 76;


    // Implementations for AssetEncryptor.

    @Override
    public OutputStream encryptAsset(OutputStream outputStream,
                                     Void         sharedEncryptionKey,
                                     Void         assetEncryptionKey  ) throws IOException
    {
        return new XorOutputStream(outputStream, OBFUSCATION_CONSTANT);
    }


    @Override
    public InputStream decryptAsset(InputStream inputStream,
                                    Void        sharedEncryptionKey,
                                    Void        assetEncryptionKey  ) throws IOException
    {
        return new XorInputStream(inputStream, OBFUSCATION_CONSTANT);
    }
}
