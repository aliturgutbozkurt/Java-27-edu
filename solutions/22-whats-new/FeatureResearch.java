// Reference solution for Homework 22.
//
// A worked research exercise. The point is not the conclusions, it is the
// method: every claim below states where it came from, and the three-question
// checklist is applied mechanically rather than by feel.

public class FeatureResearch {

    record Finding(String feature, int jep, String status, String release,
                   boolean lts, String verdict) { }

    public static void main(String[] args) {
        System.out.println("  researched on a JDK reporting version "
                + Runtime.version().feature());
        System.out.println();

        partOne();
        System.out.println();
        partTwo();
        System.out.println();
        partThree();
        System.out.println();
        partFour();
    }

    // ---------------------------------------------------------------- part 1

    private static void partOne() {
        System.out.println("--- three features, researched ---");
        System.out.println();

        // Every row below was read off the release pages, not recalled:
        //   https://openjdk.org/projects/jdk/25/
        //   https://openjdk.org/projects/jdk/27/
        Finding[] findings = {
            new Finding("Scoped values", 506, "Final", "JDK 25", true,
                    "USE IT"),
            new Finding("Structured concurrency", 533, "Seventh Preview", "JDK 27", false,
                    "DO NOT SHIP"),
            new Finding("Primitive type patterns", 532, "Fifth Preview", "JDK 27", false,
                    "DO NOT SHIP"),
        };

        System.out.printf("  %-26s %-5s %-16s %-8s %-5s %s%n",
                "FEATURE", "JEP", "STATUS", "RELEASE", "LTS", "VERDICT");
        for (Finding f : findings) {
            System.out.printf("  %-26s %-5d %-16s %-8s %-5s %s%n",
                    f.feature(), f.jep(), f.status(), f.release(),
                    f.lts() ? "yes" : "no", f.verdict());
        }

        System.out.println();
        System.out.println("  sources:");
        System.out.println("    https://openjdk.org/jeps/506");
        System.out.println("    https://openjdk.org/jeps/533");
        System.out.println("    https://openjdk.org/jeps/532");
    }

    // ---------------------------------------------------------------- part 2

    private static void partTwo() {
        System.out.println("--- the three questions, applied ---");
        System.out.println();

        decide("Scoped values", "Final", 0, 25, true, 21);
        System.out.println();
        decide("Structured concurrency", "Preview", 7, 27, false, 21);

        // WHY QUESTION 3 IS THE ONE THAT ACTUALLY DECIDES, in my own words:
        //
        // The first two questions are about the feature. The third is about my
        // situation, and it overrules both.
        //
        // Scoped values are final and shipped in an LTS, which sounds like a
        // clear yes. But if the minimum version I must support is JDK 21, I
        // still cannot use them, because a class file compiled for 25 will not
        // load on 21 and the API simply is not there.
        //
        // So a feature being finalised tells me it is SAFE. It does not tell me
        // it is AVAILABLE. Those are different questions and I have watched
        // people conflate them and ship a library nobody could depend on.
    }

    // Takes feature versions as ints, not strings. Comparing "JDK 21" with
    // "JDK 25" as text happens to work; comparing "JDK 9" with "JDK 25" does
    // not, because "9" sorts after "2". That is the same class of mistake as
    // parsing java.version, which part four is about.
    private static void decide(String feature, String status, int previewRound,
                               int releaseVersion, boolean lts, int minimumSupported) {
        System.out.println("  " + feature);
        System.out.println("    1. Final?        " + status
                + (previewRound > 0 ? " (round " + previewRound + " of an unknown total)" : ""));
        System.out.println("    2. Which release? JDK " + releaseVersion + ", LTS: " + lts);
        System.out.println("    3. Minimum I support? JDK " + minimumSupported);

        String verdict;
        if (!"Final".equals(status)) {
            verdict = "NO. Preview class files are rejected by other releases even with "
                    + "--enable-preview, so every JDK upgrade may break the build.";
        } else if (minimumSupported < releaseVersion) {
            verdict = "NOT YET. It is safe, but it did not exist in JDK " + minimumSupported
                    + ", so I cannot compile against it while supporting that version.";
        } else {
            verdict = "YES.";
        }
        System.out.println("    -> " + verdict);
    }

    // ---------------------------------------------------------------- part 3

    private static void partThree() {
        System.out.println("--- what the round number told me ---");
        System.out.println();
        System.out.println("  Structured concurrency history, read from the History section");
        System.out.println("  of https://openjdk.org/jeps/533 itself:");
        System.out.println();
        System.out.println("    JDK 19  JEP 428   INCUBATOR");
        System.out.println("    JDK 20  JEP 437   incubator again");
        System.out.println("    JDK 21  JEP 453   first preview");
        System.out.println("    JDK 22  JEP 462   second");
        System.out.println("    JDK 23  JEP 480   third");
        System.out.println("    JDK 24  JEP 499   fourth");
        System.out.println("    JDK 25  JEP 505   fifth, with API changes");
        System.out.println("    JDK 26  JEP 525   sixth");
        System.out.println("    JDK 27  JEP 533   seventh");
        System.out.println();
        System.out.println("  I had assumed the previews started at JDK 21. Reading the JEP");
        System.out.println("  showed two INCUBATOR rounds before that, so this has been in");
        System.out.println("  development across NINE releases, not seven. Checking the");
        System.out.println("  source rather than trusting the count was the whole exercise.");
        System.out.println();
        System.out.println("  Nine rounds, with a NEW JEP NUMBER each");
        System.out.println("  time. That last detail is the signal I would otherwise have");
        System.out.println("  missed: a re-preview under the same number would mean 'no");
        System.out.println("  change, just more soak time'. A new number means the proposal");
        System.out.println("  itself was revised.");
        System.out.println();
        System.out.println("  And it was. Code written against the JDK 21 version does not");
        System.out.println("  compile on 27: the factory method, the scope types and the");
        System.out.println("  result accessors all changed.");
        System.out.println();
        System.out.println("  COMPARE with records: JEP 359 previewed in 14, JEP 384 in 15,");
        System.out.println("  final as JEP 395 in 16. Two rounds, then done.");
        System.out.println();
        System.out.println("  So the heuristic I am taking away: two rounds means nearly");
        System.out.println("  settled, and anything past four means the design is genuinely");
        System.out.println("  contested and I should expect my code to need rewriting.");
    }

    // ---------------------------------------------------------------- part 4

    private static void partFour() {
        System.out.println("--- the version check, done properly ---");
        System.out.println();

        Runtime.Version version = Runtime.version();
        System.out.println("  Runtime.version()            = " + version);
        System.out.println("  .feature()                   = " + version.feature());
        System.out.println("  java.version property        = " + System.getProperty("java.version"));

        System.out.println();
        int required = 25;
        boolean ok = version.feature() >= required;
        System.out.println("  needs at least Java " + required + ": " + (ok ? "satisfied" : "NOT satisfied"));

        // WHY NOT TO PARSE java.version, in my own words:
        //
        // The string format changed in Java 9. Before that it was 1.8.0_301,
        // where the meaningful number was the SECOND component. After, it is
        // 9.0.1, where the meaningful number is the first.
        //
        // Every piece of code that did version.startsWith("1.") or split on
        // dots and took index 1 broke at once, across the entire ecosystem, and
        // some of it broke silently by concluding it was running on Java 0.
        //
        // Runtime.version() returns a structured object with a feature() method
        // that means the same thing on every release, including ones that do
        // not exist yet. It is not just tidier, it is the difference between
        // code that survives the next format change and code that does not.
    }
}
