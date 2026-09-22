// Packages are namespaces, and the directory layout that goes with them is not
// a convention. It is a rule the compiler enforces.
//
// This example BUILDS a small packaged project in a temporary directory,
// compiles it with the real javac, and runs it, so you can see the layout rule
// in action rather than being told about it. Everything is deleted afterwards.

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.*;
import java.util.Comparator;
import java.util.spi.ToolProvider;

public class Packages {

    public static void main(String[] args) throws Exception {
        Path project = Files.createTempDirectory("java27-packages");
        try {
            System.out.println("--- the rule ---");
            System.out.println("  package com.example.util;   ->   com/example/util/Greeter.java");
            System.out.println();
            System.out.println("  The package statement and the directory path must match.");
            System.out.println("  javac uses the directory to FIND sources it has not been");
            System.out.println("  given, and the JVM uses it to find classes at runtime.");

            // Build the layout the rule requires.
            Path srcRoot = project.resolve("src");
            Path utilDir = srcRoot.resolve("com/example/util");
            Path appDir = srcRoot.resolve("com/example/app");
            Files.createDirectories(utilDir);
            Files.createDirectories(appDir);

            Files.writeString(utilDir.resolve("Greeter.java"), """
                    package com.example.util;

                    public class Greeter {

                        // public: visible everywhere
                        public static String greet(String name) {
                            return decorate("Hello, " + name);
                        }

                        // package-private: visible only inside com.example.util.
                        // This is the default, and it is a real access level, not
                        // an absence of one.
                        static String decorate(String text) {
                            return "<< " + text + " >>";
                        }
                    }
                    """);

            Files.writeString(appDir.resolve("Main.java"), """
                    package com.example.app;

                    import com.example.util.Greeter;

                    public class Main {
                        public static void main(String[] args) {
                            System.out.println("  " + Greeter.greet("world"));

                            // This would not compile. decorate is package-private,
                            // and com.example.app is a different package:
                            //
                            //     Greeter.decorate("x");
                            //     error: decorate(String) is not public in Greeter;
                            //            cannot be accessed from outside package
                        }
                    }
                    """);

            System.out.println();
            System.out.println("--- compiling ---");

            Path classes = project.resolve("classes");
            int rc = runTool("javac", "-d", classes.toString(),
                    utilDir.resolve("Greeter.java").toString(),
                    appDir.resolve("Main.java").toString());
            System.out.println("  javac exit code: " + rc);
            System.out.println("  produced: " + relativeFiles(classes, project));

            System.out.println();
            System.out.println("--- running ---");
            System.out.println("  java -cp classes com.example.app.Main");

            // Load and invoke through a class loader pointed at the output
            // directory. That is what -cp does, spelled out.
            try (var loader = new java.net.URLClassLoader(
                    new java.net.URL[]{classes.toUri().toURL()})) {
                Class<?> main = loader.loadClass("com.example.app.Main");
                main.getMethod("main", String[].class).invoke(null, (Object) new String[0]);
            }

            System.out.println();
            System.out.println("--- naming ---");
            System.out.println("  reverse domain name: com.example, org.apache, java.util");
            System.out.println("  all lower case, no underscores");
            System.out.println("  the reversal exists so two organisations cannot collide");
            System.out.println();
            System.out.println("  Never use the DEFAULT package for real code. A class with");
            System.out.println("  no package statement cannot be imported by any class that");
            System.out.println("  has one, so it is unusable from anywhere organised.");

            System.out.println();
            System.out.println("--- the four access levels, once more ---");
            System.out.println("  private     this class");
            System.out.println("  (none)      this package        <- the default");
            System.out.println("  protected   this package + subclasses anywhere");
            System.out.println("  public      everyone");
            System.out.println();
            System.out.println("  Package-private is the level people forget exists. It is");
            System.out.println("  how you share something between collaborating classes");
            System.out.println("  without exposing it to the world.");

        } finally {
            deleteRecursively(project);
            System.out.println();
            System.out.println("cleaned up: " + !Files.exists(project));
        }
    }

    static int runTool(String tool, String... toolArgs) {
        var provider = ToolProvider.findFirst(tool).orElseThrow(
                () -> new IllegalStateException(tool + " is not available"));
        var out = new StringWriter();
        var err = new StringWriter();
        int rc = provider.run(new PrintWriter(out), new PrintWriter(err), toolArgs);
        if (!err.toString().isBlank()) {
            System.out.println("  " + tool + " stderr: " + err.toString().strip());
        }
        return rc;
    }

    static String relativeFiles(Path root, Path base) throws java.io.IOException {
        try (var walk = Files.walk(root)) {
            return walk.filter(Files::isRegularFile)
                    .map(base::relativize)
                    .map(Path::toString)
                    .sorted()
                    .toList()
                    .toString();
        }
    }

    static void deleteRecursively(Path root) throws java.io.IOException {
        if (!Files.exists(root)) {
            return;
        }
        try (var walk = Files.walk(root)) {
            walk.sorted(Comparator.reverseOrder()).forEach(p -> {
                try {
                    Files.delete(p);
                } catch (java.io.IOException e) {
                    // best effort
                }
            });
        }
    }
}
