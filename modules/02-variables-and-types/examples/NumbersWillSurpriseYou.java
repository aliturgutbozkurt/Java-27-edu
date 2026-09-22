// Five numeric behaviours that catch people out. None of these are Java bugs.
// All of them are how fixed-width binary arithmetic works, and most languages
// share them. Java just refuses to hide it from you.

void main() {
    // 1. Integer division throws away the remainder. It does not round.
    IO.println("7 / 2 = " + (7 / 2));            // 3, not 3.5 and not 4
    IO.println("7 / 2.0 = " + (7 / 2.0));        // 3.5, because one side is a double

    // If you came from Python 3, note that / there gives 3.5 and // gives 3.
    // Java's / does both jobs depending on the operand types, which is easy to
    // get wrong when the operands are variables and you cannot see the types.

    // 2. Overflow wraps around silently.
    IO.println("Integer.MAX_VALUE     = " + Integer.MAX_VALUE);
    IO.println("Integer.MAX_VALUE + 1 = " + (Integer.MAX_VALUE + 1));
    // That prints -2147483648. No exception, no warning. An int is 32 bits and
    // adding one to the largest one rolls over to the smallest, exactly like an
    // odometer. Python would have grown the number; Java will not.
    //
    // When it matters, ask for the checked version:
    try {
        Math.addExact(Integer.MAX_VALUE, 1);
    } catch (ArithmeticException e) {
        // Module 10 explains this syntax. For now: Math.addExact refuses to
        // wrap silently and complains instead.
        IO.println("Math.addExact says: " + e.getMessage());
    }

    // 3. Floating point cannot represent most decimals exactly.
    IO.println("0.1 + 0.2 = " + (0.1 + 0.2));    // 0.30000000000000004
    // This is not Java being sloppy. 0.1 in binary is a repeating fraction, the
    // same way 1/3 is in decimal. Never use double for money. Use long counting
    // the smallest unit, such as cents, or BigDecimal.

    // 4. The % operator keeps the sign of the LEFT operand.
    IO.println("7 % -2 = " + (7 % -2));          // 1
    IO.println("-7 % 2 = " + (-7 % 2));          // -1
    // If you expected -7 % 2 to be 1, you are thinking of a mathematical modulo.
    // Java gives you a remainder. Use Math.floorMod when you want the other one.
    IO.println("Math.floorMod(-7, 2) = " + Math.floorMod(-7, 2));  // 1

    // 5. char is a number wearing a costume.
    char letter = 'A';
    IO.println("'A' + 1 = " + (letter + 1));     // 66, not "B"
    // The + promoted the char to an int. To get back to a character you have to
    // say so:
    IO.println("(char)('A' + 1) = " + (char) (letter + 1));  // B
}
