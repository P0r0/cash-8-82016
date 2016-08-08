/*
 *  Copyright (c) 2014, Facebook, Inc.
 *  All rights reserved.
 *
 *  This source code is licensed under the BSD-style license found in the
 *  LICENSE file in the root directory of this source tree. An additional grant
 *  of patent rights can be found in the PATENTS file in the same directory.
 *
 */

package com.example;

import java.util.ArrayList;

import com.facebook.crypto.exception.CryptoInitializationException;
import com.facebook.crypto.util.NativeCryptoLibrary;

public class SimpleNativeCryptoLibrary implements NativeCryptoLibrary {

  private boolean mLoadLibraries;
  private boolean mLibrariesLoaded;
  private volatile UnsatisfiedLinkError mLinkError;

  public SimpleNativeCryptoLibrary() {
    mLoadLibraries = true;
    mLibrariesLoaded = false;
    mLinkError = null;
  }

  @Override
  public synchronized void ensureCryptoLoaded() throws CryptoInitializationException {
    if (!loadLibraries()) {
      throw new CryptoInitializationException(mLinkError);
    }
  }

  private synchronized boolean loadLibraries() {
    if (!mLoadLibraries) {
      return mLibrariesLoaded;
    }
    try {
      System.loadLibrary("conceal");
      mLibrariesLoaded = true;
    } catch (UnsatisfiedLinkError error) {
      mLinkError = error;
      mLibrariesLoaded = false;
    }
    mLoadLibraries = false;
    return mLibrariesLoaded;
  }
}
