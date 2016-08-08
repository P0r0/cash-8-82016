/*
 * Sample application to illustrate class encryption with DexGuard.
 *
 * Copyright (c) 2012-2016 GuardSquare NV
 */
package com.example;

/**
 * Sample class that contains sensitive algorithms or implementations that need
 * to be hidden. DexGuard will encrypt it (see dexguard-project.txt).
 */
public class SecretClass
{
    public String getMessage()
    {
        // We're also encrypting these strings (see dexguard-project.txt),
        // to add another layer of obfuscation inside the encrypted class.
        return Math.random() > 0.5 ?
            "This is a string in an encrypted class." :
            "This is a String in an encrypted class.";
    }
}
