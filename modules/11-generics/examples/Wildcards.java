// Wildcards, and the mnemonic that makes them memorable: PECS.
//
//     Producer Extends, Consumer Super
//
// If the parameter PRODUCES values for you to read, use ? extends.
// If it CONSUMES values you write into it, use ? super.

import java.util.ArrayList;
import java.util.List;

public class Wildcards {

    public static void main(String[] args) {
        List<Integer> ints = List.of(1, 2, 3);
        List<Double> doubles = List.of(1.5, 2.5);

        // THE PROBLEM WILDCARDS SOLVE.
        //
        // Generics are INVARIANT. A List<Integer> is NOT a List<Number>, even
        // though an Integer is a Number:
        //
        //     List<Number> ns = ints;   // does not compile
        //
        // Without wildcards, sum(List<Number>) could not accept either list
        // above, and you would need one overload per element type.
        System.out.println("sum of ints:    " + sum(ints));
        System.out.println("sum of doubles: " + sum(doubles));

        System.out.println();

        // CONSUMER SUPER. addAll writes into the destination, so the
        // destination may be a list of Integer or of anything above it.
        List<Number> numbers = new ArrayList<>();
        List<Object> objects = new ArrayList<>();

        addAll(numbers, ints);
        addAll(objects, ints);

        System.out.println("into List<Number>: " + numbers);
        System.out.println("into List<Object>: " + objects);

        System.out.println();

        // WHY INVARIANCE EXISTS AT ALL. Arrays chose the other answer and it
        // was a mistake. Arrays are COVARIANT: String[] IS an Object[].
        Object[] array = new String[2];
        try {
            array[0] = 42;   // compiles fine, fails at runtime
        } catch (ArrayStoreException e) {
            System.out.println("arrays are covariant, so this compiles and then throws:");
            System.out.println("  ArrayStoreException: " + e.getMessage());
        }

        // Generics refuse that at compile time instead. The equivalent
        // List<Object> list = new ArrayList<String>() does not compile, so the
        // runtime check is never needed.

        System.out.println();

        // WHAT ? extends COSTS YOU: you cannot write into it.
        //
        //     static void broken(List<? extends Number> list) {
        //         list.add(1);   // does not compile
        //     }
        //
        // The list might be a List<Double>, and adding an Integer would corrupt
        // it. The compiler cannot know which, so it forbids all writes except
        // null. `? extends` is read-only for this reason.

        // WHAT ? super COSTS YOU: reads come back as Object.
        //
        //     static void alsoLimited(List<? super Integer> list) {
        //         Integer i = list.get(0);   // does not compile
        //         Object o = list.get(0);    // fine
        //     }
        //
        // The list might be a List<Object>, so all you can promise is Object.

        System.out.println("unbounded wildcard, size only: " + sizeOf(ints));

        // THE UNBOUNDED WILDCARD `List<?>` means "a list of something unknown".
        // You can read Objects and check the size; you cannot add anything.
        //
        // It differs from the RAW `List` in the way that matters: List<?> is
        // type-safe and the compiler enforces it. The raw type simply turns
        // checking off, as HeapPollution.java shows.
    }

    // PRODUCER EXTENDS. This reads from the list and never writes to it, so it
    // accepts a list of Number or of any subtype.
    static double sum(List<? extends Number> numbers) {
        double total = 0;
        for (Number n : numbers) {
            total += n.doubleValue();
        }
        return total;
    }

    // CONSUMER SUPER. This writes into the destination, so the destination can
    // be typed at Integer or anything above it.
    static void addAll(List<? super Integer> destination, List<Integer> source) {
        for (Integer i : source) {
            destination.add(i);
        }
    }

    // Neither reading elements as a specific type nor writing: the unbounded
    // wildcard says so.
    static int sizeOf(List<?> anyList) {
        return anyList.size();
    }
}
