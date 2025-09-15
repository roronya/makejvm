package makejvm.VM;

import makejvm.ClassFile.MethodInfo;

import java.nio.ByteBuffer;
import java.util.Stack;
import java.util.stream.IntStream;

public class Frame {
    private final Object[] locals;
    private final Stack<Object> opStack;
    private final ByteBuffer code;
    private int pc;

    private Frame(Object[] locals, Stack<Object> opStack, ByteBuffer code, int pc) {
        this.locals = locals;
        this.opStack = opStack;
        this.code = code;
        this.pc = pc;
    }

    public static Frame of(MethodInfo method) {
        var code = method.getCode();
        return new Frame(new Object[code.maxLocals()], null, ByteBuffer.wrap(code.code()), 0);
    }

    public Frame setLocal(int index, Object v) {
        locals[index] = v;
        return this;
    }

    public Object[] getLocals() {
        return locals;
    }

    public Frame setLocals(Object[] vars) {
        System.arraycopy(vars, 0, locals, 0, vars.length);
        return this;
    }

    public byte nextInstr() {
        pc = code.position();
        return code.get();
    }

    public byte nextPramByte() {
        return code.get();
    }

    public int nextPramInt() {
        return code.getShort() & 0xFFFF;
    }

    public int getPc() {
        return pc;
    }

    public void jumpPc(int pc) {
        this.pc = pc;
        code.position(pc);
    }

    public void pushOperand(Object value) {
        opStack.push(value);
    }

    public Object popOperand() {
        return opStack.pop();
    }

    public Object[] popOperands(int n) {
        return IntStream.range(0, n)
                .mapToObj(_ -> opStack.pop())
                .toArray();
    }
}
