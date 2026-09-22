// Sequenced collections, added in Java 21, fixed a thirty-year-old
// inconsistency: collections that obviously had a first and last element had no
// common way to ask for either.

import java.util.*;

public class SequencedCollections {

    public static void main(String[] args) {
        // Before Java 21 these three were all different:
        //
        //   list.get(0)                       first of a List
        //   set.iterator().next()             first of a LinkedHashSet
        //   map.entrySet().iterator().next()  first of a LinkedHashMap
        //
        // And getting the LAST element of a LinkedHashSet meant iterating the
        // whole thing. There was no reverse view at all without copying.

        System.out.println("--- SequencedCollection ---");
        List<String> list = new ArrayList<>(List.of("a", "b", "c"));
        System.out.println("  list:      " + list);
        System.out.println("  getFirst:  " + list.getFirst());
        System.out.println("  getLast:   " + list.getLast());
        System.out.println("  reversed:  " + list.reversed());

        // reversed() is a VIEW, not a copy. It reflects later changes to the
        // original and costs nothing to create.
        List<String> backwards = list.reversed();
        list.add("d");
        System.out.println("  after add, the view shows: " + backwards);

        System.out.println();
        System.out.println("--- SequencedSet ---");
        SequencedSet<Integer> set = new LinkedHashSet<>(List.of(10, 20, 30));
        System.out.println("  set:       " + set);
        System.out.println("  first/last: " + set.getFirst() + "/" + set.getLast());
        System.out.println("  reversed:  " + set.reversed());

        System.out.println();
        System.out.println("--- SequencedMap ---");
        SequencedMap<String, Integer> map = new LinkedHashMap<>();
        map.put("x", 1);
        map.put("y", 2);
        map.put("z", 3);
        System.out.println("  map:        " + map);
        System.out.println("  firstEntry: " + map.firstEntry());
        System.out.println("  lastEntry:  " + map.lastEntry());
        System.out.println("  reversed:   " + map.reversed());

        // putFirst exists too, which a LinkedHashMap could not previously do
        // without rebuilding the whole map.
        map.putFirst("w", 0);
        System.out.println("  putFirst:   " + map);

        System.out.println();
        System.out.println("--- and it works on sorted collections too ---");
        SequencedMap<String, Integer> sorted = new TreeMap<>(Map.of("b", 2, "a", 1, "c", 3));
        System.out.println("  TreeMap:    " + sorted);
        System.out.println("  reversed:   " + sorted.reversed());

        System.out.println();

        // WHICH TYPES ARE SEQUENCED:
        //
        //   SequencedCollection   List, Deque, LinkedHashSet, SortedSet
        //   SequencedSet          LinkedHashSet, SortedSet (so TreeSet)
        //   SequencedMap          LinkedHashMap, SortedMap (so TreeMap)
        //
        // HashSet and HashMap are NOT sequenced, and that is the point. They
        // have no defined order, so asking for the first element is meaningless
        // and the compiler now says so instead of letting you iterate and hope.
        Set<String> unordered = new HashSet<>(List.of("a", "b"));
        System.out.println("  a HashSet is not a SequencedSet: "
                + !(unordered instanceof SequencedSet));
    }
}
