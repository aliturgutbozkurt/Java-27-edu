// EXPECT: preview
//
// JEP 533, Structured Concurrency, SEVENTH preview in JDK 27.
//
//   https://openjdk.org/jeps/533
//
// This file needs --enable-preview to compile and run. The verification script
// runs it with:
//
//     java --enable-preview --source 27 <file>
//
// SEVEN PREVIEWS IS THE HEADLINE. The API has changed between rounds, and code
// written against JDK 21's version does not compile here. Treat everything
// below as subject to change, and check JEP 533 before using it for real.

import java.util.concurrent.StructuredTaskScope;

public class PreviewStructuredConcurrency {

    static final ScopedValue<String> REQUEST_ID = ScopedValue.newInstance();

    public static void main(String[] args) throws Exception {
        System.out.println("--- the problem it solves ---");
        System.out.println();
        System.out.println("  Module 18's ExecutorService lets you submit two tasks and");
        System.out.println("  forget one. Nothing connects them, nothing cancels the other");
        System.out.println("  when one fails, and a Future nobody inspects swallows its");
        System.out.println("  exception silently.");
        System.out.println();
        System.out.println("  Structured concurrency ties task lifetimes to a lexical scope,");
        System.out.println("  the same way a block ties variable lifetimes.");

        System.out.println();
        System.out.println("--- both succeed ---");

        try (var scope = StructuredTaskScope.open()) {
            var user = scope.fork(() -> {
                Thread.sleep(50);
                return "user:ada";
            });
            var orders = scope.fork(() -> {
                Thread.sleep(30);
                return "orders:3";
            });

            scope.join();

            System.out.println("  " + user.get() + " and " + orders.get());
        }

        // The try-with-resources is the whole point. When that block exits,
        // every forked task is finished or cancelled. There is no way to leak
        // one, because there is no way to leave the block without joining.

        System.out.println();
        System.out.println("--- one fails, the other is cancelled ---");

        try (var scope = StructuredTaskScope.open()) {
            scope.fork(() -> {
                Thread.sleep(2_000);
                return "this should never complete";
            });
            scope.fork(() -> {
                Thread.sleep(20);
                throw new IllegalStateException("the fast one failed");
            });

            scope.join();
            System.out.println("  unreachable");
        } catch (Exception e) {
            System.out.println("  scope failed: " + e.getClass().getSimpleName());
            System.out.println("  cause: " + e.getCause().getMessage());
        }

        System.out.println();
        System.out.println("  Notice how fast that returned. The two-second task was");
        System.out.println("  cancelled the moment its sibling threw, rather than being");
        System.out.println("  waited on pointlessly.");

        System.out.println();
        System.out.println("--- and scoped values ARE inherited here ---");

        ScopedValue.where(REQUEST_ID, "req-7").call(() -> {
            try (var scope = StructuredTaskScope.open()) {
                var task = scope.fork(() ->
                        "child sees: " + (REQUEST_ID.isBound() ? REQUEST_ID.get() : "NOTHING"));
                scope.join();
                System.out.println("  " + task.get());
            }
            return null;
        });

        System.out.println();
        System.out.println("  Module 19 said a plain child thread does NOT inherit a scoped");
        System.out.println("  value, and that inheritance needs structured concurrency. That");
        System.out.println("  is the line above, demonstrated.");
        System.out.println();
        System.out.println("  It works here because the scope GUARANTEES the parent outlives");
        System.out.println("  its children, so the binding cannot expire while a child is");
        System.out.println("  still reading it.");

        System.out.println();
        System.out.println("--- should you use it ---");
        System.out.println();
        System.out.println("  Not in production, on a seventh preview, with an API that has");
        System.out.println("  changed between rounds. Enabling preview features in a");
        System.out.println("  deployed service means every future JDK upgrade may break the");
        System.out.println("  build, and preview class files are refused by a different");
        System.out.println("  release even with the flag.");
        System.out.println();
        System.out.println("  Read it, try it, and watch JEP 533 for the round where it");
        System.out.println("  finalises.");
    }
}
