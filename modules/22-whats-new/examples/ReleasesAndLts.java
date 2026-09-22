// Java ships every six months. Understanding which releases matter, and why,
// is worth more than any individual feature.
//
// Sources for every date and designation below:
//   https://openjdk.org/projects/jdk/
//   https://www.oracle.com/java/technologies/java-se-support-roadmap.html

public class ReleasesAndLts {

    public static void main(String[] args) {
        System.out.println("  this JVM: Java " + Runtime.version().feature()
                + "  (" + Runtime.version() + ")");

        System.out.println();
        System.out.println("--- the cadence ---");
        System.out.println();
        System.out.println("  A feature release every six months, in March and September,");
        System.out.println("  on a fixed date whether or not anything is ready. Features");
        System.out.println("  that miss the train wait for the next one rather than");
        System.out.println("  delaying the release.");
        System.out.println();
        System.out.println("  Before 2017 releases were feature-driven and arrived every");
        System.out.println("  two to three years. Java 7 to 8 took nearly three years, and");
        System.out.println("  anything that slipped waited that long again.");

        System.out.println();
        System.out.println("--- LTS, and why it is the number that matters ---");
        System.out.println();
        System.out.println("    Java 17    LTS    September 2021");
        System.out.println("    Java 21    LTS    September 2023");
        System.out.println("    Java 25    LTS    September 2025");
        System.out.println("    Java 26           March 2026        <- six months of updates");
        System.out.println("    Java 27           September 2026    <- six months of updates");
        System.out.println("    Java 29    LTS    expected September 2027");
        System.out.println();
        System.out.println("  A non-LTS release gets security patches until the next release");
        System.out.println("  supersedes it. Six months, then nothing.");
        System.out.println();
        System.out.println("  An LTS gets years. That is why nearly every production system");
        System.out.println("  runs an LTS, and why \"what is new in Java 27\" is the wrong");
        System.out.println("  question for most teams. The right one is \"what is new since");
        System.out.println("  Java 21\", which is where they actually are.");

        System.out.println();
        System.out.println("--- what that means for you ---");
        System.out.println();
        System.out.println("  LEARNING:     use the newest release. Features finalise here");
        System.out.println("                first, and everything you learn carries forward.");
        System.out.println();
        System.out.println("  PRODUCTION:   use the current LTS, which today is 25.");
        System.out.println();
        System.out.println("  LIBRARIES:    target the oldest LTS you must support. A library");
        System.out.println("                compiled for 27 cannot be used by anyone on 21.");

        System.out.println();
        System.out.println("--- the accumulation nobody notices ---");
        System.out.println();
        System.out.println("  Any single release looks thin. The gap between LTS releases");
        System.out.println("  does not. Between Java 21 and Java 25:");
        System.out.println();
        System.out.println("    compact source files and instance main methods   JEP 512");
        System.out.println("    module import declarations                       JEP 511");
        System.out.println("    flexible constructor bodies                      JEP 513");
        System.out.println("    scoped values                                    JEP 506");
        System.out.println("    stream gatherers                                 JEP 485");
        System.out.println("    synchronize virtual threads without pinning      JEP 491");
        System.out.println();
        System.out.println("  Every one of those appears in this curriculum, and all six");
        System.out.println("  arrived in releases that individually looked like nothing much.");

        System.out.println();
        System.out.println("--- checking what you are actually on ---");
        System.out.println();
        System.out.println("  Runtime.version().feature() = " + Runtime.version().feature());
        System.out.println("  java.version property       = " + System.getProperty("java.version"));
        System.out.println("  java.vendor                 = " + System.getProperty("java.vendor"));
        System.out.println();
        System.out.println("  Runtime.version() is the one to prefer in code. Parsing the");
        System.out.println("  java.version string broke for everyone when Java 9 changed its");
        System.out.println("  format from 1.8.0_301 to 9.0.1.");
    }
}
