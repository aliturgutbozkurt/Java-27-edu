// The contract that everyone hears about and fewer people have actually seen
// break. Here it is breaking.

import java.util.HashSet;
import java.util.Objects;

public class EqualsHashCodeContract {

    public static void main(String[] args) {
        System.out.println("--- equals() only, no hashCode() ---");

        var broken = new HashSet<BadPoint>();
        broken.add(new BadPoint(1, 2));
        broken.add(new BadPoint(1, 2));

        System.out.println("two equal points added, set size: " + broken.size());
        System.out.println("contains an equal point:          " + broken.contains(new BadPoint(1, 2)));
        System.out.println("but the two ARE equal:            " + new BadPoint(1, 2).equals(new BadPoint(1, 2)));

        // Read that again. The set holds two items it agrees are equal to each
        // other, and it cannot find an item equal to one it contains.
        //
        // HashSet does not search by calling equals on everything. It computes
        // hashCode to pick a bucket, and only compares within that bucket. Two
        // equal objects with different hash codes land in different buckets and
        // never meet.
        //
        // Object.hashCode is based on identity, so two distinct instances get
        // different codes no matter what equals says.

        System.out.println();
        System.out.println("--- equals() and hashCode() together ---");

        var working = new HashSet<GoodPoint>();
        working.add(new GoodPoint(1, 2));
        working.add(new GoodPoint(1, 2));

        System.out.println("two equal points added, set size: " + working.size());
        System.out.println("contains an equal point:          " + working.contains(new GoodPoint(1, 2)));

        System.out.println();
        System.out.println("GoodPoint prints as: " + new GoodPoint(1, 2));
        System.out.println("BadPoint prints as:  " + new BadPoint(1, 2));
    }
}

class BadPoint {

    final int x;
    final int y;

    BadPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof BadPoint other && other.x == x && other.y == y;
    }

    // hashCode deliberately not overridden. This class is a bug.
    // toString deliberately not overridden either, so you can see the default.
}

class GoodPoint {

    final int x;
    final int y;

    GoodPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // THE CONTRACT, in full:
    //
    //   1. If a.equals(b) then a.hashCode() == b.hashCode().   <- the one that breaks things
    //   2. Equal hash codes do NOT imply equality. Collisions are legal.
    //   3. equals must be reflexive:  a.equals(a)
    //   4. symmetric:                 a.equals(b) == b.equals(a)
    //   5. transitive:                a=b and b=c implies a=c
    //   6. consistent:                same answer every time, if nothing changed
    //   7. a.equals(null) is always false
    //
    // Rule 1 is why overriding one without the other is always a bug.
    @Override
    public boolean equals(Object o) {
        // The modern shape: instanceof with a pattern variable, which handles
        // the null case for free because null is never an instanceof anything.
        // Module 09 covers the pattern syntax properly.
        return o instanceof GoodPoint other && other.x == x && other.y == y;
    }

    @Override
    public int hashCode() {
        // Objects.hash does the mixing for you. Pass exactly the fields that
        // equals compares, and no others. Using a field in one and not the
        // other breaks rule 1 just as thoroughly as omitting hashCode.
        return Objects.hash(x, y);
    }

    // toString is the third member of the family. Not part of the contract, but
    // the difference between a readable log line and GoodPoint@1b6d3586.
    @Override
    public String toString() {
        return "GoodPoint(" + x + ", " + y + ")";
    }
}

// THE SHORTCUT: a record writes all three for you, correctly, from the
// components. Module 08 covers them, and for data-carrying classes like this
// one a record is almost always the better answer.
