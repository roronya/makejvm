package makejvm.ClassFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ClassFile {
    private static final long MAGIC_NUMBER = 0xCAFEBABEL;

    private final ConstantPool constantPool;

    private ClassFile(ConstantPool cp) {
        constantPool = cp;
    }

    public static ClassFile read(InputStream cfInputStream) throws IOException {
        byte[] allBytes = cfInputStream.readAllBytes();
        ByteBuffer buffer = ByteBuffer.wrap(allBytes);
        buffer.order(ByteOrder.BIG_ENDIAN);

        // check magic number
        checkMagicNumber(buffer);

        // skip minor major version
        buffer.getInt();

        // read ConstantPool
        ConstantPool cp = ConstantPool.read(buffer);

        // skip access_flags, this_class, super_classs
        buffer.position(buffer.position() + 6);
        // skip interfaces[interface_count]
        final int ifCount = buffer.getShort() & 0xFFFF;
        buffer.position(buffer.position() + ifCount * 2);


        return new ClassFile(cp);
    }

    static void checkMagicNumber(ByteBuffer buffer) throws IOException {
        long magic = buffer.getInt() & 0xFFFFFFFFL;
        if (magic != MAGIC_NUMBER) {
            throw new IOException("invalid magic number");
        }
    }
}
