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
        long magic = buffer.getInt() & 0xFFFFFFFFL;
        if (magic != MAGIC_NUMBER) {
            throw new IOException("invalid magic number");
        }

        // skip minor major version
        buffer.getInt();

        // read ConstantPool
        ConstantPool cp = ConstantPool.read(buffer);

        return new ClassFile(cp);
    }
}
