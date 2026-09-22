// count++ is not one operation. This file proves it.

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TheRaceCondition {

    static int unsafeCounter = 0;

    private static final int THREADS = 4;
    private static final int INCREMENTS = 200_000;
    private static final int EXPECTED = THREADS * INCREMENTS;

    public static void main(String[] args) throws InterruptedException {
        System.out.println("--- " + THREADS + " threads, " + INCREMENTS + " increments each ---");
        System.out.println("  expected total: " + EXPECTED);

        ExecutorService pool = Executors.newFixedThreadPool(THREADS);
        for (int t = 0; t < THREADS; t++) {
            pool.submit(() -> {
                for (int i = 0; i < INCREMENTS; i++) {
                    unsafeCounter++;
                }
            });
        }
        pool.shutdown();
        boolean finished = pool.awaitTermination(30, TimeUnit.SECONDS);

        System.out.println("  actual total:   " + unsafeCounter);
        System.out.println("  all tasks completed: " + finished);

        int lost = EXPECTED - unsafeCounter;
        if (lost > 0) {
            System.out.println("  -> " + lost + " increments were LOST");
        } else {
            System.out.println("  -> this run happened to survive. It will not always.");
        }

        // The guard above matters. On a single-core container or under a
        // different scheduler this can occasionally come out correct, and an
        // example that fails its own verification on those machines would be
        // teaching the wrong lesson about reliability.

        System.out.println();
        System.out.println("--- why count++ loses increments ---");
        System.out.println("  it is THREE operations, not one:");
        System.out.println("    1. read the current value from memory");
        System.out.println("    2. add one to it");
        System.out.println("    3. write the result back");
        System.out.println();
        System.out.println("  two threads can both perform step 1 before either performs");
        System.out.println("  step 3. Both read 100, both write 101, and one increment");
        System.out.println("  has vanished. Nothing in the language prevents that.");

        System.out.println();
        System.out.println("--- the same shape, everywhere ---");
        System.out.println("  x += 1, x = x + 1, list.add(item), map.put(k, v)");
        System.out.println("  balance -= amount, and every check-then-act pair:");
        System.out.println();
        System.out.println("    if (!map.containsKey(k)) { map.put(k, v); }");
        System.out.println();
        System.out.println("  Two threads can both pass the check before either puts.");
        System.out.println("  That is the same race with a different spelling, and it is");
        System.out.println("  why Map has putIfAbsent as a single atomic operation.");

        System.out.println();
        System.out.println("FixingTheRace.java shows the three ways to fix it.");
    }
}
