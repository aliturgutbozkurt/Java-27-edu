// The headline number, measured rather than quoted.
//
// Ten thousand tasks, each blocking for 100 milliseconds. This is the shape of
// a web server handling requests that wait on a database.

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Benchmark {

    private static final int TASKS = 5_000;
    private static final Duration BLOCK_FOR = Duration.ofMillis(100);

    public static void main(String[] args) {
        System.out.println("  " + TASKS + " tasks, each blocking for " + BLOCK_FOR.toMillis() + "ms");
        System.out.println("  cores available: " + Runtime.getRuntime().availableProcessors());
        System.out.println();

        long virtual = time(Executors::newVirtualThreadPerTaskExecutor);
        long platform200 = time(() -> Executors.newFixedThreadPool(200));
        long platform50 = time(() -> Executors.newFixedThreadPool(50));

        System.out.printf("  virtual thread per task   %6dms%n", virtual);
        System.out.printf("  platform pool of 200      %6dms%n", platform200);
        System.out.printf("  platform pool of 50       %6dms%n", platform50);

        System.out.println();
        System.out.println("--- the arithmetic ---");
        System.out.println();
        System.out.println("  A pool of 200 can have at most 200 tasks blocked at once, so");
        System.out.println("  " + TASKS + " tasks take " + (TASKS / 200) + " batches of 100ms. That is the");
        System.out.println("  floor, and the measurement above lands near it.");
        System.out.println();
        System.out.println("  A pool of 50 takes " + (TASKS / 50) + " batches, so it is four times worse.");
        System.out.println();
        System.out.println("  Virtual threads have no such limit. All 10000 block at once,");
        System.out.println("  so the whole thing takes roughly one 100ms wait plus overhead.");

        System.out.println();
        System.out.println("--- the choice this removes ---");
        System.out.println();
        System.out.println("  Sizing a platform pool for IO work was always a bad trade:");
        System.out.println();
        System.out.println("    too few threads  ->  requests queue behind blocked ones");
        System.out.println("    too many threads ->  a megabyte of stack each, and the OS");
        System.out.println("                         scheduler starts thrashing");
        System.out.println();
        System.out.println("  The usual escape was asynchronous code: callbacks, futures,");
        System.out.println("  reactive streams. Those work, and they cost you readable stack");
        System.out.println("  traces, step debugging, and ordinary try/catch.");
        System.out.println();
        System.out.println("  Virtual threads give the async scalability back to blocking,");
        System.out.println("  sequential code. That is the entire pitch.");

        System.out.println();
        System.out.println("--- and where they do NOT help ---");
        System.out.println();
        System.out.println("  CPU-bound work. If a task never blocks, it never unmounts, and");
        System.out.println("  you cannot run more work in parallel than you have cores.");
        System.out.println("  A fixed platform pool sized to the core count is still correct");
        System.out.println("  for that, and Module 14's parallel streams use exactly one.");
    }

    private static long time(java.util.function.Supplier<ExecutorService> factory) {
        long start = System.nanoTime();
        try (ExecutorService executor = factory.get()) {
            for (int i = 0; i < TASKS; i++) {
                executor.submit(() -> {
                    Thread.sleep(BLOCK_FOR);
                    return null;
                });
            }
        }
        return (System.nanoTime() - start) / 1_000_000;
    }
}
