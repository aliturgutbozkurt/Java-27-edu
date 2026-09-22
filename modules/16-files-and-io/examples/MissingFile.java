// EXPECT: runtime-error
//
// Reading a file that is not there.
//
//     Exception in thread "main" java.nio.file.NoSuchFileException: /tmp/.../absent.txt
//
// Compare that with what the legacy API would have given you: a `false` from
// File.exists(), or a FileNotFoundException whose message is a String you have
// to parse if you want the path back.
//
// NoSuchFileException is a subclass of IOException and carries the path as
// STRUCTURED DATA:
//
//     catch (NoSuchFileException e) {
//         log.warn("missing config at {}", e.getFile());
//     }
//
// Module 10's advice about custom exceptions carrying fields rather than
// encoding everything in the message is the same idea, and the JDK follows it.
//
// THE OTHER EXCEPTIONS IN THIS FAMILY, all IOException subclasses:
//
//     NoSuchFileException         the path does not exist
//     FileAlreadyExistsException  it does, and you asked to create it
//     AccessDeniedException       permissions
//     DirectoryNotEmptyException  delete on a non-empty directory
//     NotDirectoryException       you treated a file as a directory
//
// Catching the specific one lets you react differently. Catching IOException
// catches them all when you cannot.
//
// A NOTE ON CHECKING FIRST:
//
//     if (Files.exists(path)) {        // don't
//         return Files.readString(path);
//     }
//
// That answers a question about the past. The file can vanish between the check
// and the read, and then you get the exception anyway, from a line that looks
// like it was already guarded. Try the operation and handle the failure.

import java.io.IOException;
import java.nio.file.*;

public class MissingFile {

    public static void main(String[] args) throws IOException {
        Path dir = Files.createTempDirectory("java27-missing");

        Path present = dir.resolve("present.txt");
        Files.writeString(present, "this one exists\n");
        System.out.println("reading a file that exists: " + Files.readString(present).strip());

        Path absent = dir.resolve("absent.txt");
        System.out.println("now one that does not:");
        System.out.println(Files.readString(absent));
    }
}
