# DexGuard configuration for release versions.
# Copyright (c) 2012-2016 GuardSquare NV
#
# Note that the DexGuard plugin jars generally contain their own copies
# of this file.

# Don't touch the contents of resource xml files and native libraries.
-adaptresourcefilecontents !AndroidManifest.xml,
                           !res/**.xml,
                           !jni/**.so

-keepclasseswithmembernames,includedescriptorclasses class * {
    native <methods>;
}

-include dexguard-library-release.pro
