package pg.jni.codec;

import java.nio.ByteBuffer;

public interface ICodec {
    Object decodeTxt(String str);
    Object decodeBin(ByteBuffer bb);
    String encodeTxt(Object obj);
    ByteBuffer encodeBin(Object obj);
}
