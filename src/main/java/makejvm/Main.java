package makejvm;

import makejvm.ClassFile.ClassFile;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class Main {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java Main <path/to/your/classfile.class>");
            return;
        }

        String filePath = args[0];
        System.out.println("Reading class file: " + filePath);

        try (InputStream fis = new FileInputStream(filePath)) {
            ClassFile cf = ClassFile.read(fis);
            System.out.println("Successfully parsed the class file.");

            cf.methods().forEach(System.out::println);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}