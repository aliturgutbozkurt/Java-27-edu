// Four shapes, several implementations each. Picking correctly is most of what
// there is to learn here.

import java.util.*;

public class ChoosingACollection {

    public static void main(String[] args) {
        System.out.println("--- List: ordered, duplicates allowed, indexed ---");
        List<String> list = new ArrayList<>(List.of("b", "a", "b"));
        System.out.println("  " + list + "  get(1)=" + list.get(1));

        // ArrayList vs LinkedList. The short version: use ArrayList.
        //
        //   ArrayList    array-backed. get by index is O(1). add at the end is
        //                amortised O(1). insert or remove in the middle is O(n)
        //                because everything after shifts.
        //
        //   LinkedList   nodes with pointers. insert or remove at a known
        //                position is O(1), but FINDING that position is O(n),
        //                and every element costs an extra object plus two
        //                pointers, so it thrashes the cache.
        //
        // In practice ArrayList wins almost always, including for cases the
        // big-O table says it should lose, because sequential memory is fast and
        // pointer chasing is not. Reach for LinkedList only when you need Deque
        // behaviour, and even then ArrayDeque is usually better.

        System.out.println();
        System.out.println("--- Set: no duplicates ---");
        System.out.println("  HashSet:       " + new HashSet<>(List.of("b", "a", "c", "a")));
        System.out.println("  LinkedHashSet: " + new LinkedHashSet<>(List.of("b", "a", "c", "a")));
        System.out.println("  TreeSet:       " + new TreeSet<>(List.of("b", "a", "c", "a")));

        //   HashSet         fastest. NO ORDER GUARANTEE AT ALL. The order you
        //                   see may change between JDK versions or even runs.
        //   LinkedHashSet   insertion order, tiny extra cost. Use this whenever
        //                   output is read by a human or compared in a test.
        //   TreeSet         sorted. O(log n) instead of O(1), and elements must
        //                   be Comparable or you must supply a Comparator.
        //
        // A HashSet's iteration order looking stable in your tests is not a
        // promise. Depending on it is a bug waiting for a JDK upgrade.

        System.out.println();
        System.out.println("--- Map: keys to values ---");
        Map<String, Integer> scores = new LinkedHashMap<>();
        scores.put("ada", 95);
        scores.put("grace", 88);
        System.out.println("  " + scores);

        // The three Map methods worth knowing before anything else.
        System.out.println("  getOrDefault: " + scores.getOrDefault("alan", 0));

        // computeIfAbsent: the standard way to build a map of lists without
        // writing a null check every time.
        Map<Character, List<String>> byInitial = new LinkedHashMap<>();
        for (String name : List.of("ada", "alan", "grace")) {
            byInitial.computeIfAbsent(name.charAt(0), k -> new ArrayList<>()).add(name);
        }
        System.out.println("  computeIfAbsent: " + byInitial);

        // merge: read-modify-write in one call, with the initial case handled.
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (String word : List.of("a", "b", "a")) {
            counts.merge(word, 1, Integer::sum);
        }
        System.out.println("  merge:        " + counts);

        System.out.println();
        System.out.println("--- Deque: both ends ---");
        Deque<String> deque = new ArrayDeque<>();
        deque.addFirst("middle");
        deque.addFirst("front");
        deque.addLast("back");
        System.out.println("  " + deque);
        System.out.println("  pollFirst=" + deque.pollFirst() + " pollLast=" + deque.pollLast());

        // ArrayDeque is the right choice for both a stack and a queue.
        //
        // There is a java.util.Stack. Do not use it. It extends Vector, which
        // synchronises every method for a threading model abandoned in 1998,
        // and it iterates bottom-to-top, which is the opposite of what a stack
        // should do.

        System.out.println();
        System.out.println("--- the quick guide ---");
        System.out.println("  need an index, or duplicates?      ArrayList");
        System.out.println("  need uniqueness, order irrelevant? HashSet");
        System.out.println("  need uniqueness, stable output?    LinkedHashSet");
        System.out.println("  need sorted?                       TreeSet / TreeMap");
        System.out.println("  need key to value?                 HashMap, or LinkedHashMap");
        System.out.println("  need a stack or a queue?           ArrayDeque");
    }
}
