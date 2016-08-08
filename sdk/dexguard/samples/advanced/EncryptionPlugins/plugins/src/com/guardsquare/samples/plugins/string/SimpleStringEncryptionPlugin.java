/*
 * Sample to illustrate encryption plugins for DexGuard.
 *
 * Copyright (c) 2015-2016 GuardSquare NV
 */
package com.guardsquare.samples.plugins.string;

import com.guardsquare.dexguard.encryption.string.StringEncryptionPlugin;
import com.guardsquare.samples.plugins.XorUtil;

/**
 * This StringEncryptor converts the String to a char array and XOR's it
 * with a given constant.
 */
public class SimpleStringEncryptionPlugin
extends      StringEncryptionPlugin<Void, SimpleStringEncryptionPlugin.EncryptedString>
{
    private static final char OBFUSCATION_CONSTANT = 76;

    // Implementations for StringEncryptor.

    @Override
    public EncryptedString encryptString(Void   sharedEncryptionKey,
                                         long   stringSeed,
                                         String string              )
    {
        EncryptedString encryptedString = new EncryptedString();
        encryptedString.data = XorUtil.xor( string.toCharArray(), OBFUSCATION_CONSTANT);
        return encryptedString;
    }


    @Override
    public String decryptString(Void            sharedEncryptionKey,
                                EncryptedString encryptedString     )
    {
        return new String(XorUtil.xor(encryptedString.data, OBFUSCATION_CONSTANT));
    }


    public static class EncryptedString
    {
        char[] data;
    }
}
