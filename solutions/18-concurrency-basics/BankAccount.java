// Reference solution for Homework 18.

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

public class BankAccount {

    private static final int THREADS = 4;
    private static final int OPS = 100_000;
    private static final long EXPECTED = (long) THREADS * OPS;

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

    private static void partOne() throws Exception {
        System.out.println("--- four counters, same workload ---");

        var unsafe = new UnsafeCounter();
        var synced = new SynchronizedCounter();
        var atomic = new AtomicCounter();
        var adder = new AdderCounter();

        List<Counter> counters = List.of(unsafe, synced, atomic, adder);

        for (Counter counter : counters) {
            long ms = hammer(counter);
            long value = counter.value();
            System.out.printf("  %-16s %8d  %-24s %4dms%n",
                    counter.name(), value,
                    value == EXPECTED ? "correct" : "LOST " + (EXPECTED - value),
                    ms);
        }

        System.out.println();
        System.out.println("  expected: " + EXPECTED);

        // WHY THE UNSAFE ONE LOSES, in my own words:
        //
        // count++ compiles to three separate steps: read the field, add one,
        // write it back. Nothing makes those three indivisible.
        //
        // Two threads can both execute the read before either executes the
        // write. Both see 100, both compute 101, both store 101. Two increments
        // happened and the counter moved by one. At four threads and a hundred
        // thousand iterations each, that collision happens constantly.
        //
        // The number lost differs every run, which is worse than a fixed wrong
        // answer: it means a test can pass and the bug still be there.
    }

    // ---------------------------------------------------------------- part 2

    private static void partTwo() throws InterruptedException {
        System.out.println("--- the visibility problem ---");

        var shared = new SharedFlag();

        Thread reader = new Thread(() -> {
            long spins = 0;
            while (!shared.plainStop) {
                spins++;
            }
            System.out.println("  plain reader exited after " + spins + " spins");
        });
        // Daemon, because this thread very probably never finishes and a
        // non-daemon one would keep the JVM running forever.
        reader.setDaemon(true);
        reader.start();

        Thread.sleep(200);
        shared.plainStop = true;
        reader.join(1000);
        System.out.println("  plain flag:    reader still alive? " + reader.isAlive());

        Thread volatileReader = new Thread(() -> {
            while (!shared.volatileStop) {
                // spin
            }
        });
        volatileReader.setDaemon(true);
        volatileReader.start();

        Thread.sleep(200);
        shared.volatileStop = true;
        volatileReader.join(1000);
        System.out.println("  volatile flag: reader still alive? " + volatileReader.isAlive());

        // WHY THE PLAIN ONE HANGS, in my own words:
        //
        // This is NOT a race. There is no interleaving; one thread writes once
        // and the other only reads.
        //
        // The loop body touches nothing shared, so the compiler is allowed to
        // conclude that plainStop cannot change during the loop and read it
        // once, before the loop, hoisting it into a constant. The loop becomes
        // while (true).
        //
        // That optimisation is legal because the Java Memory Model only
        // promises visibility between threads that are connected by a
        // happens-before edge, and reading and writing a plain field creates
        // none. The compiler is not being clever at my expense; it is doing
        // exactly what the spec permits.
        //
        // volatile creates the edge, which forbids both the caching and the
        // hoisting.
        //
        // WHY volatile IS NOT ENOUGH FOR THE COUNTER:
        //
        // volatile guarantees each individual read sees the latest write. It
        // does not make a read-modify-write sequence indivisible, so count++ on
        // a volatile field still loses increments. Visibility and atomicity are
        // separate problems and volatile solves only one of them.
    }

    // ---------------------------------------------------------------- part 3

    private static void partThree() throws Exception {
        System.out.println("--- check-then-act ---");

        // THE BROKEN VERSION. containsKey and put are each atomic; the PAIR is
        // not. Two threads can both find the key absent before either inserts.
        Map<String, Integer> broken = new ConcurrentHashMap<>();
        var brokenWinners = ConcurrentHashMap.<Integer>newKeySet();

        runConcurrently(8, id -> {
            if (!broken.containsKey("winner")) {
                broken.put("winner", id);
                brokenWinners.add(id);
            }
        });

        System.out.println("  broken:  threads that believed they won: " + brokenWinners.size());
        System.out.println("           (more than 1 means the race fired)");

        // THE FIXED VERSION. putIfAbsent performs the check and the insert as
        // one atomic operation, so exactly one caller can ever see null.
        Map<String, Integer> fixed = new ConcurrentHashMap<>();
        var fixedWinners = ConcurrentHashMap.<Integer>newKeySet();

        runConcurrently(8, id -> {
            if (fixed.putIfAbsent("winner", id) == null) {
                fixedWinners.add(id);
            }
        });

        System.out.println("  fixed:   threads that believed they won: " + fixedWinners.size());

        // Note that using a ConcurrentHashMap did NOT fix the broken version.
        // A thread-safe collection guarantees each individual operation is
        // atomic; it cannot guarantee anything about a sequence of them. That
        // is the most common misunderstanding about concurrent collections, and
        // it is why compute, merge and putIfAbsent exist.
    }

    // ---------------------------------------------------------------- part 4

    private static void partFour() throws Exception {
        System.out.println("--- silent failure in a pool ---");

        try (ExecutorService pool = Executors.newFixedThreadPool(2)) {
            // Submitted and ignored. The exception is captured in the Future,
            // and because nothing ever calls get(), nobody hears about it.
            pool.submit(() -> {
                throw new IllegalStateException("this failure vanishes");
            });
            Thread.sleep(100);
            System.out.println("  a task threw. nothing was printed. no stack trace.");
        }

        try (ExecutorService pool = Executors.newFixedThreadPool(2)) {
            Future<?> future = pool.submit(() -> {
                throw new IllegalStateException("this one is inspected");
            });
            try {
                future.get();
            } catch (ExecutionException e) {
                // The original exception is the cause, which is why Module 10
                // insisted on always passing one.
                System.out.println("  inspecting the Future surfaced it: " + e.getCause().getMessage());
            }
        }

        System.out.println();
        System.out.println("  the three ways to not lose a failure:");
        System.out.println("    1. always call get() on the Future");
        System.out.println("    2. use execute() instead of submit(), so the default");
        System.out.println("       uncaught-exception handler reports it");
        System.out.println("    3. catch inside the task and log there");
    }

    // ---------------------------------------------------------------- harness

    private static long hammer(Counter counter) throws Exception {
        long start = System.nanoTime();
        try (ExecutorService pool = Executors.newFixedThreadPool(THREADS)) {
            for (int t = 0; t < THREADS; t++) {
                pool.submit(() -> {
                    for (int i = 0; i < OPS; i++) {
                        counter.increment();
                    }
                });
            }
        }
        return (System.nanoTime() - start) / 1_000_000;
    }

    private static void runConcurrently(int threads, java.util.function.IntConsumer body)
            throws Exception {
        var ready = new CountDownLatch(threads);
        var go = new CountDownLatch(1);

        try (ExecutorService pool = Executors.newFixedThreadPool(threads)) {
            for (int t = 0; t < threads; t++) {
                int id = t;
                pool.submit(() -> {
                    // Both latches exist to maximise the chance of a genuine
                    // collision: every thread arrives and waits, then all are
                    // released at once. Without this the first thread usually
                    // finishes before the last one starts and no race occurs.
                    ready.countDown();
                    try {
                        go.await();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                    body.accept(id);
                });
            }
            ready.await();
            go.countDown();
        }
    }

    // ---------------------------------------------------------------- types

    interface Counter {
        void increment();

        long value();

        String name();
    }

    static final class UnsafeCounter implements Counter {
        private long count = 0;

        public void increment() {
            count++;
        }

        public long value() {
            return count;
        }

        public String name() {
            return "plain ++";
        }
    }

    static final class SynchronizedCounter implements Counter {
        // A private final lock object. Not `this`, because anyone holding a
        // reference to this counter could then block it, and not a String or a
        // boxed Integer, because both are shared far more widely than expected.
        private final Object lock = new Object();
        private long count = 0;

        public void increment() {
            synchronized (lock) {
                count++;
            }
        }

        public long value() {
            synchronized (lock) {
                return count;
            }
        }

        public String name() {
            return "synchronized";
        }
    }

    static final class AtomicCounter implements Counter {
        private final AtomicLong count = new AtomicLong();

        public void increment() {
            count.incrementAndGet();
        }

        public long value() {
            return count.get();
        }

        public String name() {
            return "AtomicLong";
        }
    }

    static final class AdderCounter implements Counter {
        private final LongAdder count = new LongAdder();

        public void increment() {
            count.increment();
        }

        public long value() {
            return count.sum();
        }

        public String name() {
            return "LongAdder";
        }
    }

    static final class SharedFlag {
        boolean plainStop = false;
        volatile boolean volatileStop = false;
    }
}
