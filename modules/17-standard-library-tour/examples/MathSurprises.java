// Math is mostly unremarkable. These five cases are not, and each one has
// produced real bugs.

public class MathSurprises {

    public static void main(String[] args) {
        System.out.println("--- 1. abs() can return a negative number ---");

        System.out.println("  Integer.MIN_VALUE:      " + Integer.MIN_VALUE);
        System.out.println("  Math.abs(MIN_VALUE):    " + Math.abs(Integer.MIN_VALUE));
        System.out.println("  is it negative?         " + (Math.abs(Integer.MIN_VALUE) < 0));

        // The int range is asymmetric: -2147483648 to 2147483647. The negation
        // of the minimum does not fit, so it wraps back to itself.
        //
        // This matters in real code. `Math.abs(hash) % buckets` is a common
        // idiom and produces a NEGATIVE index for one hash value in four
        // billion, which is an ArrayIndexOutOfBoundsException that reproduces
        // roughly never.
        System.out.println("  absExact throws instead:");
        try {
            Math.absExact(Integer.MIN_VALUE);
        } catch (ArithmeticException e) {
            System.out.println("    " + e.getMessage());
        }
        // 7 buckets, not 8. MIN_VALUE is divisible by 8, so with 8 both forms
        // return 0 and the bug stays hidden. Choosing the example so the
        // failure actually appears is the whole point.
        int buckets = 7;
        System.out.println("  the bucket-index bug, with " + buckets + " buckets:");
        System.out.println("    abs(MIN_VALUE) % " + buckets + "      = "
                + (Math.abs(Integer.MIN_VALUE) % buckets) + "   <- a negative array index");
        System.out.println("    floorMod(MIN_VALUE, " + buckets + ") = "
                + Math.floorMod(Integer.MIN_VALUE, buckets) + "   <- always in range");

        System.out.println();
        System.out.println("--- 2. integer division rounds toward zero ---");

        System.out.println("  -7 / 2          = " + (-7 / 2));
        System.out.println("  Math.floorDiv   = " + Math.floorDiv(-7, 2));

        // Division truncates toward zero, so -3.5 becomes -3. floorDiv rounds
        // toward negative infinity and gives -4. Which you want depends on the
        // problem; assuming they are the same is the bug.

        System.out.println();
        System.out.println("--- 3. round() is not symmetric ---");

        System.out.println("  Math.round(2.5)  = " + Math.round(2.5));
        System.out.println("  Math.round(-2.5) = " + Math.round(-2.5));

        // round() adds 0.5 and floors, so it rounds half toward POSITIVE
        // infinity. 2.5 goes to 3 and -2.5 goes to -2, not -3. If you expected
        // "round half away from zero", this is not it.

        System.out.println();
        System.out.println("--- 4. overflow is silent unless you ask ---");

        System.out.println("  MAX_VALUE + 1        = " + (Integer.MAX_VALUE + 1));
        try {
            Math.addExact(Integer.MAX_VALUE, 1);
        } catch (ArithmeticException e) {
            System.out.println("  Math.addExact throws: " + e.getMessage());
        }

        // addExact, subtractExact, multiplyExact and incrementExact all refuse
        // to wrap. Use them wherever a wrapped value would be worse than a
        // crash, which for anything counting money or memory is always.

        System.out.println();
        System.out.println("--- 5. floating point is not exact ---");

        System.out.println("  0.1 + 0.2        = " + (0.1 + 0.2));
        System.out.println("  0.1 + 0.2 == 0.3 = " + (0.1 + 0.2 == 0.3));

        // Never compare doubles with ==. Compare against a tolerance, or avoid
        // floating point entirely for money, as Module 02 argued.
        double epsilon = 1e-9;
        System.out.println("  within a tolerance: " + (Math.abs((0.1 + 0.2) - 0.3) < epsilon));

        System.out.println();
        System.out.println("--- the ones that behave ---");

        System.out.println("  max/min:   " + Math.max(3, 7) + " " + Math.min(3, 7));
        System.out.println("  pow/sqrt:  " + Math.pow(2, 10) + " " + Math.sqrt(144));
        System.out.println("  ceil/floor: " + Math.ceil(2.1) + " " + Math.floor(2.9));
        System.out.println("  clamp:     " + Math.clamp(15, 0, 10));
    }
}
