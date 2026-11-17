#ifndef __BYTEBUFFER_H__
#define __BYTEBUFFER_H__

typedef struct BB BB;

BB *bb_wrap(char *buf, size_t size);

void bb_free(BB *bb);

int bb_put_char(BB *bb, char value);

int bb_put_int(BB *bb, int value);

int bb_put_long(BB *bb, long value);

int bb_put_str(BB *bb, char *src);

#endif /* __BYTEBUFFER_H__ */
