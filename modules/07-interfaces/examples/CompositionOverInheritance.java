// "Prefer composition to inheritance" is repeated so often it has stopped
// meaning anything. Here is the actual problem it solves, and it is worse than
// the slogan suggests.

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class CompositionOverInheritance {

    public static void main(String[] args) {
        var items = List.of("a", "b", "c");

        System.out.println("--- inheritance, extending HashSet ---");
        var brokenSet = new CountingHashSet();
        brokenSet.addAll(items);
        System.out.println("added 3, counter says: " + brokenSet.added() + "   <- wrong");

        System.out.println();
        System.out.println("--- the SAME code, extending ArrayList ---");
        var luckyList = new CountingArrayList();
        luckyList.addAll(items);
        System.out.println("added 3, counter says: " + luckyList.added() + "   <- right, by luck");

        System.out.println();
        System.out.println("--- composition ---");
        var composed = new CountingCollection();
        composed.addAll(items);
        System.out.println("added 3, counter says: " + composed.added() + "   <- right, by design");

        System.out.println();
        System.out.println("Two classes from the same library. Identical overrides.");
        System.out.println("Different answers. Neither subclass can see why.");
    }
}

// THE BROKEN ONE.
//
// It extends HashSet and overrides both add methods to keep a tally. That looks
// obviously correct, and it is wrong.
//
// HashSet.addAll happens to call this.add for each element. So addAll adds 3 to
// the counter, and then the three internal add calls, which dispatch to the
// override, add 3 more. Six.
//
// Nothing in this class is visibly at fault. The bug lives in a detail of the
// parent's implementation that the parent never documented and is free to change
// in any release. This is the FRAGILE BASE CLASS problem.
class CountingHashSet extends HashSet<String> {

    private int added = 0;

    @Override
    public boolean add(String item) {
        added++;
        return super.add(item);
    }

    @Override
    public boolean addAll(Collection<? extends String> items) {
        added += items.size();
        return super.addAll(items);
    }

    int added() {
        return added;
    }
}

// THE SAME CODE AGAINST A DIFFERENT PARENT, and this is the part that should
// worry you.
//
// ArrayList.addAll copies the elements in bulk rather than calling add, so the
// override is never re-entered and the count comes out right.
//
// Identical source, same standard library, opposite results. The correctness of
// this class depends entirely on an implementation choice made inside a class
// you did not write and cannot see from here. Swap the parent, or upgrade the
// JDK, and the answer can change underneath you.
class CountingArrayList extends ArrayList<String> {

    private int added = 0;

    @Override
    public boolean add(String item) {
        added++;
        return super.add(item);
    }

    @Override
    public boolean addAll(Collection<? extends String> items) {
        added += items.size();
        return super.addAll(items);
    }

    int added() {
        return added;
    }
}

// THE COMPOSED ONE.
//
// It HOLDS a collection instead of BEING one. There is no inherited behaviour to
// be surprised by, because nothing is inherited, and the class controls every
// path into its own state. Its correctness does not depend on how HashSet
// happens to be written today.
//
// The cost is real: you write the methods you want to expose instead of getting
// forty for free. That cost is usually worth paying, and it forces a useful
// question, namely which of those forty methods this type actually wanted to
// offer in the first place.
class CountingCollection {

    private final Collection<String> items = new HashSet<>();
    private int added = 0;

    boolean add(String item) {
        added++;
        return items.add(item);
    }

    boolean addAll(Collection<? extends String> newItems) {
        added += newItems.size();
        return items.addAll(newItems);
    }

    int added() {
        return added;
    }

    Collection<String> items() {
        return List.copyOf(items);
    }
}

// THE TEST FOR WHICH TO USE:
//
// Inheritance is right when the subclass can honestly be handed to any code
// expecting the parent and still behave correctly. That is the Liskov
// substitution principle, stated without the name.
//
// Ask: would a caller holding this as the parent type ever be surprised? If yes,
// compose. If the relationship is really "uses a" or "is implemented with", it
// was never inheritance to begin with.
//
// And note the sharper lesson from the two counters above: you cannot answer
// that question by reading your own class. You have to know how the parent is
// implemented, which is exactly the coupling inheritance was supposed to hide.
