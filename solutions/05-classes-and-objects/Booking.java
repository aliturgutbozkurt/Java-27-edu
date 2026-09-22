// Reference solution for Homework 05.
//
// A seat booking that cannot be put into an invalid state. The point of the
// exercise is that the guarantees are enforced by the compiler and the
// constructor, not by a comment asking callers to behave.

import java.util.ArrayList;
import java.util.List;

public class Booking {

    public static void main(String[] args) {
        Reservation r = Reservation.forRow("Ada", 12, List.of("A", "B"));
        System.out.println(r);

        System.out.println();
        System.out.println("--- the invariants, tested ---");

        reject(() -> new Reservation(null, 5, List.of("A")), "null passenger");
        reject(() -> new Reservation("Ada", 0, List.of("A")), "row 0");
        reject(() -> new Reservation("Ada", 5, List.of()), "no seats");
        reject(() -> new Reservation("  ", 5, List.of("A")), "blank passenger");

        System.out.println();
        System.out.println("--- the defensive copy, tested ---");

        List<String> mutableSeats = new ArrayList<>(List.of("C", "D"));
        Reservation copied = new Reservation("Grace", 7, mutableSeats);

        // Try to reach in through the list we handed over.
        mutableSeats.add("SMUGGLED");
        System.out.println("after mutating the caller's list: " + copied.seats());

        // Try to reach in through the list we got back.
        try {
            copied.seats().add("SMUGGLED");
        } catch (UnsupportedOperationException e) {
            System.out.println("the returned list refused modification");
        }
        System.out.println("still: " + copied.seats());
    }

    // A small helper so each invariant test reads as one line. Module 13 covers
    // the lambda syntax; here it is only scaffolding.
    private static void reject(Runnable attempt, String what) {
        try {
            attempt.run();
            System.out.println("  PROBLEM: " + what + " was accepted");
        } catch (IllegalArgumentException | NullPointerException e) {
            System.out.println("  rejected " + what + ": " + e.getMessage());
        }
    }
}

class Reservation {

    // All three are private and final. Private keeps outsiders out; final means
    // nothing inside the class can reassign them after construction either.
    private final String passenger;
    private final int row;
    private final List<String> seats;

    Reservation(String passenger, int row, List<String> seats) {
        // Validate everything BEFORE assigning anything. An object that throws
        // from its constructor never becomes visible to the caller, so there is
        // no half-built Reservation anywhere.
        if (passenger == null) {
            throw new NullPointerException("passenger must not be null");
        }
        if (passenger.isBlank()) {
            throw new IllegalArgumentException("passenger must not be blank");
        }
        if (row < 1) {
            throw new IllegalArgumentException("row must be 1 or greater, got " + row);
        }
        if (seats == null || seats.isEmpty()) {
            throw new IllegalArgumentException("at least one seat is required");
        }

        this.passenger = passenger;
        this.row = row;

        // COPY ON THE WAY IN.
        //
        // Without this, the caller keeps a reference to the very list this
        // object depends on and can add seats to a booking that is already
        // made. List.copyOf both copies and returns an immutable list, so it
        // handles the way out as well.
        this.seats = List.copyOf(seats);
    }

    // A static factory. It has a name, so the intent is readable at the call
    // site, and a second factory taking the same parameter types could exist
    // alongside it where a second constructor could not.
    static Reservation forRow(String passenger, int row, List<String> seats) {
        return new Reservation(passenger, row, seats);
    }

    String passenger() {
        return passenger;
    }

    int row() {
        return row;
    }

    // COPY ON THE WAY OUT, already handled.
    //
    // Because the field was built with List.copyOf it is immutable, so handing
    // it back directly is safe: a caller who tries to modify it gets an
    // UnsupportedOperationException instead of quietly changing our state.
    //
    // Had the field been a plain ArrayList, this getter would need to return
    // List.copyOf(seats) or a fresh copy instead. The cost of that is a new
    // list on every call, which is why making the field immutable once, in the
    // constructor, is usually the better trade.
    List<String> seats() {
        return seats;
    }

    @Override
    public String toString() {
        return "Reservation{" + passenger + ", row " + row + ", seats " + seats + "}";
    }
}
