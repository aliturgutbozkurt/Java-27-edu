// Generics let one piece of code work over many types while the compiler still
// checks every use.

import java.util.ArrayList;
import java.util.List;

public class Generics {

    public static void main(String[] args) {
        // A generic CLASS. The type argument is fixed when you create it.
        Box<String> words = new Box<>("hello");
        Box<Integer> numbers = new Box<>(42);

        System.out.println("words:   " + words.get().toUpperCase());
        System.out.println("numbers: " + (numbers.get() + 1));

        // No cast anywhere. Before generics arrived in Java 5, every collection
        // held Object and every read needed a cast that could fail at runtime:
        //
        //     List list = new ArrayList();
        //     list.add("hello");
        //     String s = (String) list.get(0);   // trust me
        //
        // Generics moved that check from runtime to compile time. That is the
        // entire point of the feature.

        System.out.println();

        // A generic METHOD. The angle brackets before the return type declare
        // the type variable, and it is inferred from the arguments.
        System.out.println("first of words: " + first(List.of("a", "b")));
        System.out.println("first of ints:  " + first(List.of(1, 2)));

        // Several type variables are fine.
        System.out.println("paired: " + pair("age", 30));

        System.out.println();

        // BOUNDED TYPE PARAMETERS. `T extends Comparable<T>` says T must be
        // comparable to itself, which is what lets max() call compareTo.
        //
        // Without the bound, T would be treated as Object and compareTo would
        // not exist. The bound is how a generic method gets to DO anything with
        // its values rather than just move them around.
        System.out.println("max int:    " + max(List.of(3, 9, 2)));
        System.out.println("max string: " + max(List.of("pear", "apple", "fig")));

        // Multiple bounds use &, with the class first if there is one:
        //
        //     <T extends Number & Comparable<T>>

        System.out.println();

        // A generic class can be bounded too.
        NumberBox<Integer> boxed = new NumberBox<>(7);
        System.out.println("doubled: " + boxed.doubled());

        System.out.println();

        // NAMING CONVENTION, worth knowing so you can read other people's code:
        //
        //   T   type        E   element      K   key
        //   V   value       R   result       N   number
        //
        // They are only conventions. `<Item>` is legal and sometimes clearer.
        var stack = new SimpleStack<String>();
        stack.push("first");
        stack.push("second");
        System.out.println("popped: " + stack.pop() + ", then " + stack.pop());
        System.out.println("empty now: " + stack.isEmpty());
    }

    static <T> T first(List<T> items) {
        return items.get(0);
    }

    static <K, V> String pair(K key, V value) {
        return key + "=" + value;
    }

    static <T extends Comparable<T>> T max(List<T> items) {
        T best = items.get(0);
        for (T item : items) {
            if (item.compareTo(best) > 0) {
                best = item;
            }
        }
        return best;
    }
}

class Box<T> {

    private final T value;

    Box(T value) {
        this.value = value;
    }

    T get() {
        return value;
    }
}

// The bound applies to the whole class, so every method can rely on it.
class NumberBox<T extends Number> {

    private final T value;

    NumberBox(T value) {
        this.value = value;
    }

    double doubled() {
        // doubleValue() exists because T is known to be a Number.
        return value.doubleValue() * 2;
    }
}

class SimpleStack<E> {

    private final List<E> items = new ArrayList<>();

    void push(E item) {
        items.add(item);
    }

    E pop() {
        if (items.isEmpty()) {
            throw new IllegalStateException("stack is empty");
        }
        return items.remove(items.size() - 1);
    }

    boolean isEmpty() {
        return items.isEmpty();
    }
}
