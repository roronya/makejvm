package makejvm.ClassFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

public class ClassFile {
    private static final long MAGIC_NUMBER = 0xCAFEBABEL;

    private final ConstantPool constantPool;

    private final List<MethodInfo> methodInfos;

    private ClassFile(ConstantPool cp, List<MethodInfo> methodInfos) {
        this.constantPool = cp;
        this.methodInfos = methodInfos;
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

        // skip class field
        // call readMethodInfo to skip class field because they have same construction of MethodInfo
        MethodInfo.read(buffer, cp);

        var methodInfos = MethodInfo.read(buffer, cp);

        return new ClassFile(cp, methodInfos);
    }

    static void checkMagicNumber(ByteBuffer buffer) throws IOException {
        long magic = buffer.getInt() & 0xFFFFFFFFL;
        if (magic != MAGIC_NUMBER) {
            throw new IOException("invalid magic number");
        }
    }
}
