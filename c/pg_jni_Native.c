#include <jni.h>

JNIEXPORT jlong JNICALL Java_pg_jni_Native_getNull
  (JNIEnv *env, jclass jcls) {
    return (jlong) NULL;
}
