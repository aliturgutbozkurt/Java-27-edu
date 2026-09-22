// `finally` always runs. That is the promise, and it is also the trap.

public class FinallySwallows {

    public static void main(String[] args) {
        System.out.println("--- return inside finally ---");
        System.out.println("  got: " + swallowsTheException());
        System.out.println("  an exception was thrown inside that method.");
        System.out.println("  it is gone. no log, no stack trace, nothing.");

        System.out.println();
        System.out.println("--- finally overwriting a good return value ---");
        System.out.println("  got: " + overwritesTheResult());

        System.out.println();
        System.out.println("--- what finally is actually for ---");
        System.out.println("  got: " + properUse());
    }

    // THE BUG.
    //
    // A `return` inside finally discards whatever the try block was doing,
    // including an exception in flight. The RuntimeException below never
    // reaches the caller and leaves no trace at all.
    //
    // javac warns about this if you enable lint, and most style checkers ban it
    // outright. Never put return, break or continue inside a finally block.
    @SuppressWarnings("finally")
    static int swallowsTheException() {
        try {
            throw new RuntimeException("you will never see this");
        } finally {
            return 42;
        }
    }

    // The same mechanism, less dramatic and easier to miss in review. The try
    // block's value is computed, then thrown away.
    @SuppressWarnings("finally")
    static String overwritesTheResult() {
        try {
            return "the real answer";
        } finally {
            return "the finally block's answer";
        }
    }

    // THE LEGITIMATE USE: cleanup that must happen either way, with no control
    // flow of its own.
    //
    // Even here, try-with-resources is better whenever the thing being cleaned
    // up is AutoCloseable. Reach for finally when it is not, for example
    // restoring a flag or unlocking a lock.
    static String properUse() {
        boolean flagWasSet = false;
        try {
            flagWasSet = true;
            return "did the work";
        } finally {
            // Runs on the way out, whether by return or by exception, and
            // changes neither.
            System.out.println("  cleanup ran, flag was " + flagWasSet);
        }
    }

    // ONE MORE CASE: finally does NOT run if the JVM exits.
    //
    //     try { System.exit(0); } finally { System.out.println("never"); }
    //
    // Nor if the thread is killed or the machine loses power. "Always runs" means
    // always within a normal method exit, not a guarantee against everything.
}
