LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := secret
LOCAL_SRC_FILES := com_example_jni_NativeSecret.c

include $(BUILD_SHARED_LIBRARY)
