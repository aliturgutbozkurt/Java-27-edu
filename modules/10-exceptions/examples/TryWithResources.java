// Anything that must be closed should be opened in a try-with-resources.
// The behaviour when things go wrong is more interesting than the happy path.

public class TryWithResources {

    public static void main(String[] args) {
        System.out.println("--- the happy path ---");
        try (Resource a = new Resource("A");
             Resource b = new Resource("B")) {
            System.out.println("  using " + a.name() + " and " + b.name());
        }

        System.out.println();
        System.out.println("--- the body throws, and so does every close ---");
        try (FailingResource a = new FailingResource("A");
             FailingResource b = new FailingResource("B")) {
            System.out.println("  body running");
            throw new IllegalStateException("body failed");
        } catch (Exception e) {
            System.out.println("  caught: " + e.getMessage());
            for (Throwable suppressed : e.getSuppressed()) {
                System.out.println("    suppressed: " + suppressed.getMessage());
            }
        }

        // TWO THINGS TO TAKE FROM THAT OUTPUT:
        //
        // 1. RESOURCES CLOSE IN REVERSE ORDER. B was opened last and closed
        //    first. That matters when one resource depends on another, for
        //    example a writer wrapping a stream.
        //
        // 2. SUPPRESSED EXCEPTIONS. The body's failure is the one you catch.
        //    Failures from close() are attached to it rather than replacing it,
        //    and you can read them with getSuppressed().
        //
        // Both of those exist because the old hand-written equivalent got them
        // wrong. The classic bug:
        //
        //     Resource r = null;
        //     try {
        //         r = open();
        //         use(r);
        //     } finally {
        //         if (r != null) r.close();   // if this throws, it REPLACES
        //     }                               // the real exception
        //
        // A close() failure would hide the actual problem, and the real cause
        // was gone. try-with-resources keeps both.

        System.out.println();
        System.out.println("--- close still runs when the body returns early ---");
        System.out.println("  returned: " + earlyReturn());
    }

    static String earlyReturn() {
        try (Resource r = new Resource("C")) {
            return "value computed with " + r.name();
        }
        // close() runs after the return value is computed and before the
        // method actually returns. You do not need a finally block for this.
    }
}

// The interface is AutoCloseable. Implementing it is the entire requirement;
// there is no registration or annotation.
class Resource implements AutoCloseable {

    private final String name;

    Resource(String name) {
        this.name = name;
        System.out.println("  open " + name);
    }

    String name() {
        return name;
    }

    @Override
    public void close() {
        System.out.println("  close " + name);
    }
}

class FailingResource implements AutoCloseable {

    private final String name;

    FailingResource(String name) {
        this.name = name;
        System.out.println("  open " + name);
    }

    @Override
    public void close() {
        System.out.println("  close " + name + " (and failing)");
        throw new IllegalStateException("close " + name + " failed");
    }
}
