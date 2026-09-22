// Reference solution for Homework 06.
//
// Part one: a HashSet that loses its contents, then the fix.
// Part two: a small hierarchy with honest dynamic dispatch.

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Library {

    public static void main(String[] args) {
        partOneBroken();
        System.out.println();
        partOneFixed();
        System.out.println();
        partOneMutationTrap();
        System.out.println();
        partTwo();
    }

    // ---------------------------------------------------------------- part 1

    private static void partOneBroken() {
        System.out.println("--- BrokenIsbn: equals without hashCode ---");

        Set<BrokenIsbn> shelf = new HashSet<>();
        shelf.add(new BrokenIsbn("978-0134685991"));
        shelf.add(new BrokenIsbn("978-0134685991"));

        System.out.println("added the same ISBN twice, size: " + shelf.size());
        System.out.println("contains it:                     "
                + shelf.contains(new BrokenIsbn("978-0134685991")));
        System.out.println("yet equals says:                 "
                + new BrokenIsbn("978-0134685991").equals(new BrokenIsbn("978-0134685991")));

        // DIAGNOSIS, in my own words:
        //
        // HashSet never compares every element. That would make contains() a
        // linear scan and throw away the reason to use a hash set at all.
        //
        // Instead it calls hashCode() to work out which bucket an object
        // belongs in, goes to that one bucket, and only calls equals() on what
        // it finds there.
        //
        // BrokenIsbn inherits Object.hashCode, which is derived from the
        // object's identity. Two separately constructed BrokenIsbn objects
        // therefore get two different hash codes, land in two different
        // buckets, and the equals() I carefully wrote is never reached.
        //
        // So the set is not ignoring my equals. It is never getting far enough
        // to ask.
    }

    private static void partOneFixed() {
        System.out.println("--- Isbn: equals and hashCode together ---");

        Set<Isbn> shelf = new HashSet<>();
        shelf.add(new Isbn("978-0134685991"));
        shelf.add(new Isbn("978-0134685991"));

        System.out.println("added the same ISBN twice, size: " + shelf.size());
        System.out.println("contains it:                     "
                + shelf.contains(new Isbn("978-0134685991")));
        System.out.println("prints as:                       " + new Isbn("978-0134685991"));
    }

    private static void partOneMutationTrap() {
        System.out.println("--- the mutable-field trap ---");

        MutableTag tag = new MutableTag("fiction");
        Set<MutableTag> tags = new HashSet<>();
        tags.add(tag);

        System.out.println("before mutation, contains it: " + tags.contains(tag));

        // Changing a field that hashCode depends on, while the object sits in
        // a hash-based collection.
        tag.setName("non-fiction");

        System.out.println("after mutation,  contains it: " + tags.contains(tag));
        System.out.println("but the set still reports size " + tags.size());

        // The object is filed under the bucket for its OLD hash code. Asking
        // contains() computes the NEW one, looks in a different bucket, and
        // finds nothing. The object is lost inside a set that still counts it.
        //
        // This is why fields used by equals and hashCode should be final. An
        // object whose identity can change has no stable identity at all.
    }

    // ---------------------------------------------------------------- part 2

    private static void partTwo() {
        System.out.println("--- dynamic dispatch over a mixed shelf ---");

        LibraryItem[] items = {
            new Book("Effective Java", "Bloch", 412),
            new Audiobook("Project Hail Mary", "Weir", 970),
            new Magazine("Nature", 8123)
        };

        for (LibraryItem item : items) {
            System.out.printf("%-20s %-28s %s%n",
                    item.title(), item.loanSummary(), item.describe());
        }

        // One loop, three behaviours, and adding a fourth item type would not
        // require editing this method at all.
    }
}

// ------------------------------------------------------------------ part 1 types

class BrokenIsbn {

    private final String code;

    BrokenIsbn(String code) {
        this.code = code;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof BrokenIsbn other && other.code.equals(code);
    }

    // hashCode deliberately missing. This class is the bug.
}

final class Isbn {

    private final String code;

    Isbn(String code) {
        this.code = Objects.requireNonNull(code, "code");
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Isbn other && other.code.equals(code);
    }

    @Override
    public int hashCode() {
        // Exactly the field equals compares. Nothing more, nothing less.
        return Objects.hash(code);
    }

    @Override
    public String toString() {
        return "Isbn(" + code + ")";
    }
}

class MutableTag {

    // Not final, on purpose, to demonstrate what that costs.
    private String name;

    MutableTag(String name) {
        this.name = name;
    }

    void setName(String name) {
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
}

// ------------------------------------------------------------------ part 2 types

abstract class LibraryItem {

    // protected and final: subclasses can read it, nobody can reassign it.
    protected final String title;

    LibraryItem(String title) {
        this.title = title;
    }

    String title() {
        return title;
    }

    // Each subclass measures its size differently, so the base class cannot
    // answer this and does not pretend to.
    abstract String loanSummary();

    // An ordinary method, written once and inherited. This is what an abstract
    // class gives you that an interface traditionally did not.
    String describe() {
        return "[" + getClass().getSimpleName().toLowerCase() + "]";
    }

    // final because "how long can this be borrowed" is a policy the library
    // sets, not something an individual item type may redefine.
    final int loanDays() {
        return 21;
    }
}

final class Book extends LibraryItem {

    private final String author;
    private final int pages;

    Book(String title, String author, int pages) {
        super(title);
        this.author = author;
        this.pages = pages;
    }

    @Override
    String loanSummary() {
        return pages + " pages by " + author;
    }
}

final class Audiobook extends LibraryItem {

    private final String author;
    private final int seconds;

    Audiobook(String title, String author, int seconds) {
        super(title);
        this.author = author;
        this.seconds = seconds;
    }

    @Override
    String loanSummary() {
        return String.format("%d:%02d by %s", seconds / 60, seconds % 60, author);
    }

    @Override
    String describe() {
        // Extending the parent's behaviour rather than replacing it.
        return super.describe() + " (headphones advised)";
    }
}

final class Magazine extends LibraryItem {

    private final int issue;

    Magazine(String title, int issue) {
        super(title);
        this.issue = issue;
    }

    @Override
    String loanSummary() {
        return "issue #" + issue;
    }
}
