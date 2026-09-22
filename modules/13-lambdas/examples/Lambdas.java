// A lambda is an implementation of an interface that has exactly one abstract
// method, written without the ceremony of a class.
//
// That definition matters. A lambda is not a new kind of value in Java. It is
// still an object implementing an interface. Module 07 said an interface with
// one abstract method can be implemented without writing a class; this is that.

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Lambdas {

    public static void main(String[] args) {
        // THE SAME THING, THREE WAYS.

        // 1. A named class. This is what Java looked like in 2013.
        Greeter named = new PoliteGreeter();
        System.out.println("named class:      " + named.greet("Ada"));

        // 2. An anonymous class. Shorter, still mostly ceremony.
        Greeter anonymous = new Greeter() {
            @Override
            public String greet(String name) {
                return "Good day, " + name;
            }
        };
        System.out.println("anonymous class:  " + anonymous.greet("Ada"));

        // 3. A lambda. The interface name, the method name, the modifiers and
        //    the parameter type are all inferred from the target type.
        Greeter lambda = name -> "Good day, " + name;
        System.out.println("lambda:           " + lambda.greet("Ada"));

        // All three produce an object implementing Greeter. The lambda just
        // omits everything the compiler can work out for itself.

        System.out.println();
        System.out.println("--- syntax forms ---");

        // No parameters: empty parentheses required.
        Runnable nothing = () -> System.out.println("  ran with no arguments");
        nothing.run();

        // One parameter: parentheses optional, and most people omit them.
        Greeter one = name -> "  hello " + name;
        System.out.println(one.greet("Grace"));

        // Types may be written out when inference needs help or clarity does.
        Greeter typed = (String name) -> "  typed parameter: " + name;
        System.out.println(typed.greet("Alan"));

        // A block body needs braces and an explicit return.
        Greeter block = name -> {
            String trimmed = name.strip();
            return "  block body: " + trimmed.toUpperCase();
        };
        System.out.println(block.greet("  edsger  "));

        // Two parameters always need parentheses.
        Combiner join = (a, b) -> a + " and " + b;
        System.out.println("  " + join.combine("salt", "pepper"));

        System.out.println();
        System.out.println("--- where you will actually meet them ---");

        List<String> names = new ArrayList<>(List.of("charlie", "al", "bob"));

        // Sorting. Comparator is a functional interface, so a lambda is a
        // comparator.
        names.sort((a, b) -> a.length() - b.length());
        System.out.println("  sorted by length: " + names);

        // Though subtracting like that overflows for large values. The built-in
        // factories are both safer and clearer:
        names.sort(Comparator.comparingInt(String::length).thenComparing(Comparator.naturalOrder()));
        System.out.println("  sorted properly:  " + names);

        // Iterating.
        names.forEach(n -> System.out.println("  forEach: " + n));

        // Filtering in place.
        List<String> copy = new ArrayList<>(names);
        copy.removeIf(n -> n.length() < 3);
        System.out.println("  after removeIf:   " + copy);

        System.out.println();

        // WHAT A FUNCTIONAL INTERFACE IS: exactly one abstract method. Default
        // and static methods do not count, which is why Comparator can have
        // dozens of methods and still work as a lambda target.
        //
        // @FunctionalInterface is optional and worth writing: it makes the
        // compiler check the "exactly one" rule, so adding a second abstract
        // method later becomes a compile error at the interface rather than a
        // mystery at every lambda that used it.
        System.out.println("Comparator has many methods but one abstract method,");
        System.out.println("which is why this compiles: "
                + ((Comparator<String>) (a, b) -> 0).getClass().getSimpleName().isEmpty());
    }
}

@FunctionalInterface
interface Greeter {
    String greet(String name);
}

@FunctionalInterface
interface Combiner {
    String combine(String a, String b);
}

class PoliteGreeter implements Greeter {
    @Override
    public String greet(String name) {
        return "Good day, " + name;
    }
}
