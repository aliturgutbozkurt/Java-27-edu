// Three fixes for the same race, with the trade-offs made explicit.

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;

public class FixingTheRace {

    private static final int THREADS = 4;
    private static final int INCREMENTS = 200_000;
    private static final int EXPECTED = THREADS * INCREMENTS;

    static int unsafe = 0;
    static int synced = 0;
    static final Object LOCK = new Object();
    static final AtomicInteger atomic = new AtomicInteger();
    static final LongAdder adder = new LongAdder();

    public static void main(String[] args) throws InterruptedException {
        long start = System.nanoTime();
        runAll();
        long elapsedMs = (System.nanoTime() - start) / 1_000_000;

        System.out.println("  expected:      " + EXPECTED);
        System.out.println();
        System.out.printf("  unsafe ++      %8d   %s%n", unsafe,
                unsafe == EXPECTED ? "correct this run, by luck" : "WRONG");
        System.out.printf("  synchronized   %8d   %s%n", synced,
                synced == EXPECTED ? "correct" : "WRONG");
        System.out.printf("  AtomicInteger  %8d   %s%n", atomic.get(),
                atomic.get() == EXPECTED ? "correct" : "WRONG");
        System.out.printf("  LongAdder      %8d   %s%n", adder.sum(),
                adder.sum() == EXPECTED ? "correct" : "WRONG");
        System.out.println();
        System.out.println("  (all four ran in " + elapsedMs + "ms together)");

        System.out.println();
        System.out.println("--- 1. synchronized ---");
        System.out.println("  synchronized (LOCK) { synced++; }");
        System.out.println();
        System.out.println("  Only one thread may hold a given object's monitor at a time,");
        System.out.println("  so the read-modify-write cannot be interleaved.");
        System.out.println();
        System.out.println("  It also creates a HAPPENS-BEFORE edge: everything one thread");
        System.out.println("  did before releasing the lock is visible to the next thread");
        System.out.println("  that acquires it. That second guarantee is the one people");
        System.out.println("  forget, and it is why synchronized fixes visibility too.");
        System.out.println();
        System.out.println("  Cost: threads that cannot get the lock block. Under heavy");
        System.out.println("  contention they spend their time queueing.");

        System.out.println();
        System.out.println("--- 2. atomics ---");
        System.out.println("  atomic.incrementAndGet();");
        System.out.println();
        System.out.println("  Uses a compare-and-swap instruction the CPU provides: read the");
        System.out.println("  value, compute the new one, and write it back ONLY if nothing");
        System.out.println("  changed in between. If something did, retry.");
        System.out.println();
        System.out.println("  No blocking, so no thread is ever parked. Under contention the");
        System.out.println("  retries themselves become the cost.");

        System.out.println();
        System.out.println("--- 3. LongAdder, when contention is the problem ---");
        System.out.println("  adder.increment();");
        System.out.println();
        System.out.println("  Keeps several internal cells and sums them on demand, so");
        System.out.println("  threads mostly do not touch the same memory at all. Faster");
        System.out.println("  than AtomicLong under heavy write contention, and slower to");
        System.out.println("  read, because sum() has to add the cells up.");
        System.out.println();
        System.out.println("  Use it for counters and metrics. Use AtomicLong when you need");
        System.out.println("  the current value after every update.");

        System.out.println();
        System.out.println("--- and the fourth option, which is usually best ---");
        System.out.println("  Do not share the state at all.");
        System.out.println();
        System.out.println("  Module 14's parallel stream section made the same point: the");
        System.out.println("  fix for a shared mutable collection was not a lock, it was");
        System.out.println("  collect(), which gives each thread its own container.");
        System.out.println();
        System.out.println("  A race you cannot have is cheaper than one you synchronise.");

        System.out.println();
        System.out.println("--- what NOT to synchronise on ---");
        System.out.println("  synchronized (this)        anyone holding a reference can lock you out");
        System.out.println("  synchronized (SomeClass)   the same, globally");
        System.out.println("  synchronized (someString)  literals are pooled, so unrelated code");
        System.out.println("                             holding the same text shares your lock");
        System.out.println("  synchronized (Integer)     boxed values below 128 are cached");
        System.out.println();
        System.out.println("  Use a private final Object, as LOCK above is.");
    }

    static void runAll() throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(THREADS);
        for (int t = 0; t < THREADS; t++) {
            pool.submit(() -> {
                for (int i = 0; i < INCREMENTS; i++) {
                    unsafe++;
                    synchronized (LOCK) {
                        synced++;
                    }
                    atomic.incrementAndGet();
                    adder.increment();
                }
            });
        }
        pool.shutdown();
        pool.awaitTermination(60, TimeUnit.SECONDS);
    }
}
