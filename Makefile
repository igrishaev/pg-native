
SRC_PATH = src/pg/jni
C_PATH = c

JAVAFILES = \
	$(SRC_PATH)/Native.java \

headers:
	javac -Xlint:deprecation -h $(C_PATH) $(JAVAFILES)
