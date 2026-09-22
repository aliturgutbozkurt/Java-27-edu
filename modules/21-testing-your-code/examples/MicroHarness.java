// A test harness in under fifty lines, with no dependencies.
//
// The point is not that you should use this. It is that a test framework is not
// magic, and seeing the whole of one makes the real ones easier to reason about.

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class MicroHarness {

    public static void main(String[] args) {
        Tests tests = new Tests();

        tests.check("addition works", () -> 2 + 2 == 4);
        tests.check("string concatenation", () -> "ab".equals("a" + "b"));
        tests.check("list size", () -> List.of(1, 2, 3).size() == 3);

        // A deliberately failing test, so the report has something to report.
        tests.check("this one is wrong on purpose", () -> 2 + 2 == 5);

        // And one that throws rather than returning false, which a harness must
        // also handle or a single bad test takes the whole run down.
        tests.check("this one throws", () -> {
            throw new IllegalStateException("boom");
        });

        tests.equals("equality with a useful message", 4, 2 + 2);
        tests.equals("and one that fails", 5, 2 + 2);

        tests.report();

        // NOTE ON THE EXIT CODE.
        //
        // A real harness exits non-zero when anything failed, so CI notices.
        // This one deliberately does not, because it is a teaching example that
        // must run clean under this repository's verification script.
        //
        // In your own harness the last line would be:
        //
        //     System.exit(tests.failed() > 0 ? 1 : 0);
    }
}

final class Tests {

    private record Result(String name, boolean passed, String detail) { }

    private final List<Result> results = new ArrayList<>();

    void check(String name, Supplier<Boolean> condition) {
        try {
            boolean ok = condition.get();
            results.add(new Result(name, ok, ok ? "" : "returned false"));
        } catch (Throwable t) {
            // A test that throws is a failure, not a crash of the run. Catching
            // Throwable here is one of the few places it is correct: the
            // harness must survive anything a test does to it.
            results.add(new Result(name, false, t.getClass().getSimpleName() + ": " + t.getMessage()));
        }
    }

    void equals(String name, Object expected, Object actual) {
        boolean ok = java.util.Objects.equals(expected, actual);
        results.add(new Result(name, ok, ok ? "" : "expected <" + expected + "> but was <" + actual + ">"));
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
