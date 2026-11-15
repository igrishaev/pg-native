package pg.jni;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

public class Arena {

    static {
        Native.loadLib();
    }

    private final ByteBuffer bb;
    private final int bbSize;
    private final long bbAddr;
    private final ByteOrder BO_JVM;
    private final ByteOrder BO_JNI;

    private final int LEN_LONG = 8;
    private static final long NULL;

    private static native long getNull();
    static {
        NULL = getNull();
    }

    private Arena(ByteBuffer bb, int bbSize, long bbAddr, ByteOrder BO_JVM, ByteOrder BO_JNI) {
        this.bb = bb;
        this.bbSize = bbSize;
        this.bbAddr = bbAddr;
        this.BO_JVM = BO_JVM;
        this.BO_JNI = BO_JNI;
    }

    private static native long getBBAddr(ByteBuffer bb);

    public static Arena make(int size) {
        final ByteBuffer bb = ByteBuffer.allocateDirect(size);
        final long bbAddr = getBBAddr(bb);
        ByteOrder BO_JVM = bb.order();
        ByteOrder BO_JNI = ByteOrder.nativeOrder();
        if (bbAddr == NULL) {
            throw new RuntimeException("failed to get a native address of a byte buffer");
        }
        return new Arena(bb, size, bbAddr, BO_JVM, BO_JNI);
    }

    @Override
    public String toString() {
        return String.format("<Arena, size: %d>", bbSize);
    }

    @SuppressWarnings("unused")
    public int size() {
        return bbSize;
    }

    public Arena clear() {
        bb.clear();
        return this;
    }

    public Arena boJava() {
        bb.order(BO_JVM);
        return this;
    }

    public Arena boNative() {
        bb.order(BO_JNI);
        return this;
    }

    public long intPtr(int value) {
        int pos = bb.position();
        bb.putInt(value);
        return bbAddr + pos;
    }

    public long longPtr(long value) {
        int pos = bb.position();
        bb.putLong(value);
        return bbAddr + pos;
    }

    public long intArrayPtr(int[] values) {
        int pos = bb.position();
        for (int i: values) {
            bb.putInt(i);
        }
        return bbAddr + pos;
    }

    public long longArrayPtr(long[] values) {
        int pos = bb.position();
        for (long l: values) {
            bb.putLong(l);
        }
        return bbAddr + pos;
    }

    public long charPtr(String s) {
        int pos = bb.position();
        byte[] buf = s.getBytes(StandardCharsets.UTF_8);
        bb.put(buf);
        bb.put((byte) 0);
        return bbAddr + pos;
    }

    public long stringArrayPtr(String[] arr) {
        String s;
        int pos = bb.position();
        int posTmp;
        bb.position(pos + arr.length * LEN_LONG);
        for (int i = 0; i < arr.length; i++) {
            posTmp = bb.position();
            s = arr[i];
            if (s == null) {
                bb.putLong(NULL);
            } else {
                byte[] buf = s.getBytes(StandardCharsets.UTF_8);
                bb.put(buf);
                bb.put((byte) 0);
            }
            bb.putLong(pos + i * LEN_LONG, bbAddr + posTmp);
        }
        return bbAddr + pos;
    }

    public long byteArray2Ptr(byte[][] arr) {
        byte[] buf;
        int pos = bb.position();
        int posTmp;
        bb.position(pos + arr.length * LEN_LONG);
        for (int i = 0; i < arr.length; i++) {
            posTmp = bb.position();
            buf = arr[i];
            if (buf == null) {
                bb.putLong(NULL);
            } else {
                bb.put(buf);
                bb.put((byte) 0);
            }
            bb.putLong(pos + i * LEN_LONG, bbAddr + posTmp);
        }
        return bbAddr + pos;
    }

    public int getInt() {
        return bb.getInt();
    }

    public void get(byte[] buf) {
        bb.get(buf);
    }

    public Arena skip(int n) {
        bb.position(bb.position() + n);
        return this;
    }
}
