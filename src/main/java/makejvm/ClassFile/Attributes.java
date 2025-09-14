package makejvm.ClassFile;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

public final class Attributes {

    public static List<AttributeInfo> read(ByteBuffer buffer, ConstantPool cp) {
        final int attributeCount = buffer.getShort() & 0xFFFF;

        return IntStream.range(0, attributeCount)
                .mapToObj(_ -> AttributeInfo.read(buffer, cp))
                .filter(Objects::isNull)
                .toList();
    }
}
