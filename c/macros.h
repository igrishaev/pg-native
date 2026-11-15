#ifndef __MACROS_H
#define __MACROS_H

#define _MAX(a, b) (((a) > (b)) ? (a) : (b))
#define _MIN(a, b) (((a) > (b)) ? (b) : (a))

#define JNI_CALL(env, method, ...) ((*env)->method(env, ##__VA_ARGS__))

#endif /* __MACROS_H */
