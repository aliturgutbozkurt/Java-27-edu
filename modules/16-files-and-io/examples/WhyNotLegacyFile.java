// java.io.File dates from 1996 and is still there for compatibility. You will
// meet it in old code. You should not write new code with it, and this is why.

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

public class WhyNotLegacyFile {

    public static void main(String[] args) throws IOException {
        Path dir = Files.createTempDirectory("java27-legacy");
        try {
            Path missing = dir.resolve("does-not-exist.txt");

            System.out.println("--- reason 1: failures are silent booleans ---");

            File legacy = missing.toFile();
            boolean deleted = legacy.delete();
            System.out.println("  File.delete() returned: " + deleted);
            System.out.println("  why did it fail? no permission? not there? locked?");
            System.out.println("  the API does not say, and cannot be made to.");

            try {
                Files.delete(missing);
            } catch (NoSuchFileException e) {
                System.out.println("  Files.delete() threw NoSuchFileException");
                System.out.println("  naming the file: " + e.getFile());
            }

            // That is the central complaint. Every File method that can fail
            // returns false, so error handling means guessing. Files throws a
            // specific exception carrying the path and the reason.

            System.out.println();
            System.out.println("--- reason 2: no symbolic link support ---");
            System.out.println("  File cannot create, detect or follow symlinks.");
            System.out.println("  Files.isSymbolicLink, createSymbolicLink and the");
            System.out.println("  NOFOLLOW_LINKS option all exist for a reason.");

            System.out.println();
            System.out.println("--- reason 3: no file attributes ---");

            Path real = dir.resolve("real.txt");
            Files.writeString(real, "content");

            var attrs = Files.readAttributes(real, java.nio.file.attribute.BasicFileAttributes.class);
            System.out.println("  Files gives creation time: " + (attrs.creationTime() != null));
            System.out.println("  and size, modified time, isDirectory, fileKey, all in ONE call");
            System.out.println("  File would need several calls, each hitting the disk again");

            System.out.println();
            System.out.println("--- reason 4: no directory streaming ---");
            System.out.println("  File.listFiles() returns an ARRAY, so a directory with");
            System.out.println("  a million entries becomes a million-element array.");
            System.out.println("  Files.list() returns a lazy stream.");

            System.out.println();
            System.out.println("--- when you still need one ---");

            // Old APIs take a File. The conversion is one call in each direction.
            File asFile = real.toFile();
            Path backToPath = asFile.toPath();
            System.out.println("  path.toFile() and file.toPath() convert freely");
            System.out.println("  round trip equal: " + backToPath.equals(real));

            // The rule: convert at the boundary, and use Path everywhere inside
            // your own code.

        } finally {
            deleteRecursively(dir);
            System.out.println();
            System.out.println("cleaned up: " + !Files.exists(dir));
        }
    }

    static void deleteRecursively(Path root) throws IOException {
        if (!Files.exists(root)) {
            return;
        }
        try (var walk = Files.walk(root)) {
            walk.sorted(java.util.Comparator.reverseOrder()).forEach(p -> {
                try {
                    Files.delete(p);
                } catch (IOException e) {
                    // best effort
                }
            });
        }
    }
}
