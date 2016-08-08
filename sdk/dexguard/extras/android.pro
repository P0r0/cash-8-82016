#
# This DexGuard configuration file illustrates how to process Android
# applications, outside of the Android Development Tools.
# Usage:
#     java -jar dexguard.jar @android.pro
#
# If you're using the DexGuard plugins for Gradle, Ant, Eclipse, or Maven, they
# already take care of the proper settings. You only need to enable the plugins
# as documented in the manual. You can still add project-specificconfiguration
# in proguard-project.txt or dexguard-project.txt.
#
# This configuration file is for custom, stand-alone builds.

# Specify the input jars, output jars, and library jars.
# Note that DexGuard reads Java bytecode (.class) and writes Dalvik code (.dex).

-injars  bin/classes
-injars  libs
-outjars bin/application.apk

-libraryjars /usr/local/android-sdk/platforms/android-15/android.jar
#-libraryjars /usr/local/android-sdk/add-ons/google_apis-7_r01/libs/maps.jar
# ...

# Sign the output apk.
-keystore         /home/user/.android/debug.keystore
-keystorepassword android
-keyalias         AndroiddebugKey
-keypassword      android

# Save the obfuscation mapping to a file, so you can de-obfuscate any stack
# traces later on.

-printmapping bin/classes-processed.map

# You can print out the seeds that are matching the keep options below.

#-printseeds bin/classes-processed.seeds

# Align some types of files in the output archive.

-zipalign 4
-dontcompress resources.arsc,**.jpg,**.jpeg,**.png,**.gif,**.wav

# Convert the Java bytecode to Dalvik bytecode.

-dalvik

# Reduce the size of the output some more.

-repackageclasses ''
-allowaccessmodification

# Keep the Android manifest as the main entry point of the application.

-keepresourcefiles         AndroidManifest.xml
-adaptresourcefilecontents AndroidManifest.xml,resources.arsc,
                           !res/raw**,res/**.xml

# Keep the minimally required attribute names in the Android manifest file.

-keepresourcexmlattributenames
    manifest/installLocation,
    manifest/versionCode,
    manifest/application/*/intent-filter/*/name

# Keep a fixed source file attribute and all line number tables to get line
# numbers in the stack traces.
# You can comment this out if you're not interested in stack traces.

-renamesourcefileattribute ''
-keepattributes SourceFile,LineNumberTable

# RemoteViews and some external frameworks might need annotations.

-keepattributes *Annotation*

# Preserve some constructors that the Android run-time invokes by reflection.

-keepclassmembers class * {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# Preserve some setters for View implementations.

-keepclassmembers class * extends android.view.View {
    public void set*(...);
}

# Preserve all possible onClick handlers.

-keepclassmembers class * extends android.content.Context {
   public void *(android.view.View);
   public void *(android.view.MenuItem);
}

# Preserve the special fields of all Parcelable implementations.

-keepclassmembers class * implements android.os.Parcelable {
    static ** CREATOR;
}

# Preserve static fields of inner classes of R classes that might be accessed
# through introspection.

-keepclassmembers class **.R$* {
  public static <fields>;
}

# Preserve annotated Javascript interface methods.

-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# Preserve the required interface from the License Verification Library
# (but don't nag the developer if the library is not used at all).

-keep public interface com.android.vending.licensing.ILicensingService

-dontnote com.android.vending.licensing.ILicensingService

# The Android Compatibility library references some classes that may not be
# present in all versions of the API, but we know that's ok.

-dontwarn android.support.**

# Preserve all native method names and the names of their classes.

-keepclasseswithmembernames,includedescriptorclasses class * {
    native <methods>;
}

# Preserve the special static methods that are required in all enumeration
# classes.

-keepclassmembers,allowoptimization enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Explicitly preserve all serialization members. The Serializable interface
# is only a marker interface, so it wouldn't save them.
# You can comment this out if your application doesn't use serialization.
# If your code contains serializable classes that have to be backward 
# compatible, please refer to the manual.

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# Your application may contain more items that need to be preserved; 
# typically classes that are dynamically created using Class.forName:

# -keep public class mypackage.MyClass
# -keep public interface mypackage.MyInterface
# -keep public class * implements mypackage.MyInterface

# If you wish, you can let the optimization step remove Android logging calls.

#-assumenosideeffects class android.util.Log {
#    public static boolean isLoggable(java.lang.String, int);
#    public static int v(...);
#    public static int i(...);
#    public static int w(...);
#    public static int d(...);
#    public static int e(...);
#}
