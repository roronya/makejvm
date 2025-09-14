package makejvm.ClassFile;

import java.nio.ByteBuffer;

public sealed interface AttributeInfo {
    static AttributeInfo read(ByteBuffer buffer, ConstantPool cp) {
        String attributeName = cp.getUtf8(buffer.getShort() & 0xFFFF);
        int attributeLength = buffer.getInt();
        return switch (attributeName) {
            case "Code" -> CodeAttribute.read(buffer, cp);
            default -> {
                buffer.position(buffer.position() + attributeLength);
                yield null;
            }
        };
    }

    record CodeAttribute(int maxStack, int maxLocals, byte[] code) implements AttributeInfo {
        public static AttributeInfo.CodeAttribute read(ByteBuffer buffer, ConstantPool cp) {
            int maxStack = buffer.getShort() & 0xFFFF;
            int maxLocals = buffer.getShort() & 0xFFFF;
            int codeLength = buffer.getInt();
            byte[] code = new byte[codeLength];
            buffer.get(code);

            // skip exception_table
            int exceptionTableLength = buffer.getShort() & 0xFFFF;
            buffer.position(buffer.position() + exceptionTableLength * 8);

            // skip attributes
            Attributes.read(buffer, cp);

            return new CodeAttribute(maxStack, maxLocals, code);
        }
    }
}
