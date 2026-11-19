package pg.jni;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Arena {

    static {
        Native.loadLib();
    }

    private ByteBuffer bb;
    private int size;
    private long ptr;

    private static final int SIZE_GROW = 1014;
    private static final int SIZE_LONG = 8;
    private static final int SIZE_INT = 4;
    private static final long NULL;

    private static native long getNull();
    static {
        NULL = getNull();
    }

    private Arena(ByteBuffer bb, int size, long ptr) {
        this.bb = bb;
        this.size = size;
        this.ptr = ptr;
    }

    private static native long bbAddress(ByteBuffer bb);

    public static Arena make(int size) {
        final ByteBuffer bb = ByteBuffer.allocateDirect(size);
        bb.order(ByteOrder.nativeOrder());
        final long ptr = bbAddress(bb);
        if (ptr == NULL) {
            throw Err.error("failed to allocate arena, size: %s", size);
        }
        return new Arena(bb, size, ptr);
    }

    private void maybeReallocate(int delta) {
        if (delta <= bb.remaining()) {
            return;
        }
        final int sizeNew = this.size + Math.max(delta, SIZE_GROW);
        final ByteBuffer bbNew = ByteBuffer.allocateDirect(sizeNew);
        bbNew.order(ByteOrder.nativeOrder());
        final long ptrNew = bbAddress(bbNew);
        if (ptrNew == NULL) {
            throw Err.error("failed to reallocate arena, size: %s", sizeNew);
        }
        bb.flip();
        bbNew.put(bb);
        this.bb = bbNew;
        this.size = sizeNew;
        this.ptr = ptrNew;
    }

    // setters

    public long longArrayPtr(final long[] values) {
        if (values == null) {
            return NULL;
        }
        maybeReallocate(values.length * SIZE_LONG);
        int pos = bb.position();
        for (long l: values) {
            bb.putLong(l);
        }
        return ptr + pos;
    }

    public long intArrayPtr(final int[] values) {
        if (values == null) {
            return NULL;
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
            return NULL;
        }
        byte[] buf = s.getBytes(StandardCharsets.UTF_8);
        return charPtr(buf);
    }

    public long charPtr(final byte[] buf) {
        if (buf == null) {
            return NULL;
        }
        int pos = bb.position();
        maybeReallocate(buf.length + 1);
        bb.put(buf);
        bb.put((byte) 0);
        return ptr + pos;
    }

    public long charArrayPtr(final String[] arr) {
        if (arr == null) {
            return NULL;
        }
        long[] pointers = new long[arr.length];
        for (int i = 0; i < arr.length; i++) {
            pointers[i] = charPtr(arr[i]);
        }
        return longArrayPtr(pointers);
    }

    public long charArrayPtr(final byte[][] arr) {
        if (arr == null) {
            return NULL;
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

    public long getLong(int i) {
        return bb.getLong(i);
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

    @SuppressWarnings("unused")
    public Arena skip(int n) {
        bb.position(bb.position() + n);
        return this;
    }

    public ByteBuffer slice(int len) {
        return bb.slice(bb.position(), len);
    }

    @SuppressWarnings("unused")
    public int position() {
        return bb.position();
    }

    @SuppressWarnings("unused")
    public void debug(final int len) {
        final byte[] ba = new byte[len];
        bb.get(0, ba);
        System.out.println(Arrays.toString(ba));
    }

    public static void main(String... args) {
        var a = Arena.make(64);
        String[] arr = new String[]{"foo", "bar"};
        var ptr = a.charArrayPtr(arr);
        System.out.println(a.ptr);
        System.out.println(ptr);
        System.out.println(a.getLong(8));
        System.out.println(a.getLong(8+8));
        a.debug(64);
    }
}
