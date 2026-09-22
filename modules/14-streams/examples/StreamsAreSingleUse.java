// EXPECT: runtime-error
//
// A stream is consumed once. Using it twice throws.
//
//     Exception in thread "main" java.lang.IllegalStateException:
//       stream has already been operated upon or closed
//
// WHY: a stream is a pipeline over a source, not a container. Once a terminal
// operation has pulled everything through, there is nothing left to pull. It
// holds no elements of its own to replay.
//
// This catches people who store a stream in a variable expecting it to behave
// like a collection. The rule of thumb: do not put a Stream in a variable that
// outlives a single expression, and never return one from a method unless the
// caller clearly owns consuming it.
//
// THE FIX: keep the SOURCE, not the stream. A collection can produce as many
// streams as you like:
//
//     List<String> words = List.of("a", "b");
//     words.stream().filter(...).toList();
//     words.stream().map(...).toList();      // a brand new stream, fine
//
// Or, if the source is expensive, collect once and reuse the result.

import java.util.List;
import java.util.stream.Stream;

public class StreamsAreSingleUse {

    public static void main(String[] args) {
        List<String> words = List.of("apple", "fig", "cherry");

        Stream<String> stream = words.stream().filter(w -> w.length() > 3);

        System.out.println("first use:  " + stream.toList());
        System.out.println("now using the same stream again:");
        System.out.println("second use: " + stream.count());
    }
}
