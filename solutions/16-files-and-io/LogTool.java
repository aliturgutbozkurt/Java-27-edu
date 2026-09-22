// Reference solution for Homework 16.
//
// Everything is created inside a temporary directory and deleted on the way
// out, so running this leaves nothing behind.

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.*;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class LogTool {

    public static void main(String[] args) throws IOException {
        Path workspace = Files.createTempDirectory("java27-logtool");
        try {
            Path log = workspace.resolve("app.log");
            seed(log, 1_000);

            partOne(log);
            System.out.println();
            partTwo(log, workspace);
            System.out.println();
            partThree(workspace);
            System.out.println();
            partFour(workspace);
        } finally {
            deleteRecursively(workspace);
            System.out.println();
            System.out.println("cleaned up: " + !Files.exists(workspace));
        }
    }

    // ---------------------------------------------------------------- part 1

    private static void partOne(Path log) throws IOException {
        System.out.println("--- streaming, not slurping ---");
        System.out.println("  file size: " + Files.size(log) + " bytes, "
                + Files.readAllLines(log).size() + " lines");

        // Every query below opens its own stream and closes it. Files.lines
        // holds an open file handle, and the stream is the only thing that can
        // release it, so the try-with-resources is load-bearing rather than
        // decorative. Without it this method would leak four descriptors.

        try (var lines = Files.lines(log)) {
            Map<String, Long> byLevel = lines
                    .map(LogTool::levelOf)
                    .collect(Collectors.groupingBy(l -> l, TreeMap::new, Collectors.counting()));
            System.out.println("  counts:    " + byLevel);
        }

        try (var lines = Files.lines(log)) {
            System.out.println("  first ERROR: "
                    + lines.filter(l -> l.startsWith("ERROR")).findFirst().orElse("none"));
        }

        // Short-circuiting matters here. findFirst stops reading as soon as it
        // has an answer, so on a multi-gigabyte log this reads a few kilobytes
        // rather than all of it. readAllLines could not do that at any size.

        try (var lines = Files.lines(log)) {
            System.out.println("  slowest:     " + lines
                    .filter(l -> l.contains("ms="))
                    .max(Comparator.comparingInt(LogTool::millisOf))
                    .orElse("none"));
        }

        try (var lines = Files.lines(log)) {
            var stats = lines.filter(l -> l.contains("ms="))
                    .mapToInt(LogTool::millisOf)
                    .summaryStatistics();
            System.out.println("  timing:    " + stats);
        }
    }

    // ---------------------------------------------------------------- part 2

    private static void partTwo(Path log, Path workspace) throws IOException {
        System.out.println("--- writing a filtered copy ---");

        Path errors = workspace.resolve("errors.log");

        // Two resources in one statement. They close in reverse order, so the
        // writer is flushed and closed before the reader, and a failure in
        // either is reported with the other's failure suppressed rather than
        // replacing it. Module 10 covered why that ordering matters.
        try (var lines = Files.lines(log);
             var out = Files.newBufferedWriter(errors)) {
            lines.filter(l -> l.startsWith("ERROR")).forEach(line -> {
                try {
                    out.write(line);
                    out.newLine();
                } catch (IOException e) {
                    // forEach takes a Consumer, which cannot throw a checked
                    // exception. Wrapping in UncheckedIOException is the
                    // standard escape, and it keeps the original as the cause
                    // so nothing is lost.
                    throw new UncheckedIOException(e);
                }
            });
        }

        System.out.println("  wrote " + Files.readAllLines(errors).size() + " error lines");

        // Appending needs an explicit option, because the default is truncate.
        Files.writeString(errors, "# end of report\n", StandardOpenOption.APPEND);
        System.out.println("  after append: " + Files.readAllLines(errors).size() + " lines");

        Path archive = workspace.resolve("archive/errors-2026.log");
        Files.createDirectories(archive.getParent());
        Files.move(errors, archive, StandardCopyOption.REPLACE_EXISTING);
        System.out.println("  moved to archive, original gone: " + !Files.exists(errors));
    }

    // ---------------------------------------------------------------- part 3

    private static void partThree(Path workspace) throws IOException {
        System.out.println("--- the path validator ---");

        Path uploads = Files.createDirectories(workspace.resolve("uploads")).toRealPath();
        Files.writeString(uploads.resolve("photo.jpg"), "image bytes");

        for (String requested : List.of(
                "photo.jpg",
                "nested/../photo.jpg",
                "../../etc/passwd",
                "/etc/passwd",
                "..")) {
            System.out.printf("  %-22s -> %s%n", requested, describe(uploads, requested));
        }

        System.out.println();
        System.out.println("--- why Path.startsWith and not String.startsWith ---");

        // A sibling directory whose name merely begins with the same text.
        Path sibling = Files.createDirectories(workspace.resolve("uploads-evil")).toRealPath();
        Path attacker = sibling.resolve("stolen.txt");

        System.out.println("  uploads:  " + uploads.getFileName());
        System.out.println("  attacker: " + attacker.getParent().getFileName() + "/stolen.txt");
        System.out.println("  String.startsWith says inside: "
                + attacker.toString().startsWith(uploads.toString()));
        System.out.println("  Path.startsWith says inside:   "
                + attacker.startsWith(uploads));

        // String comparison sees "uploads" as a prefix of "uploads-evil" and
        // waves it through. Path compares NAME ELEMENTS, so "uploads-evil" is
        // simply a different element and the check fails correctly.
        //
        // This is not a hypothetical. It is one of the standard ways a path
        // check gets bypassed.
    }

    // Returns a description rather than throwing, so all five cases can print
    // on one line each.
    private static String describe(Path base, String requested) {
        // normalize() collapses .. and . lexically. It must happen BEFORE the
        // containment check, or "a/../../b" still looks like it starts with a.
        Path resolved = base.resolve(requested).normalize();

        if (!resolved.startsWith(base)) {
            return "REJECTED, escapes the base directory";
        }
        if (!Files.exists(resolved)) {
            return "inside the base, but does not exist";
        }
        return "allowed: " + base.relativize(resolved);
    }

    // ---------------------------------------------------------------- part 4

    private static void partFour(Path workspace) throws IOException {
        System.out.println("--- exceptions that say something ---");

        Path absent = workspace.resolve("not-here.txt");

        try {
            Files.readString(absent);
        } catch (NoSuchFileException e) {
            // getFile() is structured data, not a message to be parsed.
            System.out.println("  NoSuchFileException.getFile(): " + Path.of(e.getFile()).getFileName());
        }

        Path existing = workspace.resolve("exists.txt");
        Files.writeString(existing, "x");
        try {
            Files.createFile(existing);
        } catch (FileAlreadyExistsException e) {
            System.out.println("  FileAlreadyExistsException for: "
                    + Path.of(e.getFile()).getFileName());
        }

        Path notEmpty = Files.createDirectories(workspace.resolve("full"));
        Files.writeString(notEmpty.resolve("child.txt"), "x");
        try {
            Files.delete(notEmpty);
        } catch (DirectoryNotEmptyException e) {
            System.out.println("  DirectoryNotEmptyException for: "
                    + Path.of(e.getFile()).getFileName());
        }

        System.out.println();
        System.out.println("--- and what the legacy API gives instead ---");

        boolean deleted = absent.toFile().delete();
        System.out.println("  File.delete() on a missing file: " + deleted);
        System.out.println("  same false for missing, locked, or no permission.");
        System.out.println("  the caller cannot tell which, so cannot react correctly.");

        // WHY exists() IS NOT A FIX, in my own words:
        //
        // Checking exists() before reading looks safer and is not. The check
        // and the read are two separate trips to the filesystem, and anything
        // can happen in between: another process deletes the file, a mount goes
        // away, permissions change.
        //
        // So the exception can still arrive, now from a line the reader assumes
        // was already guarded, which makes it harder to diagnose rather than
        // easier. Attempting the operation and catching the specific exception
        // is both shorter and correct.
    }

    // ---------------------------------------------------------------- helpers

    private static String levelOf(String line) {
        int space = line.indexOf(' ');
        return space < 0 ? line : line.substring(0, space);
    }

    private static int millisOf(String line) {
        int at = line.indexOf("ms=");
        if (at < 0) {
            return 0;
        }
        String tail = line.substring(at + 3);
        int end = tail.indexOf(' ');
        return Integer.parseInt(end < 0 ? tail : tail.substring(0, end));
    }

    private static void seed(Path log, int lines) throws IOException {
        var sb = new StringBuilder();
        for (int i = 0; i < lines; i++) {
            String level = switch (i % 7) {
                case 0 -> "ERROR";
                case 1, 2 -> "WARN";
                default -> "INFO";
            };
            sb.append(level)
              .append(" request=").append(i)
              .append(" ms=").append((i * 37) % 900 + 10)
              .append(System.lineSeparator());
        }
        Files.writeString(log, sb.toString());
    }

    private static void deleteRecursively(Path root) throws IOException {
        if (!Files.exists(root)) {
            return;
        }
        // Files.delete refuses a non-empty directory, so children must go
        // first. Reverse order does that, because a child's path sorts after
        // its parent's.
        try (var walk = Files.walk(root)) {
            walk.sorted(Comparator.reverseOrder()).forEach(p -> {
                try {
                    Files.delete(p);
                } catch (IOException e) {
                    // Best effort. The OS clears its temp directory regardless.
                }
            });
        }
    }
}
