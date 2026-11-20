#include <jni.h>
#include "libpq-fe.h"

JNIEXPORT jlong JNICALL Java_pg_jni_Statement__1closePrepared
  (JNIEnv *env, jclass jcls, jlong jconn, jlong namePtr) {
    PGconn *conn = (PGconn *) jconn;
    return (jlong) PQclosePrepared(conn, (char *) namePtr);
}
