#include <jni.h>
#include "libpq-fe.h"
#include "bytebuffer.h"

JNIEXPORT jint JNICALL Java_pg_jni_Result_getResultInfo
  (JNIEnv *env, jclass jcls, jlong jresult, jlong jbb) {

    int i;
    char *colName;
    int colFormat;
    Oid colType;

    PGresult *result = (PGresult *) jresult;
    char *bb = (char *) jbb;
    int nTuples = PQntuples(result);
    int nFields = PQnfields(result);

    bb = bb_put_int(bb, nTuples);
    bb = bb_put_int(bb, nFields);

    for (i = 0; i < nFields; i++) {
        colName = PQfname(result, i);
        colFormat = PQfformat(result, i);
        colType = PQftype(result, i);

        bb = bb_put_char(bb, colName);
        bb = bb_put_int(bb, colFormat);
        bb = bb_put_int(bb, colType);
    }

    return 1;
}

JNIEXPORT jint JNICALL Java_pg_jni_Result_getFieldInfo
  (JNIEnv *env, jclass jcls, jlong jresult, jint jrow, jint jcol, jlong jbb) {
    PGresult *result = (PGresult *) jresult;
    char *bb = (char *) jbb;

    Oid colType = PQftype(result, jcol);
    int colFormat = PQfformat(result, jcol);
    int size = PQgetlength(result, jrow, jcol);
    int isNull = PQgetisnull(result, jrow, jcol);
    char *value = PQgetvalue(result, jrow, jcol);

    bb = bb_put_int(bb, colType);
    bb = bb_put_int(bb, colFormat);
    bb = bb_put_int(bb, size);
    bb = bb_put_int(bb, isNull);
    bb = bb_put_char(bb, value);

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
