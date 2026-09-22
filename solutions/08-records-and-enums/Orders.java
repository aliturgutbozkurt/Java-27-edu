// Reference solution for Homework 08.

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Orders {

    public static void main(String[] args) {
        partOne();
        System.out.println();
        partTwo();
        System.out.println();
        partThree();
    }

    // ---------------------------------------------------------------- part 1

    private static void partOne() {
        System.out.println("--- records and normalisation ---");

        Money a = new Money(1250, "gbp");
        Money b = new Money(1250, "GBP  ");

        // Equal despite different raw input, because the compact constructor
        // normalised the currency before it was stored. Without that, these two
        // would be different values representing the same amount.
        System.out.println("  " + a + " and " + b);
        System.out.println("  equal: " + a.equals(b));

        // Records work as set elements for free.
        Set<Money> prices = new HashSet<>(List.of(a, b, new Money(99, "gbp")));
        System.out.println("  distinct prices: " + prices.size());

        try {
            new Money(-1, "gbp");
        } catch (IllegalArgumentException e) {
            System.out.println("  rejected: " + e.getMessage());
        }
        try {
            new Money(100, "pounds");
        } catch (IllegalArgumentException e) {
            System.out.println("  rejected: " + e.getMessage());
        }
    }

    // ---------------------------------------------------------------- part 2

    private static void partTwo() {
        System.out.println("--- the two leaks ---");

        List<String> notes = new ArrayList<>(List.of("gift wrap"));
        LeakyOrder leaky = new LeakyOrder("A-1", notes);
        System.out.println("  leaky built with:        " + leaky);

        notes.add("leaked in");
        leaky.notes().add("leaked out");
        System.out.println("  leaky after two attacks: " + leaky);

        List<String> otherNotes = new ArrayList<>(List.of("gift wrap"));
        Order safe = new Order("A-1", otherNotes);
        System.out.println("  safe built with:         " + safe);

        otherNotes.add("leaked in");
        try {
            safe.notes().add("leaked out");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        System.out.println("  safe after two attacks:  " + safe);

        // THE TWO LEAKS, in my own words:
        //
        // 1. ON THE WAY IN. The constructor stored the caller's list object
        //    itself. The caller still has a reference to it, so anything they
        //    do to it afterwards happens to my record's contents.
        //
        // 2. ON THE WAY OUT. The accessor handed the same list back to whoever
        //    asked, so any caller could modify it without going through me.
        //
        // List.copyOf closes both with one line. It copies, which defeats the
        // first, and returns an immutable list, which defeats the second.
        //
        // Why this matters more for a record than an ordinary class: records
        // are used as map keys and set elements because equals and hashCode are
        // free. A record whose contents can change from outside is a key whose
        // hash can change while it sits in a map, which is Module 06's mutation
        // trap with a nicer syntax.
    }

    // ---------------------------------------------------------------- part 3

    private static void partThree() {
        System.out.println("--- enum with per-constant behaviour ---");

        Money subtotal = new Money(10_000, "gbp");

        for (Shipping option : Shipping.values()) {
            System.out.printf("  %-11s %-18s %s%n",
                    option, option.description(), option.costFor(subtotal));
        }

        System.out.println();
        System.out.println("  status advice:");
        for (OrderStatus s : OrderStatus.values()) {
            System.out.println("    " + s + " (code " + s.code() + ") -> " + advice(s));
        }
    }

    // No default branch. Adding a constant to OrderStatus breaks this method at
    // compile time and points straight at it, which is what I want: a new status
    // should force a decision here rather than fall into a catch-all.
    private static String advice(OrderStatus status) {
        return switch (status) {
            case PLACED -> "await payment";
            case PAID -> "pick and pack";
            case SHIPPED -> "nothing to do";
            case CANCELLED -> "refund if paid";
        };
    }
}

// ------------------------------------------------------------------ records

record Money(long cents, String currency) {

    private static final Set<String> SUPPORTED = Set.of("gbp", "eur", "usd");

    Money {
        if (cents < 0) {
            throw new IllegalArgumentException("amount must not be negative: " + cents);
        }
        if (currency == null) {
            throw new IllegalArgumentException("currency must not be null");
        }

        // Normalise BEFORE validating against the supported set, so "GBP  " is
        // accepted and stored identically to "gbp". Doing it the other way
        // round would reject input that is only badly formatted.
        currency = currency.strip().toLowerCase();

        if (!SUPPORTED.contains(currency)) {
            throw new IllegalArgumentException("unsupported currency: " + currency);
        }
    }

    Money plus(Money other) {
        if (!currency.equals(other.currency)) {
            throw new IllegalArgumentException(
                    "cannot add " + currency + " to " + other.currency);
        }
        // Records are immutable, so "adding" means building a new one.
        return new Money(cents + other.cents, currency);
    }

    Money percent(int percent) {
        // Half-up in whole cents, the same decision Module 02's receipt made.
        return new Money((cents * percent + 50) / 100, currency);
    }

    @Override
    public String toString() {
        return String.format("%d.%02d %s", cents / 100, cents % 100, currency.toUpperCase());
    }
}

// The deliberately broken version, kept so the two can be compared side by side.
record LeakyOrder(String reference, List<String> notes) {
}

record Order(String reference, List<String> notes) {

    Order {
        if (reference == null || reference.isBlank()) {
            throw new IllegalArgumentException("reference is required");
        }
        // One line, both leaks.
        notes = List.copyOf(notes);
    }
}

// ------------------------------------------------------------------ enums

enum Shipping {

    // Each constant supplies its own costing rule. Adding a constant without
    // costFor() will not compile, so the rule cannot be forgotten.
    STANDARD("3 to 5 days") {
        @Override
        Money costFor(Money subtotal) {
            // Free over 50.00.
            return subtotal.cents() >= 5_000
                    ? new Money(0, subtotal.currency())
                    : new Money(399, subtotal.currency());
        }
    },
    EXPRESS("next day") {
        @Override
        Money costFor(Money subtotal) {
            return new Money(899, subtotal.currency());
        }
    },
    COLLECTION("collect in store") {
        @Override
        Money costFor(Money subtotal) {
            return new Money(0, subtotal.currency());
        }
    };

    private final String description;

    Shipping(String description) {
        this.description = description;
    }

    String description() {
        return description;
    }

    abstract Money costFor(Money subtotal);
}

enum OrderStatus {

    // Explicit stable codes. These are what would go in a database column.
    // The ordinal is a position in this file and means nothing outside it, so
    // persisting it would break the moment someone inserts a constant.
    PLACED("PLC"),
    PAID("PAY"),
    SHIPPED("SHP"),
    CANCELLED("CAN");

    private final String code;

    OrderStatus(String code) {
        this.code = code;
    }

    String code() {
        return code;
    }
}
