// When a lambda does nothing but call an existing method, a method reference
// says so more directly.
//
// There are four forms, and being able to name them makes unfamiliar code
// readable.

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class MethodReferences {

    public static void main(String[] args) {
        System.out.println("--- 1. static method:  Type::staticMethod ---");
        Function<String, Integer> parse = Integer::parseInt;
        System.out.println("  Integer::parseInt applied to \"42\" -> " + parse.apply("42"));
        //  equivalent lambda: s -> Integer.parseInt(s)

        System.out.println();
        System.out.println("--- 2. constructor:  Type::new ---");
        Supplier<List<String>> maker = ArrayList::new;
        List<String> made = maker.get();
        made.add("built by a constructor reference");
        System.out.println("  ArrayList::new -> " + made);
        //  equivalent lambda: () -> new ArrayList<>()

        System.out.println();
        System.out.println("--- 3. bound instance:  object::method ---");
        String prefix = "pre-";
        Function<String, String> addPrefix = prefix::concat;
        System.out.println("  prefix::concat applied to \"fix\" -> " + addPrefix.apply("fix"));
        //  equivalent lambda: s -> prefix.concat(s)
        //
        //  The receiver is fixed at the moment the reference is created. The
        //  lambda's own argument becomes the method's argument.

        System.out.println();
        System.out.println("--- 4. unbound instance:  Type::instanceMethod ---");
        Function<String, String> upper = String::toUpperCase;
        System.out.println("  String::toUpperCase applied to \"x\" -> " + upper.apply("x"));
        //  equivalent lambda: s -> s.toUpperCase()
        //
        //  THIS IS THE ONE PEOPLE MISREAD. The lambda's FIRST argument becomes
        //  the RECEIVER, not an argument. So a Function<String, String> works
        //  even though toUpperCase takes no parameters.

        // The same shape with two arguments: the first becomes the receiver and
        // the second becomes the method's argument.
        BiFunction<String, String, Boolean> startsWith = String::startsWith;
        System.out.println("  String::startsWith(\"hello\", \"he\") -> "
                + startsWith.apply("hello", "he"));

        System.out.println();
        System.out.println("--- forms 3 and 4 look identical and are not ---");

        //     prefix::concat     BOUND. prefix is a variable, fixed now.
        //     String::concat     UNBOUND. String is a type; the receiver comes
        //                        from the first argument at each call.
        BiFunction<String, String, String> unboundConcat = String::concat;

        // The bound one always concatenates onto `prefix`, whatever you pass.
        System.out.println("  bound, arg \"fix\":            " + addPrefix.apply("fix"));
        System.out.println("  bound, arg \"amble\":          " + addPrefix.apply("amble"));

        // The unbound one takes its receiver from the first argument, so the
        // thing being concatenated ONTO changes from call to call.
        System.out.println("  unbound, (\"pre-\", \"fix\"):    " + unboundConcat.apply("pre-", "fix"));
        System.out.println("  unbound, (\"post-\", \"fix\"):   " + unboundConcat.apply("post-", "fix"));

        // Telling them apart is a matter of what is on the left of the ::. A
        // variable means bound; a type name means unbound or static.

        System.out.println();
        System.out.println("--- when NOT to use one ---");

        List<String> words = new ArrayList<>(List.of("b", "a", "c"));

        // A method reference is clearer here.
        words.forEach(System.out::println);

        // But a lambda is clearer when anything at all is happening beyond the
        // single call:
        //
        //     words.forEach(w -> System.out.println("item: " + w));
        //
        // Contorting code so a method reference fits usually makes it worse.
    }
}
