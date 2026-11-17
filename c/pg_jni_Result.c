#include <jni.h>
#include "libpq-fe.h"
#include "bytebuffer.h"

#define BB_SIZE 64

// TODO: pass bb size

JNIEXPORT jint JNICALL Java_pg_jni_Result_getResultInfo
  (JNIEnv *env, jclass jcls, jlong jresult, jlong jbb) {

    int i;
    int res = 0;
    char *colName;
    int colFormat;
    Oid colType;

    PGresult *result = (PGresult *) jresult;
    int nTuples = PQntuples(result);
    int nFields = PQnfields(result);

    BB *bb = bb_wrap((char *)jbb, BB_SIZE);
    if (!bb) {
        res = -1;
        goto cleanup;
    }

    res = bb_put_int(bb, nTuples);
    if (res) goto cleanup;

    res = bb_put_int(bb, nFields);
    if (res) goto cleanup;

    for (i = 0; i < nFields; i++) {
        colName = PQfname(result, i);
        colFormat = PQfformat(result, i);
        colType = PQftype(result, i);

        res = bb_put_str(bb, colName);
        if (res) goto cleanup;

        res = bb_put_int(bb, colFormat);
        if (res) goto cleanup;

        res = bb_put_int(bb, colType);
        if (res) goto cleanup;
    }

cleanup:
    if (bb) bb_free(bb);
    return res;
}

JNIEXPORT jint JNICALL Java_pg_jni_Result_getFieldInfo
  (JNIEnv *env, jclass jcls, jlong jresult, jint jrow, jint jcol, jlong jbb) {
    PGresult *result = (PGresult *) jresult;

    int res = 0;
    Oid colType = PQftype(result, jcol);
    int colFormat = PQfformat(result, jcol);
    int size = PQgetlength(result, jrow, jcol);
    int isNull = PQgetisnull(result, jrow, jcol);
    char *value = PQgetvalue(result, jrow, jcol);

    BB *bb = bb_wrap((char *)jbb, BB_SIZE);
    if (!bb) {
        res = -1;
        goto cleanup;
    }

    res = bb_put_int(bb, isNull);
    if (res) goto cleanup;

    res = bb_put_int(bb, colType);
    if (res) goto cleanup;

    res = bb_put_int(bb, colFormat);
    if (res) goto cleanup;

    res = bb_put_int(bb, size);
    if (res) goto cleanup;

    res = bb_put_str(bb, value);
    if (res) goto cleanup;

cleanup:
    if (bb) bb_free(bb);
    return res;
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
