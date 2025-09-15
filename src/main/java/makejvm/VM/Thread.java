package makejvm.VM;

import makejvm.ClassFile.MethodInfo;

import java.util.List;

public class Thread {
    private List<Frame> frames;

    public void pushFrame(Frame frame) {
        frames.add(frame);
    }

    public void popFrame() {
        frames.removeLast();
    }

    public Frame currentFrame() {
        return frames.getFirst();
    }

    public void execMethod(MethodInfo methodInfo) {
        Frame invokerFrame = this.currentFrame();
        pushFrame(Frame.of(methodInfo).setLocals(invokerFrame.popOperands(methodInfo.getNumArgs())));

        while (true) {
            Frame curFrame = currentFrame();
            if (curFrame == invokerFrame) break;

            InstructionSet.execInstr(this);
        }
    }
}
