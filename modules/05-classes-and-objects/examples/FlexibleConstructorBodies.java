// JEP 513, Flexible Constructor Bodies. Finalised in JDK 25, so it is stable
// here with no flags.
//
// The old rule: super(...) or this(...) had to be the very first statement in a
// constructor. Nothing could run before it.
//
// That rule forced you to construct a parent object first and only then discover
// the arguments were invalid. The new rule lets you validate first.

public class FlexibleConstructorBodies {

    public static void main(String[] args) {
        Positive ok = new Positive(5);
        System.out.println("built: " + ok.value());

        System.out.println();
        System.out.println("now with an invalid value:");
        try {
            new Positive(-1);
        } catch (IllegalArgumentException e) {
            System.out.println("  rejected: " + e.getMessage());
        }

        // Look at the output. "Measurement constructor ran" appears exactly
        // once, for the valid case. The parent constructor never executed for
        // the invalid one, because the check threw before super() was reached.
        //
        // Under the old rule that was impossible. You would have had to write
        // the parent object first, or smuggle the check into a static helper
        // inside the super() call itself:
        //
        //     super(requirePositive(v));
        //
        // which worked but read badly and forced the check to be static.
    }
}

class Measurement {
    private final int value;

    Measurement(int value) {
        this.value = value;
        System.out.println("  Measurement constructor ran with " + value);
    }

    int value() {
        return value;
    }
}

class Positive extends Measurement {

    Positive(int value) {
        // Statements BEFORE super(). This is the new part.
        if (value <= 0) {
            throw new IllegalArgumentException("must be positive, got " + value);
        }

        super(value);

        // After super() the object exists, so `this` is usable from here on.
        // Before super(), `this` is off limits, because the parent has not run
        // and the object is not yet in a valid state. The compiler enforces it.
    }
}
