// What JDK 26 and 27 actually delivered.
//
// Every entry below comes from the OpenJDK release pages, not from memory:
//
//   https://openjdk.org/projects/jdk/26/
//   https://openjdk.org/projects/jdk/27/
//
// If you are reading this later, check those pages rather than trusting this
// file. That habit is the real subject of this module.

public class WhatChanged {

    record Jep(int number, String title, String status) { }

    public static void main(String[] args) {
        System.out.println("  running on Java " + Runtime.version().feature());
        System.out.println();

        System.out.println("--- JDK 26, GA 17 March 2026 ---");
        print(
                new Jep(500, "Prepare to Make Final Mean Final", "Final"),
                new Jep(504, "Remove the Applet API", "Final"),
                new Jep(516, "Ahead-of-Time Object Caching with Any GC", "Final"),
                new Jep(517, "HTTP/3 for the HTTP Client API", "Final"),
                new Jep(522, "G1 GC: Improve Throughput by Reducing Synchronization", "Final"),
                new Jep(524, "PEM Encodings of Cryptographic Objects", "Second Preview"),
                new Jep(525, "Structured Concurrency", "Sixth Preview"),
                new Jep(526, "Lazy Constants", "Second Preview"),
                new Jep(529, "Vector API", "Eleventh Incubator"),
                new Jep(530, "Primitive Types in Patterns, instanceof, and switch", "Fourth Preview"));

        System.out.println();
        System.out.println("--- JDK 27, GA 15 September 2026 ---");
        print(
                new Jep(523, "Make G1 the Default Garbage Collector in All Environments", "Final"),
                new Jep(527, "Post-Quantum Hybrid Key Exchange for TLS 1.3", "Final"),
                new Jep(531, "Lazy Constants", "Third Preview"),
                new Jep(532, "Primitive Types in Patterns, instanceof, and switch", "Fifth Preview"),
                new Jep(533, "Structured Concurrency", "Seventh Preview"),
                new Jep(534, "Compact Object Headers by Default", "Final"),
                new Jep(536, "JFR In-Process Data Redaction", "Final"),
                new Jep(537, "Vector API", "Twelfth Incubator"),
                new Jep(538, "PEM Encodings of Cryptographic Objects", "Third Preview"));

        System.out.println();
        System.out.println("--- read those two lists again ---");
        System.out.println();
        System.out.println("  Nothing in JDK 27 changes how you write Java.");
        System.out.println();
        System.out.println("  Four of its five finalised JEPs are runtime and security work:");
        System.out.println("  a garbage collector default, smaller object headers, TLS key");
        System.out.println("  exchange, and flight recorder redaction. You benefit from all");
        System.out.println("  four without editing a line.");
        System.out.println();
        System.out.println("  Everything language-shaped is still in preview, some of it for");
        System.out.println("  the seventh consecutive release.");

        System.out.println();
        System.out.println("--- which is why this curriculum is shaped as it is ---");
        System.out.println();
        System.out.println("  Module 01 said it: JDK 27 is the runtime and toolchain, and the");
        System.out.println("  language you learned is the stable Java accumulated through");
        System.out.println("  JDK 25. Twenty-one modules later, the JEP lists confirm it.");
        System.out.println();
        System.out.println("  A course promising \"the new features of Java 27\" would have");
        System.out.println("  had to either pad the list or teach preview features as though");
        System.out.println("  they were settled. Both are worse than saying so plainly.");

        System.out.println();
        System.out.println("--- the two in JDK 26 worth knowing about ---");
        System.out.println();
        System.out.println("  JEP 517, HTTP/3 for the HTTP Client API. The built-in");
        System.out.println("  HttpClient speaks HTTP/3 now. A genuine capability, and one");
        System.out.println("  you get by changing a version request rather than a dependency.");
        System.out.println();
        System.out.println("  JEP 500, Prepare to Make Final Mean Final. Warns when code");
        System.out.println("  mutates a final field by reflection, ahead of forbidding it.");
        System.out.println("  Worth knowing because some serialisation and mocking libraries");
        System.out.println("  do exactly that, and the warning is aimed at them.");
    }

    static void print(Jep... jeps) {
        for (Jep jep : jeps) {
            System.out.printf("  JEP %-4d %-58s %s%n", jep.number(), jep.title(), jep.status());
        }
    }
}
