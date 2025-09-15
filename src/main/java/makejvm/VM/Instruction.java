package makejvm.VM;


@FunctionalInterface
public interface Instruction {
    void execute(Thread thread);
}