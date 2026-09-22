// Building a jar, and what "module" actually means at runtime.
//
// This example builds and inspects a jar in a temporary directory, then deletes
// it. Nothing is left behind.

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.*;
import java.util.Comparator;
import java.util.spi.ToolProvider;

public class JarsAndModules {

    public static void main(String[] args) throws Exception {
        Path project = Files.createTempDirectory("java27-jar");
        try {
            System.out.println("--- building a jar ---");

            Path src = project.resolve("src/com/example/tool");
            Files.createDirectories(src);
            Files.writeString(src.resolve("Tool.java"), """
                    package com.example.tool;

                    public class Tool {
                        public static void main(String[] args) {
                            System.out.println("  tool running from a jar");
                        }
                    }
                    """);

            Path classes = project.resolve("classes");
            run("javac", "-d", classes.toString(), src.resolve("Tool.java").toString());
            System.out.println("  compiled");

            Path jar = project.resolve("tool.jar");
            run("jar", "--create", "--file", jar.toString(),
                    "--main-class", "com.example.tool.Tool",
                    "-C", classes.toString(), ".");
            System.out.println("  created " + jar.getFileName() + ", "
                    + Files.size(jar) + " bytes");

            System.out.println();
            System.out.println("  contents:");
            var out = new StringWriter();
            ToolProvider.findFirst("jar").orElseThrow()
                    .run(new PrintWriter(out), new PrintWriter(new StringWriter()),
                            "--list", "--file", jar.toString());
            out.toString().lines().forEach(line -> System.out.println("    " + line));

            System.out.println("  MANIFEST.MF is what --main-class wrote, which is why");
            System.out.println("  `java -jar tool.jar` knows where to start.");

            System.out.println();
            System.out.println("--- named and unnamed modules ---");

            System.out.println("  this class's module: " + JarsAndModules.class.getModule());
            System.out.println("  is it named?         " + JarsAndModules.class.getModule().isNamed());
            System.out.println("  String's module:     " + String.class.getModule().getName());
            System.out.println("  List's module:       " + java.util.List.class.getModule().getName());

            // Anything on the classpath lands in the UNNAMED module, which
            // reads every other module and exports all its packages. That is
            // the compatibility mode, and it is where most code still lives.

            System.out.println();
            System.out.println("--- what a module adds over a jar ---");
            System.out.println();
            System.out.println("  module com.example.tool {");
            System.out.println("      requires java.logging;      what it needs");
            System.out.println("      exports com.example.tool;   what others may use");
            System.out.println("  }");
            System.out.println();
            System.out.println("  Three things a plain jar cannot do:");
            System.out.println();
            System.out.println("  1. STRONG ENCAPSULATION. A package not exported is");
            System.out.println("     inaccessible from outside, even by reflection. public no");
            System.out.println("     longer means universally reachable.");
            System.out.println();
            System.out.println("  2. DEPENDENCIES CHECKED AT STARTUP. A missing `requires` is");
            System.out.println("     an error when the JVM launches, not a");
            System.out.println("     NoClassDefFoundError an hour into production.");
            System.out.println();
            System.out.println("  3. NO SPLIT PACKAGES. Two modules cannot both contain the");
            System.out.println("     same package, which is exactly the silent shadowing that");
            System.out.println("     TheClasspath.java describes.");

            System.out.println();
            System.out.println("--- and why most projects still use the classpath ---");
            System.out.println();
            System.out.println("  Modularising means every dependency must be modular too, or");
            System.out.println("  be treated as an automatic module, which gets you the name");
            System.out.println("  checking without the encapsulation.");
            System.out.println();
            System.out.println("  The JDK itself is fully modular, and that is where most");
            System.out.println("  developers meet the system: an InaccessibleObjectException");
            System.out.println("  from a library reflecting into java.base, fixed with");
            System.out.println("  --add-opens. Knowing why that flag exists is the practical");
            System.out.println("  minimum, and it is this: java.base does not export its");
            System.out.println("  internals to you, and --add-opens overrides that.");

            System.out.println();
            System.out.println("--- the tools, for reference ---");
            System.out.println("  jar --list --file x.jar          see inside");
            System.out.println("  jar --describe-module --file x.jar   is it modular");
            System.out.println("  jdeps --list-deps x.jar          what it needs");
            System.out.println("  javadoc -d docs src/**/*.java    generate documentation");

        } finally {
            deleteRecursively(project);
            System.out.println();
            System.out.println("cleaned up: " + !Files.exists(project));
        }
    }

    static void run(String tool, String... toolArgs) {
        var err = new StringWriter();
        int rc = ToolProvider.findFirst(tool).orElseThrow()
                .run(new PrintWriter(new StringWriter()), new PrintWriter(err), toolArgs);
        if (rc != 0) {
            throw new IllegalStateException(tool + " failed: " + err);
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
