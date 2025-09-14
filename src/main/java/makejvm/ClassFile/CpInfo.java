package makejvm.ClassFile;

public sealed interface CpInfo {
    // CONSTANT_Utf8_info
    record Utf8CpInfo(String value) implements CpInfo {
    }

    // CONSTANT_Class_info
    record ClassCpInfo(int nameIndex) implements CpInfo {
    }

    // CONSTANT_Methodref_info
    record MethodRefCpInfo(int classIndex, int nameAndTypeIndex) implements CpInfo {
    }

    // CONSTANT_NameAndType_info
    record NameAndTypeCpInfo(int nameIndex, int descriptorIndex) implements CpInfo {
    }
}
