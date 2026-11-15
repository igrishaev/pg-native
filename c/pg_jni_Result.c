#include <jni.h>
#include "libpq-fe.h"

JNIEXPORT jint JNICALL Java_pg_jni_Result_getResultInfo
  (JNIEnv *env, jclass jcls, jlong jresult) {
    PGresult *result = (PGresult *) jresult;
    return 1;
}

JNIEXPORT jint JNICALL Java_pg_jni_Result_getFieldInfo
  (JNIEnv *env, jclass jcls, jlong jresult, jint jrow, jint jcol) {
    PGresult *result = (PGresult *) jresult;
    return 1;
}

JNIEXPORT void JNICALL Java_pg_jni_Result_PQclear
  (JNIEnv *env, jclass jcls, jlong jresult) {
    PGresult *result = (PGresult *) jresult;
    PQclear(result);
}

JNIEXPORT jint JNICALL Java_pg_jni_Result_PQresultStatus
  (JNIEnv *env, jclass jcls, jlong jresult) {
    PGresult *result = (PGresult *) jresult;
    return PQresultStatus(result);

}
