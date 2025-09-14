package makejvm.ClassFile;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class ConstantPool {
    private static final int UTF8_TAG = 1;
    private static final int CLASS_TAG = 7;
    private static final int METHOD_REF_TAG = 10;
    private static final int NAME_AND_TYPE_TAG = 12;

    private final CpInfo[] pool;

    ConstantPool(CpInfo[] pool) {
        this.pool = pool;
    }

    public static ConstantPool read(ByteBuffer buffer) {
        final int cpCount = buffer.getShort() & 0xFFFF;
        CpInfo[] pool = new CpInfo[cpCount];


        // 1 base index because of JVM specification
        for (int i = 1; i < cpCount; i++) {
            final int tag = buffer.get() & 0xFF;
            switch (tag) {
                case UTF8_TAG -> {
                    int len = buffer.getShort() & 0xFFFF;
                    byte[] bytes = new byte[len];
                    buffer.get(bytes);
                    pool[i] = new CpInfo.Utf8CpInfo(new String(bytes, StandardCharsets.UTF_8));
                }
                case CLASS_TAG -> pool[i] = new CpInfo.ClassCpInfo(buffer.getShort() & 0xFFFF);
                case METHOD_REF_TAG ->
                        pool[i] = new CpInfo.MethodRefCpInfo(buffer.getShort() & 0xFFFF, buffer.getShort() & 0xFFFF);
                case NAME_AND_TYPE_TAG ->
                        pool[i] = new CpInfo.NameAndTypeCpInfo(buffer.getShort() & 0xFFFF, buffer.getShort() & 0xFFFF);
                // TODO: implement handling for other tags.
            }
        }

        return new ConstantPool(pool);
    }

    public String getUtf8(int index) {
        if (index > 0 && index < pool.length && pool[index] instanceof CpInfo.Utf8CpInfo) {
            return ((CpInfo.Utf8CpInfo) pool[index]).value();
        }
        return null;
    }
}