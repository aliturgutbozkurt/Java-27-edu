// EXPECT: compile-error
//
// A checked exception, ignored. This is the compiler doing the one thing
// checked exceptions exist to do.
//
// The error:
//
//     error: unreported exception IOException; must be caught or declared to be thrown
//         readConfig("app.conf");
//                   ^
//
// You have exactly three options:
//
//   1. catch it, and do something real with it
//   2. add `throws IOException` to main and make the problem someone else's
//   3. decide it cannot happen here and wrap it in an unchecked exception:
//
//          try {
//              readConfig("app.conf");
//          } catch (IOException e) {
//              throw new UncheckedIOException(e);
//          }
//
// Option 3 is legitimate and common. What is NOT legitimate is the fourth
// option people actually reach for:
//
//     try { readConfig("app.conf"); } catch (IOException e) { }
//
// An empty catch block compiles, silences the compiler, and converts a visible
// failure into a program that quietly does the wrong thing. It is the single
// worst line you can write in Java, and checked exceptions are the reason it
// gets written at all. That is the strongest argument against the feature.

import java.io.IOException;

public class UnhandledChecked {

    public static void main(String[] args) {
        readConfig("app.conf");
    }

    static String readConfig(String path) throws IOException {
        throw new IOException("no such file: " + path);
    }
}
