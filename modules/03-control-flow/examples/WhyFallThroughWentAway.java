// The old switch statement, kept here so you can recognise it in existing code
// and understand why the arrow form was added.
//
// The old form falls through: once a case matches, execution keeps running into
// the following cases until it hits a break. That was borrowed from C in 1995
// and it caused bugs for the next thirty years.

void main() {
    IO.println("n = 2, old statement form:");
    int n = 2;

    switch (n) {
        case 1:
            IO.println("  one");
        case 2:
            IO.println("  two");
        case 3:
            IO.println("  three");   // prints, because case 2 had no break
            break;
        default:
            IO.println("  other");
    }

    // Output is "two" then "three". The 2 branch matched, printed, and then
    // fell into the 3 branch because nothing stopped it. Exactly one missing
    // keyword, and the behaviour is wrong in a way that reads as correct.

    IO.println("n = 2, arrow form:");
    switch (n) {
        case 1 -> IO.println("  one");
        case 2 -> IO.println("  two");
        case 3 -> IO.println("  three");
        default -> IO.println("  other");
    }

    // Output is just "two". Arrow branches never fall through, so there is no
    // break to forget.

    // Fall-through was occasionally useful for grouping cases:
    //
    //     case 1:
    //     case 2:
    //     case 3:
    //         handleSmall();
    //         break;
    //
    // The arrow form covers that with a comma instead, and without the risk:
    //
    //     case 1, 2, 3 -> handleSmall();

    // Rule for new code: use the arrow form. Reach for the old one only when
    // you genuinely want to run several branches, which is close to never.
}
