// Reference solution for Homework 20.
//
// Builds a real two-package project in a temporary directory, compiles it,
// jars it, runs it, then reproduces three startup failures. Cleans up on exit.

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.Comparator;
import java.util.spi.ToolProvider;

public class ProjectBuilder {

    public static void main(String[] args) throws Exception {
        Path project = Files.createTempDirectory("java27-hw20");
        try {
            Path classes = partOne(project);
            System.out.println();
            Path jar = partTwo(project, classes);
            System.out.println();
            partThree(classes);
            System.out.println();
            partFour(jar);
        } finally {
            deleteRecursively(project);
            System.out.println();
            System.out.println("cleaned up: " + !Files.exists(project));
        }
    }

    // ---------------------------------------------------------------- part 1

    private static Path partOne(Path project) throws Exception {
        System.out.println("--- a two-package project ---");

        Path src = project.resolve("src");
        Path model = src.resolve("com/example/model");
        Path app = src.resolve("com/example/app");
        Files.createDirectories(model);
        Files.createDirectories(app);

        // The directory path mirrors the package statement exactly. That is not
        // a convention I chose; javac resolves com.example.model.Item by
        // looking for com/example/model/Item.java relative to the source root,
        // and the JVM does the same for the .class file at runtime.
        Files.writeString(model.resolve("Item.java"), """
                package com.example.model;

                public record Item(String name, int quantity) {

                    // public: part of the API other packages use.
                    public String describe() {
                        return format(name, quantity);
                    }

                    // package-private: a helper shared within com.example.model
                    // and invisible outside it. This is the access level people
                    // forget exists, and it is the right one here because no
                    // other package has any business calling it.
                    static String format(String name, int quantity) {
                        return quantity + " x " + name;
                    }
                }
                """);

        Files.writeString(app.resolve("Inventory.java"), """
                package com.example.app;

                import com.example.model.Item;
                import java.util.List;

                public class Inventory {
                    public static void main(String[] args) {
                        List<Item> items = List.of(
                                new Item("bolt", 40),
                                new Item("nut", 120));

                        for (Item item : items) {
                            System.out.println("    " + item.describe());
                        }

                        // This line would not compile, and I checked:
                        //
                        //     Item.format("x", 1);
                        //
                        //     error: format(String,int) is not public in Item;
                        //            cannot be accessed from outside package
                        //
                        // Package-private is enforced by the compiler, unlike
                        // Python's leading underscore which is only a request.
                    }
                }
                """);

        Path classes = project.resolve("classes");
        int rc = run("javac", "-d", classes.toString(),
                model.resolve("Item.java").toString(),
                app.resolve("Inventory.java").toString());

        System.out.println("  javac exit code: " + rc);
        System.out.println("  class files:");
        try (var walk = Files.walk(classes)) {
            walk.filter(Files::isRegularFile)
                    .map(classes::relativize)
                    .sorted()
                    .forEach(p -> System.out.println("    " + p));
        }

        System.out.println("  running via a classloader over the output directory,");
        System.out.println("  which is what -cp does:");
        try (var loader = new URLClassLoader(new URL[]{classes.toUri().toURL()})) {
            Class<?> main = loader.loadClass("com.example.app.Inventory");
            main.getMethod("main", String[].class).invoke(null, (Object) new String[0]);
        }

        return classes;
    }

    // ---------------------------------------------------------------- part 2

    private static Path partTwo(Path project, Path classes) throws Exception {
        System.out.println("--- packaging it ---");

        Path jar = project.resolve("inventory.jar");
        run("jar", "--create", "--file", jar.toString(),
                "--main-class", "com.example.app.Inventory",
                "-C", classes.toString(), ".");

        System.out.println("  created inventory.jar (" + Files.size(jar) + " bytes)");
        System.out.println("  contents:");

        var listing = new StringWriter();
        ToolProvider.findFirst("jar").orElseThrow().run(
                new PrintWriter(listing), new PrintWriter(new StringWriter()),
                "--list", "--file", jar.toString());
        listing.toString().lines().forEach(l -> System.out.println("    " + l));

        System.out.println();
        System.out.println("  WHAT --main-class DID, in my own words:");
        System.out.println();
        System.out.println("  It wrote a Main-Class entry into META-INF/MANIFEST.MF. That");
        System.out.println("  entry is the only reason `java -jar inventory.jar` knows which");
        System.out.println("  of the classes inside to start. Without it the same jar is");
        System.out.println("  still perfectly usable, but only on a classpath with the main");
        System.out.println("  class named explicitly.");

        var describe = new StringWriter();
        ToolProvider.findFirst("jar").orElseThrow().run(
                new PrintWriter(describe), new PrintWriter(new StringWriter()),
                "--describe-module", "--file", jar.toString());
        System.out.println();
        System.out.println("  jar --describe-module says:");
        describe.toString().lines().limit(3).forEach(l -> System.out.println("    " + l.strip()));
        System.out.println("  an AUTOMATIC module: no module-info, so the name was derived");
        System.out.println("  from the filename and every package is exported. That is the");
        System.out.println("  compatibility path, not real modularity.");

        return jar;
    }

    // ---------------------------------------------------------------- part 3

    private static void partThree(Path classes) throws Exception {
        System.out.println("--- three startup failures ---");

        // FAILURE 1: ClassNotFoundException.
        System.out.println();
        System.out.println("  1. asking for a class that was never built");
        try {
            Class.forName("com.example.app.DoesNotExist");
        } catch (ClassNotFoundException e) {
            System.out.println("     " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
        System.out.println("     A CHECKED exception. Something looked a class up BY NAME at");
        System.out.println("     runtime. The compiler never knew about it, so it could not");
        System.out.println("     have warned me. Typical causes: reflection, a driver name in");
        System.out.println("     a config file, a framework instantiating a configured class.");

        // FAILURE 2: the class exists but is not on THIS loader's path.
        System.out.println();
        System.out.println("  2. the class exists, but not where this loader is looking");
        try (var empty = new URLClassLoader(new URL[0], null)) {
            empty.loadClass("com.example.app.Inventory");
        } catch (ClassNotFoundException e) {
            System.out.println("     " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
        System.out.println("     Identical symptom, completely different cause. The class was");
        System.out.println("     compiled successfully a moment ago and sits on disk. The");
        System.out.println("     loader simply has no path containing it.");
        System.out.println("     This is what a wrong -cp looks like from the inside.");

        // FAILURE 3: NoClassDefFoundError from a failed initialiser.
        System.out.println();
        System.out.println("  3. a static initialiser that threw");
        try {
            new Fragile();
        } catch (Throwable t) {
            System.out.println("     first attempt:  " + t.getClass().getSimpleName()
                    + ", caused by " + t.getCause());
        }
        try {
            new Fragile();
        } catch (Throwable t) {
            System.out.println("     second attempt: " + t.getClass().getSimpleName()
                    + ": " + t.getMessage());
        }

        System.out.println();
        System.out.println("     THE DIAGNOSTIC LESSON, in my own words:");
        System.out.println();
        System.out.println("     The real cause appears EXACTLY ONCE, on the first touch of");
        System.out.println("     the class. After that the JVM marks the class as erroneous");
        System.out.println("     and every later attempt gets NoClassDefFoundError with no");
        System.out.println("     cause attached at all.");
        System.out.println();
        System.out.println("     In a server that starts many components, the first failure");
        System.out.println("     is often buried thousands of lines above the error anyone");
        System.out.println("     notices, and it usually looks unrelated. So the rule is:");
        System.out.println("     NoClassDefFoundError for a class you KNOW is present means");
        System.out.println("     scroll up, do not go looking for a missing jar.");
    }

    // ---------------------------------------------------------------- part 4

    private static void partFour(Path jar) throws Exception {
        System.out.println("--- where classes come from ---");

        System.out.println("  String's loader:      " + String.class.getClassLoader()
                + "   (null = bootstrap, built into the JVM)");
        System.out.println("  this class's loader:  " + ProjectBuilder.class.getClassLoader());
        System.out.println("  String's module:      " + String.class.getModule().getName());
        System.out.println("  this class's module:  " + ProjectBuilder.class.getModule()
                + ", named=" + ProjectBuilder.class.getModule().isNamed());

        System.out.println();
        System.out.println("  Anything loaded from the classpath lands in the UNNAMED module,");
        System.out.println("  which reads every other module and exports everything it has.");
        System.out.println("  That is the compatibility mode, and it is where nearly all");
        System.out.println("  application code still lives.");

        System.out.println();
        System.out.println("  WHY --add-opens EXISTS, in my own words:");
        System.out.println();
        System.out.println("  The JDK itself is fully modular. java.base does not export its");
        System.out.println("  internal packages, and a module's non-exported packages are");
        System.out.println("  closed even to reflection. A library that reaches into");
        System.out.println("  java.lang internals therefore gets an");
        System.out.println("  InaccessibleObjectException rather than quietly working.");
        System.out.println();
        System.out.println("  --add-opens punches a hole in that specific package for that");
        System.out.println("  specific reader. It is sometimes necessary and never free: it");
        System.out.println("  is a documented decision to bypass an encapsulation boundary");
        System.out.println("  the JDK put there on purpose.");
    }

    // ---------------------------------------------------------------- helpers

    static class Fragile {
        static final int VALUE = boom();

        static int boom() {
            throw new IllegalStateException("configuration missing at startup");
        }
    }

    static int run(String tool, String... toolArgs) {
        var err = new StringWriter();
        int rc = ToolProvider.findFirst(tool).orElseThrow()
                .run(new PrintWriter(new StringWriter()), new PrintWriter(err), toolArgs);
        if (rc != 0) {
            throw new IllegalStateException(tool + " failed: " + err);
        }
        return rc;
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
