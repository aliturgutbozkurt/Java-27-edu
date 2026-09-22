// The first thing to know about `assert` is that it does nothing.
//
// Assertions are DISABLED by default. Every assert statement in your program is
// a no-op unless the JVM was started with -ea, and that is not the default in
// development, in CI, or in production.
//
// This file detects the situation rather than describing it. Run it both ways
// and compare.

public class AssertionsAreOff {

    public static void main(String[] args) {
        // THE STANDARD DETECTION TRICK.
        //
        // The assignment inside the assert only executes if assertions are
        // enabled, because the whole statement is skipped otherwise. It reads
        // strangely on purpose: it is one of the few places where putting a
        // side effect inside an assert is correct.
        boolean enabled = false;
        assert enabled = true;

        System.out.println("  assertions enabled: " + enabled);
        System.out.println();

        if (enabled) {
            System.out.println("  You ran this with -ea, so the assert below will fire.");
        } else {
            System.out.println("  You ran this WITHOUT -ea, which is the default.");
            System.out.println("  The assert below is dead code. It costs nothing and does");
            System.out.println("  nothing, which is exactly the trap.");
        }

        System.out.println();
        try {
            assert 1 == 2 : "one is not two";
            System.out.println("  the assert did not fire");
        } catch (AssertionError e) {
            System.out.println("  AssertionError: " + e.getMessage());
        }

        System.out.println();
        System.out.println("--- try both ---");
        System.out.println("  java     modules/21-testing-your-code/examples/AssertionsAreOff.java");
        System.out.println("  java -ea modules/21-testing-your-code/examples/AssertionsAreOff.java");

        System.out.println();
        System.out.println("--- what follows from that ---");
        System.out.println();
        System.out.println("  1. NEVER put logic inside an assert. This is a real bug:");
        System.out.println();
        System.out.println("       assert list.remove(item);");
        System.out.println();
        System.out.println("     It works in your tests with -ea and silently stops");
        System.out.println("     removing anything in production.");
        System.out.println();
        System.out.println("  2. NEVER use assert to validate arguments from callers.");
        System.out.println("     Use an exception, which is always on:");
        System.out.println();
        System.out.println("       if (n < 0) throw new IllegalArgumentException(...);");
        System.out.println();
        System.out.println("  3. assert is for INTERNAL invariants you believe cannot be");
        System.out.println("     false: a switch default that should be unreachable, a");
        System.out.println("     state your own code has just established.");

        System.out.println();
        System.out.println("--- so what do you test with ---");
        System.out.println("  MicroHarness.java, next, and then a real framework.");
    }
}
