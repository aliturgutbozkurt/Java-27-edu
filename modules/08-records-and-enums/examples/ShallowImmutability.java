// Records are IMMUTABLE the way `final` is immutable, which is to say not very,
// if you are careless about what you put in them.

import java.util.ArrayList;
import java.util.List;

public class ShallowImmutability {

    public static void main(String[] args) {
        System.out.println("--- the leak ---");

        List<String> tags = new ArrayList<>(List.of("draft"));
        Leaky leaky = new Leaky("post", tags);

        System.out.println("built with: " + leaky);

        // The caller still holds the list. The record copied the REFERENCE.
        tags.add("smuggled-in");
        System.out.println("after the caller edited their own list: " + leaky);

        // And the accessor hands the same list straight back out.
        leaky.tags().add("smuggled-out");
        System.out.println("after editing the accessor's result:    " + leaky);

        System.out.println();
        System.out.println("--- sealed off ---");

        List<String> other = new ArrayList<>(List.of("draft"));
        Safe safe = new Safe("post", other);

        System.out.println("built with: " + safe);

        other.add("smuggled-in");
        System.out.println("after the caller edited their own list: " + safe);

        try {
            safe.tags().add("smuggled-out");
        } catch (UnsupportedOperationException e) {
            System.out.println("the accessor's list refused modification");
        }
        System.out.println("still:      " + safe);

        // WHAT "IMMUTABLE" ACTUALLY MEANS FOR A RECORD:
        //
        // The component fields are final, so nothing can repoint them. That is
        // all the language promises. If a component points at something mutable,
        // that something is still mutable, and the record has no say in it.
        //
        // This matters more for records than for ordinary classes, because
        // records are used as map keys and set elements precisely BECAUSE their
        // equals and hashCode come for free. Module 06's mutation trap applies
        // in full: put a Leaky in a HashSet, edit its list, and it is lost
        // inside the set that still counts it.
        //
        // The fix is one line in a compact constructor, and it is worth writing
        // every time a component is a collection.
    }
}

record Leaky(String title, List<String> tags) {
}

record Safe(String title, List<String> tags) {

    Safe {
        // List.copyOf does both halves at once: it copies, so the caller's list
        // is no longer ours, and it returns an immutable list, so the accessor
        // is safe to hand out directly.
        tags = List.copyOf(tags);
    }
}
