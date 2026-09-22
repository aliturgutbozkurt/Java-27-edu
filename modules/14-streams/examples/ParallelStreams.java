// Adding .parallel() is one word and it is almost never the right call. Here is
// what it actually does and the trap it sets.

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ParallelStreams {

    public static void main(String[] args) {
        System.out.println("--- it does work, for the right shape of problem ---");

        long sequential = time(() -> IntStream.rangeClosed(1, 5_000_000)
                .mapToLong(ParallelStreams::expensive).sum());
        long parallel = time(() -> IntStream.rangeClosed(1, 5_000_000)
                .parallel().mapToLong(ParallelStreams::expensive).sum());

        System.out.println("  sequential took " + sequential + "ms");
        System.out.println("  parallel took   " + parallel + "ms");
        System.out.println("  (timings vary by machine and load; the point is that");
        System.out.println("   this shape of work can benefit at all)");

        System.out.println();
        System.out.println("--- THE TRAP: a shared mutable collection ---");

        List<Integer> unsafe = new ArrayList<>();

        // Wrapped, because unsynchronised concurrent add does not merely lose
        // elements. It can also throw, when one thread resizes the backing
        // array while another is mid-write. Which of the two you get is a race,
        // and that unpredictability is the actual lesson.
        String outcome;
        try {
            IntStream.range(0, 100_000).parallel().forEach(unsafe::add);
            outcome = unsafe.size() == 100_000
                    ? "this run happened to survive. It will not always."
                    : "elements were LOST. ArrayList is not thread safe.";
        } catch (RuntimeException e) {
            outcome = "it threw " + e.getClass().getSimpleName()
                    + ", the other way this fails";
        }

        System.out.println("  added 100000 elements to an ArrayList in parallel");
        System.out.println("  the list now holds: " + unsafe.size());
        System.out.println("  correct would be:   100000");
        System.out.println("  -> " + outcome);

        // THIS IS THE IMPORTANT PART. The number above is not reliably wrong,
        // which is worse than being reliably wrong. ArrayList.add reads a size,
        // writes an element and increments the size, with no synchronisation.
        // Two threads interleaving there can overwrite each other's slot or
        // resize the backing array from underneath one another.
        //
        // On a small input, or an unloaded machine, it often works. That is how
        // this reaches production.

        System.out.println();
        System.out.println("--- the fix is not a lock, it is collect ---");

        List<Integer> safe = IntStream.range(0, 100_000).parallel().boxed().toList();
        System.out.println("  collected properly: " + safe.size());

        // collect() and toList() are built for this. Each thread accumulates
        // into its own container and the results are merged at the end, so
        // nothing is shared while it is being written.
        //
        // Synchronising add() would also be correct and would be slower than
        // the sequential version, because every thread would queue on one lock.

        System.out.println();
        System.out.println("--- forEach does not preserve order; forEachOrdered does ---");

        System.out.println("  parallel forEach:        "
                + collectOrder(false));
        System.out.println("  parallel forEachOrdered: "
                + collectOrder(true));

        System.out.println();
        System.out.println("--- when parallel is worth considering ---");
        System.out.println("  large N, and the work per element is genuinely expensive");
        System.out.println("  the source splits cheaply: arrays, ArrayList, IntStream.range");
        System.out.println("  the operation has no shared state and no ordering requirement");
        System.out.println();
        System.out.println("--- when it is not ---");
        System.out.println("  small N. the fork/join overhead dominates.");
        System.out.println("  LinkedList or Iterator sources, which cannot split evenly");
        System.out.println("  anything doing IO. the common pool is sized for CPUs,");
        System.out.println("    and blocking it starves every other parallel stream in the JVM");
        System.out.println("  anything where order matters");
        System.out.println();
        System.out.println("The honest default: do not write .parallel() until a profiler");
        System.out.println("tells you this specific pipeline is the bottleneck.");
    }

    static String collectOrder(boolean ordered) {
        var seen = new ArrayList<Integer>();
        var sink = java.util.Collections.synchronizedList(seen);
        var stream = IntStream.range(0, 8).boxed().parallel();
        if (ordered) {
            stream.forEachOrdered(sink::add);
        } else {
            stream.forEach(sink::add);
        }
        return seen.toString();
    }

    static long expensive(int n) {
        // Deliberately not optimisable away.
        long acc = n;
        for (int i = 0; i < 20; i++) {
            acc = (acc * 31 + i) % 1_000_003;
        }
        return acc;
    }

    static long time(Runnable work) {
        long start = System.nanoTime();
        work.run();
        return (System.nanoTime() - start) / 1_000_000;
    }
}
