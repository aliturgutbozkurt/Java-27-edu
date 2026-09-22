// The harness in the previous file is about forty lines and genuinely works.
// So why does every real project use a framework?
//
// This file answers that by showing what the micro harness cannot do, using
// only the JDK.

import java.util.ArrayList;
import java.util.List;

public class WhatJUnitAdds {

    public static void main(String[] args) {
        System.out.println("--- 1. discovery ---");
        System.out.println();
        System.out.println("  The micro harness needs every test registered by hand:");
        System.out.println();
        System.out.println("      tests.check(\"addition works\", () -> 2 + 2 == 4);");
        System.out.println();
        System.out.println("  Forget a line and the test silently does not exist. Nothing");
        System.out.println("  reports a test that was never registered.");
        System.out.println();
        System.out.println("  JUnit finds @Test methods by scanning. The failure mode of");
        System.out.println("  forgetting to call something disappears entirely.");

        System.out.println();
        System.out.println("--- 2. isolation and lifecycle ---");
        System.out.println();
        System.out.println("  Every @Test gets a fresh instance of the test class, so state");
        System.out.println("  cannot leak between tests. @BeforeEach and @AfterEach run");
        System.out.println("  around each one.");
        System.out.println();
        System.out.println("  Here is what leaking looks like without that:");

        // Shared mutable state across "tests", which is exactly what a real
        // framework prevents by construction.
        List<String> shared = new ArrayList<>();

        shared.add("left behind by test one");
        System.out.println("    test one added an item; list size is now " + shared.size());
        System.out.println("    test two starts with size " + shared.size() + ", not 0");
        System.out.println();
        System.out.println("  A test that passes only because a previous one ran is worse");
        System.out.println("  than no test, because it fails when someone reorders them.");

        System.out.println();
        System.out.println("--- 3. assertion messages that do the work ---");
        System.out.println();

        List<String> expected = List.of("a", "b", "c");
        List<String> actual = List.of("a", "x", "c");

        System.out.println("  the micro harness would say:");
        System.out.println("    expected <" + expected + "> but was <" + actual + ">");
        System.out.println();
        System.out.println("  assertEquals in a real framework says the same, and");
        System.out.println("  assertThat-style libraries point at the differing element.");
        System.out.println("  On a ten-field object that difference is the whole debugging");
        System.out.println("  session.");

        System.out.println();
        System.out.println("--- 4. the things there is no room to hand-roll ---");
        System.out.println();
        System.out.println("  parameterised tests      one test, twenty input rows");
        System.out.println("  assertThrows             assert that something fails, and how");
        System.out.println("  timeouts                 fail a hanging test instead of hanging");
        System.out.println("  tagging and filtering    run the fast ones on every commit");
        System.out.println("  IDE and build support    a red bar, and a rerun-failed button");
        System.out.println("  standard report format   which is how CI knows what broke");

        System.out.println();
        System.out.println("--- what a JUnit test looks like ---");
        System.out.println();
        System.out.println("      import org.junit.jupiter.api.Test;");
        System.out.println("      import static org.junit.jupiter.api.Assertions.*;");
        System.out.println();
        System.out.println("      class CalculatorTest {");
        System.out.println("          @Test");
        System.out.println("          void addsTwoNumbers() {");
        System.out.println("              assertEquals(4, Calculator.add(2, 2));");
        System.out.println("          }");
        System.out.println();
        System.out.println("          @Test");
        System.out.println("          void rejectsNegativeInput() {");
        System.out.println("              assertThrows(IllegalArgumentException.class,");
        System.out.println("                      () -> Calculator.sqrt(-1));");
        System.out.println("          }");
        System.out.println("      }");
        System.out.println();
        System.out.println("  It is not shown running here because this curriculum has a");
        System.out.println("  zero-dependency rule, and JUnit is a dependency. Adding it");
        System.out.println("  means a build tool, which is the whole reason Module 01 chose");
        System.out.println("  not to have one.");

        System.out.println();
        System.out.println("--- what to actually do ---");
        System.out.println();
        System.out.println("  On a real project: add JUnit 5 through Maven or Gradle on day");
        System.out.println("  one. It is the default everywhere and nothing about it is");
        System.out.println("  controversial.");
        System.out.println();
        System.out.println("  What carries over from this module is not the harness. It is");
        System.out.println("  knowing that a test is just a method that checks something and");
        System.out.println("  reports, that isolation between tests is a deliberate feature,");
        System.out.println("  and that `assert` is not a testing tool.");
    }
}
