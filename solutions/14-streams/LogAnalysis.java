// Reference solution for Homework 14.

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Gatherers;
import java.util.stream.IntStream;

public class LogAnalysis {

    record Entry(String level, String service, int millis) { }

    private static final List<String> RAW = List.of(
            "INFO  auth    120",
            "ERROR auth    450",
            "INFO  billing 80",
            "WARN  billing 900",
            "ERROR auth    380",
            "INFO  search  60",
            "ERROR billing 1200",
            "INFO  auth    95",
            "WARN  search  700",
            "INFO  billing 110");

    public static void main(String[] args) {
        List<Entry> entries = parse();

        partOne(entries);
        System.out.println();
        partTwo(entries);
        System.out.println();
        partThree();
        System.out.println();
        partFour(entries);
    }

    // ---------------------------------------------------------------- part 1

    private static void partOne(List<Entry> entries) {
        System.out.println("--- grouping ---");

        // TreeMap so the services come out alphabetically. With the default
        // HashMap the order would be undefined, and this gets printed, so it
        // matters. Same argument as Module 12.
        Map<String, Long> countByService = entries.stream()
                .collect(Collectors.groupingBy(Entry::service, TreeMap::new, Collectors.counting()));
        System.out.println("  entries per service: " + countByService);

        Map<String, Double> avgByService = entries.stream()
                .collect(Collectors.groupingBy(Entry::service, TreeMap::new,
                        Collectors.averagingInt(Entry::millis)));
        System.out.println("  average ms:          " + avgByService);

        // A downstream mapping collector, so each group holds the levels rather
        // than whole Entry objects.
        Map<String, Set<String>> levelsByService = entries.stream()
                .collect(Collectors.groupingBy(Entry::service, TreeMap::new,
                        Collectors.mapping(Entry::level, Collectors.toCollection(TreeSet::new))));
        System.out.println("  levels seen:         " + levelsByService);

        System.out.println();
        System.out.println("--- partitioning ---");

        // partitioningBy rather than groupingBy on a boolean, because it
        // guarantees BOTH keys exist even if one side is empty. A groupingBy
        // would simply omit the missing key and the lookup below would return
        // null.
        Map<Boolean, List<String>> slow = entries.stream()
                .collect(Collectors.partitioningBy(e -> e.millis() > 500,
                        Collectors.mapping(e -> e.service() + "/" + e.millis(),
                                Collectors.toList())));
        System.out.println("  slow (>500ms): " + slow.get(true));
        System.out.println("  fast:          " + slow.get(false));

        System.out.println();
        System.out.println("--- toMap and its duplicate-key edge ---");

        try {
            entries.stream().collect(Collectors.toMap(Entry::service, Entry::millis));
        } catch (IllegalStateException e) {
            System.out.println("  plain toMap threw on a duplicate service key");
        }

        // The merge function decides what a collision means. Here: keep the
        // slowest, which is the interesting one for a latency report.
        Map<String, Integer> worst = entries.stream()
                .collect(Collectors.toMap(Entry::service, Entry::millis, Integer::max, TreeMap::new));
        System.out.println("  worst per service: " + worst);
    }

    // ---------------------------------------------------------------- part 2

    private static void partTwo(List<Entry> entries) {
        System.out.println("--- gatherers ---");

        List<Integer> timings = entries.stream().map(Entry::millis).toList();
        System.out.println("  raw timings:    " + timings);

        // windowFixed for batching. The final window is short rather than
        // dropped, so no readings are silently lost.
        System.out.println("  batches of 3:   " + timings.stream()
                .gather(Gatherers.windowFixed(3))
                .toList());

        // windowSliding to compare each reading with the one before it. This is
        // the thing that needed a loop before gatherers existed: map cannot see
        // two elements at once.
        System.out.println("  deltas:         " + timings.stream()
                .gather(Gatherers.windowSliding(2))
                .map(pair -> pair.get(1) - pair.get(0))
                .toList());

        // scan for a running total. reduce would give only the final sum; scan
        // emits every step, which is what a cumulative view needs.
        System.out.println("  running total:  " + timings.stream()
                .gather(Gatherers.scan(() -> 0, Integer::sum))
                .toList());

        // WHY A GATHERER AND NOT map, in my own words:
        //
        // map is one element in, one element out, with no memory of what came
        // before. Every operation above breaks at least one of those rules.
        // windowFixed emits fewer elements than it consumes, windowSliding
        // needs to remember the previous element, and scan carries an
        // accumulator across the whole stream.
        //
        // Before gatherers, all three meant leaving the pipeline, writing a
        // loop, and coming back with a list.
    }

    // ---------------------------------------------------------------- part 3

    private static void partThree() {
        System.out.println("--- the parallel data race ---");

        // Three runs of the same broken code, to show the result is not merely
        // wrong but INCONSISTENTLY wrong.
        for (int run = 1; run <= 3; run++) {
            List<Integer> unsafe = new ArrayList<>();
            String outcome;
            try {
                IntStream.range(0, 50_000).parallel().forEach(unsafe::add);
                outcome = unsafe.size() == 50_000 ? "survived by luck" : "lost elements";
            } catch (RuntimeException e) {
                // The other failure mode: one thread resizing the backing array
                // while another writes into it.
                outcome = "threw " + e.getClass().getSimpleName();
            }
            System.out.printf("  run %d: size=%-6d  %s%n", run, unsafe.size(), outcome);
        }

        // The correct version, which cannot vary.
        List<Integer> safe = IntStream.range(0, 50_000).parallel().boxed().toList();
        System.out.println("  collected:  size=" + safe.size() + "   always");

        // WHY THE RACE HAPPENS, in my own words:
        //
        // ArrayList.add is three steps: read the current size, write the
        // element at that index, increment the size. Nothing synchronises
        // them. Two threads that read the same size both write to the same
        // slot, one overwrites the other, and the size ends up incremented
        // twice for one surviving element. Grow the array in the middle of
        // that and you can get an exception instead.
        //
        // WHY collect IS DIFFERENT:
        //
        // It never shares a container while writing. Each worker accumulates
        // into a private one, and the framework merges them at the end, when
        // only one thread touches each pair. There is no window in which two
        // threads write the same memory.
        //
        // WHY A LOCK WOULD BE THE WRONG FIX:
        //
        // Synchronising add would be correct and would make the parallel
        // version SLOWER than the sequential one, because every thread would
        // queue on a single lock and do nothing in parallel except contend.

        // A counter has the same problem and the same shape of fix.
        AtomicInteger atomic = new AtomicInteger();
        IntStream.range(0, 50_000).parallel().forEach(i -> atomic.incrementAndGet());
        System.out.println("  AtomicInteger: " + atomic.get() + " (correct, but still");
        System.out.println("                 shared state; count() would be better)");
        System.out.println("  count():       " + IntStream.range(0, 50_000).parallel().count());
    }

    // ---------------------------------------------------------------- part 4

    private static void partFour(List<Entry> entries) {
        System.out.println("--- laziness and single use ---");

        // Nothing runs while the pipeline is being built.
        var pipeline = entries.stream()
                .peek(e -> System.out.println("    examined " + e.service()))
                .filter(e -> e.level().equals("ERROR"));
        System.out.println("  pipeline built, nothing examined yet");

        System.out.println("  now finding the first error:");
        var first = pipeline.findFirst();
        System.out.println("  found: " + first.map(Entry::service).orElse("none"));

        // Short-circuiting: it stopped as soon as it had an answer rather than
        // examining all ten entries.

        System.out.println();
        System.out.println("  reusing that same stream:");
        try {
            pipeline.count();
        } catch (IllegalStateException e) {
            System.out.println("    " + e.getMessage());
        }

        // The fix is to keep the SOURCE. A List can make as many streams as I
        // want; a Stream cannot make a second one of itself.
        System.out.println("  from the source again: "
                + entries.stream().filter(e -> e.level().equals("ERROR")).count() + " errors");
    }

    // ---------------------------------------------------------------- parsing

    private static List<Entry> parse() {
        return RAW.stream()
                .map(line -> line.split("\\s+"))
                .map(parts -> new Entry(parts[0], parts[1], Integer.parseInt(parts[2])))
                .toList();
    }
}
