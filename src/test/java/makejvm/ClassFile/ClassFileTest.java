package makejvm.ClassFile;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

class ClassFileTest {
    @Nested
    class read {
        @Test
        void validMagicNumber() {
            byte[] bytes = {(byte) 0xCA, (byte) 0xFE, (byte) 0xBA, (byte) 0xBE};
            InputStream inputStream = new ByteArrayInputStream(bytes);

            Assertions.assertDoesNotThrow(() -> ClassFile.read(inputStream));
        }

        @Test
        void invalidMagicNumber() {
            byte[] bytes = {(byte) 0xAA, (byte) 0xBB, (byte) 0xCC, (byte) 0xDD};
            InputStream inputStream = new ByteArrayInputStream(bytes);

            Assertions.assertThrows(IOException.class, () -> ClassFile.read(inputStream));
        }
    }

}