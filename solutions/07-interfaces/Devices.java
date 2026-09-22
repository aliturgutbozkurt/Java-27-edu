// Reference solution for Homework 07.

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class Devices {

    public static void main(String[] args) {
        partOne();
        System.out.println();
        partTwo();
        System.out.println();
        partThree();
    }

    // ---------------------------------------------------------------- part 1

    private static void partOne() {
        System.out.println("--- capabilities ---");

        Lamp lamp = new Lamp();
        Thermostat thermostat = new Thermostat();
        Speaker speaker = new Speaker();

        // Each helper below asks for a capability, never for a concrete class.
        // That is what lets one method serve three unrelated device types.
        for (Switchable s : List.of(lamp, thermostat, speaker)) {
            System.out.println("  " + s.turnOn());
        }

        for (Dimmable d : List.of(lamp, speaker)) {
            System.out.println("  " + d.setLevel(40));
        }

        // A thermostat is Switchable but not Dimmable, so passing it to
        // setLevel would not compile. The capability is checked, not hoped for.

        System.out.println("  thermostat is Dimmable? " + (thermostat instanceof Dimmable));
    }

    // ---------------------------------------------------------------- part 2

    private static void partTwo() {
        System.out.println("--- the conflicting defaults ---");

        // Both interfaces supply describe(). Entry had to resolve it explicitly
        // or the class would not compile at all.
        System.out.println("  " + new SmartBulb().describe());
    }

    // ---------------------------------------------------------------- part 3

    private static void partThree() {
        System.out.println("--- fragile base class ---");

        var items = List.of("a", "b", "c");

        var inherited = new LoggingHashSet();
        inherited.addAll(items);
        System.out.println("  extending HashSet:  " + inherited.recorded() + " recorded, 3 added");

        var composed = new LoggingCollection();
        composed.addAll(items);
        System.out.println("  composing HashSet:  " + composed.recorded() + " recorded, 3 added");

        // WHY THE FIRST ONE IS WRONG, in my own words:
        //
        // HashSet.addAll is written as a loop that calls add() on itself for
        // every element. Because add() is overridden here, each of those
        // internal calls runs my counter as well. addAll counts three, then the
        // three add() calls count three more.
        //
        // I cannot see any of that from inside LoggingHashSet. Nothing in my own
        // source is wrong. The behaviour depends on a choice made inside HashSet
        // that HashSet never promised to keep, and which ArrayList happens to
        // make differently.
        //
        // The composed version has no such dependency. It calls a collection it
        // owns, and nothing calls back into it. Its correctness is decided
        // entirely by code I can read.
    }
}

// ------------------------------------------------------------------ part 1 types

interface Switchable {
    String name();

    // A default method: every switchable device gets this without writing it.
    default String turnOn() {
        return name() + " on";
    }

    default String turnOff() {
        return name() + " off";
    }
}

interface Dimmable {
    String name();

    // A private interface method shared by the defaults below, so the clamping
    // rule lives in one place and is not exposed to implementers.
    private int clamp(int level) {
        return Math.max(0, Math.min(100, level));
    }

    default String setLevel(int level) {
        return name() + " at " + clamp(level) + "%";
    }

    default String full() {
        return setLevel(100);
    }
}

class Lamp implements Switchable, Dimmable {
    @Override
    public String name() {
        return "lamp";
    }
}

class Thermostat implements Switchable {
    @Override
    public String name() {
        return "thermostat";
    }
}

class Speaker implements Switchable, Dimmable {
    @Override
    public String name() {
        return "speaker";
    }

    // Overriding a default because this type can say something better.
    @Override
    public String setLevel(int level) {
        return "speaker volume " + Math.max(0, Math.min(100, level));
    }
}

// ------------------------------------------------------------------ part 2 types

interface Timestamped {
    default String describe() {
        return "installed 2026-09-23";
    }
}

interface Versioned {
    default String describe() {
        return "firmware 2.1";
    }
}

class SmartBulb implements Timestamped, Versioned {

    // Without this override the class does not compile:
    //
    //     error: types Timestamped and Versioned are incompatible;
    //       class SmartBulb inherits unrelated defaults for describe()
    //
    // Java will not pick for me, which is the right call. Either default could
    // be the one I meant, and silently choosing would produce a bug with no
    // symptom at the point of the mistake.
    @Override
    public String describe() {
        return Timestamped.super.describe() + ", " + Versioned.super.describe();
    }
}

// ------------------------------------------------------------------ part 3 types

// The fragile version. Identical code against ArrayList would give the right
// answer, which is exactly what makes this dangerous.
class LoggingHashSet extends HashSet<String> {

    private final List<String> log = new ArrayList<>();

    @Override
    public boolean add(String item) {
        log.add(item);
        return super.add(item);
    }

    @Override
    public boolean addAll(Collection<? extends String> items) {
        log.addAll(items);
        return super.addAll(items);
    }

    int recorded() {
        return log.size();
    }
}

// The composed version. It holds a set rather than being one, so no inherited
// method can re-enter it.
class LoggingCollection {

    private final Collection<String> delegate = new HashSet<>();
    private final List<String> log = new ArrayList<>();

    boolean add(String item) {
        log.add(item);
        return delegate.add(item);
    }

    boolean addAll(Collection<? extends String> items) {
        log.addAll(items);
        return delegate.addAll(items);
    }

    int recorded() {
        return log.size();
    }

    Collection<String> contents() {
        return List.copyOf(delegate);
    }
}
