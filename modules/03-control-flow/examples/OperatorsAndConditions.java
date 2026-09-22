// Operators and `if`. Most of this will look familiar. The parts that will not
// are marked, and they are the reason this file exists.

void main() {
    // NO TRUTHINESS. Only a boolean goes in an if.
    //
    //     String name = "";
    //     if (name) { }          // does not compile
    //     if (0) { }             // does not compile
    //
    // Coming from Python or JavaScript this feels verbose. The payoff is that
    // there is no table of falsy values to memorise, and no argument about
    // whether an empty list is false.
    String name = "";
    if (!name.isEmpty()) {
        IO.println("has a name");
    } else {
        IO.println("no name, and I had to say so explicitly");
    }

    // && and || short-circuit: the right side is skipped when the left decides
    // the answer. This is not just an optimisation, it is how you guard.
    String maybe = null;
    if (maybe != null && maybe.length() > 3) {
        IO.println("long enough");
    } else {
        // length() was never called, so no NullPointerException.
        IO.println("null or short, and nothing crashed");
    }

    // Swap the order and it crashes. The order of && operands is a correctness
    // decision, not a style one.

    // & and | exist too and do NOT short-circuit. You will almost never want
    // them on booleans. Their real job is bitwise arithmetic.
    IO.println("6 & 3 = " + (6 & 3));   // 2, bitwise AND
    IO.println("6 | 3 = " + (6 | 3));   // 7, bitwise OR
    IO.println("6 ^ 3 = " + (6 ^ 3));   // 5, bitwise XOR
    IO.println("6 << 1 = " + (6 << 1)); // 12, shift left is multiply by 2

    // The ternary, which is an expression and therefore has a value.
    int age = 20;
    String status = age >= 18 ? "adult" : "minor";
    IO.println(status);

    // Compound assignment hides a cast, which occasionally matters.
    byte b = 10;
    b += 300;          // compiles, and silently truncates
    IO.println("byte after += 300: " + b);
    // Writing `b = b + 300;` would NOT compile, because b + 300 is an int and
    // an int does not fit in a byte without you saying so. The compound form
    // inserts the cast for you. Convenient, and occasionally a trap.

    // ++ before and after, the classic interview question that is genuinely
    // useful to know.
    int i = 5;
    IO.println("i++ evaluates to " + (i++) + ", i is now " + i);
    int j = 5;
    IO.println("++j evaluates to " + (++j) + ", j is now " + j);

    // Equality on objects, again, because it never stops mattering.
    String a1 = "hello";
    String a2 = "hel" + "lo";                 // folded at compile time
    String a3 = new StringBuilder("hel").append("lo").toString();  // built at runtime
    IO.println("a1 == a2 : " + (a1 == a2));   // true
    IO.println("a1 == a3 : " + (a1 == a3));   // false
    IO.println("a1.equals(a3) : " + a1.equals(a3));
}
