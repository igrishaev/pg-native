#include <jni.h>
#include "libpq-fe.h"

JNIEXPORT jlong JNICALL Java_pg_jni_Connection_PQconnectdb
  (JNIEnv *env, jclass jcls, jlong jptr) {
    return (jlong) PQconnectdb((char *) jptr);
}

JNIEXPORT jint JNICALL Java_pg_jni_Connection_PQstatus
  (JNIEnv *env, jclass jcls, jlong jconn) {
    PGconn *conn = (PGconn *) jconn;
    return PQstatus(conn);
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
