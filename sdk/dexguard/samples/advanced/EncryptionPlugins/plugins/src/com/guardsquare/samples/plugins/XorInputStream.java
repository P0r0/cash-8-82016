/*
 * Sample to illustrate encryption plugins for DexGuard.
 *
 * Copyright (c) 2015-2016 GuardSquare NV
 */
package com.guardsquare.samples.plugins;

import java.io.*;

/**
 * InputStream that combines each read byte with another, constant value
 * using the XOR operator.
 */
public class XorInputStream
extends      FilterInputStream
{
    private final byte xorValue;


    public XorInputStream(InputStream inputStream,
                          byte        xorValue    )
    {
        super(inputStream);
        this.xorValue = xorValue;
    }


    // Implementations for InputStream.

    public int read() throws IOException
    {
        int read = super.read();
        return read == -1 ? -1 : (byte) (read ^ xorValue) & 0xff ;
    }


    public int read(byte[] b, int off, int len) throws IOException
    {
        int read = super.read(b, off, len);
        for (int i = 0; i < read; i++)
        {
            b[off + i] = (byte) (b[off + i] ^ xorValue);
        }
        return read;
    }
}
