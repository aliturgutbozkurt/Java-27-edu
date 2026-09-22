// Files has a one-line method for every common case. Reach for those before
// anything involving a stream or a reader.
//
// Everything this file creates lives in a temporary directory and is deleted
// before it exits, so running it leaves nothing behind.

import java.io.IOException;
import java.nio.file.*;
import java.util.Comparator;
import java.util.List;

public class ReadingAndWriting {

    public static void main(String[] args) throws IOException {
        Path dir = Files.createTempDirectory("java27-io-demo");
        try {
            Path notes = dir.resolve("notes.txt");

            System.out.println("--- writing ---");

            // writeString creates the file, or truncates it if it exists.
            Files.writeString(notes, "alpha\nbeta\ngamma\n");
            System.out.println("  wrote " + Files.size(notes) + " bytes");

            // Appending takes an explicit option. The default is to overwrite,
            // which is the right default and the wrong assumption to make.
            Files.writeString(notes, "delta\n", StandardOpenOption.APPEND);
            System.out.println("  after append: " + Files.size(notes) + " bytes");

            // Since Java 18 the default charset is UTF-8 everywhere, so these
            // methods no longer depend on the machine's locale. Before that,
            // the same code read different bytes on different machines, which
            // produced a long tail of mojibake bugs.
            System.out.println("  default charset: " + java.nio.charset.Charset.defaultCharset());

            System.out.println();
            System.out.println("--- reading ---");

            // The whole file as one String.
            String whole = Files.readString(notes);
            System.out.println("  readString gave " + whole.length() + " characters");

            // As a list of lines, with the line endings stripped.
            List<String> lines = Files.readAllLines(notes);
            System.out.println("  readAllLines: " + lines);

            // BOTH OF THOSE LOAD THE ENTIRE FILE INTO MEMORY. That is fine for
            // a config file and fatal for a log. StreamingLargeFiles.java shows
            // what to do instead.

            System.out.println();
            System.out.println("--- asking about a file ---");

            System.out.println("  exists:        " + Files.exists(notes));
            System.out.println("  isRegularFile: " + Files.isRegularFile(notes));
            System.out.println("  isDirectory:   " + Files.isDirectory(notes));
            System.out.println("  size:          " + Files.size(notes));
            System.out.println("  readable:      " + Files.isReadable(notes));

            // NOTE ON exists(): it answers a question about the past. By the
            // time you act on the answer, the file may be gone. Prefer trying
            // the operation and handling the exception over checking first.
            // That race has a name, TOCTOU, and it is a real source of bugs.

            System.out.println();
            System.out.println("--- directories ---");

            Path sub = dir.resolve("archive/2026");
            Files.createDirectories(sub);      // creates every missing parent
            Files.writeString(sub.resolve("old.txt"), "archived\n");
            System.out.println("  created " + sub.getFileName() + " and a file in it");

            try (var entries = Files.list(dir)) {
                System.out.println("  top level: " + entries.map(Path::getFileName).sorted().toList());
            }

            // Files.list and Files.walk return streams backed by an open
            // directory handle, so they MUST be closed. try-with-resources does
            // it; forgetting leaks a file descriptor until garbage collection,
            // which on a busy server means running out of them.

            System.out.println();
            System.out.println("--- copying and moving ---");

            Path copy = dir.resolve("notes-copy.txt");
            Files.copy(notes, copy, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("  copied, sizes match: " + (Files.size(copy) == Files.size(notes)));

            Path moved = dir.resolve("renamed.txt");
            Files.move(copy, moved, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("  moved, old gone: " + !Files.exists(copy));

            // deleteIfExists returns a boolean instead of throwing when absent.
            System.out.println("  deleteIfExists: " + Files.deleteIfExists(moved));
            System.out.println("  again:          " + Files.deleteIfExists(moved));

        } finally {
            deleteRecursively(dir);
            System.out.println();
            System.out.println("cleaned up, temp dir gone: " + !Files.exists(dir));
        }
    }

    // Files.delete refuses to remove a non-empty directory, so the walk is
    // sorted in reverse to delete children before their parents.
    static void deleteRecursively(Path root) throws IOException {
        if (!Files.exists(root)) {
            return;
        }
        try (var walk = Files.walk(root)) {
            walk.sorted(Comparator.reverseOrder()).forEach(p -> {
                try {
                    Files.delete(p);
                } catch (IOException e) {
                    // Cleanup is best effort; the temp directory will be
                    // removed by the OS eventually in any case.
                }
            });
        }
    }
}
