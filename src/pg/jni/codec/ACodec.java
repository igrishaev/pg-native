package pg.jni.codec;

import java.nio.ByteBuffer;

public class ACodec implements ICodec {
    @Override
    public Object decodeTxt(String str) {
        throw new RuntimeException("decodeTxt not implemented");
    }
    @Override
    public Object decodeBin(ByteBuffer bb) {
        throw new RuntimeException("decodeBin not implemented");
    }
    @Override
    public String encodeTxt(Object obj) {
        throw new RuntimeException("encodeTxt not implemented");
    }
    @Override
    public ByteBuffer encodeBin(Object obj) {
        throw new RuntimeException("encodeBin not implemented");
    }
}
