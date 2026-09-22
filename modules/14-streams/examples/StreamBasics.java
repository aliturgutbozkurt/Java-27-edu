// A stream is a pipeline, not a collection. It holds no data and it is consumed
// once. Getting that straight explains most of its behaviour.
//
//   SOURCE          where the elements come from
//   INTERMEDIATE    zero or more lazy transformations, each returning a stream
//   TERMINAL        the one operation that actually runs the pipeline

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class StreamBasics {

    public static void main(String[] args) {
        List<String> words = List.of("banana", "fig", "apple", "cherry", "date");

        System.out.println("--- a pipeline ---");
        List<String> result = words.stream()      // source
                .filter(w -> w.length() > 4)      // intermediate
                .map(String::toUpperCase)         // intermediate
                .sorted()                         // intermediate
                .toList();                        // terminal
        System.out.println("  " + result);

        System.out.println();
        System.out.println("--- laziness, demonstrated ---");

        // Building a pipeline runs nothing at all.
        Stream<String> pipeline = words.stream()
                .peek(w -> System.out.println("  peeked at " + w))
                .map(String::toUpperCase);

        System.out.println("  pipeline built. notice nothing was peeked.");
        System.out.println("  now adding a terminal operation:");
        System.out.println("  result: " + pipeline.toList());

        // peek exists for exactly this: seeing what flows through. It is a
        // debugging tool, not a way to cause side effects in real code.

        System.out.println();
        System.out.println("--- laziness means short-circuiting ---");

        var found = Stream.of(1, 2, 3, 4, 5)
                .peek(n -> System.out.println("  examined " + n))
                .filter(n -> n > 2)
                .findFirst();
        System.out.println("  found " + found);

        // It examined 1, 2 and 3, then stopped. Elements 4 and 5 were never
        // touched. A loop with a break does the same thing; the difference is
        // that the stream version says what it wants rather than how to get it.
        //
        // This is also why an infinite stream is usable:
        System.out.println("  first 5 squares: "
                + Stream.iterate(1, n -> n + 1).map(n -> n * n).limit(5).toList());

        System.out.println();
        System.out.println("--- element-wise, not stage-wise ---");

        // A common misreading is that filter runs over everything, then map
        // runs over everything. It does not. Each element is pushed through the
        // whole pipeline before the next one starts.
        Stream.of("a", "b")
                .peek(s -> System.out.println("  filter sees " + s))
                .map(s -> {
                    System.out.println("  map sees " + s);
                    return s;
                })
                .forEach(s -> System.out.println("  forEach sees " + s));

        System.out.println();
        System.out.println("--- the operations you will use constantly ---");

        System.out.println("  filter:    " + words.stream().filter(w -> w.contains("a")).toList());
        System.out.println("  map:       " + words.stream().map(String::length).toList());
        System.out.println("  sorted:    " + words.stream().sorted().toList());
        System.out.println("  distinct:  " + Stream.of(1, 2, 2, 3).distinct().toList());
        System.out.println("  limit:     " + words.stream().limit(2).toList());
        System.out.println("  skip:      " + words.stream().skip(3).toList());
        System.out.println("  anyMatch:  " + words.stream().anyMatch(w -> w.startsWith("f")));
        System.out.println("  allMatch:  " + words.stream().allMatch(w -> w.length() > 2));
        System.out.println("  count:     " + words.stream().filter(w -> w.length() == 3).count());
        System.out.println("  reduce:    " + words.stream().reduce("", (a, b) -> a + b.charAt(0)));

        // flatMap flattens one level. This is how you turn a list of lists into
        // a single stream, and how you expand one element into several.
        List<List<Integer>> nested = List.of(List.of(1, 2), List.of(3, 4));
        System.out.println("  flatMap:   " + nested.stream().flatMap(List::stream).toList());
        System.out.println("  flatMap 2: "
                + words.stream().flatMap(w -> w.chars().mapToObj(c -> (char) c)).distinct().sorted().toList());

        System.out.println();
        System.out.println("--- primitive streams avoid boxing ---");

        // IntStream, LongStream and DoubleStream exist for the same reason
        // IntPredicate does: Stream<Integer> boxes every element.
        System.out.println("  sum:     " + IntStream.rangeClosed(1, 10).sum());
        System.out.println("  average: " + IntStream.of(1, 2, 3).average().orElse(0));
        System.out.println("  stats:   " + words.stream().mapToInt(String::length).summaryStatistics());

        // mapToInt to get there, boxed() to come back.
        System.out.println("  boxed:   " + IntStream.range(0, 3).boxed().toList());
    }
}
