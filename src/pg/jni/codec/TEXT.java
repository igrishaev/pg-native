package pg.jni.codec;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class TEXT extends ACodec {
    @Override
    public Object decodeTxt(String str) {
        return str;
    }

    @Override
    public Object decodeBin(ByteBuffer bb) {
        return new String(bb.array(), StandardCharsets.UTF_8);
    }

    @Override
    public String encodeTxt(Object obj) {
        if (obj == null) {
            return "null";
        } else {
            return obj.toString();
        }
    }

    @Override
    public ByteBuffer encodeBin(Object obj) {
        return null;
    }
}
