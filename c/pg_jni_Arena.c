#include <jni.h>
#include "macros.h"

JNIEXPORT jlong JNICALL Java_pg_jni_Arena_getNull
  (JNIEnv *env, jclass jcls) {
    return (jlong) NULL;
}

JNIEXPORT jlong JNICALL Java_pg_jni_Arena_getBBAddr
  (JNIEnv *env, jclass jcls, jobject jbb) {
    return (jlong) (*env)->GetDirectBufferAddress(env, jbb);
}
