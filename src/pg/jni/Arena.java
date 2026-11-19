package pg.jni;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Arena implements AutoCloseable {

    static {
        Native.loadLib();
    }

    private ByteBuffer bb;
    private int size;
    private long ptr;
    private boolean isClosed;

    private static final int growSize = 1014;
    private static final int SIZE_LONG = 8;
    private static final int SIZE_INT = 4;
    private static final long NULL;

    private static native long getNull();
    static {
        NULL = getNull();
    }

    private Arena(ByteBuffer bb, int size, long ptr, boolean isClosed) {
        this.bb = bb;
        this.size = size;
        this.ptr = ptr;
        this.isClosed = isClosed;
    }

    private static native ByteBuffer malloc(int size);
    private static native ByteBuffer realloc(long ptr, int size);
    private static native void free(long ptr);

    public static Arena make(int size) {
        final ByteBuffer bb = malloc(size);
        if (bb == null) {
            throw Err.error("failed to allocate arena, size: %s", size);
        }
        bb.order(ByteOrder.nativeOrder());
        final long ptr = bb.getLong(0);
        return new Arena(bb, size, ptr, false);
    }

    private void maybeReallocate(int delta) {
        if (delta >= bb.remaining()) {
            return;
        }
        final int newSize = this.size + Math.max(delta, growSize);
        final int pos = bb.position();
        final ByteBuffer bb = realloc(ptr, newSize);
        if (bb == null) {
            throw Err.error("failed to reallocate arena, new size: %s", newSize);
        }
        bb.order(ByteOrder.nativeOrder());
        bb.position(pos);
        final long ptr = bb.getLong(0);
        this.bb = bb;
        this.size = newSize;
        this.ptr = ptr;
    }

    @Override
    public void close() {
        if (!isClosed) {
            free(ptr);
            this.bb = null;
            this.size = -1;
            this.ptr = NULL;
            isClosed = true;
        }
    }

    // setters

    private long putNULL() {
        maybeReallocate(SIZE_LONG);
        int pos = bb.position();
        bb.putLong(NULL);
        return ptr + pos;
    }

    public long longArrayPtr(final long[] values) {
        if (values == null) {
            return putNULL();
        }
        maybeReallocate(values.length * SIZE_INT);
        int pos = bb.position();
        for (long l: values) {
            bb.putLong(l);
        }
        return ptr + pos;
    }

    public long intArrayPtr(final int[] values) {
        if (values == null) {
            return putNULL();
        }
        maybeReallocate(values.length * SIZE_INT);
        int pos = bb.position();
        for (int i: values) {
            bb.putInt(i);
        }
        return ptr + pos;
    }

    public long charPtr(String s) {
        if (s == null) {
            return putNULL();
        }
        byte[] buf = s.getBytes(StandardCharsets.UTF_8);
        return charPtr(buf);
    }

    public long charPtr(final byte[] buf) {
        if (buf == null) {
            return putNULL();
        }
        int pos = bb.position();
        maybeReallocate(buf.length + 1);
        bb.put(buf);
        bb.put((byte) 0);
        return ptr + pos;
    }

    public long charArrayPtr(final String[] arr) {
        if (arr == null) {
            return putNULL();
        }
        long[] pointers = new long[arr.length];
        for (int i = 0; i < arr.length; i++) {
            pointers[i] = charPtr(arr[i]);
        }
        return longArrayPtr(pointers);
    }

    public long charArrayPtr(final byte[][] arr) {
        if (arr == null) {
            return putNULL();
        }
        long[] pointers = new long[arr.length];
        for (int i = 0; i < arr.length; i++) {
            pointers[i] = charPtr(arr[i]);
        }
        return longArrayPtr(pointers);
    }

    // getters

    public int getInt() {
        return bb.getInt();
    }

    public void get(byte[] buf) {
        bb.get(buf);
    }

    public String getCString() {
        int pos = bb.position();
        if (bb.get(pos) == 0) {
            bb.get();
            return null;
        }
        int len = 0;
        for (int i = pos; i < bb.limit(); i++) {
            if (bb.get(pos + len) == 0) {
                break;
            }
            len++;
        }
        ByteBuffer slice = bb.slice(pos, len);
        bb.position(pos + len + 1);
        return StandardCharsets.UTF_8.decode(slice).toString();
    }

    // misc

    @Override
    public String toString() {
        return String.format("<Arena, size: %d>", size);
    }

    public void clear() {
        bb.clear();
    }

    public Arena skip(int n) {
        bb.position(bb.position() + n);
        return this;
    }

    public ByteBuffer slice(int len) {
        return bb.slice(bb.position(), len);
    }

    @SuppressWarnings("unused")
    public Arena debug(final int len) {
        final byte[] ba = new byte[len];
        bb.get(0, ba);
        System.out.println(Arrays.toString(ba));
        return this;
    }
}
