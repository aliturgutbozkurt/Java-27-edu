// Four ways people get virtual threads wrong, three of which come from applying
// platform-thread habits unchanged.

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class CommonMisuses {

    public static void main(String[] args) throws Exception {
        System.out.println("--- 1. pooling them ---");
        System.out.println();
        System.out.println("  Executors.newFixedThreadPool(200, Thread.ofVirtual().factory())");
        System.out.println();
        System.out.println("  This caps you at 200 concurrent tasks and gains nothing. Pools");
        System.out.println("  exist to reuse an expensive resource. Virtual threads are not");
        System.out.println("  expensive, so there is nothing to reuse.");
        System.out.println();
        System.out.println("  Use newVirtualThreadPerTaskExecutor. One thread per task.");

        System.out.println();
        System.out.println("--- 2. thinking a pool was your rate limit ---");

        // A platform pool did two jobs: it provided threads AND it bounded
        // concurrency. Virtual threads only replace the first. If you were
        // relying on a pool of 10 to avoid overwhelming a database, removing
        // the pool removes the limit, and you will find out at 3am.
        //
        // Bound it explicitly instead. A Semaphore says what you actually mean.
        Semaphore limit = new Semaphore(4);

        long start = System.nanoTime();
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 16; i++) {
                executor.submit(() -> {
                    limit.acquire();
                    try {
                        Thread.sleep(Duration.ofMillis(50));
                    } finally {
                        limit.release();
                    }
                    return null;
                });
            }
        }
        long ms = (System.nanoTime() - start) / 1_000_000;
        System.out.println("  16 tasks, 50ms each, at most 4 at once: " + ms + "ms");
        System.out.println("  (four batches of 50ms, as intended, but now the limit is");
        System.out.println("   a stated constraint rather than a side effect of pool size)");

        System.out.println();
        System.out.println("--- 3. using them for CPU-bound work ---");
        System.out.println();
        System.out.println("  A task that never blocks never unmounts. Ten thousand virtual");
        System.out.println("  threads doing arithmetic will not beat " + Runtime.getRuntime().availableProcessors()
                + " cores; they will");
        System.out.println("  just add scheduling overhead.");
        System.out.println();
        System.out.println("  Fixed platform pool sized to the cores, as before.");

        System.out.println();
        System.out.println("--- 4. ThreadLocal at scale ---");
        System.out.println();
        System.out.println("  A ThreadLocal holds one entry per thread. That was fine for a");
        System.out.println("  pool of 200 and is not fine for a million threads, especially");
        System.out.println("  when the value is a buffer sized for reuse.");
        System.out.println();
        System.out.println("  ScopedValues.java shows the replacement.");

        System.out.println();
        System.out.println("--- the one-line summary ---");
        System.out.println();
        System.out.println("  Virtual threads make BLOCKING cheap.");
        System.out.println("  They do not make SHARING safe, and they are not a CPU multiplier.");
        System.out.println();
        System.out.println("  Everything in Module 18 about races, visibility and locks still");
        System.out.println("  applies, word for word.");
    }
}
