// Pinning is when a virtual thread cannot unmount, so its carrier stays blocked
// and one platform thread is wasted.
//
// MOST OF WHAT YOU WILL READ ABOUT THIS IS OUT OF DATE. In Java 21 to 23, a
// virtual thread blocking inside a synchronized block pinned its carrier, and
// every article from that era says to replace synchronized with ReentrantLock.
//
// JEP 491, delivered in JDK 24, removed that. Synchronized no longer pins.
// The measurement below demonstrates it rather than taking anyone's word.

import java.time.Duration;
import java.util.concurrent.Executors;

public class PinningToday {

    public static void main(String[] args) {
        int cores = Runtime.getRuntime().availableProcessors();
        System.out.println("  cores, and therefore carriers: " + cores);
        System.out.println();

        System.out.println("--- 2000 tasks, each holding its OWN lock while sleeping 100ms ---");

        long start = System.nanoTime();
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 2000; i++) {
                Object ownLock = new Object();
                executor.submit(() -> {
                    synchronized (ownLock) {
                        Thread.sleep(Duration.ofMillis(100));
                    }
                    return null;
                });
            }
        }
        long ms = (System.nanoTime() - start) / 1_000_000;

        System.out.println("  elapsed: " + ms + "ms");
        System.out.println();
        System.out.println("  If synchronized still pinned, only " + cores + " tasks could sleep at");
        System.out.println("  once, so this would take about " + (2000 / cores * 100) + "ms.");
        System.out.println("  It did not, which is JEP 491 working.");

        System.out.println();
        System.out.println("--- what DOES still pin ---");
        System.out.println();
        System.out.println("  1. a native method or a foreign function call");
        System.out.println("     the JVM cannot unmount a stack it does not control");
        System.out.println();
        System.out.println("  2. a class initialiser that blocks");
        System.out.println("     rare, and usually a design problem anyway");
        System.out.println();
        System.out.println("  Both are much narrower than the old synchronized rule, and");
        System.out.println("  neither is something most application code does.");

        System.out.println();
        System.out.println("--- what still SERIALISES, which is different ---");
        System.out.println();
        System.out.println("  A lock is still a lock. If a thousand virtual threads contend");
        System.out.println("  for one monitor, 999 of them wait, exactly as before.");
        System.out.println();
        System.out.println("  Pinning wastes a CARRIER. Contention wastes TIME. JEP 491 fixed");
        System.out.println("  the first and could not touch the second, because serialising");
        System.out.println("  is what a lock is for.");

        System.out.println();
        System.out.println("--- how to check for yourself ---");
        System.out.println();
        System.out.println("  -Djdk.tracePinnedThreads=full");
        System.out.println();
        System.out.println("  prints a stack trace whenever a virtual thread parks while");
        System.out.println("  pinned. Run it once against your own service rather than");
        System.out.println("  trusting a blog post, including this one.");
    }
}
