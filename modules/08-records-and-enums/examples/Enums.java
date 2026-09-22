// An enum is a class with a fixed, known set of instances. It is not a list of
// integer constants, which is what the name suggests and what C gave you.

public class Enums {

    public static void main(String[] args) {
        // Each constant is a full object with its own state.
        for (Planet p : Planet.values()) {
            System.out.printf("%-8s gravity %5.2f   a 75kg person weighs %6.1f N%n",
                    p, p.surfaceGravity(), p.surfaceWeight(75));
        }

        System.out.println();

        // Constants can carry behaviour that differs per constant. This is the
        // feature that makes enums worth reaching for.
        for (Operation op : Operation.values()) {
            System.out.printf("6 %s 3 = %.1f%n", op.symbol(), op.apply(6, 3));
        }

        System.out.println();

        // Enums get useful methods for free.
        Planet earth = Planet.valueOf("EARTH");
        System.out.println("valueOf(\"EARTH\") = " + earth);
        System.out.println("name()           = " + earth.name());
        System.out.println("ordinal()        = " + earth.ordinal());

        // ORDINAL IS A TRAP. It is the declaration position, so inserting a new
        // constant silently renumbers everything after it. Never persist an
        // ordinal to a database or a file, and never switch on one. Use name(),
        // or give the constant an explicit field, as Status does below.
        System.out.println();
        for (Status s : Status.values()) {
            System.out.println(s + " -> stored as " + s.code() + ", ordinal happens to be " + s.ordinal());
        }

        System.out.println();

        // Switching over an enum with no default. Add a constant and this stops
        // compiling, pointing here. Module 03 made the case for leaving default
        // out; enums are where it pays off most.
        System.out.println(advice(Status.FAILED));

        // Enums are singletons per constant, so == is safe and idiomatic here,
        // unlike almost everywhere else in Java.
        System.out.println("== works on enums: " + (earth == Planet.EARTH));
    }

    static String advice(Status status) {
        return switch (status) {
            case PENDING -> "wait";
            case ACTIVE -> "nothing to do";
            case FAILED -> "retry or escalate";
        };
    }
}

enum Planet {

    // Each constant calls the constructor with its own arguments.
    MERCURY(3.303e+23, 2.4397e6),
    EARTH(5.976e+24, 6.37814e6),
    MARS(6.421e+23, 3.3972e6);

    private static final double G = 6.67300E-11;

    // Enum fields should be final. The set of constants is fixed; their state
    // should be too, or you have global mutable state with a friendly name.
    private final double mass;
    private final double radius;

    // The constructor is implicitly private. Nothing outside can create a new
    // Planet, which is the guarantee the whole type rests on.
    Planet(double mass, double radius) {
        this.mass = mass;
        this.radius = radius;
    }

    double surfaceGravity() {
        return G * mass / (radius * radius);
    }

    double surfaceWeight(double otherMass) {
        return otherMass * surfaceGravity();
    }
}

enum Operation {

    // CONSTANT-SPECIFIC BEHAVIOUR. Each constant supplies its own
    // implementation of the abstract method, as an anonymous subclass.
    //
    // This is the alternative to a switch statement that has to be updated in
    // several places whenever a constant is added. Here, adding a constant
    // without its apply() is a compile error.
    PLUS("+") {
        @Override double apply(double a, double b) { return a + b; }
    },
    MINUS("-") {
        @Override double apply(double a, double b) { return a - b; }
    },
    TIMES("*") {
        @Override double apply(double a, double b) { return a * b; }
    },
    DIVIDE("/") {
        @Override double apply(double a, double b) { return a / b; }
    };

    private final String symbol;

    Operation(String symbol) {
        this.symbol = symbol;
    }

    String symbol() {
        return symbol;
    }

    abstract double apply(double a, double b);
}

enum Status {

    // An explicit stable code, deliberately not matching the ordinal, so the
    // difference is visible. Persist this, never the ordinal.
    PENDING("P"),
    ACTIVE("A"),
    FAILED("X");

    private final String code;

    Status(String code) {
        this.code = code;
    }

    String code() {
        return code;
    }
}
