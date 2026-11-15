#include <jni.h>

#define JNI_CALL(env, method, ...) ((*env)->method(env, ##__VA_ARGS__))

typedef void (*FN_V_S)(char *s);

JNIEXPORT void JNICALL Java_pg_jni_Native_call_1v_1s
  (JNIEnv *env, jclass jcls, jlong jf, jstring js) {
    FN_V_S f = (FN_V_S *) jf;
    char *s = JNI_CALL(env, GetStringUTFChars, js, NULL);
    if (s) {
        f(s);
        JNI_CALL(env, ReleaseStringUTFChars, js, s);
    } else {

    }
}

typedef long (*FN_L)();

JNIEXPORT jlong JNICALL Java_pg_jni_Native_call_1l
  (JNIEnv *env, jclass jcls, jlong jf) {
    FN_L f = (FN_L *) jf;
    return f();
}

typedef long (*FN_L_L)(long f, long l);

JNIEXPORT jint JNICALL Java_pg_jni_Native_call_1i_1l
  (JNIEnv *env, jclass jcls, jlong jf, jlong jl) {
    FN_L_L f = (FN_L_L *) jf;
    return f(jl);
}

typedef (char *) (*FN_L_LS)(long l, char *s);

JNIEXPORT jstring JNICALL Java_pg_jni_Native_call_1s_1ls
  (JNIEnv *env, jclass jcls, jlong jf, jlong jl, jstring js) {
    FN_L_LS f = (FN_L_LS *) jf;

    char *s = JNI_CALL(env, GetStringUTFChars, js, NULL);
    if (s) {
        f(s);
        JNI_CALL(env, ReleaseStringUTFChars, js, s);
    } else {

    }

    return f(l, s);
}
