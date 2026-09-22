// Java splits exceptions into two families, and it is nearly alone in doing so.
// Understanding the split, and the long-running argument about it, is most of
// what you need here.
//
//                      Throwable
//                     /         \
//                 Error          Exception
//              (do not catch)    /        \
//                     RuntimeException   everything else
//                       (UNCHECKED)        (CHECKED)
//
//   CHECKED     the compiler forces you to catch it or declare it
//   UNCHECKED   the compiler says nothing
//   Error       the JVM is in trouble. OutOfMemoryError, StackOverflowError.
//               Do not catch these; you cannot fix them from inside.

import java.io.IOException;

public class CheckedVsUnchecked {

    public static void main(String[] args) {
        // UNCHECKED. Nothing in the signature warns you, nothing forces a catch.
        // These represent programming mistakes: the fix is to change the code,
        // not to handle the failure at runtime.
        for (String input : new String[]{"12x", null}) {
            try {
                Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("unchecked: " + e.getClass().getSimpleName()
                        + " -> " + e.getMessage());
            }
        }

        // A surprise worth knowing: parseInt(null) throws NumberFormatException,
        // not NullPointerException. Guessing the type instead of reading the
        // javadoc is how you write a catch block that never fires.

        System.out.println();

        // CHECKED. The compiler will not let you ignore this one. Calling
        // readConfig without handling IOException is a compile error, which
        // UnhandledCheckedException.java demonstrates.
        try {
            System.out.println(readConfig("missing.conf"));
        } catch (IOException e) {
            System.out.println("checked:   " + e.getClass().getSimpleName()
                    + " -> " + e.getMessage());
        }

        System.out.println();

        // MULTI-CATCH, when two failures want the same handling. The variable
        // is implicitly final, so you cannot reassign `e` inside.
        for (String input : new String[]{"5", "oops"}) {
            try {
                System.out.println("halved: " + (Integer.parseInt(input) / 0));
            } catch (NumberFormatException | ArithmeticException e) {
                System.out.println("multi-catch: " + e.getClass().getSimpleName());
            }
        }

        System.out.println();

        // ORDER MATTERS. A catch for a supertype must come after its subtypes,
        // because the first matching block wins:
        //
        //     catch (Exception e) { }
        //     catch (IOException e) { }    // error: already caught
        //
        // The compiler rejects the unreachable one, which is one of the few
        // places Java protects you from dead code.
        System.out.println("exception types you will meet constantly:");
        show(() -> Integer.parseInt("12x"));
        show(() -> { Object o = "s"; Integer i = (Integer) o; });
        show(() -> { int[] a = new int[2]; a[5] = 1; });
        show(() -> { String s = null; s.length(); });
    }

    // `throws IOException` is part of the method's contract. Every caller must
    // deal with it, which is exactly the thing people argue about.
    static String readConfig(String path) throws IOException {
        throw new IOException("no such file: " + path);
    }

    static void show(Runnable r) {
        try {
            r.run();
        } catch (RuntimeException e) {
            System.out.println("  " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
}

// THE ARGUMENT, fairly stated.
//
// FOR checked exceptions: a failure that callers can reasonably recover from
// should be visible in the type system. A file might not exist; that is not a
// bug, it is Tuesday, and the compiler making you think about it is a feature.
//
// AGAINST: in practice they push people towards `catch (Exception e) {}` to
// make the compiler stop complaining, which is strictly worse than no checking
// at all. They also leak through abstractions: adding a `throws` to a method
// changes every caller's signature all the way up. And they compose badly with
// lambdas, which is why nothing in the streams API accepts a throwing function.
//
// No language designed after Java has copied them. C# considered and rejected
// the idea. That is evidence, though not proof.
//
// WHAT TO DO TODAY:
//
//   - Use unchecked exceptions for programming errors. IllegalArgumentException,
//     IllegalStateException, NullPointerException.
//   - Use checked exceptions only when the caller can genuinely do something
//     different, and that something is not just logging.
//   - Never declare `throws Exception`. It tells the caller nothing and forces
//     them to catch everything.
