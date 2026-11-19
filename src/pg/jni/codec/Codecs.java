package pg.jni.codec;

import java.util.HashMap;
import java.util.Map;

public class Codecs {

    private static final Map<Integer, ICodec> codecs;

    public static ICodec get(int i) {
        return codecs.get(i);
    }

    static {
        codecs = new HashMap<>();
        codecs.put(16, new BOOL());
        codecs.put(23, new INT4());
        codecs.put(25, new TEXT());
    }
}
