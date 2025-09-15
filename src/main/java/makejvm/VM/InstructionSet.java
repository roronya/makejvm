package makejvm.VM;

import java.util.Arrays;

public final class InstructionSet {
    private static final Instruction[] TABLE = new Instruction[256];

    static {
        Arrays.fill(TABLE, null);

        TABLE[0x00] = InstructionSet::instrNop;
        TABLE[0x03] = instrConst(0);
        TABLE[0x04] = instrConst(1);

        TABLE[0x1A] = instrILoad(0);
        TABLE[0x1B] = instrILoad(1);
        TABLE[0x1C] = instrILoad(2);

        TABLE[0x3C] = instrIStore(1);
        TABLE[0x3D] = instrIStore(2);

        TABLE[0x60] = InstructionSet::instrIAddr;

        TABLE[0x84] = InstructionSet::instrIInc;

        TABLE[0xA3] = InstructionSet::instrIfICmpGt;

        TABLE[0xA7] = InstructionSet::instrGoTo;

        TABLE[0xAC] = InstructionSet::instrIReturn;
    }

    private InstructionSet() {
    }

    public static void execInstr(Thread t) {
        int opCode = t.currentFrame().nextInstr();

        Instruction instr = TABLE[opCode];
        if (instr == null) {
            throw new IllegalStateException(String.format("op(code = 0x%02X) has been Not implemented", opCode));
        }

        instr.execute(t);
    }

    private static void instrNop(Thread t) {
    }

    private static Instruction instrConst(int n) {
        return t -> t.currentFrame().pushOperand(n);
    }

    private static Instruction instrILoad(int n) {
        return t -> {
            Frame f = t.currentFrame();
            f.pushOperand(f.getLocals()[n]);
        };
    }

    private static Instruction instrIStore(int n) {
        return t -> {
            Frame f = t.currentFrame();
            f.setLocal(n, f.popOperand());
        };
    }

    private static void instrIAddr(Thread t) {
        Frame f = t.currentFrame();
        int v2 = (int) f.popOperand();
        int v1 = (int) f.popOperand();
        f.pushOperand(v1 + v2);
    }

    private static void instrIInc(Thread t) {
        Frame f = t.currentFrame();
        byte index = f.nextPramByte();
        int count = f.nextPramByte();
        int value = (int) f.getLocals()[index];
        f.setLocal(index, value + count);
    }

    private static void instrIfICmpGt(Thread t) {
        Frame f = t.currentFrame();
        int branch = f.nextPramInt();
        int v2 = (int) f.popOperand();
        int v1 = (int) f.popOperand();
        if (v1 > v2) {
            f.jumpPc(f.getPc() + branch);
        }
    }

    private static void instrGoTo(Thread t) {
        Frame f = t.currentFrame();
        int branch = f.nextPramInt();
        f.jumpPc(f.getPc() + branch);
    }

    private static void instrIReturn(Thread t) {
        Object retVal = t.currentFrame().popOperand();
        t.popFrame();

        t.currentFrame().pushOperand(retVal);
    }


}
