// The classpath is the list of places the JVM looks for classes. Most of the
// startup failures you will meet come from it being wrong.

import java.nio.file.*;
import java.util.Comparator;

public class TheClasspath {

    public static void main(String[] args) throws Exception {
        System.out.println("--- what it is ---");
        System.out.println("  a list of directories and jar files, searched in order");
        System.out.println();
        System.out.println("    java -cp classes:lib/one.jar:lib/two.jar com.example.Main");
        System.out.println("    java -cp \"classes;lib/*\" com.example.Main      (Windows uses ;)");
        System.out.println();
        System.out.println("  this JVM's classpath: " + System.getProperty("java.class.path"));

        System.out.println();
        System.out.println("--- the two errors, which are not the same ---");

        System.out.println();
        System.out.println("  1. ClassNotFoundException");
        try {
            Class.forName("com.example.NeverExisted");
        } catch (ClassNotFoundException e) {
            System.out.println("     " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
        System.out.println("     A CHECKED exception, thrown when something asked for a class");
        System.out.println("     BY NAME at runtime and it was not found. Usually reflection,");
        System.out.println("     a JDBC driver, or a framework loading a configured class.");

        System.out.println();
        System.out.println("  2. NoClassDefFoundError");
        System.out.println("     An ERROR, not an exception. It means the class was present at");
        System.out.println("     COMPILE time and is missing or unusable now.");
        System.out.println();
        System.out.println("     The case people misdiagnose: a class whose static initialiser");
        System.out.println("     threw. The first attempt reports the real cause, and EVERY");
        System.out.println("     attempt after that reports NoClassDefFoundError with no hint");
        System.out.println("     at all. Watch:");

        try {
            new Broken();
        } catch (Throwable t) {
            // ExceptionInInitializerError has a null message of its own; the
            // real failure is the CAUSE. Reporting only getMessage() here is
            // how the useful half gets lost in a log.
            System.out.println("       first attempt:  " + t.getClass().getSimpleName()
                    + ", caused by " + t.getCause());
        }
        try {
            new Broken();
        } catch (Throwable t) {
            System.out.println("       second attempt: " + t.getClass().getSimpleName()
                    + ": " + t.getMessage());
        }

        System.out.println();
        System.out.println("     If you see NoClassDefFoundError for a class you are certain");
        System.out.println("     is on the classpath, scroll UP the log. The real failure was");
        System.out.println("     reported once, earlier, and looked unrelated.");

        System.out.println();
        System.out.println("--- where classes actually come from ---");

        System.out.println("  String's loader:        " + String.class.getClassLoader());
        System.out.println("  (null means the bootstrap loader, built into the JVM)");
        System.out.println("  this class's loader:    " + TheClasspath.class.getClassLoader());

        System.out.println();
        System.out.println("--- finding a class you already have ---");

        // Useful for answering "which jar did this come from", which is the
        // first question when two versions of a library are both present.
        var location = TheClasspath.class.getProtectionDomain().getCodeSource();
        System.out.println("  this class was loaded from: "
                + (location == null ? "(the JDK itself)" : location.getLocation()));

        System.out.println();
        System.out.println("--- the failure mode nobody expects ---");
        System.out.println("  Two jars containing the same class. The classpath is searched");
        System.out.println("  IN ORDER, so the first one wins and the second is silently");
        System.out.println("  ignored. Nothing warns you. Symptoms are a method that does not");
        System.out.println("  exist, or behaviour from a version you thought you had replaced.");
        System.out.println();
        System.out.println("  This is the problem the module system was built to solve.");
    }

    // A class whose static initialiser fails.
    static class Broken {
        static final int VALUE = compute();

        static int compute() {
            throw new IllegalStateException("the static initialiser failed");
        }
    }
}
