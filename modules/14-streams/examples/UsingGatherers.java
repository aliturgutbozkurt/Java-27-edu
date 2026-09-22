// Gatherers are custom intermediate operations. They were finalised in JDK 24,
// so they need no flags here.
//
// The gap they fill: before them, you could write your own terminal operation
// (a Collector) but not your own intermediate one. Anything stateful across
// elements, such as windowing or running totals, meant leaving the stream,
// doing it with a loop, and coming back.

import java.util.List;
import java.util.stream.Gatherers;
import java.util.stream.Stream;

public class UsingGatherers {

    public static void main(String[] args) {
        System.out.println("--- windowFixed: non-overlapping batches ---");
        System.out.println("  " + Stream.of(1, 2, 3, 4, 5)
                .gather(Gatherers.windowFixed(2))
                .toList());
        // The last window is short rather than dropped, which is what you want
        // for batching work: no records go missing.

        System.out.println();
        System.out.println("--- windowSliding: overlapping pairs ---");
        System.out.println("  " + Stream.of(1, 2, 3, 4)
                .gather(Gatherers.windowSliding(2))
                .toList());
        // Useful for comparing each element with its neighbour, for example
        // finding differences between consecutive readings.

        List<Integer> readings = List.of(10, 14, 13, 20);
        System.out.println("  deltas: " + readings.stream()
                .gather(Gatherers.windowSliding(2))
                .map(pair -> pair.get(1) - pair.get(0))
                .toList());

        System.out.println();
        System.out.println("--- scan: a running total that stays in the stream ---");
        System.out.println("  " + Stream.of(1, 2, 3, 4)
                .gather(Gatherers.scan(() -> 0, Integer::sum))
                .toList());
        // reduce gives you only the final value. scan emits every intermediate
        // one, which is what you need for a cumulative chart or a running
        // balance.

        System.out.println();
        System.out.println("--- fold: reduce with a different starting type ---");
        System.out.println("  " + Stream.of("a", "b", "c")
                .gather(Gatherers.fold(() -> "", (acc, s) -> acc + s.toUpperCase()))
                .toList());

        System.out.println();
        System.out.println("--- mapConcurrent: bounded parallel mapping ---");

        // Unlike parallel(), this gives you an explicit concurrency limit and
        // preserves order. It runs each mapping on a virtual thread, which
        // Module 19 covers.
        List<String> fetched = Stream.of("a", "b", "c", "d")
                .gather(Gatherers.mapConcurrent(2, id -> {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    return "fetched-" + id;
                }))
                .toList();
        System.out.println("  " + fetched);
        System.out.println("  order is preserved despite running concurrently");

        System.out.println();
        System.out.println("--- when to reach for one ---");
        System.out.println("  needs state across elements?   a gatherer");
        System.out.println("  needs to emit a different count than it consumed?   a gatherer");
        System.out.println("  one element in, one out, no memory?   just use map");

        // You can write your own with Gatherer.ofSequential, though the built-in
        // ones cover most cases. If a loop is clearer than a custom gatherer,
        // write the loop; streams are not an obligation.
    }
}
