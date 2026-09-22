// Reference solution for Homework 12.

import java.util.*;

public class WordIndex {

    private static final String TEXT = """
            the quick brown fox jumps over the lazy dog
            the dog barks and the fox runs
            a quick fox is a happy fox""";

    public static void main(String[] args) {
        partOne();
        System.out.println();
        partTwo();
        System.out.println();
        partThree();
    }

    // ---------------------------------------------------------------- part 1

    private static void partOne() {
        System.out.println("--- counting ---");

        // LinkedHashMap, not HashMap. The counts get printed, so the order is
        // observable, and a HashMap would give an order that is undefined and
        // free to change between JDK versions. Paying a few bytes for a
        // predictable, diffable output is worth it every time.
        Map<String, Integer> counts = new LinkedHashMap<>();

        for (String word : words()) {
            // merge does read-modify-write in one call and handles the
            // first-occurrence case, replacing the usual
            //   Integer n = counts.get(w);
            //   counts.put(w, n == null ? 1 : n + 1);
            counts.merge(word, 1, Integer::sum);
        }

        System.out.println("  distinct words: " + counts.size());
        System.out.println("  counts:         " + counts);

        System.out.println();
        System.out.println("--- top three ---");

        // A List, because we need ordering and duplicates are irrelevant here.
        List<Map.Entry<String, Integer>> byFrequency = new ArrayList<>(counts.entrySet());
        byFrequency.sort(Map.Entry.<String, Integer>comparingByValue().reversed()
                .thenComparing(Map.Entry.comparingByKey()));

        // Sorting by count alone would leave ties in whatever order they
        // happened to be in. Adding the key as a tiebreaker makes the result
        // deterministic, which matters the moment anyone writes a test for it.
        for (var entry : byFrequency.subList(0, 3)) {
            System.out.println("  " + entry.getKey() + " x" + entry.getValue());
        }
    }

    // ---------------------------------------------------------------- part 2

    private static void partTwo() {
        System.out.println("--- index by initial ---");

        // TreeMap, because the initials should come out alphabetically and a
        // TreeMap keeps them that way without a separate sort step.
        //
        // The values are TreeSets: uniqueness is required, since a word
        // appearing twice should be listed once, and sorted output is wanted.
        Map<Character, SortedSet<String>> byInitial = new TreeMap<>();

        for (String word : words()) {
            // computeIfAbsent replaces the null check that would otherwise be
            // written at every insertion point.
            byInitial.computeIfAbsent(word.charAt(0), k -> new TreeSet<>()).add(word);
        }

        byInitial.forEach((initial, wordSet) ->
                System.out.println("  " + initial + ": " + wordSet));

        System.out.println();
        System.out.println("--- sequenced access ---");

        // Both of these need the map to be sequenced. A HashMap could answer
        // neither question, and the compiler would say so rather than letting
        // us iterate and guess.
        SequencedMap<Character, SortedSet<String>> sequenced = (TreeMap<Character, SortedSet<String>>) byInitial;
        System.out.println("  firstEntry: " + sequenced.firstEntry());
        System.out.println("  lastEntry:  " + sequenced.lastEntry());
        System.out.println("  reversed keys: " + sequenced.reversed().keySet());

        System.out.println();
        System.out.println("--- the unique-words set, three ways ---");

        Set<String> hash = new HashSet<>(words());
        Set<String> linked = new LinkedHashSet<>(words());
        Set<String> tree = new TreeSet<>(words());

        System.out.println("  HashSet       " + hash);
        System.out.println("  LinkedHashSet " + linked);
        System.out.println("  TreeSet       " + tree);

        // All three hold the same elements. Only the last two make a promise
        // about the order, and only LinkedHashSet's promise is "the order they
        // first appeared". The HashSet line above may print differently on
        // another JDK, which is exactly why it must never be relied on.
    }

    // ---------------------------------------------------------------- part 3

    private static void partThree() {
        System.out.println("--- losing an entry ---");

        MutableTag tag = new MutableTag("draft");
        Map<MutableTag, Integer> views = new HashMap<>();
        views.put(tag, 100);

        System.out.println("  before: get=" + views.get(tag)
                + " containsKey=" + views.containsKey(tag) + " size=" + views.size());

        tag.rename("published");

        System.out.println("  after:  get=" + views.get(tag)
                + " containsKey=" + views.containsKey(tag) + " size=" + views.size());
        System.out.println("  iteration still yields it: " + views.keySet());

        views.remove(tag);
        System.out.println("  remove(tag) did nothing, size=" + views.size());

        // WHAT HAPPENED, in my own words:
        //
        // put() computed hashCode() once, at insertion, and filed the entry in
        // the bucket that number selected. Renaming the tag changed what
        // hashCode() returns, but nothing told the map to re-file anything,
        // because a map has no way to know a key mutated.
        //
        // So get() computes the new hash, goes to a different bucket, finds
        // nothing, and reports null. containsKey does the same. remove does the
        // same, which is why the entry cannot even be deleted by key.
        //
        // Iteration walks every bucket in turn and does not use the hash at
        // all, so it still finds the entry. That is the confusing part: the
        // data is plainly there, and the map insists it is not.
        //
        // The entry is now permanently unreachable by lookup. The only way to
        // remove it is to iterate and use Iterator.remove, or to rebuild the map.

        System.out.println();
        System.out.println("--- and with an immutable key ---");

        Map<Tag, Integer> safe = new HashMap<>();
        safe.put(new Tag("draft"), 100);
        System.out.println("  get with a fresh equal key: " + safe.get(new Tag("draft")));

        // A record cannot be renamed, so the hash it was filed under is the hash
        // it will always have. Nothing can put it out of reach.
    }

    // ---------------------------------------------------------------- helpers

    private static List<String> words() {
        List<String> result = new ArrayList<>();
        for (String line : TEXT.lines().toList()) {
            result.addAll(Arrays.asList(line.strip().split("\\s+")));
        }
        return result;
    }
}

record Tag(String name) { }

class MutableTag {

    private String name;

    MutableTag(String name) {
        this.name = name;
    }

    void rename(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof MutableTag other && Objects.equals(other.name, name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "MutableTag(" + name + ")";
    }
}
