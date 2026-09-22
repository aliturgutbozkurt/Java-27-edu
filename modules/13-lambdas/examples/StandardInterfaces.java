// You rarely declare your own functional interface. java.util.function has the
// shapes, and learning the four core ones covers most code you will read.

import java.util.Comparator;
import java.util.List;
import java.util.function.*;

public class StandardInterfaces {

    public static void main(String[] args) {
        System.out.println("--- the four you must know ---");

        //  Function<T,R>   takes a T, returns an R      apply
        //  Predicate<T>    takes a T, returns boolean   test
        //  Supplier<T>     takes nothing, returns a T   get
        //  Consumer<T>     takes a T, returns nothing   accept

        Function<String, Integer> length = String::length;
        Predicate<String> isEmpty = String::isEmpty;
        Supplier<String> greeting = () -> "hello";
        Consumer<String> printer = s -> System.out.println("  consumed: " + s);

        System.out.println("  Function: " + length.apply("four"));
        System.out.println("  Predicate: " + isEmpty.test(""));
        System.out.println("  Supplier: " + greeting.get());
        printer.accept("a value");

        System.out.println();
        System.out.println("--- the variants ---");

        //  BiFunction<T,U,R>     two arguments
        //  BiPredicate<T,U>      two arguments, boolean
        //  BiConsumer<T,U>       two arguments, nothing back
        //  UnaryOperator<T>      Function<T,T>, same type in and out
        //  BinaryOperator<T>     BiFunction<T,T,T>

        BiFunction<Integer, Integer, Integer> add = Integer::sum;
        UnaryOperator<String> shout = String::toUpperCase;
        BinaryOperator<Integer> max = Integer::max;

        System.out.println("  BiFunction: " + add.apply(3, 4));
        System.out.println("  UnaryOperator: " + shout.apply("quiet"));
        System.out.println("  BinaryOperator: " + max.apply(3, 9));

        System.out.println();
        System.out.println("--- composition ---");

        // Predicates combine with and, or, negate.
        Predicate<String> notBlank = s -> !s.isBlank();
        Predicate<String> isShort = s -> s.length() < 5;
        System.out.println("  and:    " + notBlank.and(isShort).test("abc"));
        System.out.println("  negate: " + notBlank.negate().test("  "));

        // Functions chain with andThen and compose, which run in opposite orders.
        Function<Integer, Integer> twice = n -> n * 2;
        Function<Integer, Integer> plusOne = n -> n + 1;

        System.out.println("  twice.andThen(plusOne) on 5: " + twice.andThen(plusOne).apply(5));
        System.out.println("  twice.compose(plusOne) on 5: " + twice.compose(plusOne).apply(5));

        // andThen means "do me first, then the argument".
        // compose means "do the argument first, then me".
        // Getting them the wrong way round is a classic bug, and the results
        // differ whenever the operations are not commutative.

        System.out.println();
        System.out.println("--- primitive specialisations ---");

        // Function<Integer,Integer> boxes every value, twice. For hot code the
        // primitive versions avoid it entirely.
        //
        //   IntPredicate, IntFunction, IntUnaryOperator, IntSupplier,
        //   IntConsumer, ToIntFunction, and the same for long and double.
        IntPredicate even = n -> n % 2 == 0;
        ToIntFunction<String> toLength = String::length;
        IntUnaryOperator square = n -> n * n;

        System.out.println("  IntPredicate: " + even.test(4));
        System.out.println("  ToIntFunction: " + toLength.applyAsInt("hello"));
        System.out.println("  IntUnaryOperator: " + square.applyAsInt(7));

        // This is Module 02's boxing cost showing up in the API design. The
        // library grew a parallel set of interfaces rather than pay it, which
        // is also why java.util.function is so large.

        System.out.println();
        System.out.println("--- and the one you already used ---");

        // Comparator is a functional interface, and its factory methods build
        // comparators out of functions.
        List<String> words = List.of("banana", "fig", "apple");
        System.out.println("  by length then alphabetically: "
                + words.stream()
                        .sorted(Comparator.comparingInt(String::length)
                                .thenComparing(Comparator.naturalOrder()))
                        .toList());

        // Module 14 covers streams properly. The point here is that everything
        // in that pipeline is one of the interfaces above.
    }
}
