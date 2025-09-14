package makejvm.ClassFile;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AttributeInfoTest {

    /**
     * テスト用のConstantPoolインスタンスを生成するヘルパーメソッド。
     *
     * @param utf8Map {index: Utf8文字列} のマップ
     * @return ConstantPoolのインスタンス
     */
    private ConstantPool createConstantPool(Map<Integer, String> utf8Map) {
        int maxIndex = utf8Map.keySet().stream().mapToInt(Integer::intValue).max().orElse(0);
        CpInfo[] pool = new CpInfo[maxIndex + 1];
        utf8Map.forEach((index, value) -> {
            if (index > 0) {
                pool[index] = new CpInfo.Utf8CpInfo(value);
            }
        });
        return new ConstantPool(pool);
    }

    @Nested
    @DisplayName("AttributeInfo.read(ByteBuffer, ConstantPool) のテスト")
    class ReadGeneralAttribute {

        @Test
        @DisplayName("属性名が 'Code' の場合、CodeAttributeを正しく読み取れること")
        void shouldReadCodeAttributeWhenNameIsCode() {
            // Arrange
            // 定数プールにはインデックス1に"Code"、2に"DummyAttribute"があると仮定
            ConstantPool cp = createConstantPool(Map.of(1, "Code", 2, "DummyAttribute"));

            byte[] codeBytes = {(byte) 0x2a, (byte) 0xb7, 0x00, 0x01, (byte) 0xb1}; // 簡単なバイトコード

            // CodeAttribute内にネストされた、スキップ対象の属性データ
            int nestedAttributeLength = 6;

            // CodeAttribute全体の長さ = 各フィールドのバイト長の合計
            int attributeLength = 2 + 2 + 4 + codeBytes.length + 2 + 2 + (2 + 4 + nestedAttributeLength);
            // max_stack(2) + max_locals(2) + code_length(4) + code(5) +
            // exception_table_length(2) + exception_table(0) +
            // attributes_count(2) + nested_attribute(header(6) + body(6)) = 29

            // AttributeInfo全体のバイトデータを作成
            ByteBuffer buffer = ByteBuffer.allocate(2 + 4 + attributeLength);
            buffer.putShort((short) 1); // attribute_name_index -> "Code"
            buffer.putInt(attributeLength);
            // --- CodeAttribute の中身 ---
            buffer.putShort((short) 10); // max_stack
            buffer.putShort((short) 5);  // max_locals
            buffer.putInt(codeBytes.length); // code_length
            buffer.put(codeBytes); // code
            buffer.putShort((short) 0); // exception_table_length = 0
            buffer.putShort((short) 1); // attributes_count = 1
            // Nested attribute (これはCodeAttribute.read内部でスキップされる)
            buffer.putShort((short) 2); // attribute_name_index -> "DummyAttribute"
            buffer.putInt(nestedAttributeLength);
            buffer.put(new byte[nestedAttributeLength]);
            // --- ここまで ---
            buffer.flip(); // 読み取りモードへ

            // Act
            AttributeInfo attribute = AttributeInfo.read(buffer, cp);

            // Assert
            assertNotNull(attribute, "属性はnullであってはなりません");
            assertInstanceOf(AttributeInfo.CodeAttribute.class, attribute, "属性はCodeAttributeのインスタンスであるべきです");

            var codeAttribute = (AttributeInfo.CodeAttribute) attribute;
            assertEquals(10, codeAttribute.maxStack());
            assertEquals(5, codeAttribute.maxLocals());
            assertArrayEquals(codeBytes, codeAttribute.code());

            assertEquals(0, buffer.remaining(), "バッファを最後まで読み切っているべきです");
        }

        @Test
        @DisplayName("未知の属性の場合、データをスキップしてnullを返すこと")
        void shouldSkipUnknownAttributeAndReturnNull() {
            // Arrange
            ConstantPool cp = createConstantPool(Map.of(1, "UnknownAttribute"));
            int attributeLength = 12;

            ByteBuffer buffer = ByteBuffer.allocate(2 + 4 + attributeLength);
            buffer.putShort((short) 1); // attribute_name_index -> "UnknownAttribute"
            buffer.putInt(attributeLength);
            buffer.put(new byte[attributeLength]); // ダミーデータ
            buffer.flip(); // 読み取りモードへ

            // Act
            AttributeInfo attribute = AttributeInfo.read(buffer, cp);

            // Assert
            assertNull(attribute, "未知の属性の場合はnullが返されるべきです");
            assertEquals(0, buffer.remaining(), "バッファのポインタが属性の最後まで進んでいるべきです");
        }
    }

    @Nested
    @DisplayName("CodeAttribute.read(ByteBuffer, ConstantPool) のテスト")
    class ReadCodeAttributeDetail {

        @Test
        @DisplayName("全フィールドをパースし、exception_tableとネストされた属性を正しくスキップすること")
        void shouldParseAllFieldsAndSkipTablesAndNestedAttributes() {
            // Arrange
            // ネストされた属性の読み取り（スキップ）のためにConstantPoolが必要
            ConstantPool cp = createConstantPool(Map.of(1, "LineNumberTable"));
            byte[] codeBytes = {(byte) 0xb1}; // return

            int exceptionTableCount = 1; // 例外テーブルが1つ
            int exceptionEntrySize = 8;   // 各エントリは8バイト
            int nestedAttributeLength = 4; // ネストされた属性の長さ

            int bufferSize = 2 + 2 + 4 + codeBytes.length + 2 + (exceptionTableCount * exceptionEntrySize) + 2 + (2 + 4 + nestedAttributeLength);
            ByteBuffer buffer = ByteBuffer.allocate(bufferSize);

            buffer.putShort((short) 20); // max_stack
            buffer.putShort((short) 3);  // max_locals
            buffer.putInt(codeBytes.length); // code_length
            buffer.put(codeBytes); // code
            buffer.putShort((short) exceptionTableCount); // exception_table_length
            buffer.put(new byte[exceptionTableCount * exceptionEntrySize]); // exception_table data (スキップ対象)
            buffer.putShort((short) 1); // attributes_count
            // Nested attribute (スキップ対象)
            buffer.putShort((short) 1); // attribute_name_index -> "LineNumberTable"
            buffer.putInt(nestedAttributeLength);
            buffer.put(new byte[nestedAttributeLength]); // dummy data
            buffer.flip(); // 読み取りモードへ

            // Act
            AttributeInfo.CodeAttribute attribute = AttributeInfo.CodeAttribute.read(buffer, cp);

            // Assert
            assertNotNull(attribute);
            assertEquals(20, attribute.maxStack());
            assertEquals(3, attribute.maxLocals());
            assertArrayEquals(codeBytes, attribute.code());
            System.out.println(buffer);
            assertEquals(0, buffer.remaining(), "バッファを最後まで読み切っているべきです");
        }
    }
}