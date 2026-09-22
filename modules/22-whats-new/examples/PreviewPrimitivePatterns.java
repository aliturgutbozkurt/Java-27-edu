// EXPECT: preview
//
// JEP 532, Primitive Types in Patterns, instanceof, and switch.
// FIFTH preview in JDK 27.
//
//   https://openjdk.org/jeps/532
//
// Module 09 taught pattern matching over reference types. This extends the same
// syntax to primitives, and the meaning is subtly different in a way worth
// understanding.

void main() {
    IO.println("--- instanceof on a primitive asks about FIT ---");
    IO.println("");

    int small = 42;
    int big = 300;

    IO.println("  42 instanceof byte:  " + (small instanceof byte));
    IO.println("  300 instanceof byte: " + (big instanceof byte));

    IO.println("");
    IO.println("  For a reference type, instanceof asks \"is this object of that");
    IO.println("  type\". For a primitive it asks something different: \"can this");
    IO.println("  value be converted to that type WITHOUT LOSS\".");
    IO.println("");
    IO.println("  42 fits in a byte, so it is true. 300 does not, so it is false.");
    IO.println("  Neither is a question about what the variable was declared as.");

    IO.println("");
    IO.println("--- which makes narrowing safe ---");
    IO.println("");

    for (int value : new int[]{ 42, 300, 70_000 }) {
        IO.println("  " + classify(value));
    }

    IO.println("");
    IO.println("  Today that narrowing is written as a cast:");
    IO.println("");
    IO.println("      byte b = (byte) value;     // silently wraps if it does not fit");
    IO.println("");
    IO.println("  300 cast to a byte gives " + (byte) 300 + ", with no warning at all.");
    IO.println("  That is Module 02's overflow, and the pattern form refuses rather");
    IO.println("  than wrapping.");

    IO.println("");
    IO.println("--- and it completes switch ---");
    IO.println("");
    IO.println("  Module 09's switch patterns could not match primitives, so a");
    IO.println("  sealed hierarchy of records worked and a plain int did not. This");
    IO.println("  removes that gap, and the exhaustiveness rules carry over.");

    IO.println("");
    IO.println("--- five previews in ---");
    IO.println("");
    IO.println("  JEP 488, 494, 507, 530, 532: fourth and fifth rounds in 26 and 27.");
    IO.println("  Fewer rounds than structured concurrency, and still not final.");
    IO.println("");
    IO.println("  As HowToReadAJep.java put it: the round number tells you how");
    IO.println("  settled the design is. Read it before depending on it.");
}

String classify(int value) {
    return switch (value) {
        case byte b -> value + " fits in a byte (" + b + ")";
        case short s -> value + " needs a short (" + s + ")";
        default -> value + " needs a full int";
    };
}
