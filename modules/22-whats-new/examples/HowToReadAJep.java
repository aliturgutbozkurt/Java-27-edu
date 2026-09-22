// The skill this module actually exists to teach.
//
// Anything you read about Java, including this curriculum, goes out of date.
// JEPs do not, because they ARE the specification, and they record when they
// changed and why.

public class HowToReadAJep {

    public static void main(String[] args) {
        System.out.println("--- where they live ---");
        System.out.println();
        System.out.println("  https://openjdk.org/jeps/0          the index");
        System.out.println("  https://openjdk.org/jeps/512        one JEP, by number");
        System.out.println("  https://openjdk.org/projects/jdk/27 what shipped in a release");

        System.out.println();
        System.out.println("--- the fields that matter, in order of usefulness ---");
        System.out.println();
        System.out.println("  Status        Draft, Candidate, Proposed to Target, Integrated,");
        System.out.println("                Closed/Delivered. Only Delivered means it shipped.");
        System.out.println();
        System.out.println("  Release       which JDK version. A JEP with no Release field");
        System.out.println("                has not been scheduled for anything.");
        System.out.println();
        System.out.println("  Summary       one paragraph. Often all you need.");
        System.out.println();
        System.out.println("  Motivation    WHY the feature exists. This is the section people");
        System.out.println("                skip and the one worth reading. It usually names");
        System.out.println("                the problem better than any tutorial will.");
        System.out.println();
        System.out.println("  Alternatives  what was considered and rejected. Reading this");
        System.out.println("                answers most \"why didn't they just...\" questions.");

        System.out.println();
        System.out.println("--- the vocabulary ---");
        System.out.println();
        System.out.println("  PREVIEW       a complete, specified, supported feature that is");
        System.out.println("                not yet permanent. Needs --enable-preview. May");
        System.out.println("                change or be withdrawn in the next release.");
        System.out.println();
        System.out.println("  INCUBATOR     an API, not a language feature, in a jdk.incubator");
        System.out.println("                module. Needs --add-modules. Less settled than");
        System.out.println("                preview, and the Vector API has been one for");
        System.out.println("                twelve releases.");
        System.out.println();
        System.out.println("  EXPERIMENTAL  a JVM feature behind -XX:+UnlockExperimentalVMOptions.");
        System.out.println();
        System.out.println("  FINAL         permanent. It will not be removed, and code using");
        System.out.println("                it will compile in every future release.");

        System.out.println();
        System.out.println("--- reading a preview round as a signal ---");
        System.out.println();
        System.out.println("  Structured Concurrency is on its SEVENTH preview (JEP 533).");
        System.out.println("  Pattern matching for switch took four. Records took two.");
        System.out.println();
        System.out.println("  A high number means the design is still being argued about, and");
        System.out.println("  the API has usually changed between rounds. That is exactly what");
        System.out.println("  happened to structured concurrency: code written against the");
        System.out.println("  earlier previews does not compile against this one.");
        System.out.println();
        System.out.println("  So the round number is a practical warning, not trivia. A second");
        System.out.println("  preview is close to settled. A seventh is not.");

        System.out.println();
        System.out.println("--- the questions to ask about any feature ---");
        System.out.println();
        System.out.println("  1. Is it Final? If not, what preview round, and how many were");
        System.out.println("     there before?");
        System.out.println("  2. Which release delivered it, and is that release an LTS?");
        System.out.println("  3. Can the minimum version I must support actually use it?");
        System.out.println();
        System.out.println("  Answer those three and you will never ship code that does not");
        System.out.println("  compile on a colleague's machine.");

        System.out.println();
        System.out.println("--- and the honest note about this file ---");
        System.out.println();
        System.out.println("  Everything above was true when written, on a JDK reporting");
        System.out.println("  version " + Runtime.version().feature() + ". The JEP index outlives any document that");
        System.out.println("  summarises it, including this one. Check the source.");
    }
}
