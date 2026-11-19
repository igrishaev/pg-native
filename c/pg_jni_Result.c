#include <jni.h>
#include <stdlib.h>
#include "macros.h"
#include "libpq-fe.h"

JNIEXPORT void JNICALL Java_pg_jni_Result__1clear
  (JNIEnv *env, jclass jcls, jlong jresult) {
    PGresult *result = (PGresult *) jresult;
    PQclear(result);
}

JNIEXPORT jint JNICALL Java_pg_jni_Result__1status
  (JNIEnv *env, jclass jcls, jlong jresult) {
    PGresult *result = (PGresult *) jresult;
    return PQresultStatus(result);

}

JNIEXPORT jstring JNICALL Java_pg_jni_Result__1colName
  (JNIEnv *env, jclass jcls, jlong jresult, jint jcol) {
    PGresult *result = (PGresult *) jresult;
    char *name = PQfname(result, jcol);
    if (!name) {
        return NULL;
    }
    return JNI(env, NewStringUTF, name);
}

JNIEXPORT jint JNICALL Java_pg_jni_Result__1colType
  (JNIEnv *env, jclass jcls, jlong jresult, jint jcol) {
    PGresult *result = (PGresult *) jresult;
    return PQftype(result, jcol);
}

JNIEXPORT jint JNICALL Java_pg_jni_Result__1colFormat
  (JNIEnv *env, jclass jcls, jlong jresult, jint jcol) {
    PGresult *result = (PGresult *) jresult;
    return PQfformat(result, jcol);
}

JNIEXPORT jint JNICALL Java_pg_jni_Result__1colCount
  (JNIEnv *env, jclass jcls, jlong jresult) {
    PGresult *result = (PGresult *) jresult;
    return PQnfields(result);
}

JNIEXPORT jint JNICALL Java_pg_jni_Result__1rowCount
  (JNIEnv *env, jclass jcls, jlong jresult) {
    PGresult *result = (PGresult *) jresult;
    return PQntuples(result);
}

JNIEXPORT jint JNICALL Java_pg_jni_Result__1isNull
  (JNIEnv *env, jclass jcls, jlong jresult, jint jrow, jint jcol) {
    PGresult *result = (PGresult *) jresult;
    return PQgetisnull(result, jrow, jcol);
}

JNIEXPORT jstring JNICALL Java_pg_jni_Result__1getValueStr
  (JNIEnv *env, jclass jcls, jlong jresult, jint jrow, jint jcol) {
    PGresult *result = (PGresult *) jresult;
    char *val = PQgetvalue(result, jrow, jcol);
    if (!val) {
        return NULL;
    }
    return JNI(env, NewStringUTF, val);
}

JNIEXPORT jbyteArray JNICALL Java_pg_jni_Result__1getValueBuf
  (JNIEnv *env, jclass jcls, jlong jresult, jint jrow, jint jcol) {
    PGresult *result = (PGresult *) jresult;
    char *val = PQgetvalue(result, jrow, jcol);
    if (!val) {
        return NULL;
    }
    int size = PQgetlength(result, jrow, jcol);
    jbyteArray jarr = JNI(env, NewByteArray, size);
    if (!jarr) {
        return NULL;
    }
    JNI(env, SetByteArrayRegion, jarr, 0, size, (jbyte *) val);
    return jarr;
}

JNIEXPORT jint JNICALL Java_pg_jni_Result__1nParams
  (JNIEnv *env, jclass jcls, jlong jresult) {
    PGresult *result = (PGresult *) jresult;
    return PQnparams(result);
}


JNIEXPORT jint JNICALL Java_pg_jni_Result__1paramType
  (JNIEnv *env, jclass jcls, jlong jresult, jint n) {
    PGresult *result = (PGresult *) jresult;
    return PQparamtype(result, n);
}
