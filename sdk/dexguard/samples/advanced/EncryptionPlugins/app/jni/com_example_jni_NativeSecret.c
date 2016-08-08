#include <jni.h>

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jstring JNICALL Java_com_example_jni_NativeSecret_getMessage
  (JNIEnv *env, jobject obj) {

    return (*env)->NewStringUTF(env, "This is an encrypted native library.");
}

#ifdef __cplusplus
}
#endif
