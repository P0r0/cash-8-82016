# DexGuard configuration for debug versions.
# Copyright (c) 2012-2016 GuardSquare NV
#
# Note that the DexGuard plugin jars generally contain their own copies
# of this file.

-dontshrink
-dontoptimize
-dontobfuscate
-dalvik

-keepresourcefiles AndroidManifest.xml

-include dexguard-common.pro
