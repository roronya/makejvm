package makejvm.ClassFile;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.stream.IntStream;

public record MethodInfo(int accessFlag, String name, String descriptor, List<AttributeInfo> attributes) {
    public static List<MethodInfo> read(ByteBuffer buffer, ConstantPool cp) {
        int methodsCount = buffer.getShort() & 0xFFFF;
        return IntStream.range(0, methodsCount)
                .mapToObj(i -> {
                    int accessFlags = buffer.getShort() & 0xFF;
                    int nameIndex = buffer.getShort() & 0xFF;
                    String name = cp.getUtf8(nameIndex);
                    int descriptorIndex = buffer.getShort() & 0xFF;
                    String descriptor = cp.getUtf8(descriptorIndex);
                    List<AttributeInfo> attributes = Attributes.read(buffer, cp);
                    return new MethodInfo(accessFlags, name, descriptor, attributes);
                })
                .toList();
    }

    public AttributeInfo.CodeAttribute getCode() {
        return this.attributes.stream()
                .filter(AttributeInfo.CodeAttribute.class::isInstance)
                .findFirst()
                .map(AttributeInfo.CodeAttribute.class::cast)
                .orElse(null);
    }

    @Override
    public String toString() {
        return name + descriptor;
    }
}
