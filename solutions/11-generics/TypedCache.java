// Reference solution for Homework 11.

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TypedCache {

    public static void main(String[] args) {
        partOne();
        System.out.println();
        partTwo();
        System.out.println();
        partThree();
    }

    // ---------------------------------------------------------------- part 1

    private static void partOne() {
        System.out.println("--- the generic cache ---");

        Cache<String, Integer> ages = new Cache<>(3);
        ages.put("ada", 36);
        ages.put("grace", 45);
        ages.put("alan", 41);

        System.out.println("  get ada:     " + ages.get("ada"));
        System.out.println("  size:        " + ages.size());

        // Eviction, which needs no knowledge of K or V at all.
        ages.put("edsger", 52);
        System.out.println("  after a 4th: " + ages.keys());
        System.out.println("  ada evicted: " + (ages.get("ada") == null));

        // The same class over entirely different types, checked at compile time.
        Cache<Integer, List<String>> byId = new Cache<>(2);
        byId.put(1, List.of("alpha", "beta"));
        System.out.println("  other types: " + byId.get(1));

        // No cast anywhere above. Before generics every one of those reads
        // would have been (Integer) cache.get("ada"), trusted rather than
        // checked.

        System.out.println();
        System.out.println("--- bounded: largest() needs a bound to work ---");
        System.out.println("  largest of ints:    " + largest(List.of(3, 9, 2)));
        System.out.println("  largest of strings: " + largest(List.of("pear", "fig")));

        // Without `T extends Comparable<T>` the body could not call compareTo,
        // because an unbounded T is treated as Object. The bound is what turns
        // a container-shuffling method into one that can reason about values.
    }

    // ---------------------------------------------------------------- part 2

    private static void partTwo() {
        System.out.println("--- PECS ---");

        List<Integer> ints = List.of(1, 2, 3);
        List<Double> doubles = List.of(1.5, 2.5);

        // PRODUCER EXTENDS. average only reads, so it accepts a list of Number
        // or of any subtype. Typed as List<Number> it would accept neither of
        // these, because generics are invariant.
        System.out.println("  average of ints:    " + average(ints));
        System.out.println("  average of doubles: " + average(doubles));

        // CONSUMER SUPER. drainInto only writes, so the destination may be
        // typed at Integer or anything above it.
        List<Number> numbers = new ArrayList<>();
        List<Object> objects = new ArrayList<>();
        drainInto(numbers, ints);
        drainInto(objects, ints);
        System.out.println("  drained into Number: " + numbers);
        System.out.println("  drained into Object: " + objects);

        // WHY EACH SIGNATURE HAD TO BE THAT WAY, in my own words:
        //
        // average reads elements and calls doubleValue on them. It never adds
        // anything. `? extends Number` says exactly that: every element is at
        // least a Number, and I promise not to write. The compiler enforces the
        // promise by refusing any add, which is fine because I had no intention
        // of adding.
        //
        // drainInto does the opposite. It writes Integers and never needs to
        // read one back as an Integer. `? super Integer` says the destination
        // can hold an Integer, whatever else it can also hold. Reading from it
        // would only give me Object, which again is fine because I do not read.
        //
        // The asymmetry is not arbitrary. Each wildcard gives up the direction
        // it does not need, and that is precisely what makes it safe.
    }

    // ---------------------------------------------------------------- part 3

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void partThree() {
        System.out.println("--- heap pollution ---");

        List<String> names = new ArrayList<>();
        names.add("ada");

        List raw = names;
        raw.add(42);

        System.out.println("  list contents:   " + names);
        System.out.println("  declared type:   List<String>");
        System.out.println("  element 0 reads: " + names.get(0));

        try {
            String bad = names.get(1);
            System.out.println("  unreachable: " + bad);
        } catch (ClassCastException e) {
            System.out.println("  element 1 threw: " + e.getClass().getSimpleName());
        }

        // WHERE THE BLAME LANDS, in my own words:
        //
        // The damage was done by `raw.add(42)`. The crash happened at
        // `names.get(1)`.
        //
        // Nothing checks the add, because after erasure the raw List and the
        // List<String> are the same class and neither carries an element type
        // to verify against. The cast to String only appears at the read, where
        // the compiler inserted it to honour the declared type.
        //
        // So the stack trace points at code that is completely correct, and the
        // code that actually broke the invariant does not appear in it at all.
        // In a real codebase those two lines are usually in different classes,
        // which is why this class of bug eats afternoons.
        //
        // The fix is not defensive coding at the read. It is never writing the
        // raw type, and treating unchecked warnings as errors so the compiler
        // tells you at the line that matters.
    }

    // ---------------------------------------------------------------- helpers

    static <T extends Comparable<T>> T largest(List<T> items) {
        T best = items.get(0);
        for (T item : items) {
            if (item.compareTo(best) > 0) {
                best = item;
            }
        }
        return best;
    }

    static double average(List<? extends Number> numbers) {
        double total = 0;
        for (Number n : numbers) {
            total += n.doubleValue();
        }
        return numbers.isEmpty() ? 0 : total / numbers.size();
    }

    static void drainInto(List<? super Integer> destination, List<Integer> source) {
        for (Integer i : source) {
            destination.add(i);
        }
    }
}

// A bounded-size cache, generic over both key and value.
//
// THE THREE ERASURE LIMITS I HIT WRITING THIS, and what I did instead:
//
// 1. `new V[capacity]` for the storage. Illegal, because there is no element
//    type for the JVM to record. I used a LinkedHashMap, which is the right
//    answer anyway: a List or Map sidesteps the problem entirely.
//
// 2. `if (value instanceof V)` to validate on the way in. Illegal, because V
//    does not exist at runtime. If I genuinely needed it I would take a
//    Class<V> in the constructor and call type.isInstance(value).
//
// 3. `static V defaultValue;` to share a fallback. Illegal, because there is
//    one static field per class and the class does not know V. I made it an
//    instance field instead, which is more correct in any case: a default that
//    is shared across every type argument is almost never what you want.
class Cache<K, V> {

    private final int capacity;
    private final Map<K, V> entries = new LinkedHashMap<>();

    Cache(int capacity) {
        if (capacity < 1) {
            throw new IllegalArgumentException("capacity must be at least 1");
        }
        this.capacity = capacity;
    }

    void put(K key, V value) {
        if (entries.size() >= capacity && !entries.containsKey(key)) {
            // LinkedHashMap preserves insertion order, so the first key is the
            // oldest. Evicting it makes this a simple FIFO cache.
            K oldest = entries.keySet().iterator().next();
            entries.remove(oldest);
        }
        entries.put(key, value);
    }

    V get(K key) {
        return entries.get(key);
    }

    int size() {
        return entries.size();
    }

    List<K> keys() {
        return List.copyOf(entries.keySet());
    }
}
