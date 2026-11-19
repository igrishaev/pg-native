#include <jni.h>
#include "macros.h"
#include "libpq-fe.h"

JNIEXPORT jlong JNICALL Java_pg_jni_Connection_getNull
  (JNIEnv *env, jclass jcls) {
    return (jlong) NULL;
}

JNIEXPORT jlong JNICALL Java_pg_jni_Connection_PQconnectdb
  (JNIEnv *env, jclass jcls, jlong jptr) {
    return (jlong) PQconnectdb((char *) jptr);
}

JNIEXPORT jint JNICALL Java_pg_jni_Connection_PQstatus
  (JNIEnv *env, jclass jcls, jlong jconn) {
    PGconn *conn = (PGconn *) jconn;
    return PQstatus(conn);
}

JNIEXPORT jstring JNICALL Java_pg_jni_Connection_PQerrorMessage
  (JNIEnv *env, jclass jcls, jlong jconn) {
    PGconn *conn = (PGconn *) jconn;
    char *msg = PQerrorMessage(conn);
    if (msg) {
        return JNI(env, NewStringUTF, msg);
    } else {
        return NULL;
    }
}

JNIEXPORT jlong JNICALL Java_pg_jni_Connection__1exec
  (JNIEnv *env, jclass jcls, jlong jconn, jlong jsql) {
    PGconn *conn = (PGconn *) jconn;
    return (jlong) PQexec(conn, (char *) jsql);
}

JNIEXPORT jlong JNICALL Java_pg_jni_Connection_PQexecParams
  (JNIEnv *env, jclass jcls,
   jlong jconn,
   jlong jcmd,
   jint jnParams,
   jlong jparamTypes,
   jlong jparamValues,
   jlong jparamLengths,
   jlong jparamFormats,
   jint jresultFormat) {
    PGconn *conn = (PGconn *) jconn;
    return (jlong) PQexecParams(conn,
                                (char *) jcmd,
                                jnParams,
                                (Oid *) jparamTypes,
                                (char **) jparamValues,
                                (int *) jparamLengths,
                                (int *) jparamFormats,
                                jresultFormat);

}

JNIEXPORT void JNICALL Java_pg_jni_Connection_PQfinish
  (JNIEnv *env, jclass jcls, jlong jconn) {
    PGconn *conn = (PGconn *) jconn;
    PQfinish(conn);
}

JNIEXPORT jlong JNICALL Java_pg_jni_Connection__1prepare
  (JNIEnv *env, jclass jcls, jlong jconn, jlong ptrName, jlong ptrSql, jint nParams, jlong ptrOids) {
    PGconn *conn = (PGconn *) jconn;
    return (jlong) PQprepare(conn, (char *) ptrName, (char *) ptrSql, nParams, (Oid *) ptrOids);
}

JNIEXPORT jlong JNICALL Java_pg_jni_Connection__1describePrepared
  (JNIEnv *env, jclass jcls, jlong jconn, jlong ptrName) {
    PGconn *conn = (PGconn *) jconn;
    return (jlong) PQdescribePrepared(conn, (char *) ptrName);
}
