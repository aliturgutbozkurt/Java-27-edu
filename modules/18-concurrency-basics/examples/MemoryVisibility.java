// A race is not the only way threads go wrong. This one has no interleaving at
// all: one thread writes, another never sees it.

public class MemoryVisibility {

    static boolean plainFlag = false;
    static volatile boolean volatileFlag = false;

    public static void main(String[] args) throws InterruptedException {
        System.out.println("--- a plain boolean ---");

        Thread plainReader = new Thread(() -> {
            long spins = 0;
            while (!plainFlag) {
                spins++;
            }
            System.out.println("  plain reader exited after " + spins + " spins");
        });

        // DAEMON, because this thread will very likely never finish, and a
        // non-daemon thread would keep the JVM alive forever.
        plainReader.setDaemon(true);
        plainReader.start();

        Thread.sleep(200);
        plainFlag = true;
        System.out.println("  main set plainFlag = true");

        plainReader.join(1500);
        System.out.println("  after waiting 1.5s, reader still running: " + plainReader.isAlive());

        System.out.println();
        System.out.println("--- the same code with volatile ---");

        Thread volatileReader = new Thread(() -> {
            while (!volatileFlag) {
                // spin
            }
            System.out.println("  volatile reader exited");
        });
        volatileReader.setDaemon(true);
        volatileReader.start();

        Thread.sleep(200);
        volatileFlag = true;
        System.out.println("  main set volatileFlag = true");

        volatileReader.join(1500);
        System.out.println("  after waiting 1.5s, reader still running: " + volatileReader.isAlive());

        System.out.println();
        System.out.println("--- what happened ---");
        System.out.println("  The plain reader almost certainly never saw the write.");
        System.out.println();
        System.out.println("  Nothing in that loop touches anything shared, so the JIT is");
        System.out.println("  entitled to assume plainFlag cannot change and hoist the read");
        System.out.println("  out of the loop entirely, turning it into while (true).");
        System.out.println();
        System.out.println("  That is a LEGAL optimisation. The Java Memory Model only");
        System.out.println("  promises one thread sees another's write when the two are");
        System.out.println("  connected by a happens-before relationship, and a plain field");
        System.out.println("  creates none.");

        System.out.println();
        System.out.println("--- what volatile does ---");
        System.out.println("  1. every read goes to main memory; no caching, no hoisting");
        System.out.println("  2. it establishes happens-before: everything written before a");
        System.out.println("     volatile write is visible after the matching volatile read");
        System.out.println();
        System.out.println("--- what volatile does NOT do ---");
        System.out.println("  it does not make count++ atomic. Three operations are still");
        System.out.println("  three operations; volatile only guarantees each one sees the");
        System.out.println("  latest value. For atomicity use an atomic type or a lock.");
        System.out.println();
        System.out.println("  volatile is for FLAGS and for publishing a reference.");
        System.out.println("  synchronized, atomics and locks are for COMPOUND actions.");

        System.out.println();
        System.out.println("--- a note on this example's reliability ---");
        System.out.println("  Whether the plain reader hangs depends on the JIT, and so on");
        System.out.println("  the machine and the run. It hangs consistently in practice,");
        System.out.println("  which is the point: an optimisation that is legal will be");
        System.out.println("  taken, and code that depends on it not being taken is broken");
        System.out.println("  whether or not it has failed yet.");
    }
}
