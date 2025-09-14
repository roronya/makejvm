package makejvm.ClassFile;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

class ConstantPool {
    private static final int UTF8_TAG = 1;
    private static final int CLASS_TAG = 7;
    private static final int METHOD_REF_TAG = 10;
    private static final int NAME_AND_TYPE_TAG = 12;

    private final CpInfo[] pool;

    ConstantPool(CpInfo[] pool) {
        this.pool = pool;
    }

    public static ConstantPool read(DataInputStream dis) throws IOException {
        int cpCount = dis.readUnsignedShort(); // read 2 byte
        CpInfo[] pool = new CpInfo[cpCount];

        // 1 base index because of JVM specification
        for (int i = 1; i < cpCount; i++) {
            int tag = dis.readUnsignedByte(); // read 1 byte
            switch (tag) {
                case UTF8_TAG -> {
                    int len = dis.readUnsignedShort();
                    byte[] bytes = new byte[len];
                    dis.readFully(bytes);
                    pool[i] = new CpInfo.Utf8CpInfo(new String(bytes, StandardCharsets.UTF_8));
                }
                case CLASS_TAG -> pool[i] = new CpInfo.ClassCpInfo(dis.readUnsignedShort());
                case METHOD_REF_TAG ->
                        pool[i] = new CpInfo.MethodRefCpInfo(dis.readUnsignedShort(), dis.readUnsignedShort());
                case NAME_AND_TYPE_TAG ->
                        pool[i] = new CpInfo.NameAndTypeCpInfo(dis.readUnsignedShort(), dis.readUnsignedShort());
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