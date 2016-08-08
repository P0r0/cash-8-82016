/*
 * Sample to illustrate encryption plugins for DexGuard.
 *
 * Copyright (c) 2015-2016 GuardSquare NV
 */
package com.guardsquare.samples.plugins.string;

import com.guardsquare.dexguard.encryption.string.StringEncryptionPlugin;

import java.util.*;

/**
 * This StringEncryptor concatenates all Strings in a group in a single
 * byte array, and reverses their contents. The byte array is stored in
 * the shared encryption key, whereas the offset and length of each
 * individual string is stored in the encrypted string data.
 */
public class ReversedArrayStringEncryptionPlugin
extends      StringEncryptionPlugin<ReversedArrayStringEncryptionPlugin.SharedEncryptionKey,
                                    ReversedArrayStringEncryptionPlugin.EncryptedString>
{
    // Implementations for StringEncryptor.

    @Override
    public SharedEncryptionKey createStringSharedEncryptionKey(long        groupSeed,
                                                               Set<String> strings   )
    {
        // Compute the total size of all strings.
        int totalLength = 0;
        for (String string : strings)
        {
            totalLength += string.length();
        }

        // Store alll strings in a character array, reversed.
        // Remember all offsets.
        char[] encodedBytes = new char[totalLength];
        Map<String, Integer> offsets = new HashMap<String,Integer>();
        int offset = 0;
        for (String string : strings)
        {
            offsets.put(string, offset);
            for (int i = string.length() -1; i >= 0; i--)
            {
                encodedBytes[offset++] = string.charAt(i);
            }
        }

        // Store all information in a shared encryption key object.
        SharedEncryptionKey sharedEncryptionKey = new SharedEncryptionKey();
        sharedEncryptionKey.encodedCharacters   = encodedBytes;
        sharedEncryptionKey.offsets             = offsets;

        return sharedEncryptionKey;
    }


    @Override
    public EncryptedString encryptString(SharedEncryptionKey sharedEncryptionKey,
                                         long                stringSeed,
                                         String              string              )
    {
        // The string character data is already stored in the shared
        // encryption key. We only need to remember the offset and length.
        EncryptedString encryptedString = new EncryptedString();
        encryptedString.offset          = sharedEncryptionKey.offsets.get(string);
        encryptedString.length          = string.length();
        return encryptedString;
    }


    @Override
    public String decryptString(SharedEncryptionKey sharedEncryptionKey,
                                EncryptedString     encryptedString     )
    {
        StringBuilder builder = new StringBuilder();
        for (int i = encryptedString.length - 1; i >= 0; i--)
        {
            builder.append(sharedEncryptionKey.encodedCharacters[encryptedString.offset + i]);
        }
        return builder.toString();
    }


    public static class SharedEncryptionKey
    {
        char[] encodedCharacters;

        // This field is only used during encryption - it will not be present
        // in the final byte code.
        Map<String,Integer> offsets;
    }


    public static class EncryptedString
    {
        int offset;
        int length;
    }
}
