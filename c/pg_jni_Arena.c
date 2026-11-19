#include <jni.h>
#include <stdlib.h>
#include <string.h>
#include "macros.h"

/* #include <stdlib.h> exit(-123); */

JNIEXPORT jlong JNICALL Java_pg_jni_Arena_getNull
  (JNIEnv *env, jclass jcls) {
    return (jlong) NULL;
}

static jobject bb_wrap(JNIEnv *env, void *addr, jlong size) {
    jobject bb = JNI_CALL(env, NewDirectByteBuffer, addr, size);
    if (!bb) {
        return NULL;
    } else {
        return bb;
    }
}

JNIEXPORT jobject JNICALL Java_pg_jni_Arena_malloc
  (JNIEnv *env, jclass jcls, jint jlen) {
    void *addr = malloc(jlen);
    if (!addr) {
        return NULL;
    }
    long ptr = (long) addr;
    memcpy(addr, &ptr, sizeof(ptr));
    return bb_wrap(env, addr, jlen);
}

JNIEXPORT jobject JNICALL Java_pg_jni_Arena_realloc
  (JNIEnv *env, jclass jcls, jlong jaddr, jint jlen) {
    void *addr = realloc((char *) jaddr, jlen);
    if (!addr) {
        return NULL;
    }
    long ptr = (long) addr;
    memcpy(addr, &ptr, sizeof(ptr));
    return bb_wrap(env, addr, jlen);
}
