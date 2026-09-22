// Two methods that look interchangeable and are not. This is the single most
// common Optional performance bug.

import java.util.Optional;

public class OrElseVsOrElseGet {

    public static void main(String[] args) {
        System.out.println("--- with a value present ---");

        Optional<String> present = Optional.of("the real value");

        System.out.println("  calling orElse:");
        String a = present.orElse(expensiveDefault("orElse"));
        System.out.println("    result: " + a);

        System.out.println("  calling orElseGet:");
        String b = present.orElseGet(() -> expensiveDefault("orElseGet"));
        System.out.println("    result: " + b);

        System.out.println();
        System.out.println("Look at the output above. Both returned the real value,");
        System.out.println("but expensiveDefault ran for orElse and NOT for orElseGet.");

        System.out.println();
        System.out.println("--- why ---");
        System.out.println("  orElse(T other)            takes a VALUE. Java evaluates");
        System.out.println("                             arguments before the call, so the");
        System.out.println("                             default is computed every time,");
        System.out.println("                             present or not, and then discarded.");
        System.out.println();
        System.out.println("  orElseGet(Supplier<T> s)   takes a LAMBDA. It is only invoked");
        System.out.println("                             when the Optional is actually empty.");

        System.out.println();
        System.out.println("--- when it stops being a performance note ---");

        // If the "default" has side effects, orElse does them unconditionally.
        // Here the counter increments even though the value was present.
        counter = 0;
        Optional.of("x").orElse(withSideEffect());
        System.out.println("  after orElse on a PRESENT optional, counter = " + counter);

        counter = 0;
        Optional.of("x").orElseGet(OrElseVsOrElseGet::withSideEffect);
        System.out.println("  after orElseGet on the same,        counter = " + counter);

        // Now it is a correctness bug, not a performance one. A default that
        // inserts a row, sends a request, or logs will do so when it should not.

        System.out.println();
        System.out.println("--- the rule ---");
        System.out.println("  cheap constant, no side effects   ->  orElse is fine");
        System.out.println("  anything computed, or any side effect  ->  orElseGet");
        System.out.println();
        System.out.println("  orElse(\"\") and orElse(0) are fine. orElse(buildDefault())");
        System.out.println("  and orElse(repository.findDefault()) are bugs.");

        System.out.println();
        System.out.println("--- orElseThrow ---");

        Optional<String> empty = Optional.empty();
        try {
            empty.orElseThrow();
        } catch (java.util.NoSuchElementException e) {
            System.out.println("  no-arg version throws NoSuchElementException: " + e.getMessage());
        }

        try {
            empty.orElseThrow(() -> new IllegalStateException("user 9 must exist by now"));
        } catch (IllegalStateException e) {
            System.out.println("  with a supplier: " + e.getMessage());
        }

        // orElseThrow with a supplier is the honest way to say "absent here is
        // a bug". It gives a message explaining what was expected, which get()
        // never does.
    }

    static int counter = 0;

    static String expensiveDefault(String caller) {
        System.out.println("    >>> expensiveDefault ran, called from " + caller);
        return "computed default";
    }

    static String withSideEffect() {
        counter++;
        return "default";
    }
}
