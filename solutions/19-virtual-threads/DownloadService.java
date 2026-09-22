// Reference solution for Homework 19.

import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class DownloadService {

    private static final ScopedValue<String> REQUEST_ID = ScopedValue.newInstance();

    private static final int TASKS = 2_000;
    private static final Duration LATENCY = Duration.ofMillis(100);

    public static void main(String[] args) throws Exception {
        partOne();
        System.out.println();
        partTwo();
        System.out.println();
        partThree();
        System.out.println();
        partFour();
    }

    // ---------------------------------------------------------------- part 1

    private static void partOne() {
        System.out.println("--- measuring the difference ---");
        System.out.println("  " + TASKS + " downloads, each waiting " + LATENCY.toMillis() + "ms");
        System.out.println("  cores: " + Runtime.getRuntime().availableProcessors());
        System.out.println();

        long virtual = measure(Executors::newVirtualThreadPerTaskExecutor);
        long fixed100 = measure(() -> Executors.newFixedThreadPool(100));
        long fixed25 = measure(() -> Executors.newFixedThreadPool(25));

        System.out.printf("  virtual per task   %6dms%n", virtual);
        System.out.printf("  platform pool 100  %6dms%n", fixed100);
        System.out.printf("  platform pool 25   %6dms%n", fixed25);

        System.out.println();
        System.out.println("  predicted floors, from batches x latency:");
        System.out.printf("    pool of 100: %d batches x %dms = %dms%n",
                TASKS / 100, LATENCY.toMillis(), TASKS / 100 * LATENCY.toMillis());
        System.out.printf("    pool of 25:  %d batches x %dms = %dms%n",
                TASKS / 25, LATENCY.toMillis(), TASKS / 25 * LATENCY.toMillis());

        // WHY THE PREDICTION WORKS, in my own words:
        //
        // A fixed pool of N can have at most N tasks blocked at any moment.
        // Every task here spends essentially all its time blocked, so the pool
        // processes them in batches of N, and each batch costs one full
        // latency. Total time is therefore (tasks / N) x latency, and the
        // measurements land within a few percent of that.
        //
        // The virtual version has no N. Every task blocks at once, so the whole
        // run costs roughly one latency plus the overhead of creating two
        // thousand cheap objects.
        //
        // The point is not that virtual threads are fast. It is that the
        // platform numbers are a function of a number I had to guess, and
        // guessing it wrong in either direction is expensive.
    }

    private static long measure(java.util.function.Supplier<ExecutorService> factory) {
        long start = System.nanoTime();
        try (ExecutorService executor = factory.get()) {
            for (int i = 0; i < TASKS; i++) {
                executor.submit(() -> {
                    Thread.sleep(LATENCY);
                    return null;
                });
            }
        }
        return (System.nanoTime() - start) / 1_000_000;
    }

    // ---------------------------------------------------------------- part 2

    private static void partTwo() throws Exception {
        System.out.println("--- the rate limit I accidentally removed ---");

        // The downstream service tolerates 5 concurrent calls. With a platform
        // pool of 5, that limit was enforced by accident: there was simply no
        // way to have a sixth call in flight.
        var tracker = new ConcurrencyTracker();

        System.out.println("  with a platform pool of 5:");
        try (var pool = Executors.newFixedThreadPool(5)) {
            for (int i = 0; i < 40; i++) {
                pool.submit(() -> call(tracker));
            }
        }
        System.out.println("    peak concurrent calls: " + tracker.peak());

        // Swap in virtual threads and the limit vanishes silently. Nothing
        // fails, nothing warns, and the downstream service gets forty at once.
        var unbounded = new ConcurrencyTracker();
        System.out.println("  after switching to virtual threads, no other change:");
        try (var pool = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 40; i++) {
                pool.submit(() -> call(unbounded));
            }
        }
        System.out.println("    peak concurrent calls: " + unbounded.peak());
        System.out.println("    the limit was never written down, so it was never kept");

        // THE FIX: state the constraint instead of implying it.
        var limited = new ConcurrencyTracker();
        Semaphore permits = new Semaphore(5);

        System.out.println("  with virtual threads and an explicit Semaphore(5):");
        try (var pool = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 40; i++) {
                pool.submit(() -> {
                    permits.acquire();
                    try {
                        return call(limited);
                    } finally {
                        permits.release();
                    }
                });
            }
        }
        System.out.println("    peak concurrent calls: " + limited.peak());

        // WHY THE SEMAPHORE IS BETTER THAN THE POOL, in my own words:
        //
        // The pool of 5 conflated two decisions: how many threads to allocate,
        // and how hard to push the downstream service. Those have nothing to do
        // with each other, and the second was invisible in the code.
        //
        // The Semaphore says exactly one thing, at the place it applies, with a
        // number a reader can find and change. Threads are now free and the
        // limit is deliberate.
        //
        // The release() sits in a finally block, because a permit leaked on an
        // exception would shrink the limit permanently until the process
        // restarts, which is a slow and confusing failure.
    }

    private static Void call(ConcurrencyTracker tracker) throws InterruptedException {
        tracker.enter();
        try {
            Thread.sleep(Duration.ofMillis(30));
        } finally {
            tracker.exit();
        }
        return null;
    }

    // ---------------------------------------------------------------- part 3

    private static void partThree() throws Exception {
        System.out.println("--- scoped values down the call stack ---");

        ScopedValue.where(REQUEST_ID, "req-001").run(() -> {
            System.out.println("  top level:  " + REQUEST_ID.get());
            fetchProfile();
        });

        System.out.println("  outside the scope, bound? " + REQUEST_ID.isBound());

        // The binding is gone without any cleanup call, and it would be gone
        // just the same if fetchProfile had thrown. A ThreadLocal would still
        // be set, on a thread that is about to be reused for someone else's
        // request, which is how request ids end up in the wrong log lines.

        System.out.println();
        System.out.println("  a child thread started inside the scope:");
        ScopedValue.where(REQUEST_ID, "req-002").run(() -> {
            Thread child = Thread.ofVirtual().unstarted(() ->
                    System.out.println("    child sees it bound? " + REQUEST_ID.isBound()));
            child.start();
            try {
                child.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        // Not inherited, deliberately. The parent scope could end while the
        // child is still running, which would leave the child reading a value
        // whose lifetime had expired. Structured concurrency makes the parent
        // outlive its children and is what enables inheritance; it is still
        // preview here.
    }

    private static void fetchProfile() {
        System.out.println("  one frame deep:   " + REQUEST_ID.get());
        loadPreferences();
    }

    private static void loadPreferences() {
        // Nothing was passed down, and nothing had to be.
        System.out.println("  two frames deep:  " + REQUEST_ID.get());
    }

    // ---------------------------------------------------------------- part 4

    private static void partFour() throws Exception {
        System.out.println("--- what virtual threads did NOT fix ---");

        var counter = new PlainCounter();

        try (var pool = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int t = 0; t < 4; t++) {
                pool.submit(() -> {
                    for (int i = 0; i < 100_000; i++) {
                        counter.increment();
                    }
                    return null;
                });
            }
        }

        int expected = 4 * 100_000;
        System.out.println("  expected: " + expected);
        System.out.println("  actual:   " + counter.value());
        System.out.println("  " + (counter.value() == expected
                ? "-> survived this run, by luck"
                : "-> LOST " + (expected - counter.value()) + " increments"));

        // The race is identical to Module 18's. Virtual threads changed what a
        // thread COSTS, not what happens when two of them read the same field
        // before either writes it.
        //
        // If anything the risk is higher, because it is now trivial to have ten
        // thousand of them running at once, and code that was accidentally safe
        // with a pool of four is not safe with ten thousand.
    }

    // ---------------------------------------------------------------- types

    static final class ConcurrencyTracker {
        private final AtomicInteger current = new AtomicInteger();
        private final AtomicInteger peak = new AtomicInteger();

        void enter() {
            int now = current.incrementAndGet();
            // updateAndGet retries until it wins, so the peak cannot be lost to
            // a race between two threads both raising it.
            peak.updateAndGet(previous -> Math.max(previous, now));
        }

        void exit() {
            current.decrementAndGet();
        }

        int peak() {
            return peak.get();
        }
    }

    static final class PlainCounter {
        private int count = 0;

        void increment() {
            count++;
        }

        int value() {
            return count;
        }
    }
}
