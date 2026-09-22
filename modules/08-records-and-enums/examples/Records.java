// A record is a class whose job is to carry data, written in one line.
//
// Module 06 made you write equals, hashCode and toString by hand and showed
// what breaks when you get them wrong. This is the answer to that problem.

import java.util.HashSet;

public class Records {

    public static void main(String[] args) {
        Point p = new Point(3, 4);

        // All three, written by the compiler, correctly.
        System.out.println("toString: " + p);
        System.out.println("equals:   " + p.equals(new Point(3, 4)));
        System.out.println("hashCode: " + (p.hashCode() == new Point(3, 4).hashCode()));

        // Accessors are named after the components, with no "get" prefix.
        System.out.println("x:        " + p.x());

        // Which means hash-based collections work out of the box. Compare this
        // with Module 06's BadPoint, which needed the same code written by hand
        // and silently broke when half of it was missing.
        var set = new HashSet<Point>();
        set.add(new Point(1, 1));
        set.add(new Point(1, 1));
        System.out.println("set size: " + set.size());

        System.out.println();

        // WHAT THE COMPILER ACTUALLY GENERATES.
        //
        // The same trick as Module 01. Compile a record and disassemble it:
        //
        //     javac -d /tmp/rec Money.java
        //     javap -cp /tmp/rec Money
        //
        // for `public record Money(long cents, String currency) { }` gives:
        //
        //     public final class Money extends java.lang.Record {
        //       public Money(long, java.lang.String);
        //       public final java.lang.String toString();
        //       public final int hashCode();
        //       public final boolean equals(java.lang.Object);
        //       public long cents();
        //       public java.lang.String currency();
        //     }
        //
        // Three things worth noticing there:
        //
        //   final class        records cannot be extended. Ever.
        //   extends Record     every record has this implicit superclass, which
        //                      is why a record cannot extend anything else.
        //   final methods      you may override equals/hashCode/toString, but
        //                      subclasses cannot, because there are none.

        // Records are shallowly immutable: the components cannot be reassigned.
        //
        //     p.x = 9;     // does not compile, there is no such field to assign
        //
        // To "change" one, build a new one. This reads oddly at first and stops
        // feeling odd quickly.
        Point moved = new Point(p.x() + 1, p.y());
        System.out.println("original: " + p + "   moved: " + moved);

        System.out.println();

        // A record can have extra methods, static factories, and implement
        // interfaces. What it cannot have is extra instance state.
        //
        //     record Bad(int x) { private int cached; }   // does not compile
        //
        // That restriction is the whole guarantee: a record IS its components,
        // so two records with equal components are interchangeable.
        Range r = Range.of(10, 4);
        System.out.println("normalised: " + r + " length " + r.length());
    }
}

record Point(int x, int y) {

    // An extra method. Perfectly allowed.
    double distanceFromOrigin() {
        return Math.sqrt(x * x + y * y);
    }
}

record Range(int low, int high) {

    // A static factory on a record, doing work a constructor should not.
    static Range of(int a, int b) {
        return a <= b ? new Range(a, b) : new Range(b, a);
    }

    int length() {
        return high - low;
    }
}
