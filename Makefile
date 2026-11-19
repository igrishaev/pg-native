
SHARED_LIB = resources/$(shell java src/pg/jni/OS.java)

SRC_PATH = src/pg/jni
C_PATH = c

JAVAFILES = \
	$(SRC_PATH)/codec/BOOL.java \
	$(SRC_PATH)/codec/INT4.java \
	$(SRC_PATH)/codec/TEXT.java \
	$(SRC_PATH)/codec/ACodec.java \
	$(SRC_PATH)/codec/ICodec.java \
	$(SRC_PATH)/codec/Codecs.java \
	$(SRC_PATH)/Statement.java \
	$(SRC_PATH)/PGError.java \
	$(SRC_PATH)/Err.java \
	$(SRC_PATH)/OS.java \
	$(SRC_PATH)/Native.java \
	$(SRC_PATH)/Arena.java \
	$(SRC_PATH)/Connection.java \
	$(SRC_PATH)/Result.java \

MODULES = \
	$(C_PATH)/bytebuffer.c \
	$(C_PATH)/pg_jni_Arena.c \
	$(C_PATH)/pg_jni_Connection.c \
	$(C_PATH)/pg_jni_Result.c \

OBJECTS = $(MODULES:.c=.o)

JAVA_HOME ?= $(error Please specify JAVA_HOME)

JAVA_INC = \
	-I${JAVA_HOME}/include \
	-I${JAVA_HOME}/include/darwin \
	-I${JAVA_HOME}/include/win32 \
	-I${JAVA_HOME}/include/linux

all: headers $(SHARED_LIB) sync-resources

CLASSES_DIR = target/production/pg-native

sync-resources:
	cp resources/* $(CLASSES_DIR)/

.phony: clear
clear:
	find . -name '*.log'     -delete
	find . -name '*.o'       -delete
	find . -name '*.class'   -delete
	find . -name '.DS_Store' -delete

headers:
	javac -Xlint:deprecation -h $(C_PATH) $(JAVAFILES)

PG_INC = $(shell pg_config --includedir)
PG_LIB = $(shell pg_config --libdir)
CC = gcc
CFLAGS = -Wall -ansi -pedantic -fPIC ${JAVA_INC} -I${PG_INC} -L${PG_LIB} -DDEBUG
LIBS = -lpq

$(SHARED_LIB): $(OBJECTS)
	$(CC) $(CFLAGS) $(LIBS) -shared -o $@ $(OBJECTS)
