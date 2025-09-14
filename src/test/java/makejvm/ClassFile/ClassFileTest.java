package makejvm.ClassFile;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.ByteBuffer;

class ClassFileTest {
    @Nested
    class read {
        @Test
        void validMagicNumber() {
            byte[] bytes = {(byte) 0xCA, (byte) 0xFE, (byte) 0xBA, (byte) 0xBE};
            var buffer = ByteBuffer.wrap(bytes);

            Assertions.assertDoesNotThrow(() -> ClassFile.checkMagicNumber(buffer));
        }

        @Test
        void invalidMagicNumber() {
            byte[] bytes = {(byte) 0xAA, (byte) 0xBB, (byte) 0xCC, (byte) 0xDD};
            var buffer = ByteBuffer.wrap(bytes);

            Assertions.assertThrows(IOException.class, () -> ClassFile.checkMagicNumber(buffer));
        }
    }

}