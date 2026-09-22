// A virtual thread is a thread the JVM schedules, not the operating system.
//
// It implements the same java.lang.Thread API, runs the same code, and is
// debugged with the same tools. What changed is the cost: a platform thread
// reserves about a megabyte of stack and an OS-level scheduling slot, while a
// virtual thread is a heap object that grows as it needs to.
//
// Finalised in JDK 21, so this is stable and flag-free.

import java.time.Duration;
import java.util.concurrent.Executors;

public class VirtualThreads {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("--- creating one ---");

        Thread virtual = Thread.ofVirtual().name("worker").start(() ->
                System.out.println("  running on: " + Thread.currentThread()));
        virtual.join();

        System.out.println("  isVirtual: " + virtual.isVirtual());

        // Look at the toString above. It names a ForkJoinPool worker, which is
        // the CARRIER: a real platform thread that the virtual thread is
        // currently mounted on. When the virtual thread blocks, it UNMOUNTS and
        // the carrier picks up another one.
        //
        // That is the whole mechanism. Blocking stopped costing a thread.

        System.out.println();
        System.out.println("--- a platform thread, for comparison ---");

        Thread platform = Thread.ofPlatform().name("classic").start(() ->
                System.out.println("  running on: " + Thread.currentThread()));
        platform.join();
        System.out.println("  isVirtual: " + platform.isVirtual());

        System.out.println();
        System.out.println("--- the executor you will actually use ---");

        // One virtual thread PER TASK. Not a pool. There is nothing to size and
        // nothing to tune, because the threads are not the scarce resource.
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 1; i <= 3; i++) {
                int id = i;
                executor.submit(() -> {
                    Thread.sleep(Duration.ofMillis(50));
                    System.out.println("  task " + id + " finished on " + Thread.currentThread());
                    return null;
                });
            }
        }

        System.out.println();
        System.out.println("--- how many can you have ---");

        int count = 100_000;
        long start = System.nanoTime();
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < count; i++) {
                executor.submit(() -> {
                    Thread.sleep(Duration.ofMillis(10));
                    return null;
                });
            }
        }
        long ms = (System.nanoTime() - start) / 1_000_000;
        System.out.println("  " + count + " virtual threads, each sleeping 10ms: " + ms + "ms");
        System.out.println("  try that with 100000 platform threads and the JVM will not start");

        System.out.println();
        System.out.println("--- what has NOT changed ---");
        System.out.println("  every hazard from Module 18 still applies, unchanged:");
        System.out.println("    count++ still races");
        System.out.println("    a plain field still has visibility problems");
        System.out.println("    shared mutable state still needs synchronising");
        System.out.println();
        System.out.println("  virtual threads make blocking cheap. They do not make");
        System.out.println("  concurrency easy. Those are different claims.");
    }
}
