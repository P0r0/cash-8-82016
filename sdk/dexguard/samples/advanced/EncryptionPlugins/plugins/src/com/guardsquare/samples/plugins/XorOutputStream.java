/*
 * Sample to illustrate encryption plugins for DexGuard.
 *
 * Copyright (c) 2015-2016 GuardSquare NV
 */
package com.guardsquare.samples.plugins;

import java.io.*;

/**
 * InputStream that combines each written byte with another, constant value
 * using the XOR operator.
 */
public class XorOutputStream
extends      FilterOutputStream
{
    private final byte xorValue;


    public XorOutputStream(OutputStream outputStream,
                           byte         xorValue     )
    {
        super(outputStream);
        this.xorValue = xorValue;
    }


    // Implementations for InputStream.

    public void write(int i) throws IOException
    {
        out.write(i ^ xorValue);
    }
}
