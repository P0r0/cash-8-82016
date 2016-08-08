# Common DexGuard configuration for debug versions and release versions.
# Copyright (c) 2012-2016 GuardSquare NV
#
# Note that the DexGuard plugin jars generally contain their own copies
# of this file.

# Keep some attributes that the compiler needs.
-keepattributes Exceptions,Deprecated,EnclosingMethod

# Keep all resource files and their resources.
-keepresourcefiles **
-keepresources */*

-include dexguard-common.pro
