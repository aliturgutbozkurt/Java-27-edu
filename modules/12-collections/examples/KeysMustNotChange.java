// Module 06 showed an object getting lost inside a HashSet after a field
// changed. Here is the same failure as a map key, which is where it actually
// bites in real code.

import java.util.*;

public class KeysMustNotChange {

    public static void main(String[] args) {
        System.out.println("--- a mutable key ---");

        MutableKey key = new MutableKey("orders");
        Map<MutableKey, Integer> counts = new HashMap<>();
        counts.put(key, 42);

        System.out.println("  stored under: " + key);
        System.out.println("  get(key):     " + counts.get(key));
        System.out.println("  size:         " + counts.size());

        // Change a field that hashCode depends on, while the key sits in the map.
        key.setName("invoices");

        System.out.println();
        System.out.println("  after renaming the key object:");
        System.out.println("  get(key):     " + counts.get(key));
        System.out.println("  containsKey:  " + counts.containsKey(key));
        System.out.println("  size:         " + counts.size());
        System.out.println("  but iterating still finds it: " + counts.keySet());

        // The entry is filed in the bucket for its OLD hash code. Asking for it
        // computes the NEW one, looks in a different bucket, and finds nothing.
        //
        // The map still counts the entry and iteration still yields it, so the
        // data is not gone. It is simply unreachable by lookup, which is the
        // one thing a map exists to do.
        //
        // Worse, it is now unremovable by key for the same reason.
        counts.remove(key);
        System.out.println("  after remove(key), size is still: " + counts.size());

        System.out.println();
        System.out.println("--- an immutable key ---");

        record StableKey(String name) { }

        Map<StableKey, Integer> stable = new HashMap<>();
        stable.put(new StableKey("orders"), 42);
        System.out.println("  get:          " + stable.get(new StableKey("orders")));
        System.out.println("  a new equal instance finds it, because nothing can change");

        // THE RULE:
        //
        //   Map keys and Set elements must be immutable in every field that
        //   equals and hashCode use.
        //
        // A record whose components are themselves immutable satisfies this for
        // free, which is most of why records and maps go together so well.
        // Module 08's warning about a mutable component inside a record is the
        // same rule from the other direction.
    }
}

class MutableKey {

    private String name;

    MutableKey(String name) {
        this.name = name;
    }

    void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof MutableKey other && Objects.equals(other.name, name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "MutableKey(" + name + ")";
    }
}
