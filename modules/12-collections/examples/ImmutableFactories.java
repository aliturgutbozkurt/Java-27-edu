// List.of, Set.of and Map.of are the modern way to build a fixed collection.
// They have four behaviours that surprise people, and all four are deliberate.

import java.util.*;

public class ImmutableFactories {

    public static void main(String[] args) {
        List<String> fixed = List.of("a", "b", "c");
        System.out.println("built: " + fixed);

        System.out.println();
        System.out.println("--- surprise 1: genuinely immutable ---");
        try {
            fixed.add("d");
        } catch (UnsupportedOperationException e) {
            System.out.println("  add threw UnsupportedOperationException");
        }
        // Not "unmodifiable by convention". Every mutating method throws. This
        // is what makes them safe to share and to return from a getter, as
        // Module 05 and Module 08 both relied on.

        System.out.println();
        System.out.println("--- surprise 2: null is rejected ---");
        try {
            List.of("a", null);
        } catch (NullPointerException e) {
            System.out.println("  List.of(\"a\", null) threw NullPointerException");
        }
        // Deliberate. Nulls in collections cause more bugs than they solve, so
        // the modern factories refuse them outright. ArrayList still accepts
        // null, and HashMap still accepts a null key, so converting old code to
        // List.of can surface nulls you did not know you had. That is a feature,
        // though it rarely feels like one at the time.

        System.out.println();
        System.out.println("--- surprise 3: duplicates are an error, not a silent drop ---");
        try {
            Set.of(1, 1);
        } catch (IllegalArgumentException e) {
            System.out.println("  Set.of(1, 1) threw: " + e.getMessage());
        }
        try {
            Map.of("k", 1, "k", 2);
        } catch (IllegalArgumentException e) {
            System.out.println("  Map.of with a repeated key threw: " + e.getMessage());
        }
        // new HashSet<>(List.of(1, 1)) quietly gives you one element. Set.of
        // treats a duplicate in a literal as a typo, because it almost always is.

        System.out.println();
        System.out.println("--- surprise 4: iteration order of Set.of is unspecified ---");
        System.out.println("  Set.of(1,2,3) here: " + Set.of(1, 2, 3));
        // And it is deliberately randomised per JVM run in some versions, to
        // stop you depending on it. If you need order, use List.of or
        // LinkedHashSet.

        System.out.println();
        System.out.println("--- the three ways to get a 'fixed' list, and how they differ ---");

        List<String> viaOf = List.of("a", "b");
        List<String> viaArrays = Arrays.asList("a", "b");
        List<String> viaUnmodifiable = Collections.unmodifiableList(new ArrayList<>(List.of("a", "b")));

        System.out.println("  List.of                        add: " + tryAdd(viaOf)
                + "   set: " + trySet(viaOf));
        System.out.println("  Arrays.asList                  add: " + tryAdd(viaArrays)
                + "   set: " + trySet(viaArrays));
        System.out.println("  Collections.unmodifiableList   add: " + tryAdd(viaUnmodifiable)
                + "   set: " + trySet(viaUnmodifiable));

        // Arrays.asList is FIXED SIZE, not immutable. You cannot add or remove,
        // but you CAN replace an element, and it writes through to the backing
        // array. That surprises people who reach for it expecting List.of.
        //
        // Collections.unmodifiableList is a VIEW over a mutable list. Changes to
        // the original show through it. It is not a copy, and it is not a
        // guarantee unless you also throw away the original reference.
        //
        // List.copyOf is what you usually want: a genuine immutable copy,
        // cheap when the source is already immutable.

        System.out.println();
        List<String> source = new ArrayList<>(List.of("x"));
        List<String> view = Collections.unmodifiableList(source);
        List<String> copy = List.copyOf(source);
        source.add("y");
        System.out.println("  after mutating the source:");
        System.out.println("    unmodifiable VIEW sees it: " + view);
        System.out.println("    List.copyOf does not:      " + copy);
    }

    static String tryAdd(List<String> list) {
        try {
            list.add("z");
            return "ok";
        } catch (UnsupportedOperationException e) {
            return "throws";
        }
    }

    static String trySet(List<String> list) {
        try {
            list.set(0, "z");
            return "ok";
        } catch (UnsupportedOperationException e) {
            return "throws";
        }
    }
}
