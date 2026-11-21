
SHARED_LIB = resources/$(shell java src/pg/jni/OS.java)

SRC_PATH = src/pg/jni
C_PATH = c

JAVAFILES = $(shell find $(SRC_PATH) -name '*.java')

MODULES = $(shell find $(C_PATH) -name '*.c')
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

.PHONY: clear
clear:
	find . -name '*.log'     -delete
	find . -name '*.o'       -delete
	find . -name '*.class'   -delete
	find . -name '.DS_Store' -delete

headers:
	javac -Xlint:deprecation -h $(C_PATH) $(JAVAFILES)
	find . -name '*.class'   -delete

PG_INC = $(shell pg_config --includedir)
PG_LIB = $(shell pg_config --libdir)
CC = gcc
CFLAGS = -Wall -ansi -pedantic -fPIC ${JAVA_INC} -I${PG_INC} -L${PG_LIB} -DDEBUG
LIBS = -lpq

$(SHARED_LIB): $(OBJECTS)
	$(CC) $(CFLAGS) $(LIBS) -shared -o $@ $(OBJECTS)

repl:
	lein with-profile +test repl
