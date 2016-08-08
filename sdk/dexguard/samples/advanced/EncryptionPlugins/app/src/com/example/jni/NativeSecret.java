/*
 * Sample application to illustrate processing with DexGuard.
 *
 * Copyright (c) 2012-2016 GuardSquare NV
 */
package com.example.jni;

/**
 * Sample class that loads a native library and provides a native method.
 */
public class NativeSecret
{
    static {
        System.loadLibrary("secret");
    }


    /**
     * Returns the secret string "Hello world!".
     */
    public native String getMessage();
}
