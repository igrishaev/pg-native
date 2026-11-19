package pg.jni.codec;

import java.nio.ByteBuffer;

public class BOOL extends ACodec {

    @Override
    public Object decodeTxt(String str) {
        if (str.equals("t")) {
            return true;
        } else if (str.equals("f")) {
            return false;
        } else {
            throw new RuntimeException("bool");
        }
    }

    @Override
    public Object decodeBin(ByteBuffer bb) {
        return bb.get() == (byte) 1;
    }

    @Override
    public String encodeTxt(Object obj) {
        if (obj instanceof Boolean b) {
            return b ? "t" : "f";
        } else {
            throw new RuntimeException("bool");
        }
    }

    @Override
    public ByteBuffer encodeBin(Object obj) {
        if (obj instanceof Boolean b) {
            ByteBuffer bb = ByteBuffer.allocate(1);
            bb.put(b ? (byte)1 : (byte)0);
            return bb;
        } else {
            throw new RuntimeException("bool");
        }
    }
}
