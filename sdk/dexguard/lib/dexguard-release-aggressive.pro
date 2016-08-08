# DexGuard configuration for release versions.
# Copyright (c) 2012-2016 GuardSquare NV
#
# Note that the DexGuard plugin jars generally contain their own copies
# of this file.

-dalvik

-optimizationpasses 5

-obfuscationdictionary                 dictionary.txt
-classobfuscationdictionary            classdictionary.txt
-resourcefilenameobfuscationdictionary filedictionary.txt

-keepresourcefiles         AndroidManifest.xml
-adaptresourcefilecontents AndroidManifest.xml,resources.arsc,
                           !res/raw**,res/**.xml,
                           lib/**.so

# Some package required for the manifest file.
-repackageclasses 'o'
-allowaccessmodification

-include dexguard-common.pro
-include dexguard-assumptions.pro
