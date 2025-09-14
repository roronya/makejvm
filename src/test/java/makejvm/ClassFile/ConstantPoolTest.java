package makejvm.ClassFile;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

class ConstantPoolTest {

    @Nested
    class getUtf8 {
        @Test
        void test() {
            CpInfo[] pool = {
                    null, // JVMの仕様上、添字が1から始まるので、0にはnullを入れておく。
                    new CpInfo.Utf8CpInfo("test"),
                    new CpInfo.ClassCpInfo(0),
                    new CpInfo.MethodRefCpInfo(0, 0),
                    new CpInfo.NameAndTypeCpInfo(0, 0)
            };
            ConstantPool constantPool = new ConstantPool(pool);
            String actual = constantPool.getUtf8(1);
            Assertions.assertEquals("test", actual);
            actual = constantPool.getUtf8(0);
            Assertions.assertNull(actual);
        }
    }

    @Nested
    class read {
        @Test
        void test() throws IOException {
            try (
                    var baos = new ByteArrayOutputStream();
                    var dos = new DataOutputStream(baos)
            ) {

                dos.writeShort(5); // constant_pool_count = 5

                dos.writeByte(1); // tag = CONSTANT_Utf8_info
                dos.writeShort(5); // length = 5
                dos.writeBytes("hello"); // bytes[length] = "hello"

                dos.writeByte(7); // tag = CONSTANT_Class
                dos.writeShort(0); // name_index

                dos.writeByte(10); // tag = CONSTANT_Methodref
                dos.writeShort(0); // class_index
                dos.writeShort(0); // name_and_type_index

                dos.writeByte(12); // tag = CONSTANT_NameAndType
                dos.writeShort(0); // name_index
                dos.writeShort(0); // descriptor_index


                ByteBuffer buffer = ByteBuffer.wrap(baos.toByteArray());
                ConstantPool cp = ConstantPool.read(buffer);

                String actual = cp.getUtf8(1);
                Assertions.assertEquals("hello", actual);
                // TODO: should check other tags
            }
        }
    }
}