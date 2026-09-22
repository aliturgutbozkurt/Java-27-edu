// ScopedValue, finalised in JDK 25, is the replacement for ThreadLocal in a
// world with millions of threads.
//
// The problem it solves: a ThreadLocal is a mutable, unbounded, per-thread map
// entry that lives until someone remembers to remove it. With a thread per
// request and a few hundred threads, that was survivable. With a million
// virtual threads it is not.

public class ScopedValues {

    // Declared once, static and final, like a constant.
    private static final ScopedValue<String> CURRENT_USER = ScopedValue.newInstance();
    private static final ScopedValue<String> REQUEST_ID = ScopedValue.newInstance();

    public static void main(String[] args) throws InterruptedException {
        System.out.println("--- bound only inside the scope ---");

        System.out.println("  before: bound? " + CURRENT_USER.isBound());

        ScopedValue.where(CURRENT_USER, "ada").run(() -> {
            System.out.println("  inside: " + CURRENT_USER.get());
            handleRequest();
        });

        System.out.println("  after:  bound? " + CURRENT_USER.isBound());

        // The binding exists for exactly the duration of run(). There is no
        // remove() to forget, because there is nothing to clean up: the value
        // is gone when the lambda returns, including if it throws.

        System.out.println();
        System.out.println("--- several at once, and nesting ---");

        ScopedValue.where(CURRENT_USER, "grace")
                .where(REQUEST_ID, "req-42")
                .run(() -> {
                    System.out.println("  outer: " + CURRENT_USER.get() + " / " + REQUEST_ID.get());

                    // Rebinding inside a nested scope shadows, and the original
                    // is restored on the way out.
                    ScopedValue.where(REQUEST_ID, "req-99").run(() ->
                            System.out.println("  inner: " + CURRENT_USER.get() + " / " + REQUEST_ID.get()));

                    System.out.println("  outer again: " + REQUEST_ID.get());
                });

        System.out.println();
        System.out.println("--- immutable, which is the point ---");
        System.out.println();
        System.out.println("  A ThreadLocal can be set by anything that can reach it, at any");
        System.out.println("  depth, and the change persists for the rest of the thread's");
        System.out.println("  life. Tracking down who set what becomes archaeology.");
        System.out.println();
        System.out.println("  A ScopedValue cannot be reassigned. The only way to change what");
        System.out.println("  a callee sees is to open a nested scope, which is visible in");
        System.out.println("  the code and ends where the scope does.");

        System.out.println();
        System.out.println("--- an important limitation ---");

        ScopedValue.where(CURRENT_USER, "alan").run(() -> {
            Thread child = Thread.ofVirtual().unstarted(() ->
                    System.out.println("  child thread sees it bound? " + CURRENT_USER.isBound()));
            child.start();
            try {
                child.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        System.out.println();
        System.out.println("  A plain child thread does NOT inherit the binding. That is");
        System.out.println("  deliberate: the parent's scope could end while the child is");
        System.out.println("  still running, leaving the value dangling.");
        System.out.println();
        System.out.println("  Inheritance requires structured concurrency, where the parent");
        System.out.println("  is guaranteed to outlive its children. That is JEP 533, still");
        System.out.println("  in preview on this release, and Module 22 covers it.");

        System.out.println();
        System.out.println("--- when to use which ---");
        System.out.println();
        System.out.println("  ScopedValue   a value for the duration of a call, read-only,");
        System.out.println("                such as the current user or a request id");
        System.out.println();
        System.out.println("  ThreadLocal   still needed when you genuinely need mutable");
        System.out.println("                per-thread state, such as a reusable buffer or a");
        System.out.println("                SimpleDateFormat. Both are rarer than they look.");
    }

    // Note that nothing is passed down. The scoped value is readable at any
    // depth within the scope, which is the convenience ThreadLocal offered and
    // the reason people reached for it.
    private static void handleRequest() {
        System.out.println("  three frames deep: " + CURRENT_USER.get());
    }
}
