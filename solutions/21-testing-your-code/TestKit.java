// Reference solution for Homework 21.
//
// Deliberately NOT marked to run with -ea. Part three demonstrates a removal
// hidden inside an assert, and without assertions the bug actually manifests:
// the item is not removed and the count stays wrong. That is what production
// would do.
//
// Run it the other way to see the opposite:
//
//     java     solutions/21-testing-your-code/TestKit.java    <- bug visible
//     java -ea solutions/21-testing-your-code/TestKit.java    <- bug hidden
//
// The fact that -ea HIDES the bug is the entire point of that section.

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class TestKit {

    public static void main(String[] args) {
        partOne();
        System.out.println();
        partTwo();
        System.out.println();
        partThree();
        System.out.println();
        partFour();
    }

    // ---------------------------------------------------------------- part 1

    private static void partOne() {
        System.out.println("--- the extended harness ---");

        var suite = new Suite();

        suite.test("addition", () -> suite.assertEquals(4, 2 + 2));
        suite.test("string building", () -> suite.assertEquals("ab", "a" + "b"));
        suite.test("a deliberate failure", () -> suite.assertEquals(5, 2 + 2));

        suite.test("expected exception", () ->
                suite.assertThrows(IllegalArgumentException.class,
                        () -> Inventory.take(-1)));

        suite.test("exception that does NOT arrive", () ->
                suite.assertThrows(IllegalArgumentException.class,
                        () -> Inventory.take(1)));

        suite.test("a test that throws unexpectedly", () -> {
            throw new IllegalStateException("something unrelated broke");
        });

        // Parameterised: one behaviour, several rows. Naming each case with its
        // input is what makes a failure readable; a bare "case 3 failed" would
        // send me counting.
        for (int[] row : new int[][]{{2, 2, 4}, {0, 5, 5}, {-3, 3, 0}, {1, 1, 3}}) {
            suite.test("sum(" + row[0] + ", " + row[1] + ") == " + row[2],
                    () -> suite.assertEquals(row[2], row[0] + row[1]));
        }

        suite.report();

        System.out.println();
        System.out.println("  exit code a real harness would use: " + (suite.failed() > 0 ? 1 : 0));
        System.out.println("  this one returns 0 anyway, so the repository's verification");
        System.out.println("  script stays green while still showing failures.");
    }

    // ---------------------------------------------------------------- part 2

    private static void partTwo() {
        System.out.println("--- isolation, or the lack of it ---");

        // SHARED STATE. Both "tests" use the same object, exactly as they would
        // if a framework reused one instance of the test class.
        var leaky = new Basket();

        boolean firstPassed = leaky.add("apple") && leaky.size() == 1;
        System.out.println("  test A (shared): " + (firstPassed ? "PASS" : "FAIL"));

        boolean secondPassed = leaky.size() == 0;
        System.out.println("  test B (shared): " + (secondPassed ? "PASS" : "FAIL")
                + "   expected an empty basket, found " + leaky.size());

        // FRESH STATE. A supplier gives each test its own object, which is what
        // @BeforeEach and per-test instances do for you.
        Supplier<Basket> fresh = Basket::new;

        boolean firstIsolated = isolatedAdd(fresh.get());
        boolean secondIsolated = fresh.get().size() == 0;
        System.out.println("  test A (fresh):  " + (firstIsolated ? "PASS" : "FAIL"));
        System.out.println("  test B (fresh):  " + (secondIsolated ? "PASS" : "FAIL"));

        // WHY ORDER-DEPENDENT TESTS ARE WORSE THAN NO TESTS, in my own words:
        //
        // A test that passes only because an earlier one ran is not testing the
        // thing it claims to test. It is testing a sequence.
        //
        // The damage arrives later, when someone adds a test in the middle,
        // runs the suite in parallel, or uses a runner that randomises order.
        // Then a test fails, and the change that broke it had nothing to do
        // with the code under test. Whoever picks that up loses a morning
        // reading the wrong file.
        //
        // The absence of a test is at least honest about what it does not
        // cover. An order-dependent test actively lies.
    }

    private static boolean isolatedAdd(Basket basket) {
        return basket.add("apple") && basket.size() == 1;
    }

    // ---------------------------------------------------------------- part 3

    private static void partThree() {
        System.out.println("--- the bug assert hides ---");

        var basket = new Basket();
        basket.add("apple");
        basket.add("pear");

        System.out.println("  before removal: " + basket.size() + " items");

        // The broken version. Under -ea it works; without it the whole
        // statement is skipped and nothing is removed.
        boolean assertionsOn = false;
        assert assertionsOn = true;

        basket.removeWithAssert("apple");

        System.out.println("  assertions enabled: " + assertionsOn);
        System.out.println("  after removeWithAssert: " + basket.size() + " items");
        System.out.println("  " + (basket.size() == 1
                ? "-> the removal happened, because -ea is on"
                : "-> NOTHING WAS REMOVED. The call lives inside the assert."));

        // The fixed version always performs the removal and checks separately.
        var fixed = new Basket();
        fixed.add("apple");
        fixed.add("pear");
        fixed.removeProperly("apple");
        System.out.println("  after removeProperly:   " + fixed.size() + " items");

        // WHY THIS IS THE WORST SHAPE OF BUG, in my own words:
        //
        // It passes every test. Tests are usually run with -ea, or at least the
        // developer runs them that way, so the assert executes and the removal
        // happens and everything looks right.
        //
        // Production is not run with -ea. The statement vanishes, the removal
        // never occurs, and the failure shows up as data that slowly drifts
        // wrong rather than as a crash pointing at a line.
        //
        // The rule that prevents it is absolute and easy: an assert may READ
        // state, never CHANGE it. Anything with a side effect goes outside.
    }

    // ---------------------------------------------------------------- part 4

    private static void partFour() {
        System.out.println("--- assert versus exception ---");

        // A PUBLIC caller's argument. This must be an exception: assert is off
        // in production, so the check would simply not happen and a negative
        // quantity would flow into the rest of the system unchecked.
        try {
            Inventory.take(-5);
        } catch (IllegalArgumentException e) {
            System.out.println("  public argument check -> IllegalArgumentException: "
                    + e.getMessage());
        }

        // An INTERNAL invariant. This is what assert is for: a statement about
        // something this class has just established itself, where being wrong
        // means a bug in my own reasoning rather than bad input.
        System.out.println("  internal invariant   -> " + Inventory.rebalance(10));

        System.out.println();
        System.out.println("  THE TEST I USE TO DECIDE:");
        System.out.println();
        System.out.println("    Could a caller outside my control cause this to be false?");
        System.out.println("      yes -> exception. it must be checked in production.");
        System.out.println("      no  -> assert. if it is false, I wrote a bug, and the");
        System.out.println("             cost of checking in production is not worth it.");
        System.out.println();
        System.out.println("  Put another way: an exception documents a contract with");
        System.out.println("  callers. An assert documents a belief about my own code.");
    }

    // ---------------------------------------------------------------- types

    static final class Inventory {

        // Public API: validate with an exception, always on.
        static int take(int quantity) {
            if (quantity <= 0) {
                throw new IllegalArgumentException("quantity must be positive, was " + quantity);
            }
            return quantity;
        }

        // Internal invariant: assert is appropriate, because nothing outside
        // this class can make it false.
        static String rebalance(int total) {
            int half = total / 2;
            int rest = total - half;
            assert half + rest == total : "split lost items: " + half + " + " + rest + " != " + total;
            return "split " + total + " into " + half + " and " + rest;
        }
    }

    static final class Basket {

        private final List<String> items = new ArrayList<>();

        boolean add(String item) {
            return items.add(item);
        }

        int size() {
            return items.size();
        }

        // BROKEN. The removal is a side effect inside the assert, so it only
        // happens when assertions are enabled.
        @SuppressWarnings("AssertWithSideEffects")
        void removeWithAssert(String item) {
            assert items.remove(item) : "expected " + item + " to be present";
        }

        // FIXED. The removal always happens; the check is separate and can
        // safely be an assert because it only reads the result.
        void removeProperly(String item) {
            boolean removed = items.remove(item);
            assert removed : "expected " + item + " to be present";
        }
    }
}

// The harness, extended with assertEquals, assertThrows and parameterised
// support. Still no dependencies.
final class Suite {

    private record Result(String name, boolean passed, String detail) { }

    private final List<Result> results = new ArrayList<>();
    private String pendingFailure;

    void test(String name, Runnable body) {
        pendingFailure = null;
        try {
            body.run();
            results.add(pendingFailure == null
                    ? new Result(name, true, "")
                    : new Result(name, false, pendingFailure));
        } catch (Throwable t) {
            // Catching Throwable is correct here and almost nowhere else: the
            // harness must survive anything a test does, or one bad test ends
            // the run and hides every test after it.
            results.add(new Result(name, false,
                    "unexpected " + t.getClass().getSimpleName() + ": " + t.getMessage()));
        }
    }

    void assertEquals(Object expected, Object actual) {
        if (!java.util.Objects.equals(expected, actual)) {
            pendingFailure = "expected <" + expected + "> but was <" + actual + ">";
        }
    }

    void assertThrows(Class<? extends Throwable> expected, Runnable body) {
        try {
            body.run();
            pendingFailure = "expected " + expected.getSimpleName() + " but nothing was thrown";
        } catch (Throwable actual) {
            if (!expected.isInstance(actual)) {
                pendingFailure = "expected " + expected.getSimpleName()
                        + " but got " + actual.getClass().getSimpleName();
            }
        }
    }

    long failed() {
        return results.stream().filter(r -> !r.passed()).count();
    }

    void report() {
        for (Result r : results) {
            System.out.printf("  %-6s %s%n", r.passed() ? "PASS" : "FAIL", r.name());
            if (!r.passed()) {
                System.out.println("         " + r.detail());
            }
        }
        System.out.println();
        System.out.printf("  %d run, %d passed, %d failed%n",
                results.size(), results.size() - failed(), failed());
    }
}
