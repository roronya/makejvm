package makejvm.ClassFile;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

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
        void test() {
            // 必要なバイト数 (2 + (1+2+5) + (1+2) + (1+2+2) + (1+2+2) = 23) を確保
            ByteBuffer buffer = ByteBuffer.allocate(23);

            // メソッドチェーンで順にデータを書き込む
            buffer.putShort((short) 5) // constant_pool_count = 5

                    .put((byte) 1) // tag = CONSTANT_Utf8_info
                    .putShort((short) 5) // length = 5
                    .put("hello".getBytes(StandardCharsets.UTF_8)) // bytes[length] = "hello"

                    .put((byte) 7) // tag = CONSTANT_Class
                    .putShort((short) 0) // name_index

                    .put((byte) 10) // tag = CONSTANT_Methodref
                    .putShort((short) 0) // class_index
                    .putShort((short) 0) // name_and_type_index

                    .put((byte) 12) // tag = CONSTANT_NameAndType
                    .putShort((short) 0) // name_index
                    .putShort((short) 0); // descriptor_index

            // バッファを書き込みモードから読み取りモードへ切り替える
            buffer.flip();

            ConstantPool cp = ConstantPool.read(buffer);

            String actual = cp.getUtf8(1);
            Assertions.assertEquals("hello", actual);
            // TODO: should check other tags
        }
    }
}