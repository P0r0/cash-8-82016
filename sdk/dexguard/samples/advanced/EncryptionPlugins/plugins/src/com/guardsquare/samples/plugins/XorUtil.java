/*
 * Sample to illustrate encryption plugins for DexGuard.
 *
 * Copyright (c) 2015-2016 GuardSquare NV
 */
package com.guardsquare.samples.plugins;

/**
 * Utility for performing XOR operations.
 */
public class XorUtil
{
    /**
     * Returns a new byte array that is a copy of the given array, but each
     * value in the array is combined with the given constant, using the XOR
     * opertor.
     *
     * @param data     the data to be XOR'ed.
     * @param xorValue the constant to combine with.
     *
     * @return the XOR'ed copy of the given array.
     */
    public static byte[] xor(byte[] data,
                             int    xorValue)
    {
        byte[] xorredData = new byte[data.length];

        for (int i = 0; i < data.length; i++) {
            xorredData[i] = (byte) (data[i] ^ xorValue);
        }

        return xorredData;
    }
/**
     * Returns a new char array that is a copy of the given array, but each
     * value in the array is combined with the given constant, using the XOR
     * opertor.
     *
     * @param data     the data to be XOR'ed.
     * @param xorValue the constant to combine with.
     *
     * @return the XOR'ed copy of the given array.
     */
    public static char[] xor(char[] data,
                             int    xorValue)
    {
        char[] xorredData = new char[data.length];

        for (int i = 0; i < data.length; i++) {
            xorredData[i] = (char) (data[i] ^ xorValue);
        }

        return xorredData;
    }

    /* Class has only static methods. It should never be instantiated. */
    private XorUtil() {};
}
