// Reference solution for Homework 13.

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.*;

public class RulesEngine {

    record Applicant(String name, int age, int creditScore, boolean employed) { }

    private static final List<Applicant> APPLICANTS = List.of(
            new Applicant("ada", 36, 780, true),
            new Applicant("grace", 45, 610, true),
            new Applicant("alan", 17, 800, false),
            new Applicant("edsger", 52, 550, true),
            new Applicant("barbara", 29, 720, false));

    public static void main(String[] args) {
        partOne();
        System.out.println();
        partTwo();
        System.out.println();
        partThree();
    }

    // ---------------------------------------------------------------- part 1

    private static void partOne() {
        System.out.println("--- composed predicates ---");

        // Each rule is one named Predicate. Naming them is the point: the
        // composed rule below reads as a sentence instead of as a boolean
        // expression that needs decoding.
        Predicate<Applicant> isAdult = a -> a.age() >= 18;
        Predicate<Applicant> hasGoodCredit = a -> a.creditScore() >= 700;
        Predicate<Applicant> isEmployed = Applicant::employed;

        Predicate<Applicant> eligible = isAdult.and(hasGoodCredit).and(isEmployed);
        Predicate<Applicant> needsReview = isAdult.and(hasGoodCredit.negate());

        for (Applicant a : APPLICANTS) {
            System.out.printf("  %-9s eligible=%-5s needsReview=%-5s minor=%s%n",
                    a.name(), eligible.test(a), needsReview.test(a), isAdult.negate().test(a));
        }

        // Composition beats one big lambda because each clause can be named,
        // tested and reused independently. `isAdult.and(hasGoodCredit)` appears
        // in both rules above and is written once.
    }

    // ---------------------------------------------------------------- part 2

    private static void partTwo() {
        System.out.println("--- all four method reference forms ---");

        // 1. STATIC. Integer.parseInt is a static method taking the argument.
        Function<String, Integer> parse = Integer::parseInt;
        System.out.println("  static:   Integer::parseInt(\"42\") = " + parse.apply("42"));

        // 2. CONSTRUCTOR. Builds a new object each call.
        Supplier<List<String>> newList = ArrayList::new;
        System.out.println("  ctor:     ArrayList::new = " + newList.get());

        // 3. BOUND INSTANCE. The receiver is `banner`, fixed at this line. The
        //    function's argument becomes concat's argument.
        String banner = ">> ";
        Function<String, String> decorate = banner::concat;
        System.out.println("  bound:    banner::concat(\"ada\") = " + decorate.apply("ada"));

        // 4. UNBOUND INSTANCE. No receiver yet. The function's FIRST argument
        //    becomes the receiver, which is why a one-argument Function can
        //    target a zero-argument method.
        Function<Applicant, String> nameOf = Applicant::name;
        System.out.println("  unbound:  Applicant::name = " + nameOf.apply(APPLICANTS.get(0)));

        // THE DIFFERENCE BETWEEN 3 AND 4, in my own words:
        //
        // Both are written Something::method, and that is where the confusion
        // comes from. The distinguishing detail is what sits to the LEFT of the
        // colons.
        //
        // In `banner::concat`, banner is a VARIABLE. The object is chosen now,
        // once, and every later call concatenates onto that same string. The
        // function's argument is passed to concat as its parameter.
        //
        // In `Applicant::name`, Applicant is a TYPE. No object has been chosen,
        // so one has to arrive with each call, and Java takes it from the first
        // argument. The receiver varies per call.
        //
        // The observable consequence is arity. A bound reference to a
        // one-argument method needs a one-argument functional interface; an
        // unbound reference to the same method needs a two-argument one,
        // because the receiver occupies the first slot.
        BiFunction<String, String, String> unboundConcat = String::concat;
        System.out.println("  bound again:   " + decorate.apply("grace"));
        System.out.println("  unbound equiv: " + unboundConcat.apply(">> ", "grace"));

        System.out.println();
        System.out.println("--- function composition ---");

        Function<Applicant, Integer> score = Applicant::creditScore;
        Function<Integer, String> band = s -> s >= 700 ? "A" : s >= 600 ? "B" : "C";

        // andThen: run `score` first, then feed the result to `band`.
        Function<Applicant, String> scoreBand = score.andThen(band);

        for (Applicant a : APPLICANTS) {
            System.out.println("  " + a.name() + " -> " + scoreBand.apply(a));
        }

        // compose would be the other order, which does not type-check here:
        // band.compose(score) is the same pipeline written backwards and IS
        // valid; score.compose(band) is not, because band's output is a String
        // and score expects an Applicant. Getting them the wrong way round is
        // usually a compile error, which is fortunate, but not always, and when
        // both types happen to line up the bug is silent.
        Function<Applicant, String> sameThing = band.compose(score);
        System.out.println("  band.compose(score) gives the same: "
                + sameThing.apply(APPLICANTS.get(0)));
    }

    // ---------------------------------------------------------------- part 3

    private static void partThree() {
        System.out.println("--- the capture rule ---");

        // THE VERSION THAT DOES NOT COMPILE:
        //
        //     int approved = 0;
        //     APPLICANTS.forEach(a -> { if (ok(a)) approved++; });
        //
        //     error: local variables referenced from a lambda expression must
        //            be final or effectively final
        //
        // Why: the lambda holds a COPY of approved's value, because it may run
        // after this method's frame is gone. Incrementing a copy would be
        // meaningless, so Java forbids it rather than letting me write something
        // that looks like it works.

        // THE ARRAY TRICK. It compiles, because the VARIABLE never changes;
        // only the object it points at does.
        int[] box = {0};
        Predicate<Applicant> ok = a -> a.age() >= 18 && a.creditScore() >= 700;
        APPLICANTS.forEach(a -> {
            if (ok.test(a)) {
                box[0]++;
            }
        });
        System.out.println("  counted with the array trick: " + box[0]);

        // WHY IT IS A SMELL, in my own words:
        //
        // The rule exists to stop a lambda mutating something outside itself.
        // The array does not satisfy the rule, it evades it: the compiler is
        // checking the variable, and I moved the mutable state one level down
        // where it is not looking.
        //
        // What it actually tells me is that I am using forEach to accumulate,
        // and accumulation is not what forEach is for. The loop is building a
        // value, so it should RETURN a value rather than poke at one.

        // THE HONEST VERSIONS.

        // Without streams: an ordinary loop, which was never wrong.
        int counted = 0;
        for (Applicant a : APPLICANTS) {
            if (ok.test(a)) {
                counted++;
            }
        }
        System.out.println("  counted with a plain loop:    " + counted);

        // With a stream, which Module 14 covers properly. Note there is no
        // mutable variable anywhere: the pipeline produces the answer.
        long streamed = APPLICANTS.stream().filter(ok).count();
        System.out.println("  counted with a stream:        " + streamed);

        System.out.println();
        System.out.println("--- capturing an object is fine ---");

        // `collected` is effectively final: the variable is never reassigned.
        // The list it points at changes constantly, and the compiler has no
        // objection, because the rule is about the variable.
        List<String> collected = new ArrayList<>();
        APPLICANTS.stream()
                .filter(ok)
                .map(Applicant::name)
                .sorted(Comparator.naturalOrder())
                .forEach(collected::add);
        System.out.println("  " + collected);

        // Though even this is better written as .toList(), for the same reason
        // the array trick was: the pipeline should hand me the result rather
        // than me assembling it on the side.
        System.out.println("  better: " + APPLICANTS.stream()
                .filter(ok)
                .map(Applicant::name)
                .sorted()
                .toList());
    }
}
