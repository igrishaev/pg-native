package pg.jni.codec;

import java.nio.ByteBuffer;

public class INT4 extends ACodec {
    @Override
    public Object decodeTxt(String str) {
        return Integer.parseInt(str);
    }

    @Override
    public Object decodeBin(ByteBuffer bb) {
        return bb.getInt();
    }

    @Override
    public String encodeTxt(Object obj) {
        if (obj instanceof Integer i) {
            return i.toString();
        } else {
            throw new RuntimeException("aaa");
        }
    }

    @Override
    public ByteBuffer encodeBin(Object obj) {
        if (obj instanceof Integer i) {
            ByteBuffer bb = ByteBuffer.allocate(4);
            bb.putInt(i);
            return bb;
        } else {
            throw new RuntimeException("aaa");
        }
    }
}
