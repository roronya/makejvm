package makejvm.ClassFile;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClassFileTest {
    @Test
    void hello() {
        String hello = new ClassFile().hello();
        assertEquals(hello, "Hello");
    }

}