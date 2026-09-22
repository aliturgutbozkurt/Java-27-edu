// Reference solution for Homework 10.

import java.io.IOException;
import java.util.Map;

public class ConfigLoader {

    public static void main(String[] args) {
        partOne();
        System.out.println();
        partTwo();
        System.out.println();
        partThree();
    }

    // ---------------------------------------------------------------- part 1

    private static void partOne() {
        System.out.println("--- loading configs ---");

        for (String name : new String[]{"app.conf", "missing.conf", "broken.conf"}) {
            try {
                Map<String, String> config = load(name);
                System.out.println("  " + name + " -> " + config);
            } catch (ConfigException e) {
                System.out.println("  " + name + " -> failed: " + e.getMessage());
                System.out.println("      source: " + e.source());
                System.out.println("      cause:  " + e.getCause());
            }
        }
    }

    // Checked, because a caller genuinely can do something else: fall back to
    // defaults, try another file, or prompt. That is the test for whether a
    // checked exception earns its place.
    static Map<String, String> load(String name) throws ConfigException {
        try {
            String contents = readFile(name);
            return parse(name, contents);
        } catch (IOException e) {
            // Wrap, add context, and PASS THE CAUSE. Without the third
            // argument the IOException disappears and the stack trace starts
            // here, saying nothing about what actually failed.
            throw new ConfigException("could not read config", name, e);
        }
    }

    private static String readFile(String name) throws IOException {
        return switch (name) {
            case "app.conf" -> "host=localhost\nport=8080";
            case "broken.conf" -> "host=localhost\nport=not-a-number";
            default -> throw new IOException(name + ": no such file");
        };
    }

    private static Map<String, String> parse(String name, String contents)
            throws ConfigException {
        var result = new java.util.LinkedHashMap<String, String>();
        for (String line : contents.lines().toList()) {
            String[] parts = line.split("=", 2);
            if (parts.length != 2) {
                throw new ConfigException("malformed line: " + line, name);
            }
            result.put(parts[0], parts[1]);
        }

        // Validating a value. NumberFormatException is unchecked, so nothing
        // forces this catch; it is here because a bad port is a config problem
        // the caller should hear about as a ConfigException, not as a surprise
        // runtime failure from three layers down.
        String port = result.get("port");
        if (port != null) {
            try {
                Integer.parseInt(port);
            } catch (NumberFormatException e) {
                throw new ConfigException("port is not a number: " + port, name, e);
            }
        }
        return result;
    }

    // ---------------------------------------------------------------- part 2

    private static void partTwo() {
        System.out.println("--- three anti-patterns, and their fixes ---");

        // ANTI-PATTERN 1: the empty catch block.
        System.out.println("  1. swallowed:  " + swallowed());
        System.out.println("     fixed:      " + notSwallowed());
        //
        // Why it is the worst line in Java, in my own words:
        //
        // The failure still happened. The program simply carries on as though
        // it did not, with whatever half-built state it had. There is no log,
        // no trace, and the symptom shows up somewhere unrelated and much
        // later. A crash is easier to fix than a program that lies.

        // ANTI-PATTERN 2: return inside finally.
        System.out.println("  2. eaten:      " + eatenByFinally());
        System.out.println("     fixed:      " + notEatenByFinally());
        //
        // The return discards whatever the try block was doing, INCLUDING an
        // exception in flight. It is the empty catch block again, written in a
        // way that does not look like one.

        // ANTI-PATTERN 3: catching Exception to cover everything.
        System.out.println("  3. too broad:  " + tooBroad());
        System.out.println("     fixed:      " + narrow());
        //
        // Catching Exception catches the failures I anticipated AND the bugs I
        // did not. A NullPointerException from my own broken code gets reported
        // as "could not parse", sending whoever debugs it to the wrong place.
    }

    private static String swallowed() {
        try {
            throw new IllegalStateException("something broke");
        } catch (IllegalStateException e) {
            // Nothing here. Compiles fine. Hides everything.
        }
        return "carried on as if nothing happened";
    }

    private static String notSwallowed() {
        try {
            throw new IllegalStateException("something broke");
        } catch (IllegalStateException e) {
            // If it truly cannot be handled here, let it travel with its cause
            // attached rather than pretending it did not occur.
            return "reported: " + e.getMessage();
        }
    }

    @SuppressWarnings("finally")
    private static String eatenByFinally() {
        try {
            throw new IllegalStateException("you will never see this");
        } finally {
            return "finally won";
        }
    }

    private static String notEatenByFinally() {
        try {
            throw new IllegalStateException("this one survives");
        } catch (IllegalStateException e) {
            return "caught properly: " + e.getMessage();
        } finally {
            // Cleanup only. No control flow.
        }
    }

    private static String tooBroad() {
        try {
            String s = null;
            return "parsed " + Integer.parseInt(s.trim());
        } catch (Exception e) {
            // The real failure is a NullPointerException in my own code, but
            // this handler reports it as a parsing problem.
            return "could not parse (actually a " + e.getClass().getSimpleName() + ")";
        }
    }

    private static String narrow() {
        String s = null;
        if (s == null) {
            return "no input supplied";
        }
        try {
            return "parsed " + Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return "not a number: " + s;
        }
    }

    // ---------------------------------------------------------------- part 3

    private static void partThree() {
        System.out.println("--- try-with-resources ---");

        try (Connection primary = new Connection("primary");
             Connection replica = new Connection("replica")) {
            System.out.println("  querying through " + replica.name());
            throw new IllegalStateException("query failed");
        } catch (IllegalStateException e) {
            System.out.println("  caught: " + e.getMessage());
            for (Throwable s : e.getSuppressed()) {
                System.out.println("    suppressed: " + s.getMessage());
            }
        }

        // WHAT THE OUTPUT SHOWS, in my own words:
        //
        // The replica was opened second and closed first. Reverse order matters
        // because a resource opened later may depend on one opened earlier, so
        // tearing down in the opposite order is the only safe sequence.
        //
        // Both close() calls failed, and neither of those failures replaced the
        // query failure. The exception I catch is still the real one, with the
        // close failures attached as suppressed.
        //
        // Hand-written cleanup in a finally block gets this wrong: the close
        // exception propagates and the original is lost, so the stack trace
        // blames the cleanup for a problem it did not cause.
    }
}

// A checked exception carrying structured context. The source is a field rather
// than something glued into the message, so a caller can act on it without
// parsing a string back apart.
class ConfigException extends Exception {

    private final String source;

    ConfigException(String message, String source, Throwable cause) {
        super(message, cause);
        this.source = source;
    }

    ConfigException(String message, String source) {
        super(message);
        this.source = source;
    }

    String source() {
        return source;
    }
}

class Connection implements AutoCloseable {

    private final String name;

    Connection(String name) {
        this.name = name;
        System.out.println("  open " + name);
    }

    String name() {
        return name;
    }

    @Override
    public void close() {
        System.out.println("  close " + name);
        throw new IllegalStateException("closing " + name + " failed");
    }
}
