#include <string.h>
#include <stdlib.h>
#include "bytebuffer.h"

struct BB {
    char *buf;
    size_t pos;
    size_t size;
    size_t rem;
};

BB *bb_wrap(char *buf, size_t size) {
    BB *bb = (BB *) malloc(sizeof(BB));
    if (!bb) {
        return NULL;
    }
    bb->buf = buf;
    bb->pos = 0;
    bb->size = size;
    bb->rem = size;
    return bb;
}

void bb_free(BB *bb) {
    if (bb) {
        free(bb);
    }
}

static int bb_put_val(BB *bb, void *val, size_t len) {
    if (len > bb->rem) {
        return -1;
    }
    memcpy((bb->buf + bb->pos), val, len);
    bb->pos += len;
    bb->rem -= len;
    return 0;
}

int bb_put_char(BB *bb, char value) {
    return bb_put_val(bb, &value, sizeof(value));
}

int bb_put_int(BB *bb, int value) {
    return bb_put_val(bb, &value, sizeof(value));
}

int bb_put_long(BB *bb, long value) {
    return bb_put_val(bb, &value, sizeof(value));
}

int bb_put_str(BB *bb, char *src) {
    char *dst = bb->buf + bb->pos;
    for (; *src; src++, dst++) {
        if (bb->rem == 0) {
            return -1;
        }
        *dst = *src;
        bb->rem--;
        bb->pos++;
    }
    return bb_put_char(bb, '\0');
}
