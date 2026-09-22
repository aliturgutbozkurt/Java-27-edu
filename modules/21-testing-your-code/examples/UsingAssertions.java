// EXPECT: assertions
//
// The same code as the previous example, but this file is marked so the
// verification script runs it with -ea. Assertions are therefore live here.
//
// That marker exists because an example teaching `assert` under plain
// `java <file>` would prove nothing at all: every statement would be skipped
// and the file would pass by doing nothing.

public class UsingAssertions {

    public static void main(String[] args) {
        boolean enabled = false;
        assert enabled = true;
        System.out.println("  assertions enabled: " + enabled);

        System.out.println();
        System.out.println("--- what they are good for ---");

        // An invariant this code has just established itself.
        int[] sorted = {1, 3, 5, 7};
        int index = binarySearch(sorted, 5);
        System.out.println("  binarySearch found index " + index);

        // An unreachable branch. If a new case is ever added to the enum and
        // this switch is not updated, the assert says so loudly during
        // development rather than returning something wrong.
        for (Suit suit : Suit.values()) {
            System.out.println("  " + suit + " is " + colourOf(suit));
        }

        System.out.println();
        System.out.println("--- and what happens when one is false ---");

        try {
            checkInvariant(-1);
        } catch (AssertionError e) {
            System.out.println("  AssertionError: " + e.getMessage());
        }

        // Catching an AssertionError is not something you would normally do.
        // It is done here so the file can demonstrate the failure and still
        // exit zero. In real code an AssertionError means a bug in your own
        // reasoning, and it should propagate and stop the program.

        System.out.println();
        System.out.println("--- the message form is worth using ---");
        System.out.println("    assert condition;                  no message");
        System.out.println("    assert condition : \"what broke\";   a message");
        System.out.println();
        System.out.println("  The second form costs one string and turns an");
        System.out.println("  AssertionError with a null message into one that explains");
        System.out.println("  itself. Module 10 made the same argument about exceptions.");
    }

    static int binarySearch(int[] sorted, int target) {
        // An assertion about a precondition this method's own callers inside
        // the class are expected to honour. Note it is NOT validating a public
        // caller's argument; that would need an exception.
        assert isSorted(sorted) : "binarySearch requires a sorted array";

        int low = 0;
        int high = sorted.length - 1;
        while (low <= high) {
            int mid = (low + high) >>> 1;
            if (sorted[mid] == target) {
                return mid;
            }
            if (sorted[mid] < target) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return -1;
    }

    static boolean isSorted(int[] values) {
        for (int i = 1; i < values.length; i++) {
            if (values[i - 1] > values[i]) {
                return false;
            }
        }
        return true;
    }

    enum Suit { HEARTS, DIAMONDS, CLUBS, SPADES }

    static String colourOf(Suit suit) {
        return switch (suit) {
            case HEARTS, DIAMONDS -> "red";
            case CLUBS, SPADES -> "black";
        };
    }

    static void checkInvariant(int value) {
        assert value >= 0 : "value should never be negative here, was " + value;
    }
}
