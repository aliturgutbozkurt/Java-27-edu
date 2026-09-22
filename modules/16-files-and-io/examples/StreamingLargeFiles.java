// readString and readAllLines load the whole file. Files.lines does not, and
// the difference is the difference between working and an OutOfMemoryError.

import java.io.IOException;
import java.nio.file.*;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

public class StreamingLargeFiles {

    public static void main(String[] args) throws IOException {
        Path dir = Files.createTempDirectory("java27-io-stream");
        try {
            Path log = dir.resolve("app.log");
            writeSampleLog(log);

            System.out.println("--- the whole file at once ---");
            String whole = Files.readString(log);
            System.out.println("  readString held " + whole.length() + " characters in memory");
            System.out.println("  fine here. fatal for a 4GB log.");

            System.out.println();
            System.out.println("--- streamed, one line at a time ---");

            // The try-with-resources is NOT optional. Files.lines holds an open
            // file handle, and the stream is the only thing that can close it.
            // Module 10's rules apply: this is an AutoCloseable like any other.
            try (var lines = Files.lines(log)) {
                long errors = lines.filter(l -> l.startsWith("ERROR")).count();
                System.out.println("  errors: " + errors);
            }

            // Memory used is one line at a time, whatever the file size. The
            // pipeline can also short-circuit, so finding the first error in a
            // huge file reads only as far as it needs to.
            try (var lines = Files.lines(log)) {
                System.out.println("  first error: "
                        + lines.filter(l -> l.startsWith("ERROR")).findFirst().orElse("none"));
            }

            System.out.println();
            System.out.println("--- a real aggregation in one pass ---");

            try (var lines = Files.lines(log)) {
                Map<String, Long> byLevel = lines
                        .map(l -> l.split("\\s+")[0])
                        .collect(Collectors.groupingBy(level -> level, java.util.TreeMap::new,
                                Collectors.counting()));
                System.out.println("  " + byLevel);
            }

            System.out.println();
            System.out.println("--- writing a stream back out ---");

            Path errorsOnly = dir.resolve("errors.log");
            try (var lines = Files.lines(log);
                 var out = Files.newBufferedWriter(errorsOnly)) {
                lines.filter(l -> l.startsWith("ERROR")).forEach(line -> {
                    try {
                        out.write(line);
                        out.newLine();
                    } catch (IOException e) {
                        // A lambda cannot throw a checked exception, which is
                        // Module 10's complaint about checked exceptions and
                        // lambdas, met in the wild. Wrapping is the usual fix.
                        throw new java.io.UncheckedIOException(e);
                    }
                });
            }
            System.out.println("  wrote " + Files.readAllLines(errorsOnly).size() + " error lines");

            // Both resources close, in reverse order, even if the body throws.
            // Module 10 showed why that ordering and the suppressed-exception
            // handling matter.

            System.out.println();
            System.out.println("--- walking a tree ---");

            Files.createDirectories(dir.resolve("a/b"));
            Files.writeString(dir.resolve("a/one.txt"), "1");
            Files.writeString(dir.resolve("a/b/two.txt"), "2");

            try (var walk = Files.walk(dir)) {
                System.out.println("  regular files found: "
                        + walk.filter(Files::isRegularFile).count());
            }

            // Files.walk, Files.list and Files.find all return streams holding
            // an open directory handle. All three need closing. This is the
            // most commonly leaked resource in Java file code.

        } finally {
            deleteRecursively(dir);
            System.out.println();
            System.out.println("cleaned up: " + !Files.exists(dir));
        }
    }

    static void writeSampleLog(Path log) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            String level = switch (i % 5) {
                case 0 -> "ERROR";
                case 1, 2 -> "WARN";
                default -> "INFO";
            };
            sb.append(level).append(" request ").append(i).append(System.lineSeparator());
        }
        Files.writeString(log, sb.toString());
    }

    static void deleteRecursively(Path root) throws IOException {
        if (!Files.exists(root)) {
            return;
        }
        try (var walk = Files.walk(root)) {
            walk.sorted(Comparator.reverseOrder()).forEach(p -> {
                try {
                    Files.delete(p);
                } catch (IOException e) {
                    // best effort
                }
            });
        }
    }
}
