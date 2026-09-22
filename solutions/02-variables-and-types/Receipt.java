// Reference solution for Homework 02, part one.
//
// Try it yourself before reading. The interesting bug in this exercise is the
// one hiding in "5.10", and meeting it yourself is worth more than being told.

void main() {
    // Prices in whole cents. No double anywhere in this file.
    //
    // 19.99 is stored as 1999. Every calculation below stays in integers, so
    // there is no binary fraction to lose precision to, and the numbers are
    // exact rather than nearly right.
    String[] names = {"Bread", "Milk", "Gum"};
    long[] cents = {1999, 405, 99};

    long subtotal = 0;
    for (int i = 0; i < names.length; i++) {
        IO.println(String.format("%-12s %8s", names[i], money(cents[i])));
        subtotal += cents[i];
    }

    // 20% of the subtotal, still in cents.
    //
    // subtotal * 20 / 100 rather than subtotal * 0.20, because the moment a
    // double enters the calculation the exactness is gone.
    //
    // 20% of 2503 cents is 500.6 exactly, and we cannot charge six tenths of a
    // cent. Plain integer division would truncate to 500 and quietly hand the
    // customer six tenths of a cent. Adding half a unit before dividing rounds
    // to nearest instead, giving 501. The choice is made here, in the open,
    // rather than being whatever the arithmetic happened to do.
    long tax = (subtotal * 20 + 50) / 100;
    long total = subtotal + tax;

    IO.println("-------------------");
    IO.println(String.format("%-12s %8s", "Subtotal", money(subtotal)));
    IO.println(String.format("%-12s %8s", "Tax (20%)", money(tax)));
    IO.println(String.format("%-12s %8s", "Total", money(total)));
}

// Turn whole cents into a display string.
//
// The naive version,
//
//     (cents / 100) + "." + (cents % 100)
//
// breaks on any amount whose cents are below 10. The milk costs 405 cents, and
// 405 % 100 is 5, so the naive version prints "4.5" instead of "4.05". The
// remainder lost its leading zero, because it is a number and numbers do not
// carry leading zeros.
//
// On this receipt that bug would hit four of the six lines: 4.05, the 25.03
// subtotal, the 5.01 tax and the 30.04 total. It is not an edge case.
//
// %02d pads the remainder back to two digits, which is the fix.
String money(long cents) {
    return String.format("%d.%02d", cents / 100, cents % 100);
}

// On the stretch question:
//
// 2503 cents at 7.5% is 187.725 cents. The same half-up treatment,
//
//     long tax = (subtotal * 75 + 500) / 1000;
//
// gives 188, while plain truncation would give 187.
//
// Which one is correct depends on jurisdiction. Tax authorities publish the
// rounding rule rather than leaving it to the developer, and some require
// rounding per line item instead of on the total, which produces a different
// answer again. The point for this exercise is that the rule is written down
// and visible, instead of being whatever the floating point happened to do.
