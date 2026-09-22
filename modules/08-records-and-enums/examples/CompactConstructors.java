// The compact constructor: where a record validates and normalises its input.

public class CompactConstructors {

    public static void main(String[] args) {
        System.out.println(new Email("ADA@Example.COM  "));

        try {
            new Email("not-an-email");
        } catch (IllegalArgumentException e) {
            System.out.println("rejected: " + e.getMessage());
        }

        System.out.println();

        System.out.println(new Fraction(6, 8));    // reduced automatically
        System.out.println(new Fraction(-1, -2));  // sign normalised

        try {
            new Fraction(1, 0);
        } catch (ArithmeticException e) {
            System.out.println("rejected: " + e.getMessage());
        }
    }
}

record Email(String address) {

    // A COMPACT CONSTRUCTOR. Note there is no parameter list and no closing
    // assignment. The parameters are implicit, and the compiler assigns them to
    // the fields for you AFTER this body runs.
    //
    // So you validate and reassign the PARAMETER, and the compiler takes the
    // final value of that parameter as the field. Writing `this.address = ...`
    // here is unnecessary, and in a compact constructor it is a compile error.
    Email {
        if (address == null || !address.contains("@")) {
            throw new IllegalArgumentException("not an email address: " + address);
        }

        // Normalising. This assignment changes what gets stored, which is the
        // whole reason the compact form exists.
        address = address.strip().toLowerCase();
    }
}

record Fraction(int numerator, int denominator) {

    Fraction {
        if (denominator == 0) {
            throw new ArithmeticException("denominator must not be zero");
        }

        // Keep the sign on the numerator, so -1/-2 and 1/2 are the same value
        // and therefore equal. Without this, two fractions representing the
        // same number would have different components and fail equals.
        if (denominator < 0) {
            numerator = -numerator;
            denominator = -denominator;
        }

        // Reduce, so 6/8 and 3/4 are equal too.
        int divisor = gcd(Math.abs(numerator), Math.abs(denominator));
        if (divisor > 1) {
            numerator /= divisor;
            denominator /= divisor;
        }
    }

    // Normalising in the constructor is what makes the generated equals correct
    // for the concept, not merely for the fields. A record's equals compares
    // components, so if two logically equal values can have different
    // components, the record is lying and the fix belongs here.

    private static int gcd(int a, int b) {
        return b == 0 ? a : gcd(b, a % b);
    }

    @Override
    public String toString() {
        return numerator + "/" + denominator;
    }
}
